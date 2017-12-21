package koiapp.pr.com.koiapp.moduleSchoolInfo.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import koiapp.pr.com.koiapp.R;
import koiapp.pr.com.koiapp.model.User;
import koiapp.pr.com.koiapp.moduleSearch.model.placeDetail.Review;
import koiapp.pr.com.koiapp.utils.AppUtils;
import koiapp.pr.com.koiapp.utils.FragmentUtils;
import koiapp.pr.com.koiapp.utils.debug.Debug;
import koiapp.pr.com.koiapp.utils.realm.PrRealm;

/**
 * Created by Tran Anh
 * on 6/12/2017.
 */

public class AdapterReview extends RecyclerView.Adapter<AdapterReview.ReviewViewHolder> {
    Activity activity;
    List<Review> reviews;
    String uId = "none";

    public AdapterReview(Activity activity, List<Review> reviews) {
        this.activity = activity;
        this.reviews = reviews;
        User user = PrRealm.getInstance(activity.getApplicationContext()).getCurrentUser();
        if (user != null) uId = user.getuId();
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewRV = LayoutInflater.from(activity).inflate(R.layout.item_list_review, parent, false);
        return new ReviewViewHolder(viewRV);

    }

    @Override
    public void onBindViewHolder(final ReviewViewHolder holder, final int position) {
        final Review review = reviews.get(position);

        if (review.getAuthorId() != null && review.getAuthorId().equals(uId)) {
            holder.edit.setVisibility(View.VISIBLE);
            holder.delete.setVisibility(View.VISIBLE);

            holder.delete.setOnClickListener(view -> {
                if (AppUtils.alertGetYesNo("", "Bạn có muốn xóa đánh giá này?", activity)) {
                    if (!TextUtils.isEmpty(review.getRefUrl())) {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(review.getRefUrl());
                        databaseReference.removeValue();
                        reviews.remove(review);
                        notifyItemRemoved(position);
                    }
                }
            });
            holder.edit.setOnClickListener(view -> showDialogEditReview(review));
        } else {
            holder.edit.setVisibility(View.GONE);
            holder.delete.setVisibility(View.GONE);
        }
        Debug.prLog("User", review.getProfilePhotoUrl());
        Glide.with(activity).load(review.getProfilePhotoUrl()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.ivAvatar);
        holder.name.setText(review.getAuthorName());
        holder.ratingBar.setRating(review.getRating());

        String rTime = DateUtils.getRelativeTimeSpanString(review.getTime() * 1000, System.currentTimeMillis(), 0L, DateUtils.FORMAT_ABBREV_TIME).toString();
        holder.time.setText(rTime);

        holder.text.setText(review.getText());

        holder.subtitle.setText("");
        String authorId = review.getAuthorId();
        if (TextUtils.isEmpty(authorId)) {
            holder.subtitle.setText("Người dùng Google");
        } else {
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users").child(authorId);
            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String role = String.valueOf(dataSnapshot.child("role").getValue());
                    if (TextUtils.isEmpty(role) || role.equals("null")) {
                        holder.subtitle.setText("Người dùng Google");
                    } else {
                        if (role.contains("parent")) {
                            holder.subtitle.setText("Phụ huynh");
                        } else if (role.contains("schoolManager")) {
                            holder.subtitle.setText("Quản trị trường");
                        } else holder.subtitle.setText("Admin");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    holder.subtitle.setText("Người dùng Google");
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return reviews == null ? 0 : reviews.size();
    }

    private void showDialogEditReview(final Review review) {
        View view = View.inflate(activity, R.layout.dialog_add_review, null);
        final Dialog dialog = AppUtils.createDialogFromActivity(activity);
        dialog.setContentView(view);

        TextView tvSchoolName = (TextView) view.findViewById(R.id.tv_school_name);
        TextView tvName = (TextView) view.findViewById(R.id.tv_name);
        final EditText edtReview = (EditText) view.findViewById(R.id.edt_review);
        final RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rating);

        final TextView tvRate = (TextView) view.findViewById(R.id.tv_rate);
        tvRate.setText("0");

        Button btnDone = (Button) view.findViewById(R.id.btn_review);
        btnDone.setText("Xong");
        tvSchoolName.setText("");
        ratingBar.setRating(review.getRating());
        edtReview.setText(review.getText());
        ratingBar.setOnRatingBarChangeListener((ratingBar1, v, b) -> tvRate.setText(String.valueOf(v)));
        tvName.setText(review.getAuthorName());

        btnDone.setOnClickListener(view1 -> {
            if (TextUtils.isEmpty(edtReview.getText().toString())) {
                FragmentUtils.getInstance(activity).showToast("Bạn chưa nhập đánh giá");
                return;
            }
            review.setRating(ratingBar.getRating());
            review.setText(edtReview.getText().toString());

            if (!TextUtils.isEmpty(review.getRefUrl())) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(review.getRefUrl());
                databaseReference.removeValue();
                databaseReference.setValue(review);
                databaseReference.child("modified_time").setValue(System.currentTimeMillis() / 1000);
                notifyItemChanged(reviews.indexOf(review));
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView name;
        RatingBar ratingBar;
        TextView time;
        TextView text;
        TextView subtitle;
        View delete;
        View edit;


        public ReviewViewHolder(View itemView) {
            super(itemView);
            ivAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
            name = (TextView) itemView.findViewById(R.id.tv_name);
            ratingBar = (RatingBar) itemView.findViewById(R.id.rating);
            time = (TextView) itemView.findViewById(R.id.tv_time);
            text = (TextView) itemView.findViewById(R.id.tv_review);
            subtitle = (TextView) itemView.findViewById(R.id.tv_subtitle);
            delete = itemView.findViewById(R.id.delete);
            edit = itemView.findViewById(R.id.edit);
        }
    }
}
