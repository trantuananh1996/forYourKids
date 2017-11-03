package koiapp.pr.com.koiapp.moduleChat.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.JsonObject;

import net.yazeed44.imagepicker.model.ImageEntry;
import net.yazeed44.imagepicker.util.Picker;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import koiapp.pr.com.koiapp.R;
import koiapp.pr.com.koiapp.api.FirebaseAPI;
import koiapp.pr.com.koiapp.model.ItemNotification;
import koiapp.pr.com.koiapp.model.PostSendNotification;
import koiapp.pr.com.koiapp.model.User;
import koiapp.pr.com.koiapp.model.UserNotRealm;
import koiapp.pr.com.koiapp.moduleChat.ChatUtils;
import koiapp.pr.com.koiapp.moduleChat.adapter.ChatCustomAdapter;
import koiapp.pr.com.koiapp.moduleChat.model.ChatMessage;
import koiapp.pr.com.koiapp.moduleChat.model.ChatUser;
import koiapp.pr.com.koiapp.utils.AppUtils;
import koiapp.pr.com.koiapp.utils.FragmentUtils;
import koiapp.pr.com.koiapp.utils.HTTPUtils;
import koiapp.pr.com.koiapp.utils.realm.PrRealm;
import koiapp.pr.com.koiapp.utils.view.PrFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static koiapp.pr.com.koiapp.moduleChat.ChatUtils.KEY_MESSAGES;
import static koiapp.pr.com.koiapp.moduleChat.ChatUtils.KEY_USERS;
import static koiapp.pr.com.koiapp.moduleChat.ChatUtils.createChatroomPath;


/**
 * Created by Tran Anh
 * on 1/19/2017.
 */

public class FragmentChat extends PrFragment implements SwipeRefreshLayout.OnRefreshListener {
    public static final String TAG = FragmentChat.class.getName();
    View rootView;
    FragmentUtils fragmentUtils;

    @BindView(R.id.progress)
    ProgressBar progressBar;
    @BindView(R.id.rv_chat)
    RecyclerView rvChat;
    @BindView(R.id.tv_send)
    ImageView tvSend;
    @BindView(R.id.edt_content)
    EditText etContent;
    @BindView(R.id.choose_image)
    View vChooseImage;
    ChatCustomAdapter chatAdapter;
    DatabaseReference rootRef;
    DatabaseReference chatRoomRef;
    DatabaseReference userRef;
    DatabaseReference myRef;
    DatabaseReference otherRef;
    DatabaseReference otherOnlineRef;
    DatabaseReference chatMessageRef;

    List<ChatMessage> messages = new ArrayList<>();
    @BindView(R.id.swipe)
    SwipeRefreshLayout swipeRefreshLayout;
    private String myId;
    private String otherId;
    String otherName;
    public String otherAvatar = "";
    StorageReference rootStorageRef;
    ChatUser me;
    ChatUser other;
    String myName;
    private ArrayList<ImageEntry> mSelectedImages = new ArrayList<>();
    private int limit = 0;
    private int limitCount = 100;

    User user;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentUtils = FragmentUtils.getInstance(getActivity().getApplicationContext());

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        findView();
       /* Glide.with(getActivity())
                .load(R.drawable.typing_indicator_gif)
                .asGif()  // you may not need this
                .crossFade()
                .into(ivTypingGif);*/

        user = PrRealm.getInstance(getActivity().getApplicationContext()).getCurrentUser();
        if (user != null) {
            myId = user.getuId();
            myName = user.getName();
        }
        chatAdapter = new ChatCustomAdapter(FragmentChat.this, messages);
        rvChat.setAdapter(chatAdapter);
        rvChat.setLayoutManager(new LinearLayoutManager(getActivity().getApplication()));
        rootRef = FirebaseDatabase.getInstance().getReference("chats");
        rootStorageRef = FirebaseStorage.getInstance().getReference("chats");

        chatRoomRef = rootRef.child(createChatroomPath(myId, otherId));

//        ImageView iv = (ImageView) rootView.findViewById(R.id.iv_typing_avatar);
//        if (!TextUtils.isEmpty(otherAvatar))
//            controllerFactory.getImageController().loadImage(otherAvatar, iv);
        TextView tv = (TextView) rootView.findViewById(R.id.tv_typing);
        tv.setText(String.format(getString(R.string.str_string_typing), otherName));

