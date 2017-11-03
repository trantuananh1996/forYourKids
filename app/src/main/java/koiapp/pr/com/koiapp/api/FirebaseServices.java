package koiapp.pr.com.koiapp.api;

import com.google.gson.JsonObject;

import koiapp.pr.com.koiapp.model.PostSendNotification;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by Tran Anh
 * on 6/15/2017.
 */

public interface FirebaseServices {
    @POST("fcm/send")
    Call<JsonObject> sendNotification(@Body PostSendNotification post, @Header("Authorization") String auth, @Header("Content-Type") String contentType);

}
