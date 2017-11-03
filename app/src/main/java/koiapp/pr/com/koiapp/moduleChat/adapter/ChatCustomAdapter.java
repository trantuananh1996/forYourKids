package koiapp.pr.com.koiapp.moduleChat.adapter;

import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import koiapp.pr.com.koiapp.R;
import koiapp.pr.com.koiapp.moduleChat.model.ChatMessage;
import koiapp.pr.com.koiapp.moduleChat.activity.FragmentChat;
import koiapp.pr.com.koiapp.utils.DateTimeUtils;
import koiapp.pr.com.koiapp.utils.ImageUtils;

/**
 * Created by Tran Anh
 * on 1/19/2017.
 */

public class ChatCustomAdapter extends RecyclerView.Adapter<ChatCustomAdapter.ChatViewHolder> {
    private List<ChatMessage> mModels;
    private static final int TYPE_FIRST = 0;
    private static final int TYPE_MIDDLE = 1;
    private static final int TYPE_LAST = 2;
    private static final int TYPE_SINGLE = 3;
    private FragmentChat fragmentChat;
    private FragmentActivity activity;
    private String myId;
    private long lToday = 0;
    private static final int timeDiff = 10 * 60 * 1000;

    public ChatCustomAdapter(FragmentChat fragmentChat, List<ChatMessage> mModels) {
        this.fragmentChat = fragmentChat;
        this.myId = fragmentChat.getMyId();
        this.mModels = mModels;
        activity = fragmentChat.getParentActivity();
        lToday = DateTimeUtils.getInstance(activity.getApplicationContext()).getEpochFromddMMyyyy(DateTimeUtils.getInstance(activity.getApplicationContext()).getCurrentddMMyyyy());
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }


    @SuppressWarnings("deprecation")
    private void switchSendReiceive(ChatViewHolder holder, boolean isSend, int type) {
        if (isSend) {
            holder.avatarLeft.setVisibility(View.GONE);
            holder.avatarRight.setVisibility(View.GONE);
            holder.vContentLeft.setVisibility(View.GONE);
            holder.vContentRight.setVisibility(View.VISIBLE);
        } else {
            holder.avatarLeft.setVisibility(View.INVISIBLE);
            holder.avatarRight.setVisibility(View.GONE);
            holder.vContentRight.setVisibility(View.GONE);
            holder.vContentLeft.setVisibility(View.VISIBLE);
        }
        holder.tvTime.setVisibility(View.GONE);
        switch (type) {
            case TYPE_FIRST:
                holder.tvTime.setVisibility(View.VISIBLE);
                holder.vContentRight.setBackgroundDrawable(ContextCompat.getDrawable(activity, R.drawable.bg_chat_top));
                holder.vContentLeft.setBackgroundDrawable(ContextCompat.getDrawable(activity, R.drawable.bg_chat_top_r));
                break;
            case TYPE_MIDDLE:
                holder.vContentRight.setBackgroundDrawable(ContextCompat.getDrawable(activity, R.drawable.bg_chat_middle));
                holder.vContentLeft.setBackgroundDrawable(ContextCompat.getDrawable(activity, R.drawable.bg_chat_middle_r));
                break;
            case TYPE_LAST:
                if (isSend) {
                    //  holder.avatarRight.setVisibility(View.VISIBLE);
                } else {
                    holder.avatarLeft.setVisibility(View.VISIBLE);
                }
                holder.vContentRight.setBackgroundDrawable(ContextCompat.getDrawable(activity, R.drawable.bg_chat_bottom));
                holder.vContentLeft.setBackgroundDrawable(ContextCompat.getDrawable(activity, R.drawable.bg_chat_bottom_r));
                break;
            case TYPE_SINGLE:
                if (isSend) {
                    holder.avatarRight.setVisibility(View.GONE);
                } else {
                    holder.avatarLeft.setVisibility(View.VISIBLE);
                }
                holder.vContentRight.setBackgroundDrawable(ContextCompat.getDrawable(activity, R.drawable.bg_chat_single));
                holder.vContentLeft.setBackgroundDrawable(ContextCompat.getDrawable(activity, R.drawable.bg_chat_single_r));
                break;
        }
    }

