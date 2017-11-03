package koiapp.pr.com.koiapp.utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

import static koiapp.pr.com.koiapp.utils.Constants.TAG_JSON_DATA;
import static koiapp.pr.com.koiapp.utils.Constants.TAG_JSON_STATUS;


/**
 * Created by Tran Anh on 11/5/2016.
 */
public class HTTPUtils {
    public Context mContext;
    private static volatile HTTPUtils instance = null;

    public static HTTPUtils getInstance(Context context) {
        if (instance == null) {
            synchronized (HTTPUtils.class) {
                if (instance == null) {
                    instance = new HTTPUtils(context);
                }
            }
        }
        return instance;
    }

    private HTTPUtils(Context context) {
        mContext = context;
    }

    public boolean isSuccess(int code) {
        /**
         * Kiểm tra xem response code có thành công hay không
         * Toast các error code mặc định
         */
        switch (code) {
            case 1: {
                return true;
            }
            default:
                return false;
        }
    }

    public static boolean isSuccess(Response response) {
        /**
         * Kiểm tra xem request gửi lên có nhận response thành công không
         */
        //   mFragmentController.showToast(mContext.getResources().getString(R.string.toast_error) + "\nCode: " + response.code());
        return response.isSuccessful();
    }

    public static String getDataString(Response<JsonObject> response) {
        return response.body().get(TAG_JSON_DATA).toString();
    }

    public static int getStatus(Response<JsonObject> response) {
        if (response.body().get(TAG_JSON_STATUS) == null) return 1000;
        try {
            return response.body().get(TAG_JSON_STATUS).getAsInt();
        } catch (Exception e) {
            return 1000;
        }
    }

    public static <T> T getData(Class<T> clazz, Response<JsonObject> response) {
        Gson gson = new GsonBuilder()
                .create();
        return gson.fromJson(getDataString(response), clazz);
    }

    public static <T> List<T> getDataList(Class<T> clazz, Response<JsonObject> response) {
        Gson gson = new GsonBuilder()
                .create();
        List<T> list = new ArrayList<>();
        JsonArray ja = response.body().getAsJsonArray("data");
        for (JsonElement j : ja) {
            list.add(gson.fromJson(j, clazz));
        }
        return list;
    }

    public static void postDetail(Call<JsonObject> call, Object post) {
        Log.e("////////", "-");
        if (post != null) {
            Log.e("SENT PARAMS ", new Gson().toJson(post));
        }
        Log.e("SENT URL ", String.valueOf(call.request().url()));
        Log.e("TOKEN ", call.request().header("Authorization") == null ? "" : call.request().header("Authorization"));
        Log.e("////////", "-");
    }

}
