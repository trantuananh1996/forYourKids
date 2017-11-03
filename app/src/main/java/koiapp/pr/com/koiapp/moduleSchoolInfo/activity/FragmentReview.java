package koiapp.pr.com.koiapp.moduleSchoolInfo.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import koiapp.pr.com.koiapp.R;
import koiapp.pr.com.koiapp.moduleSchoolInfo.adapter.AdapterReview;
import koiapp.pr.com.koiapp.utils.view.PrFragment;
import koiapp.pr.com.koiapp.model.User;
import koiapp.pr.com.koiapp.moduleSearch.model.placeDetail.ResultDetail;
import koiapp.pr.com.koiapp.moduleSearch.model.placeDetail.Review;
import koiapp.pr.com.koiapp.utils.realm.PrRealm;
import koiapp.pr.com.koiapp.utils.AppUtils;
import koiapp.pr.com.koiapp.utils.FragmentUtils;
import koiapp.pr.com.koiapp.utils.debug.Debug;

/**
 * Created by Tran Anh
 * on 6/12/2017.
 */

public class FragmentReview extends PrFragment {

    private ResultDetail detail;
    User user;
    AdapterReview adapterReview;
    RecyclerView rvReview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_review, container, false);
        rvReview = (RecyclerView) rootView.findViewById(R.id.rv_review);
        adapterReview = new AdapterReview(getActivity(), detail.getReviews());
        rvReview.setAdapter(adapterReview);
        rvReview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        user = PrRealm.getInstance(getActivity().getApplicationContext()).getCurrentUser();

        rootView.findViewById(R.id.add_review).setOnClickListener(view -> showDialogAddReview());
        return rootView;
    }

    private void showDialogAddReview() {
        if (user == null) {
            FragmentUtils.getInstance(getActivity()).showToast("Cần đăng nhập để gửi đánh giá");
            return;
        }
        View view = View.inflate(getActivity(), R.layout.dialog_add_review, null);
        final Dialog dialog = AppUtils.createDialogFromActivity(getActivity());
        dialog.setContentView(view);

        TextView tvSchoolName = (TextView) view.findViewById(R.id.tv_school_name);
        TextView tvName = (TextView) view.findViewById(R.id.tv_name);
        final EditText edtReview = (EditText) view.findViewById(R.id.edt_review);
        final RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rating);

        final TextView tvRate = (TextView) view.findViewById(R.id.tv_rate);
        tvRate.setText("0");

        tvSchoolName.setText(detail.getName());
        ratingBar.setRating(0);

        ratingBar.setOnRatingBarChangeListener((ratingBar1, v, b) -> tvRate.setText(String.valueOf(v)));
        tvName.setText(user.getName());

        view.findViewById(R.id.btn_review).setOnClickListener(view1 -> {
            if (TextUtils.isEmpty(edtReview.getText().toString())) {
                FragmentUtils.getInstance(getActivity()).showToast("Bạn chưa nhập đánh giá");
                return;
            }
            Review review = new Review();
            review.setAuthorId(user.getuId());
            review.setAuthorName(user.getName());
            review.setRating(ratingBar.getRating());
            review.setTime(System.currentTimeMillis() / 1000);
            review.setText(edtReview.getText().toString());
            review.setProfilePhotoUrl(user.getPhotoUrl());

            detail.getReviews().add(0, review);
            adapterReview.notifyItemInserted(0);
            sendReview(review);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void sendReview(final Review review) {
        final DatabaseReference dbRef = FirebaseDatabase.getInstance()
                .getReference("places").child(detail.getPlaceId());
        Query lastQuery = dbRef.child("reviews").orderByKey().limitToLast(1);
        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        String key = d.getRef().getKey();
                        if (!TextUtils.isEmpty(key)) {
                            try {
                                int iKey = Integer.parseInt(key);
                                Debug.prLog("Key", key);
                                review.setRefUrl("places/" + detail.getPlaceId() + "/reviews/" + (iKey + 1));
                                dbRef.child("reviews").child(String.valueOf(iKey + 1)).setValue(review);
                                return;
                            } catch (NumberFormatException e) {
                            }
                        }
                    }
                }
                review.setRefUrl("places/" + detail.getPlaceId() + "/reviews/" + (0));
                dbRef.child("reviews").child("0").setValue(review);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void setDetail(ResultDetail detail) {
        this.detail = detail;
    }
}