    @Override
    public void onBindViewHolder(final ChatViewHolder holder, final int position) {
        ImageUtils.getInstance(activity.getApplicationContext()).loadImage(fragmentChat.otherAvatar, holder.avatarLeft);
        if (TextUtils.isEmpty(mModels.get(position).getContent())) {
            holder.contentLeft.setVisibility(View.GONE);
            holder.contentRight.setVisibility(View.GONE);
        } else {
            holder.contentRight.setVisibility(View.VISIBLE);
            holder.contentLeft.setVisibility(View.VISIBLE);
        }
        if (mModels.get(position).getTime() != null)
            if (mModels.get(position).getTime() >= lToday)
                holder.tvTime.setText(DateTimeUtils.getFormatedDateTime(DateTimeUtils.HH_mm, mModels.get(position).getTime()));
            else
                holder.tvTime.setText(DateTimeUtils.getFormatedDateTime(DateTimeUtils.dd_MM_yyyy_HH_mm, mModels.get(position).getTime()));

        holder.contentLeft.setText(mModels.get(position).getContent());
        holder.contentRight.setText(mModels.get(position).getContent());
        switchSendReiceive(holder, mModels.get(position).getSenderId().equals(myId), getItemViewType(position));
        final String photo = mModels.get(position).getPhoto();
        holder.photoLeft.setVisibility(View.GONE);
        holder.photoRight.setVisibility(View.GONE);
        final ImageView imageView = mModels.get(position).getSenderId().equals(myId) ? holder.photoRight : holder.photoLeft;
        if (!TextUtils.isEmpty(photo) && !photo.equals("NOTSET") && !photo.equals("temp_image")) {
            imageView.setVisibility(View.VISIBLE);
            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(photo);
            Glide.with(activity).using(new FirebaseImageLoader())
                    .load(storageRef)
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .signature(new StringSignature(photo))
                    .fallback(ContextCompat.getDrawable(activity, R.drawable.no_image))
                    .error(ContextCompat.getDrawable(activity, R.drawable.no_image))
                    .into(imageView);
        } else if (photo.equals("temp_image")) {
            imageView.setVisibility(View.VISIBLE);
            if (mModels.get(position).getImageEntry() != null) {
             /*   Glide.with(activity)
                        .load(mModels.get(position).getImageEntry().path)
                        .asBitmap()
                        .into(imageView);*/
                Glide.with(activity)
                        .load(R.drawable.loading_gif)
                        .asGif()  // you may not need this
                        .crossFade()
                        .into(imageView);
            }
        } else {
            holder.photoLeft.setVisibility(View.GONE);
            holder.photoRight.setVisibility(View.GONE);
        }


        View.OnClickListener clickListener = v -> {
            if (!TextUtils.isEmpty(photo) && !photo.equals("NOTSET") && !photo.equals("temp_image")) {
                showPopupImage(position);
            } else {
                if (holder.tvTime.getTag() != null) {
                    if (ChatCustomAdapter.this.getItemViewType(position) != TYPE_FIRST) {
                        boolean b = (boolean) holder.tvTime.getTag();
                        if (b) holder.tvTime.setVisibility(View.VISIBLE);
                        else holder.tvTime.setVisibility(View.GONE);
                        holder.tvTime.setTag(!b);
                    }
                }
            }
        };
        holder.vContentLeft.setOnClickListener(clickListener);
        holder.vContentRight.setOnClickListener(clickListener);
    }

