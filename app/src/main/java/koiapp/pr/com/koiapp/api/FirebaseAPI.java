package koiapp.pr.com.koiapp.api;

import com.google.gson.JsonObject;

import java.util.concurrent.TimeUnit;

import koiapp.pr.com.koiapp.model.PostSendNotification;
import koiapp.pr.com.koiapp.utils.HTTPUtils;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Tran Anh
 * on 9/27/2016.
 */
public class FirebaseAPI {
    static OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build();
    static Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .baseUrl("https://fcm.googleapis.com/").build();
    private static FirebaseServices services = retrofit.create(FirebaseServices.class);

    public static FirebaseServices getServices() {
        return services;
    }

    public static void sendNotification(PostSendNotification post, String auth, String contentType) {
        Call<JsonObject> call = FirebaseAPI.getServices().sendNotification(post, auth, contentType);
        HTTPUtils.postDetail(call, post);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void setServices(FirebaseServices services) {
        this.services = services;
    }
}
