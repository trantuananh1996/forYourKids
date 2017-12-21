package koiapp.pr.com.koiapp.moduleChat.adapter;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import koiapp.pr.com.koiapp.R;
import koiapp.pr.com.koiapp.moduleChat.ChatUtils;
import koiapp.pr.com.koiapp.moduleChat.activity.ActivityContact;
import koiapp.pr.com.koiapp.moduleChat.model.UserContact;
import koiapp.pr.com.koiapp.utils.AppUtils;

/**
 * Created by Tran Anh
 * on 11/11/2016.
 */
public class ContactRecyclerAdapter extends RecyclerView.Adapter<ContactRecyclerAdapter.ContactViewHolder> {
    private List<UserContact> mData;
    private ArrayList<UserContact> tmp;
    ActivityContact activityContact;

    public ContactRecyclerAdapter(ActivityContact fragment_contact) {
        this.activityContact = fragment_contact;
        this.mData = activityContact.getUsers();
        tmp = new ArrayList<>();
        tmp.addAll(mData);
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(activityContact).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        holder.message.setTag(position);
        final UserContact contactItem = mData.get(position);
        if (contactItem.isHasMessage()) {
//            holder.message.setColorFilter(Contextbgnhjkl.r hgj Compat.getColor(parentActivity, R.color.colorPrimary));
        } else {
            holder.message.clearColorFilter();
        }
        if (contactItem.isOnline()) holder.online.setVisibility(View.VISIBLE);
        else holder.online.setVisibility(View.GONE);
        holder.name.setText(contactItem.getName());
        String info = "";
        if (contactItem.getRole().contains("parent")) {
            info += "Phụ huynh";
        }
        if (contactItem.getRole().contains("schoolManager")) {
            if (!TextUtils.isEmpty(info))
                info += " - Quản trị trường";
            else info += "Quản trị trường";
            if (!TextUtils.isEmpty(contactItem.getSchool_name()))
                info += " (" + contactItem.getSchool_name() + ")";
        }
        if (contactItem.getRole().contains("admin")) {
            if (!TextUtils.isEmpty(info))
                info += " - Admin";
            else info += "Admin";
        }
        holder.info.setText(info);
        if (TextUtils.isEmpty(contactItem.getPhotoUrl()))
            holder.avatar.setImageResource(R.drawable.ic_no_avatar);
        else
            Glide.with(activityContact).load(contactItem.getPhotoUrl()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.avatar);

//        ImageUtils.getInstance(activityContact).loadImage(contactItem.getPhotoUrl(), holder.avatar);
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ContactViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView name;
        TextView info;
        ImageView message;
        TextView numberOfMessage;
        ImageView online;

        public void setTextColor(int color) {
            name.setTextColor(ContextCompat.getColor(activityContact, color));
            info.setTextColor(ContextCompat.getColor(activityContact, color));
        }

        ContactViewHolder(View itemView) {
            super(itemView);
            avatar = (ImageView) itemView.findViewById(R.id.iv_message_avatar);
            name = (TextView) itemView.findViewById(R.id.edt_name);
            info = (TextView) itemView.findViewById(R.id.tv_child_name);
            info.setVisibility(View.VISIBLE);
            numberOfMessage = (TextView) itemView.findViewById(R.id.number_of_message);
            message = (ImageView) itemView.findViewById(R.id.iv_message);
            online = (ImageView) itemView.findViewById(R.id.iv_is_online);
            message.setOnClickListener(v -> {
                if (message.getTag() == null) return;
                ChatUtils.gotoChat(mData.get((Integer) message.getTag()), activityContact);
                mData.get((Integer) message.getTag()).setHasMessage(false);
                notifyDataSetChanged();
                activityContact.countUnread();
            });
        }
    }

    public boolean isEmpty(@Nullable String str) {
        if (str != null) str = str.replace("\\t", "").replace(" ", "").replace("\\n", "");
        return str == null || str.length() == 0;
    }

    public void filter(String charText) {
        charText = AppUtils.unAccent(charText);
        mData.clear();
        if (charText.length() == 0) {
            mData.addAll(tmp);
        } else {
            for (UserContact ct : tmp) {
                if (AppUtils.unAccent(ct.getName()).contains(charText)) {
                    mData.add(ct);
                }
            }
        }
        notifyDataSetChanged();
        if (mData.size() == 0) {
            activityContact.tv_nomatch.setVisibility(View.VISIBLE);
            activityContact.tv_nomatch.setText(String.format("%s%s", activityContact.getString(R.string.str_no_match), charText));
        } else activityContact.tv_nomatch.setVisibility(View.GONE);
    }
}
