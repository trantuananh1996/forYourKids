package koiapp.pr.com.koiapp.moduleManager;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
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
import koiapp.pr.com.koiapp.moduleManager.model.PendingRegisterLearn;
import koiapp.pr.com.koiapp.utils.AppUtils;
import koiapp.pr.com.koiapp.utils.DateTimeUtils;
import koiapp.pr.com.koiapp.utils.ImageUtils;

/**
 * Created by nguyentt
 * on 6/13/2017.
 */

public class AdapterRegisterLearn extends RecyclerView.Adapter<AdapterRegisterLearn.RegisterManagerViewHolder> {
    Activity activity;
    List<PendingRegisterLearn> list;
    private String schoolId;

    public AdapterRegisterLearn(Activity activity, List<PendingRegisterLearn> list) {

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
                    itemNotification.setTitle("Đơn đăng kí học");
                    if (b)
                        itemNotification.setSubtitle("Quản trị trường đã phê duyệt đăng kí của bạn");
                    else
                        itemNotification.setSubtitle("Quản trị trường không đồng ý đăng kí của bạn");

                    itemNotification.setTime(System.currentTimeMillis() / 1000);
                    itemNotification.setType(ItemNotification.TYPE_REGISTER_LEARN_ACCEPTED);
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
        final PendingRegisterLearn item = list.get(position);

        holder.name.setText(item.getUser_name());
        holder.subtitle.setText("Đăng kí học tại trường " + item.getSchool_name());
        ImageUtils.getInstance(activity).loadImage(item.getUser_avatar(), holder.ivAvatar);
        if (item.getRequest_time() != null)
            holder.time.setText(DateTimeUtils.getFormatedDateTime(DateTimeUtils.dd_MM_yyyy_HH_mm, item.getRequest_time()));

        if (item.getApproved() == null || item.getApproved().equals(false)) {
            holder.approve.setVisibility(View.VISIBLE);
            holder.cancel.setVisibility(View.VISIBLE);
            holder.approve.setText("Duyệt");
            holder.approve.setOnClickListener(view -> {
                if (AppUtils.alertGetYesNo("", "Duyệt yêu cầu?", activity)) {
                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("schoolManager/pending/register_learn").child(schoolId);
                    dbRef.child(item.getuId_request()).child("approved_at").setValue(System.currentTimeMillis() / 1000);
                    dbRef.child(item.getuId_request()).child("approved").setValue(true);
                    item.setApproved(true);
                    notifyItemChanged(position);
                    sendNotification(true, item.getuId_request());
                }
            });

            holder.cancel.setOnClickListener(view -> {
                if (AppUtils.alertGetYesNo("", "Xóa yêu cầu", activity)) {
                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("schoolManager/pending/register_learn").child(schoolId);
                    dbRef.child(item.getuId_request()).removeValue();
                    list.remove(item);
                    notifyItemRemoved(position);
                    sendNotification(false, item.getuId_request());
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

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
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
