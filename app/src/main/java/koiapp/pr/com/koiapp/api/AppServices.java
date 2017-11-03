package koiapp.pr.com.koiapp.api;

import com.google.gson.JsonObject;

import koiapp.pr.com.koiapp.modulePost.model.PostCategoryId;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface AppServices {
    @POST("api/kindergarten")
    Call<JsonObject> getSchoolList();

    @POST("api/v3/corner-kidsonline")
    Call<JsonObject> getDataKidsonlineCorner(@Header("Authorization") String autho);

    @POST("api/v3/corner-kidsonline/sub-categories")
    Call<JsonObject> getSubCategories(@Body PostCategoryId post, @Header("Authorization") String autho);


}
