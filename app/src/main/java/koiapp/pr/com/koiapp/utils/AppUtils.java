package koiapp.pr.com.koiapp.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.text.Normalizer;
import java.util.List;
import java.util.regex.Pattern;

import koiapp.pr.com.koiapp.R;

/**
 * Created by Tran Anh on 9/27/2016.
 * .
 */
public class AppUtils {
    public Context mContext;
    public static volatile AppUtils instance = null;

    public static AppUtils getInstance(Context context) {
        if (instance == null) {
            synchronized (AppUtils.class) {
                if (instance == null) {
                    instance = new AppUtils(context);
                }
            }
        }
        return instance;
    }

    private AppUtils(Context context) {
        mContext = context;
    }


    private static boolean mResult;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission1 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED || permission1 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    Constants.PERMISSIONS_STORAGE,
                    Constants.REQUEST_EXTERNAL_STORAGE
            );
            return false;
        } else return true;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean verifyCameraPermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, 1);
            return false;
        } else return true;
    }

    public String getStringFromRes(int id) {
        if (mContext == null) return "";
        return mContext.getResources().getString(id);
    }

    public boolean alertGetYesNo(int title, int message, Activity ac) {
        return alertGetYesNo(getStringFromRes(title)
                , getStringFromRes(message), ac);
    }



    public static boolean alertGetYesNo(String title, String message, String yesButton, String noButton, Activity activity) {
        if (Looper.myLooper() == null) {
            Looper.prepare();
        }
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message mesg) {
                throw new RuntimeException();
            }
        };
        View view = View.inflate(activity, R.layout.dialog_alert_yes_no, null);
        final Dialog dialog = createDialogFromActivity(activity);
        TextView content = (TextView) view.findViewById(R.id.tv_content);
        Button btnYes = (Button) view.findViewById(R.id.btn_yes);
        Button btnNo = (Button) view.findViewById(R.id.btn_no);
        if (!TextUtils.isEmpty(yesButton)) btnYes.setText(yesButton);
        if (!TextUtils.isEmpty(noButton)) btnNo.setText(noButton);

        if (!TextUtils.isEmpty(yesButton) && TextUtils.isEmpty(noButton))
            btnNo.setVisibility(View.GONE);
        content.setText(message);
        View.OnClickListener clicked = v -> {

            dialog.dismiss();
            switch (v.getId()) {
                case R.id.btn_yes:
                    mResult = true;
                    handler.sendMessage(handler.obtainMessage());
                    break;
                case R.id.btn_no:
                    mResult = false;
                    handler.sendMessage(handler.obtainMessage());
                    break;
            }
        };
        btnNo.setOnClickListener(clicked);
        btnYes.setOnClickListener(clicked);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(view);
        dialog.show();
        try {
            Looper.loop();
        } catch (RuntimeException e2) {
        }

        return mResult;
    }

    public static Dialog createDialogFromActivity(Activity activity) {
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }

    public static boolean alertGetYesNoLogin(List<String> versionInfo, String message, String yesButton, String noButton, Activity activity) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message mesg) {
                throw new RuntimeException();
            }
        };
        View view = View.inflate(activity, R.layout.dialog_alert_yes_no, null);
        final Dialog dialog = createDialogFromActivity(activity);
        TextView content = (TextView) view.findViewById(R.id.tv_content);
        Button btnYes = (Button) view.findViewById(R.id.btn_yes);
        Button btnNo = (Button) view.findViewById(R.id.btn_no);
        if (versionInfo != null && versionInfo.size() > 0) {
            String colorStarCode = "#00afef";
            String star = "<font color=\"" + colorStarCode + "\">" + "&bigstar;" + "</font> ";
            String strData = "<html>";
            for (String str : versionInfo) {
                strData = strData + star + str + "<br/>";
            }
            strData += "</html>";
            TextView tvInfo = (TextView) view.findViewById(R.id.tv_version_info);
            tvInfo.setText(Html.fromHtml(strData));
        }
        if (!TextUtils.isEmpty(yesButton)) btnYes.setText(yesButton);
        if (!TextUtils.isEmpty(noButton)) btnNo.setText(noButton);

        if (!TextUtils.isEmpty(yesButton) && TextUtils.isEmpty(noButton))
            btnNo.setVisibility(View.GONE);
        content.setText(message);
        View.OnClickListener clicked = v -> {

            dialog.dismiss();
            switch (v.getId()) {
                case R.id.btn_yes:
                    mResult = true;
                    handler.sendMessage(handler.obtainMessage());
                    break;
                case R.id.btn_no:
                    mResult = false;
                    handler.sendMessage(handler.obtainMessage());
                    break;
            }
        };
        btnNo.setOnClickListener(clicked);
        btnYes.setOnClickListener(clicked);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(view);
        dialog.show();
        try {
            Looper.loop();
        } catch (RuntimeException e2) {
        }

        return mResult;
    }

    public static boolean alertGetYesNo(String title, String message, Activity activity) {
        return alertGetYesNo(title, message, "", "", activity);
    }

    public static Bundle createFirebaseBundle(String itemId, String itemName, String contentType) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, itemId);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, itemName);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType);
        return bundle;
    }

    public static String unAccent(String s) {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replaceAll("Đ", "D").replaceAll("đ", "d").toLowerCase();
    }

}
