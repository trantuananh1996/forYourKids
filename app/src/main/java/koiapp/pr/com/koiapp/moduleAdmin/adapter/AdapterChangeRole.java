package koiapp.pr.com.koiapp.moduleAdmin.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import koiapp.pr.com.koiapp.R;
import koiapp.pr.com.koiapp.api.FirebaseAPI;
import koiapp.pr.com.koiapp.model.ItemNotification;
import koiapp.pr.com.koiapp.model.PostSendNotification;
import koiapp.pr.com.koiapp.model.UserNotRealm;
import koiapp.pr.com.koiapp.moduleAdmin.model.PendingChangeRole;
import koiapp.pr.com.koiapp.utils.AppUtils;
import koiapp.pr.com.koiapp.utils.DateTimeUtils;
import koiapp.pr.com.koiapp.utils.ImageUtils;


public class AdapterChangeRole extends RecyclerView.Adapter<AdapterChangeRole.RegisterManagerViewHolder> {
    Activity activity;
    List<PendingChangeRole> list;

    public AdapterChangeRole(Activity activity, List<PendingChangeRole> list) {

        this.activity = activity;
        this.list = list;
    }

    @Override
    public RegisterManagerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_pending_item, parent, false);
        return new RegisterManagerViewHolder(view);
    }

    private void sendNotification(final boolean b, final String s) {

        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("users").child(s);
        dRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserNotRealm u = dataSnapshot.getValue(UserNotRealm.class);
                if (u != null && u.getDevice_ids() != null && u.getDevice_ids().size() > 0) {
                    ItemNotification itemNotification = new ItemNotification();
                    itemNotification.setUserId(s);
                    itemNotification.setTitle("Yêu cầu thay đổi quyền người dùng");
                    if (b)
                        itemNotification.setSubtitle("Admin đã phê duyệt đăng kí của bạn");
                    else itemNotification.setSubtitle("Admin không đồng ý đăng kí của bạn");

                    itemNotification.setTime(System.currentTimeMillis() / 1000);
                    itemNotification.setType(ItemNotification.TYPE_REQUEST_CHANGE_ROLE_ACCEPTED);
                    PostSendNotification post = new PostSendNotification();
                    post.setData(itemNotification);
                    post.setRegistrationIds(u.getDevice_ids());
                    FirebaseAPI.sendNotification(post, activity.getString(R.string.firebase_key_authorization), activity.getString(R.string.firebase_content_type));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    @Override
    public void onBindViewHolder(RegisterManagerViewHolder holder, final int position) {
        final PendingChangeRole item = list.get(position);

        String oldRole = "";
        String newRole = "";
        if (item.getOld_role().contains("parent")) oldRole += " Phụ huynh";
        if (item.getOld_role().contains("schoolManager"))
            if (TextUtils.isEmpty(oldRole)) oldRole += " Quản trị trường";
            else oldRole += ", Quản trị trường";

        if (item.getNew_role().contains("parent")) newRole += " Phụ huynh";
        if (item.getNew_role().contains("schoolManager"))
            if (TextUtils.isEmpty(oldRole)) newRole += " Quản trị trường";
            else newRole += ", Quản trị trường";

        holder.name.setText(item.getName());
        holder.subtitle.setText("Muốn đổi quyền người dùng từ " + oldRole.trim() + " thành " + newRole.trim());
        ImageUtils.getInstance(activity).loadImage(item.getUser_avatar(), holder.ivAvatar);
        if (item.getRequest_time() != null)
            holder.time.setText(DateTimeUtils.getFormatedDateTime(DateTimeUtils.dd_MM_yyyy_HH_mm, item.getRequest_time()));

        if (item.getApproved() == null || item.getApproved().equals(false)) {
            holder.approve.setVisibility(View.VISIBLE);
            holder.cancel.setVisibility(View.VISIBLE);
            holder.approve.setText("Duyệt");
            holder.approve.setOnClickListener(view -> {
                if (AppUtils.alertGetYesNo("", "Duyệt yêu cầu?", activity)) {
                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users").child(item.getUser_request());
                    dbRef.child("role").setValue(item.getNew_role());
                    FirebaseDatabase.getInstance().getReference("admin/pending/role_switch").child(item.getUser_request()).child("approved").setValue(true);
                    item.setApproved(true);
                    FirebaseDatabase.getInstance().getReference("admin/pending/role_switch").child(item.getUser_request()).child("approved_at").setValue(System.currentTimeMillis() / 1000);

                    if (item.getNew_role().contains("schoolManager")) {
                        FirebaseDatabase.getInstance().getReference("users").child(item.getUser_request()).child("manager_at").removeValue();
                        FirebaseDatabase.getInstance().getReference("users").child(item.getUser_request()).child("school_name").removeValue();

                    }
                    notifyItemChanged(position);
                    item.setApproved(true);
                    notifyItemChanged(position);
                    sendNotification(true, item.getUser_request());

                }
            });

            holder.cancel.setOnClickListener(view -> {
                if (AppUtils.alertGetYesNo("", "Xóa yêu cầu", activity)) {
                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("admin/pending/role_switch");
                    dbRef.child(item.getUser_request()).removeValue();
                    list.remove(item);
                    notifyItemRemoved(position);
                    sendNotification(false, item.getUser_request());
                }
            });
        } else {
            holder.cancel.setVisibility(View.GONE);
            holder.approve.setOnClickListener(null);
            holder.approve.setText("Đã duyệt");
        }

    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class RegisterManagerViewHolder extends RecyclerView.ViewHolder {

        ImageView ivAvatar;
        TextView name;
        TextView time;
        TextView subtitle;
        TextView approve;
        View cancel;

        public RegisterManagerViewHolder(View itemView) {
            super(itemView);
            ivAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
            name = (TextView) itemView.findViewById(R.id.tv_name);
            time = (TextView) itemView.findViewById(R.id.tv_time);
            subtitle = (TextView) itemView.findViewById(R.id.tv_subtitle);
            approve = (TextView) itemView.findViewById(R.id.approve);
            cancel = itemView.findViewById(R.id.cancel);
        }
    }
}
