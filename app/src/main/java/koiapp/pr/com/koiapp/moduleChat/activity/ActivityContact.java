package koiapp.pr.com.koiapp.moduleChat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import koiapp.pr.com.koiapp.R;
import koiapp.pr.com.koiapp.model.User;
import koiapp.pr.com.koiapp.moduleChat.ChatUtils;
import koiapp.pr.com.koiapp.moduleChat.adapter.ContactRecyclerAdapter;
import koiapp.pr.com.koiapp.moduleChat.model.ChatMessage;
import koiapp.pr.com.koiapp.moduleChat.model.UserContact;
import koiapp.pr.com.koiapp.utils.FragmentUtils;
import koiapp.pr.com.koiapp.utils.HTTPUtils;
import koiapp.pr.com.koiapp.utils.realm.PrRealm;

import static koiapp.pr.com.koiapp.moduleChat.ChatUtils.KEY_LAST_UPDATE;
import static koiapp.pr.com.koiapp.moduleChat.ChatUtils.KEY_MESSAGES;
import static koiapp.pr.com.koiapp.moduleChat.ChatUtils.KEY_USERS;

/**
 * Created by Tran Anh
 * on 6/12/2017.
 */

public class ActivityContact extends AppCompatActivity {
    EditText search;
    public TextView tv_nomatch;

    HTTPUtils httpUtils;
    FragmentUtils fragmentUtils;
    List<UserContact> users;

    public List<UserContact> getUsers() {
        return users;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        httpUtils = HTTPUtils.getInstance(ActivityContact.this);
        fragmentUtils = FragmentUtils.getInstance(ActivityContact.this);
        rootRef = FirebaseDatabase.getInstance().getReference("chats");
        User user = PrRealm.getInstance(getApplicationContext()).getCurrentUser();
        if (user != null)
            FirebaseDatabase.getInstance().getReference("users").child(user.getuId()).child("online").setValue(true);
        findView();
        getContact();
    }


    ProgressBar progressBar;
    ContactRecyclerAdapter mAdapter;
    RecyclerView rvContact;

    DatabaseReference rootRef;

    private void getContact() {
        FragmentUtils.showProgress(true, progressBar);
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 users = new ArrayList<>();

                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    UserContact u = d.getValue(UserContact.class);
                    users.add(u);
                }

                if (users.size() > 0) {
                    tv_nomatch.setVisibility(View.GONE);
                    search.setVisibility(View.VISIBLE);
                    addContactList();
                } else {
                    search.setVisibility(View.GONE);
                    tv_nomatch.setVisibility(View.VISIBLE);
                    tv_nomatch.setText("Không có danh bạ");
                }
                FragmentUtils.showProgress(false, progressBar);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                FragmentUtils.showProgress(false, progressBar);

            }
        });
    }


    public void findView() {
        progressBar = (ProgressBar) findViewById(R.id.progress);
        search = (EditText) findViewById(R.id.edt_search);
        tv_nomatch = (TextView) findViewById(R.id.tv_no_match);
        rvContact = (RecyclerView) findViewById(R.id.rv_contact);
    }

    public void countUnread() {
        int count = 0;
        for (UserContact contact : users) {
            if (contact.isHasMessage()) count++;
        }
    }

    private boolean isReverse = false;

    private void addListener(final String myId, final UserContact contactItem) {
        final String otherId = contactItem.getuId();
        final DatabaseReference chatRoomRef = rootRef.child(ChatUtils.createChatroomPath(myId, otherId));

        chatRoomRef.child(KEY_MESSAGES).limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatMessage message = dataSnapshot.getValue(ChatMessage.class);
                contactItem.setLastMessageKey(dataSnapshot.getKey());
                if (message != null) {
                    boolean currentStatus = contactItem.isHasMessage();
                    if (message.getUnread() != null) {
                        if (message.getSenderId() != null && !message.getSenderId().equals(myId)) {
                            if (message.getUnread().equals(1)) {
                                contactItem.setHasMessage(true);
                            } else {
                                contactItem.setHasMessage(false);
                            }
                        } else {
                            contactItem.setHasMessage(false);
                        }
                    }
                    if (currentStatus != contactItem.isHasMessage()) {
                        if (mAdapter != null) mAdapter.notifyDataSetChanged();
                        countUnread();
                    }

                    contactItem.setLastUpdate(message.getTime());
                    Collections.sort(users);
                    if (!isReverse) {
                        Collections.reverse(users);
                        isReverse = true;
                    }

                    if (mAdapter != null) mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
        rootRef.child(KEY_USERS).child(ChatUtils.createUserPath(contactItem.getuId())).child(KEY_LAST_UPDATE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    long time = dataSnapshot.getValue(long.class);
                    long currentTime = System.currentTimeMillis() / 1000;
                    boolean currentIsOnline = contactItem.isOnline();

                    if (time + 20 < currentTime) {
                        //Thời gian cập nhật cuối quá 20s so với hiện tại -> offline
                        contactItem.setOnline(false);
                    } else {
                        contactItem.setOnline(true);
                    }
                    if (currentIsOnline != contactItem.isOnline())
                        if (mAdapter != null) mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addContactList() {
        Collections.sort(users);
        mAdapter = new ContactRecyclerAdapter(ActivityContact.this);
        rvContact.setAdapter(mAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvContact.setLayoutManager(linearLayoutManager);

        String myId = "";
        User currentU = PrRealm.getInstance(getApplicationContext()).getCurrentUser();
        if (currentU != null) myId = currentU.getuId();
        for (UserContact contact : users) {
            addListener(myId, contact);
        }

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = search.getText().toString().toLowerCase(Locale.getDefault());
                mAdapter.filter(text);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        User user = PrRealm.getInstance(getApplicationContext()).getCurrentUser();
        if (user != null)
            FirebaseDatabase.getInstance().getReference("users").child(user.getuId()).child("online").setValue(false);
        findView();

    }


}
