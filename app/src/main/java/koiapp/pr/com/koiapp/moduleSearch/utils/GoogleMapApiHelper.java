package koiapp.pr.com.koiapp.moduleSearch.utils;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import koiapp.pr.com.koiapp.R;
import koiapp.pr.com.koiapp.moduleSearch.model.DataSearchMap;
import koiapp.pr.com.koiapp.moduleSearch.model.placeDetail.DataPlaceDetail;
import koiapp.pr.com.koiapp.moduleSearch.model.placeDetail.ResultDetail;
import koiapp.pr.com.koiapp.utils.debug.Debug;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static koiapp.pr.com.koiapp.utils.Constants.URL_BASE;

/**
 * Created by nguyetdtm
 * on 6/9/2017.
 */

public class GoogleMapApiHelper {
    public enum ResultStatus {OK, ZERO_RESULTS, OVER_QUERY_LIMIT, REQUEST_DENIED, INVALID_REQUEST}

    Activity appCompatActivity;

    static String searchBaseUrl = "https://maps.googleapis.com/maps/api/place/";
    private SearchCallBack mCallBack;

    public GoogleMapApiHelper(Activity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
    }

    public static SearchServices getServices() {
        return services;
    }

    static OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build();
    static Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .baseUrl(searchBaseUrl).build();
    private static SearchServices services = retrofit.create(SearchServices.class);


    public void getSearchTextResult(String query, SearchCallBack searchCallBack) {
        this.mCallBack = searchCallBack;
        time = 1;
        Call<DataSearchMap> call = services.textSearch(query, appCompatActivity.getString(R.string.google_maps_key));
        new AsCall().execute(call);
    }


    public void getSearchNearbyResult(String query, int radius, String location, SearchCallBack searchCallBack) {
        this.mCallBack = searchCallBack;
        time = 3;
        Call<DataSearchMap> currentCall;
        currentCall = services.nearbySearch(location, radius, query, appCompatActivity.getString(R.string.google_maps_key));
        new AsCall().execute(currentCall);

    }

    int time = 3;

    public void getSearchRadarResult(String query, int radius, String location, SearchCallBack searchCallBack) {
        this.mCallBack = searchCallBack;
        time = 1;
        Call<DataSearchMap> call = services.radarSearch(location, radius, query, appCompatActivity.getString(R.string.google_maps_key));
        new AsCall().execute(call);
    }

    DataSearchMap localData = null;

    class AsCall extends AsyncTask<Call<DataSearchMap>, Void, DataSearchMap> {


        @Override
        protected DataSearchMap doInBackground(Call<DataSearchMap>... params) {
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }
            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message mesg) {
                    throw new RuntimeException();
                }
            };
            Call<DataSearchMap> call = params[0];
            System.out.println(call.request().url());
            call.enqueue(new Callback<DataSearchMap>() {
                @Override
                public void onResponse(Call<DataSearchMap> call, Response<DataSearchMap> response) {
                    if (localData != null && localData.getResults() != null) {
                        localData.getResults().addAll(response.body().getResults());
                        localData.setNextPageToken(response.body().getNextPageToken());
                        Debug.prLog("Size", new Gson().toJson(response.body()));

                    } else
                        localData = response.body();
                    time--;
                    handler.sendMessage(handler.obtainMessage());
                }

                @Override
                public void onFailure(Call<DataSearchMap> call, Throwable t) {
                    handler.sendMessage(handler.obtainMessage());
                    t.printStackTrace();
                }
            });
            try {
                Looper.loop();
            } catch (RuntimeException e2) {
            }
            return localData;
        }

        @Override
        protected void onPostExecute(DataSearchMap dataSearchMap) {
            super.onPostExecute(dataSearchMap);
            Debug.prLog("Search", "Post exe " + time);
            if (time > 0 && localData != null) {
                new Handler().postDelayed(() -> {
                    Call<DataSearchMap> call = services.nearByNextPage(localData.getNextPageToken(), appCompatActivity.getString(R.string.google_maps_key));
                    new AsCall().execute(call);
                }, 2000);

            } else if (mCallBack != null) {
                if (localData != null)
                    if (localData.getStatus().equals("OK"))
                        if (localData.getResults().size() > 0)
                            mCallBack.onSearchCompleted(localData);
                        else
                            mCallBack.onSearchFailed(ResultStatus.ZERO_RESULTS.name(), "Không tìm thấy kết quả");
                    else
                        mCallBack.onSearchFailed(localData.getStatus(), localData.getErrorMessage());
            }
        }
    }


    public void loadImage(ImageView imageView, String photoRef) {
        String url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&" +
                "photoreference=" +
                photoRef +
                "&key=" +
                appCompatActivity.getString(R.string.google_maps_key);

        System.out.println(url);
        initGlideLoad(appCompatActivity, url)
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .signature(new StringSignature(url))
                .fallback(ContextCompat.getDrawable(appCompatActivity, R.drawable.no_image))
                .error(ContextCompat.getDrawable(appCompatActivity, R.drawable.no_image))
                .into(imageView);
    }


    public static DrawableTypeRequest<String> initGlideLoad(Context context, String url) {
        if (url.contains("http")) return Glide.with(context).load(url);
        return Glide.with(context).load(URL_BASE + url);
    }


    GetDetailCallBack mDetailCallback;



    public void getDetail(final String placeId, final GetDetailCallBack mDetailCallback) {
        this.mDetailCallback = mDetailCallback;

        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("places").child(placeId);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String exist = String.valueOf(dataSnapshot.child("exist").getValue());
                if (TextUtils.isEmpty(exist) || exist.equals("null")) {
                    Debug.prLog("Chi tiết", "Chưa có, get từ API rồi lưu firebase");
                    requestGetDetail(dbRef, placeId);
                } else {
                    Debug.prLog("Chi tiết", "Có rồi, lấy từ firebase xuống hoy");

                    ResultDetail detail = dataSnapshot.getValue(ResultDetail.class);
                    if (mDetailCallback != null) {
                        if (detail != null) mDetailCallback.onSearchCompleted(detail);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void requestGetDetail(final DatabaseReference dbRef, String placeId) {
        Call<DataPlaceDetail> call = GoogleMapApiHelper.getServices().getPlaceDetail(placeId, appCompatActivity.getString(R.string.google_maps_key));
        Debug.prLog("Detail url", call.request().url().toString());
        call.enqueue(new Callback<DataPlaceDetail>() {
            @Override
            public void onResponse(Call<DataPlaceDetail> call, Response<DataPlaceDetail> response) {
                Debug.prLog("Detail response", new Gson().toJson(response.body()));
                if (response.isSuccessful()) {
                    ResultDetail detail = response.body().getResultDetail();
                    if (detail != null) {
                        if (mDetailCallback != null) {
                            mDetailCallback.onSearchCompleted(detail);
                        }
                        dbRef.setValue(detail);
                        dbRef.child("exist").setValue(1);//Push lên firebase để check cái này đã tồn tại
                    }
                }
            }

            @Override
            public void onFailure(Call<DataPlaceDetail> call, Throwable t) {

            }
        });
    }
}

