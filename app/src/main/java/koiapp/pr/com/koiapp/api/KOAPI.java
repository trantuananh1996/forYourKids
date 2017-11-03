package koiapp.pr.com.koiapp.api;

import java.util.concurrent.TimeUnit;

import koiapp.pr.com.koiapp.utils.Constants;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Tran Anh
 * on 9/27/2016.
 */
public class KOAPI {
    static OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build();
    static Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .baseUrl(Constants.URL_BASE_KOMT).build();
    private static AppServices services = retrofit.create(AppServices.class);

    public static AppServices getKOServices() {
        return services;
    }

    public void setServices(AppServices services) {
        this.services = services;
    }
}
