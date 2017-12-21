package koiapp.pr.com.koiapp.moduleSearch.utils;


import koiapp.pr.com.koiapp.moduleSearch.model.DataSearchMap;
import koiapp.pr.com.koiapp.moduleSearch.model.placeDetail.DataPlaceDetail;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by nguyetdtm
 * on 6/9/2017.
 */

public interface SearchServices {
//    @GET("textsearch/json")
    @GET("nearbysearch/json?location=21.021322500000004,105.79484765625003&type=school&radius=50000")
    Call<DataSearchMap> textSearch(@Query("keyword") String query
            , @Query("key") String apiKey);

    @GET("nearbysearch/json?type=school&name=kindergarten&rankBy=distance&sensor=true")
    Call<DataSearchMap> nearbySearch(@Query("location") String location
            , @Query("radius") int radius
            , @Query("keyword") String keyword
            , @Query("key") String key);

    @GET("nearbysearch/json")
    Call<DataSearchMap> nearByNextPage(@Query("pagetoken") String pageToken
            , @Query("key") String key);

    @GET("radarsearch/json?type=school&sensor=true")
    Call<DataSearchMap> radarSearch(@Query("location") String location
            , @Query("radius") int radius
            , @Query("keyword") String keyword
            , @Query("key") String key);

    @GET("details/json?language=vi")
    Call<DataPlaceDetail> getPlaceDetail(@Query("placeid") String placeId
            , @Query("key") String key);
}
