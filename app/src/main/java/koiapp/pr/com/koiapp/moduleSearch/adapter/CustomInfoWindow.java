package koiapp.pr.com.koiapp.moduleSearch.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import koiapp.pr.com.koiapp.R;
import koiapp.pr.com.koiapp.moduleSearch.model.placeDetail.ResultDetail;
import koiapp.pr.com.koiapp.moduleSearch.utils.GoogleMapApiHelper;

/**
 * Created by Tran Anh
 * on 6/11/2017.
 */

public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter {

    private final View myContentsView;
    private Activity activity;
    private ResultDetail detail;

    public CustomInfoWindow(Activity activity, ResultDetail resultDetail) {
        this.activity = activity;
        this.detail = resultDetail;
        myContentsView = View.inflate(activity, R.layout.custom_info_windows, null);
    }
    @Override
    public View getInfoContents(Marker marker) {
        ImageView logo;
        TextView name;
        TextView address;
        TextView description;
        logo = (ImageView) myContentsView.findViewById(R.id.iv_school_logo);
        name = (TextView) myContentsView.findViewById(R.id.tv_school_name);
        address = (TextView) myContentsView.findViewById(R.id.tv_school_address);
        description = (TextView) myContentsView.findViewById(R.id.tv_school_description);

        if (detail.getPhotos() != null && detail.getPhotos().size() > 0)
            new GoogleMapApiHelper(activity).loadImage(logo, detail.getPhotos().get(0).getPhotoReference());
        name.setText(detail.getName());
        description.setText("Liên hệ: " + detail.getFormattedPhoneNumber());
        address.setText("Địa chỉ: " + detail.getFormattedAddress());
        return myContentsView;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        // TODO Auto-generated method stub
        return null;
    }

}
