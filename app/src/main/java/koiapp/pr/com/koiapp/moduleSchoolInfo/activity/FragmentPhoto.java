package koiapp.pr.com.koiapp.moduleSchoolInfo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.JsonObject;


import java.util.List;

import koiapp.pr.com.koiapp.R;
import koiapp.pr.com.koiapp.model.ItemNotification;
import koiapp.pr.com.koiapp.model.User;
import koiapp.pr.com.koiapp.moduleSchoolInfo.adapter.AdapterReview;
import koiapp.pr.com.koiapp.moduleSchoolInfo.adapter.ImageRecyclerAdapter;
import koiapp.pr.com.koiapp.moduleSearch.model.placeDetail.ResultDetail;
import koiapp.pr.com.koiapp.utils.AppUtils;
import koiapp.pr.com.koiapp.utils.DateTimeUtils;
import koiapp.pr.com.koiapp.utils.FragmentUtils;
import koiapp.pr.com.koiapp.utils.HTTPUtils;
import koiapp.pr.com.koiapp.utils.WrappableGridLayoutManager;
import koiapp.pr.com.koiapp.utils.view.PrFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class FragmentPhoto extends PrFragment {
    public static final String TAG = FragmentPhoto.class.getName();
    View v;
    public ImageRecyclerAdapter mAdapter;
    public RecyclerView grv_photo;
    ProgressBar progressBar;
    public ResultDetail detail;
    User user;
    AdapterReview adapterReview;

    public int clicked_pos = 0;

    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_home_album_detail, container, false);
        setRetainInstance(true);
        findView();


            if (detail.getPhotos().size() > 0) {
                v.setBackgroundColor(ContextCompat.getColor(getFragmentContext(), R.color.FragmentBackground));
                loadAlbumData();
            } else {
                //noinspection deprecation
                v.setBackgroundDrawable(ContextCompat.getDrawable(getFragmentContext(), R.color.alter_album_background));
            }

        return v;
    }

    @Override
    public void findView() {
        grv_photo = v.findViewById(R.id.rv_images);
        grv_photo.setNestedScrollingEnabled(false);
        progressBar = v.findViewById(R.id.progress);
    }

    public void loadAlbumData() {
            mAdapter = new ImageRecyclerAdapter(FragmentPhoto.this);
        grv_photo.setAdapter(mAdapter);
        final WrappableGridLayoutManager gridLayoutManager = new WrappableGridLayoutManager(getFragmentContext(), 3);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        grv_photo.setLayoutManager(gridLayoutManager);
    }




    public void update() {
        if (detail != null) {
            loadAlbumData();
        }
    }



    public void setDetail(ResultDetail detail) {
        this.detail = detail;
    }
}
