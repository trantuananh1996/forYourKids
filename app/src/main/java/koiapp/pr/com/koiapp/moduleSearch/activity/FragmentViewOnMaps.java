package koiapp.pr.com.koiapp.moduleSearch.activity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import koiapp.pr.com.koiapp.R;
import koiapp.pr.com.koiapp.moduleSearch.model.placeDetail.ResultDetail;
import koiapp.pr.com.koiapp.utils.view.PrFragment;

/**
 * Created by nguyetdtm
 * on 4/18/2017.
 */

public class FragmentViewOnMaps extends PrFragment implements OnMapReadyCallback {
    public static final String TAG = FragmentViewOnMaps.class.getName();
    private GoogleMap mMap;
    SupportMapFragment mapFragment;


    private ResultDetail detail;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_view_on_maps, container, false);
        findView();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
         mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        addListener();
        return rootView;
    }

    /**
     * @param strAddress Address/Location String
     * @method getLocationFromAddress
     * @desc Get searched location points from address and plot/update on map.
     */
    public LatLng getLocationFromAddress(String strAddress) {
        //Create coder with Activity context - this
        Geocoder coder = new Geocoder(getActivity());
        List<Address> address;
        try {
            //Get latLng from String
            address = coder.getFromLocationName(strAddress, 5);
            //check for null or empty address
            if (address.isEmpty()) {
                showDialogNotFoundAddress();
                return null;
            }
            //Lets take first possibility from the all possibilities.
            Address location = address.get(0);
            return new LatLng(location.getLatitude(), location.getLongitude());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showDialogNotFoundAddress() {

    }

    private void markOnMaps(LatLng latLng, GoogleMap mMap) {
        if (latLng == null || mMap == null) return;
        //Put marker on map on that LatLng
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(detail.getName());
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
        Marker srchMarker = mMap.addMarker(markerOptions);

        //move map camera
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)      // Sets the center of the map to location user
                .zoom(15)                   // Sets the zoom
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void findView() {

    }

    @Override
    public void addListener() {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (detail != null){
            koiapp.pr.com.koiapp.moduleSearch.model.placeDetail.Location location = detail.getGeometry().getLocation();
            LatLng latLng = new LatLng(location.getLat(),location.getLng());
            markOnMaps(latLng, mMap);}
    }

    public ResultDetail getDetail() {
        return detail;
    }
    @Override
    public void onResume() {
        mapFragment.onResume();
        super.onResume();
    }
    public void setDetail(ResultDetail detail) {
        this.detail = detail;
    }
}