        handleChatroom();

        KeyboardVisibilityEvent.setEventListener(getActivity(), isOpen -> rvChat.scrollToPosition(messages.size() - 1));
        vChooseImage.setOnClickListener(v -> {
            UIUtil.hideKeyboard(getActivity());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (AppUtils.getInstance(getActivity().getApplicationContext()).verifyStoragePermissions(getActivity())) {
                    Picker picker = new Picker.Builder(getActivity(), new MyPickListener(), R.style.MIP_theme).setLimit(1).build();
                    picker.startActivity();
                }
            } else {
                Picker picker = new Picker.Builder(getActivity(), new MyPickListener(), R.style.MIP_theme).setLimit(1).build();
                picker.startActivity();
            }
        });
        swipeRefreshLayout.setOnRefreshListener(this);
        return rootView;
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        messages.clear();
        final int[] count = {0};
        limit = limit + limitCount + 1;

        chatMessageRef.limitToFirst(limit).getRef().limitToLast(limit + limitCount).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                count[0]++;
                handleChildAdded(dataSnapshot, s);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                count[0]++;
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    class MyPickListener implements Picker.PickListener {
        @Override
        public void onPickedSuccessfully(final ArrayList<ImageEntry> images) {
            if (images.size() > 0) {
                mSelectedImages.clear();
                mSelectedImages.addAll(images);
                countImageUploading = mSelectedImages.size();
                uploadImageToFirebase();
            }
        }

        @Override
        public void onCancel() {
            fragmentUtils.showToast(R.string.toast_no_image_choosed);
        }
    }

    private int countImageUploading = 0;

    private void uploadImageToFirebase() {
        if (mSelectedImages.size() == 0) {
            fragmentUtils.showToast(R.string.toast_must_select_image);
        } else if (mSelectedImages.size() == 1) {
            ChatMessage chatMessage = new ChatMessage(myId, "", "temp_image", System.currentTimeMillis() / 1000);
            chatMessage.setImageEntry(mSelectedImages.get(0));
            messages.add(chatMessage);
            chatAdapter.notifyDataSetChanged();
            rvChat.scrollToPosition(messages.size() - 1);

            uploadOneImage(mSelectedImages.get(0));
        } else {
            //TODO: Upload nhiều ảnh lên firebase
        }
    }

    private void uploadOneImage(ImageEntry imageEntry) {
        StorageReference roomRef = rootStorageRef.child(createChatroomPath(myId, otherId));
        final long timeStamp = System.currentTimeMillis() / 1000;
        String fileName = myId + "_" + timeStamp;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        imageEntry.setMaxDimen(500);
        imageEntry.getBitmapRotated().compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] aData = baos.toByteArray();
        UploadTask uploadTask = roomRef.child(fileName).putBytes(aData);
        uploadTask.addOnFailureListener(exception -> fragmentUtils.showToast(R.string.str_send_image_failed)).addOnSuccessListener(taskSnapshot -> {
            String downloadUrl = taskSnapshot.getDownloadUrl() == null ? "" : taskSnapshot.getDownloadUrl().toString();
            if (TextUtils.isEmpty(downloadUrl)) return;
//                Log.d("UploadedImage", downloadUrl);
            chatRoomRef.child(KEY_MESSAGES).push().setValue(new ChatMessage(myId, etContent.getText().toString(), downloadUrl, timeStamp));
            chatRoomRef.child("lastSenderId").setValue(myId);
            chatRoomRef.child("hasUnreadMessage").setValue(1);
            etContent.setText("");
            countMessageSent++;
            if (countMessageSent >= 5) {
                sendNotification();
                countMessageSent = 0;
            }
        });

    }

    ValueEventListener myRefListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            me = dataSnapshot.getValue(ChatUser.class);
            if (me == null) {
                me = new ChatUser(myId, myName, 0);
                myRef.setValue(me);
            } else {

            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
    ValueEventListener otherRefListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            other = dataSnapshot.getValue(ChatUser.class);
            if (other == null) {
                other = new ChatUser(otherId, otherName, 0);
                otherRef.setValue(other);
            } else {
                if (other.getTyping() != null && other.getTyping().equals(1)) {
                    rvChat.scrollToPosition(messages.size() - 1);
                    rootView.findViewById(R.id.rl_typing).setVisibility(View.VISIBLE);
                } else {
                    rvChat.scrollToPosition(messages.size() - 1);
                    rootView.findViewById(R.id.rl_typing).setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public void handleChildAdded(DataSnapshot dataSnapshot, String s) {
        ChatMessage message = dataSnapshot.getValue(ChatMessage.class);
        if (message != null) {
            if (message.getSenderId().equals(otherId))
                chatRoomRef.child(KEY_MESSAGES).child(dataSnapshot.getKey()).child("unread").setValue(0);
            if (countImageUploading > 0) {
                countImageUploading--;
                messages.remove(messages.size() - 1);
                messages.add(message);
                chatAdapter.notifyDataSetChanged();
                rvChat.scrollToPosition(messages.size() - 1);
            } else {
                messages.add(message);
                chatAdapter.notifyDataSetChanged();
                rvChat.scrollToPosition(messages.size() - 1);
            }
        }

    }

    long otherLastUpdate;
    Handler handlerUpdate = new Handler();
    final int delay = 1000 * 5 * 60;
    ValueEventListener otherOnlineListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                otherLastUpdate = dataSnapshot.getValue(long.class);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    ChildEventListener chatMessageListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            handleChildAdded(dataSnapshot, s);
            messageLoadedCount++;

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//            handleChildAdded(dataSnapshot, s);

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private boolean isNotiSending = false;

    private long lastSendTime = 0;


    private long messagesToLoadCount = 0;
    private long messageLoadedCount = 0;

    private long countMessageSent = 0;

    private void handleChatroom() {
        FragmentUtils.showProgress(true, progressBar);
        chatRoomRef = rootRef.child(createChatroomPath(myId, otherId));
//        Log.d(TAG, "Chat room" + createChatroomPath(myId, otherId));

        userRef = chatRoomRef.child(KEY_USERS);
        myRef = userRef.child(String.valueOf(myId));
        myRef.addValueEventListener(myRefListener);
        otherRef = userRef.child(String.valueOf(otherId));
        otherRef.addValueEventListener(otherRefListener);
        otherOnlineRef = rootRef.child(KEY_USERS).child(ChatUtils.createUserPath(otherId)).child(ChatUtils.KEY_LAST_UPDATE);
        chatMessageRef = chatRoomRef.child(KEY_MESSAGES);
        otherOnlineRef.addValueEventListener(otherOnlineListener);

        chatMessageRef.limitToLast(limit + limitCount).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messagesToLoadCount = dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        chatMessageRef.limitToLast(limit + limitCount).addChildEventListener(chatMessageListener);
        handlerUpdate.postDelayed(() -> {
//                if (otherLastUpdate + 60 < System.currentTimeMillis() / 1000) {
//                    if (otherLastUpdate + 60 * 10 < System.currentTimeMillis() / 1000) {
//                        isNotificationSent = false;
//                    }
          /*  if (!isNotiSending) {
                if (lastSendTime != 0 && lastSendTime + 15 < System.currentTimeMillis() / 1000)
                    sendNotification();
            }
//                }
            if (handlerUpdate != null) handlerUpdate.postDelayed(this, delay);*/
        }, delay);

        final TextWatcher textWatcher = new TextWatcher() {
            private Timer timer = new Timer();
            private final long DELAY = 1000; // milliseconds
            private boolean isTyping = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                me.setTyping(1);
                myRef.setValue(me);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(etContent.getText().toString())) {
                    tvSend.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                } else {
                    tvSend.clearColorFilter();
                    me.setTyping(0);
                    myRef.setValue(me);
                    return;
                }
                if (!isTyping) {
                    me.setTyping(1);
                    myRef.setValue(me);
                    isTyping = true;
                }
                timer.cancel();
                timer = new Timer();
                timer.schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                isTyping = false;
                                me.setTyping(0);
                                myRef.setValue(me);
                            }
                        },
                        DELAY
                );
            }
        };
        etContent.addTextChangedListener(textWatcher);
        tvSend.setOnClickListener(v -> {
            if (TextUtils.isEmpty(etContent.getText().toString())) {
                fragmentUtils.showToast(R.string.toast_must_enter_message);
                return;
            }
            me.setTyping(0);
            myRef.setValue(me);
            chatRoomRef.child(KEY_MESSAGES).push().setValue(new ChatMessage(myId, etContent.getText().toString(), System.currentTimeMillis() / 1000));
            chatRoomRef.child("lastSenderId").setValue(myId);
            chatRoomRef.child("hasUnreadMessage").setValue(1);
            etContent.setText("");
            lastSendTime = System.currentTimeMillis() / 1000;
            countMessageSent++;
            if (countMessageSent >= 5) {
                sendNotification();
                countMessageSent = 0;
            }
        });

        /**
         * messageLoadedCount: Đếm số tin nhắn đã load về
         * messageToLoadCount: Tổng số tin nhắn cần phải load
         * Mỗi 0.5s check lại
         */
        final Handler handlerLoadData = new Handler();
        handlerLoadData.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (messageLoadedCount >= messagesToLoadCount) {
                    FragmentUtils.showProgress(false, progressBar);
                    handlerLoadData.removeCallbacks(this);
                } else handlerLoadData.postDelayed(this, 500);
            }
        }, 500);
    }

    private boolean isNotificationSent = false;


    private void unregisterEvent() {
        if (chatMessageRef != null) chatMessageRef.removeEventListener(chatMessageListener);
        if (otherOnlineRef != null) otherOnlineRef.removeEventListener(otherOnlineListener);
        chatMessageRef = null;
        otherOnlineRef = null;
    }

    @Override
    public void onPause() {
//        Log.d(TAG, "Chat onPause");
        super.onPause();
        handlerUpdate = null;
//        unregisterEvent();
    }

    @Override
    public void onDestroy() {
//        Log.d(TAG, "Chat onDestroy");
        super.onDestroy();
        handlerUpdate = null;
        unregisterEvent();
    }

    @Override
    public void onDestroyView() {
//        Log.d(TAG, "Chat onDestroyView");
        if (countMessageSent < 5 && countMessageSent > 0) {
            sendNotification();
            countMessageSent = 0;
        }
        super.onDestroyView();
        handlerUpdate = null;
        unregisterEvent();
    }

    @Override
    public void onResume() {
//        Log.d(TAG, "Chat onResum");
        super.onResume();
    }


    private void sendNotification() {
        if (isNotificationSent) return;
        isNotiSending = true;
        isNotificationSent = true;

        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("users").child(otherId);
        dRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserNotRealm u = dataSnapshot.getValue(UserNotRealm.class);
                if (u != null && u.getDevice_ids() != null && u.getDevice_ids().size() > 0) {
                    ItemNotification itemNotification = new ItemNotification();
                    itemNotification.setUserId(otherId);
                    itemNotification.setTitle("Bạn có tin nhắn mới");
                    itemNotification.setSubtitle("Tin nhắn từ " + myName);
                    itemNotification.setTime(System.currentTimeMillis() / 1000);
                    itemNotification.setType(ItemNotification.TYPE_HAS_NEW_MESSAGE);
                    PostSendNotification post = new PostSendNotification();
                    post.setData(itemNotification);
                    post.setRegistrationIds(u.getDevice_ids());
                    Call<JsonObject> call = FirebaseAPI.getServices().sendNotification(post, "key=AIzaSyBpgEIIaKqrg_Bj8S6tMC4bQuBbG-WIdSI", "application/json");
                    HTTPUtils.postDetail(call, post);
                    call.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            isNotificationSent = true;
                            isNotiSending = false;
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void findView() {
        super.findView();
        ButterKnife.bind(this, rootView);
    }

    public String getOtherAvatar() {
        return otherAvatar;
    }

    public void setOtherAvatar(String otherAvatar) {
        this.otherAvatar = otherAvatar;
    }

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    public String getMyId() {
        return myId;
    }

    public void setMyId(String myId) {
        this.myId = myId;
    }

    public String getOtherId() {
        return otherId;
    }

    public void setOtherId(String otherId) {
        this.otherId = otherId;
    }
}

