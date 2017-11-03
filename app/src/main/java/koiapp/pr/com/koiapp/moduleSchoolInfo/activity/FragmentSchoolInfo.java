package koiapp.pr.com.koiapp.moduleSchoolInfo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

import koiapp.pr.com.koiapp.R;
import koiapp.pr.com.koiapp.moduleSearch.activity.FragmentViewOnMaps;
import koiapp.pr.com.koiapp.moduleSearch.model.placeDetail.ResultDetail;
import koiapp.pr.com.koiapp.moduleSearch.utils.GoogleMapApiHelper;
import koiapp.pr.com.koiapp.utils.view.PrFragment;

/**
 * Created by Tran Anh
 * on 6/12/2017.
 */

public class FragmentSchoolInfo extends PrFragment {
    public static java.lang.String TAG = FragmentSchoolInfo.class.getName();
    TextView tvSchoolName;
    ImageView ivSchoolCover;
    FloatingActionButton viewOnMap;
    private ResultDetail detail;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_school_infomation, container, false);
        findView();

        bindData();


        addListener();

        return rootView;
    }

    public void addListener() {
        viewOnMap.setOnClickListener(v -> {
//                viewOnMap.hide(true);
            FragmentViewOnMaps fragmentViewOnMaps = new FragmentViewOnMaps();
            fragmentViewOnMaps.setDetail(detail);
            fragmentViewOnMaps.show(getFragmentManager(), R.id.bottom_sheet_school_info);
        });
    }

    private void addInfo(LinearLayout parent, String title, String content) {
        Log.e("Ahihi", title + content);
        if (TextUtils.isEmpty(content)) return;
        View view = View.inflate(getActivity(), R.layout.item_general_info, null);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        TextView tvContent = (TextView) view.findViewById(R.id.tv_content);
        tvTitle.setText(title);
        tvContent.setText(content);
        parent.addView(view);
    }

    private void addDetailInfo(ResultDetail detail, LinearLayout llCOntent) {
        if (detail == null) return;
        addInfo(llCOntent, "Liên hệ", detail.getFormattedPhoneNumber());
        addInfo(llCOntent, "Website", detail.getWebsite());
    }

    private void bindData() {
        if (detail == null) {
            return;
        }
        final LinearLayout llCOntent = (LinearLayout) rootView.findViewById(R.id.ll_general_info);
        TextView tvAbout = (TextView) rootView.findViewById(R.id.tv_about);
        tvAbout.setText("Giới thiệu về " + detail.getName());
        tvSchoolName.setText(detail.getName());
        if (detail.getPhotos() != null && detail.getPhotos().size() > 0)
            new GoogleMapApiHelper(getActivity()).loadImage(ivSchoolCover, detail.getPhotos().get(0).getPhotoReference());
        addInfo(llCOntent, "Tổng quan", detail.getName());
        addInfo(llCOntent, "Địa chỉ", detail.getFormattedAddress());
        addDetailInfo(detail, llCOntent);
    }


    public void findView() {
        tvSchoolName = (TextView) rootView.findViewById(R.id.tv_school_name);
        ivSchoolCover = (ImageView) rootView.findViewById(R.id.iv_cover);
        viewOnMap = (FloatingActionButton) rootView.findViewById(R.id.view_on_map);
    }




    public void setDetail(ResultDetail detail) {
        this.detail = detail;
    }
}