    private void showPopupImage(final int position) {
        View view = View.inflate(activity, R.layout.dialog_view_edit_image, null);

        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        final PopupWindow popWindow = new PopupWindow(view, displayMetrics.widthPixels, displayMetrics.heightPixels, true);
        popWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popWindow.setAnimationStyle(R.style.PopupAnimation);

        final ImageView image = (ImageView) view.findViewById(R.id.iv_selected);
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(mModels.get(position).getPhoto());
        Glide.with(activity).using(new FirebaseImageLoader())
                .load(storageRef)
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .signature(new StringSignature(mModels.get(position).getPhoto()))
                .fallback(ContextCompat.getDrawable(activity, R.drawable.no_image))
                .error(ContextCompat.getDrawable(activity, R.drawable.no_image))
                .into(image);
        final View menu = view.findViewById(R.id.ll_menu);
        menu.setTag(false);
        View rotateLeft = view.findViewById(R.id.ll_rotate_left);
        View rotateRight = view.findViewById(R.id.ll_rotate_right);
        View delete = view.findViewById(R.id.ll_remove);
        View save = view.findViewById(R.id.ll_save);
        save.setVisibility(View.VISIBLE);
        rotateLeft.setVisibility(View.GONE);
        rotateRight.setVisibility(View.GONE);
        delete.setVisibility(View.GONE);
        save.setOnClickListener(v -> {
//                ActivityMain.getInstance().saveImage(mModels.get(position).getPhoto(), true);
            menu.setTag(false);
            menu.setVisibility(View.GONE);
        });
        View.OnClickListener clickCloseMenu = v -> {
            menu.setTag(false);
            menu.setVisibility(View.GONE);
        };
        image.setOnClickListener(clickCloseMenu);
        view.findViewById(R.id.background).setOnClickListener(clickCloseMenu);
        View.OnClickListener clickSwitchMenu = v -> {
            boolean b = menu.getTag() != null && (boolean) menu.getTag();
            if (b) {
                menu.setVisibility(View.GONE);
            } else {
                menu.setVisibility(View.VISIBLE);
            }
            menu.setTag(!b);
        };
        View ivFunction = view.findViewById(R.id.iv_function);
        ivFunction.setOnClickListener(clickSwitchMenu);
        view.findViewById(R.id.rootView).setOnClickListener(v -> popWindow.dismiss());
        popWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            if (position + 1 < getItemCount()) {
                ChatMessage nextMessage = mModels.get(position + 1);
                if (mModels.get(position + 1).getSenderId().equals(mModels.get(position).getSenderId())) {
                    if (Math.abs(mModels.get(position).getTime() - nextMessage.getTime()) >= timeDiff) {
                        return TYPE_SINGLE;
                    } else return TYPE_FIRST;
                }
                //return TYPE_FIRST;
                else return TYPE_SINGLE;
            } else return TYPE_SINGLE;
        } else if (position == getItemCount() - 1) {
            if (position - 1 >= 0) {
                ChatMessage preMessage = mModels.get(position - 1);
                if (mModels.get(position - 1).getSenderId().equals(mModels.get(position).getSenderId()))
                    if (Math.abs(mModels.get(position).getTime() - preMessage.getTime()) >= timeDiff)
                        return TYPE_SINGLE;
                    else
                        return TYPE_LAST;
                else return TYPE_SINGLE;
            } else return TYPE_SINGLE;
        }
        ChatMessage currentMessage = mModels.get(position);
        ChatMessage nextMessage = mModels.get(position + 1);
        ChatMessage preMessage = mModels.get(position - 1);
        if (currentMessage.getSenderId().equals(preMessage.getSenderId())) {
            if (currentMessage.getSenderId().equals(nextMessage.getSenderId())) {
                if (Math.abs(currentMessage.getTime() - nextMessage.getTime()) >= timeDiff)
                    if (Math.abs(currentMessage.getTime() - preMessage.getTime()) >= timeDiff)
                        return TYPE_SINGLE;
                    else return TYPE_LAST;
                else if (Math.abs(currentMessage.getTime() - preMessage.getTime()) >= timeDiff)
                    return TYPE_FIRST;
                else return TYPE_MIDDLE;
            } else return TYPE_LAST;
        } else {
            if (currentMessage.getSenderId().equals(nextMessage.getSenderId())) {
                if (Math.abs(currentMessage.getTime() - nextMessage.getTime()) >= timeDiff)
                    return TYPE_FIRST;
                else
                    return TYPE_FIRST;
            } else return TYPE_SINGLE;
        }
    }

    @Override
    public int getItemCount() {
        return mModels == null ? 0 : mModels.size();
    }


    class ChatViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_content_left)
        TextView contentLeft;
        @BindView(R.id.tv_content_right)
        TextView contentRight;
        @BindView(R.id.iv_avatar_left)
        ImageView avatarLeft;
        @BindView(R.id.iv_avatar_right)
        ImageView avatarRight;
        @BindView(R.id.photo_left)
        ImageView photoLeft;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.photo_right)
        ImageView photoRight;
        @BindView(R.id.center)
        View vCenter;
        @BindView(R.id.content_left)
        View vContentLeft;
        @BindView(R.id.content_right)
        View vContentRight;

        ChatViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            avatarLeft.setVisibility(View.GONE);
            avatarRight.setVisibility(View.GONE);
            tvTime.setTag(tvTime.getVisibility() == View.VISIBLE);
        }
    }
}