package koiapp.pr.com.koiapp.moduleSchoolInfo.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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

    private void addInfo(LinearLayout parent, @DrawableRes int id, String content) {
        if (TextUtils.isEmpty(content)) return;
        View view = View.inflate(getActivity(), R.layout.item_general_info, null);
        ImageView tvTitle = view.findViewById(R.id.tv_title);
        TextView tvContent = view.findViewById(R.id.tv_content);
        tvContent.setText(content);
        tvTitle.setImageResource(id);
        view.setOnClickListener(v -> {
            if (id == R.drawable.ic_phone) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + content));
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (id == R.drawable.ic_web) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(content));
                try {
                    startActivity(i);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }            } else if (id == R.drawable.ic_location) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + content);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                try {
                    startActivity(mapIntent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }            }

        });
        parent.addView(view);
    }

    private void addDetailInfo(ResultDetail detail, LinearLayout llCOntent) {
        if (detail == null) return;
        addInfo(llCOntent, R.drawable.ic_phone, detail.getFormattedPhoneNumber());
        addInfo(llCOntent, R.drawable.ic_web, detail.getWebsite());
    }

    private void bindData() {
        if (detail == null) {
            return;
        }
        final LinearLayout llCOntent = rootView.findViewById(R.id.ll_general_info);
        TextView tvAbout = rootView.findViewById(R.id.tv_about);
        tvAbout.setVisibility(View.GONE);
//        tvAbout.setText("Giới thiệu về " + detail.getName());
        tvSchoolName.setText(detail.getName());
        if (detail.getPhotos() != null && detail.getPhotos().size() > 0)
            new GoogleMapApiHelper(getActivity()).loadImage(ivSchoolCover, detail.getPhotos().get(0).getPhotoReference());
//        addInfo(llCOntent, R.drawable.ic_phone, detail.getName());
        addInfo(llCOntent, R.drawable.ic_location, detail.getFormattedAddress());
        addDetailInfo(detail, llCOntent);
    }


    public void findView() {
        tvSchoolName = rootView.findViewById(R.id.tv_school_name);
        ivSchoolCover = rootView.findViewById(R.id.iv_cover);
        viewOnMap = rootView.findViewById(R.id.view_on_map);
    }


    public void setDetail(ResultDetail detail) {
        this.detail = detail;
    }
}
