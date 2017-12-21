package koiapp.pr.com.koiapp.moduleSearch.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.io.IOException;

import koiapp.pr.com.koiapp.R;
import koiapp.pr.com.koiapp.moduleSchoolInfo.activity.ActivitySchoolInfo;
import koiapp.pr.com.koiapp.moduleSearch.model.Result;
import koiapp.pr.com.koiapp.moduleSearch.model.placeDetail.DataPlaceDetail;
import koiapp.pr.com.koiapp.moduleSearch.model.placeDetail.ResultDetail;
import koiapp.pr.com.koiapp.moduleSearch.utils.GetDetailCallBack;
import koiapp.pr.com.koiapp.moduleSearch.utils.GoogleMapApiHelper;
import koiapp.pr.com.koiapp.utils.debug.Debug;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static koiapp.pr.com.koiapp.R.id.map;

public class ActivityFindNearby extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    ProgressDialog progressDialog;
    private GoogleMap mMap;
    double latitude;
    double longitude;
    GoogleApiClient mGoogleApiClient;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    EditText edtRadius;
    Spinner spinnerUnits;
    View spinnerArrow;
    View btnMyLocation;
    DatabaseReference refPlaces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_nearby);
        refPlaces = FirebaseDatabase.getInstance().getReference("places");
        edtRadius = (EditText) findViewById(R.id.edtDistance);
        spinnerUnits = (Spinner) findViewById(R.id.spinner_units);
        spinnerArrow = findViewById(R.id.spinner_drop);
        btnMyLocation = findViewById(R.id.btnMyLocation);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        //Kiểm tra GooglePlay Services có hay chưa
        if (!CheckGooglePlayServices()) {
            Debug.prLog("onCreate", "Google Play Service không tồn tại");
            finish();
        } else {
            Debug.prLog("onCreate", "Google Play Service tồn tại");
        }

        //Khởi tạo map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
    }

    private boolean CheckGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        0).show();
            }
            return false;
        }
        return true;
    }

    private void getMyLocation() {
        buildGoogleApiClient();
//        mMap.setOnMyLocationChangeListener(location -> changeLocation(new LatLng(location.getLatitude(), location.getLongitude())));
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        btnMyLocation.setOnClickListener(v -> getMyLocation());
        edtRadius.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                search();
                return true;
            }
            return false;
        });
        spinnerArrow.setOnClickListener(view -> spinnerUnits.performClick());
        final Button btnSchool = (Button) findViewById(R.id.btnSearch);
        btnSchool.setOnClickListener(v -> search());
        mMap.setOnInfoWindowClickListener(marker -> {
            ResultDetail abc = (ResultDetail) marker.getTag();
            if (abc == null) return;
            Intent intent = new Intent(ActivityFindNearby.this, ActivitySchoolInfo.class);
            intent.putExtra("SchoolId", abc.getPlaceId());
            startActivity(intent);
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            Marker marker;

            @Override
            public boolean onMarkerClick(Marker mMarker) {
                marker = mMarker;
                final PlacesSearchResult abc = (PlacesSearchResult) marker.getTag();
                if (abc == null) {
                    mMap.moveCamera(CameraUpdateFactory.zoomIn());
                    return true;
                }
                new GoogleMapApiHelper(ActivityFindNearby.this).getDetail(abc.placeId, new GetDetailCallBack() {
                    @Override
                    public void onSearchCompleted(final ResultDetail detail) {
                        if (detail == null) return;
                        final Dialog d = new Dialog(ActivityFindNearby.this);
                        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        //d.setTitle("Select");
                        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                        d.setContentView(R.layout.custom_info_windows);

                        ImageView logo;
                        TextView name;
                        TextView address;
                        TextView description;
                        logo = (ImageView) d.findViewById(R.id.iv_school_logo);
                        name = (TextView) d.findViewById(R.id.tv_school_name);
                        address = (TextView) d.findViewById(R.id.tv_school_address);
                        description = (TextView) d.findViewById(R.id.tv_school_description);

                        if (detail.getPhotos() != null && detail.getPhotos().size() > 0)
                            new GoogleMapApiHelper(ActivityFindNearby.this).loadImage(logo, detail.getPhotos().get(0).getPhotoReference());
                        name.setText(detail.getName());
                        description.setText("Liên hệ: " + detail.getFormattedPhoneNumber());
                        address.setText("Địa chỉ: " + detail.getFormattedAddress());

                       /* d.getCurrentFocus().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(ActivityFindNearby.this, ActivitySchoolInfo.class);
                                intent.putExtra("SchoolId",detail.getPlaceId());
                                startActivity(intent);
                            }
                        });*/
//                        d.show();


                        final MarkerOptions markerOptions = new MarkerOptions();
                        String placeName = detail.getName();
                        if (placeName.toLowerCase().contains("mầm non")
                                || placeName.toLowerCase().contains("kindergarten")
                                || placeName.toLowerCase().contains("mẫu giáo")) {
                            final LatLng latLng = detail.getGeometry().getLocation().getLatLng();
                            marker = mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title(detail.getName())
                                    .snippet("Địa chỉ: " + detail.getFormattedAddress()));
                            marker.setTag(detail);
                            marker.showInfoWindow();

                        }
                    }

                    @Override
                    public void onSearchFailed(String status, String message) {

                    }
                });

                try {
                    Looper.loop();
                } catch (RuntimeException e2) {
                }
                return true;
            }
        });

        findViewById(R.id.btnPlacePicker).setOnClickListener(view -> {
            try {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                startActivityForResult(builder.build(ActivityFindNearby.this), 17);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 17) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                changeLocation(place.getLatLng());
            }
        }
    }

    private void search() {
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(getString(R.string.google_maps_key))
                .build();
        String School = "mầm non mẫu giáo kindergarten";

        progressDialog = ProgressDialog.show(ActivityFindNearby.this, "", "Đang tìm kiếm", true);
        UIUtil.hideKeyboard(ActivityFindNearby.this);
        String strRadius = edtRadius.getText().toString();
        String unit = spinnerUnits.getSelectedItem().toString();
        int radius = 1000;
        try {
            radius = Integer.parseInt(strRadius);
            if (unit.equals("km")) radius = radius * 1000;
        } catch (NumberFormatException n) {
            n.printStackTrace();
        }
        mMap.clear();
        changeLocation(new LatLng(latitude, longitude));
        try {
            PlacesSearchResponse a = PlacesApi.nearbySearchQuery(context, new com.google.maps.model.LatLng(latitude, longitude))
                    .keyword(School)
                    .radius(radius).await();
            handleNearbySearchResult(a);
        } catch (ApiException e) {
            e.printStackTrace();
            Snackbar.make(edtRadius, e.getMessage(), Snackbar.LENGTH_LONG).show();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Snackbar.make(edtRadius, e.getMessage(), Snackbar.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Snackbar.make(edtRadius, e.getMessage(), Snackbar.LENGTH_LONG).show();
        } finally {
            progressDialog.dismiss();

        }
    }

    private void handlePlaceDetail(String placeId) {
        Call<DataPlaceDetail> call = GoogleMapApiHelper.getServices().getPlaceDetail(placeId, getString(R.string.google_maps_key));
        Debug.prLog("Detail url", call.request().url().toString());
        call.enqueue(new Callback<DataPlaceDetail>() {
            @Override
            public void onResponse(Call<DataPlaceDetail> call, Response<DataPlaceDetail> response) {
                if (response.isSuccessful()) {
                    final DataPlaceDetail dataPlaceDetail = response.body();
                    ResultDetail resultDetail = dataPlaceDetail.getResultDetail();
                    if (resultDetail == null) return;
                    final MarkerOptions markerOptions = new MarkerOptions();
                    String placeName = resultDetail.getName();
                    if (placeName.toLowerCase().contains("mầm non")
                            || placeName.toLowerCase().contains("kindergarten")
                            || placeName.toLowerCase().contains("mẫu giáo")) {
                        String vicinity = resultDetail.getFormattedAddress();
                        final LatLng latLng = resultDetail.getGeometry().getLocation().getLatLng();
                        markerOptions.position(latLng);
                        markerOptions.title(placeName + " : " + vicinity);
                        Marker marker = mMap.addMarker(markerOptions);
                        marker.setTag(resultDetail);
//                        mMap.setInfoWindowAdapter(new CustomInfoWindow(ActivityFindNearby.this, resultDetail));
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                    }
                }
            }

            @Override
            public void onFailure(Call<DataPlaceDetail> call, Throwable t) {

            }
        });
    }


    private void handleNearbySearchResult(PlacesSearchResponse dataSearchMap) {
        if (dataSearchMap.results != null && dataSearchMap.results.length > 0) {
            for (final PlacesSearchResult ab : dataSearchMap.results) {
//                handlePlaceDetail(ab.getPlaceId());
                final MarkerOptions markerOptions = new MarkerOptions();

                final LatLng latLng = new LatLng(ab.geometry.location.lat, ab.geometry.location.lng);
                markerOptions.position(latLng);
                Marker marker = mMap.addMarker(markerOptions);
                marker.setTag(ab);
//                        mMap.setInfoWindowAdapter(new CustomInfoWindow(ActivityFindNearby.this, resultDetail));
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));


            }
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private void changeLocation(LatLng latLng) {
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        latitude = latLng.latitude;
        longitude = latLng.longitude;
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Vị trí của bạn");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)      // Sets the center of the map to location user
                .zoom(15)                   // Sets the zoom
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        Log.d("onLocationChanged", String.format("latitude:%.3f longitude:%.3f", latitude, longitude));

        //stop location updates
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        changeLocation(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }
}
