package koiapp.pr.com.koiapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.View;


/**
 * Created by Tran Anh on 11/5/2016.
 */
public class ViewUtils {
    public Context mContext;
    private static volatile ViewUtils instance = null;


    public static ViewUtils getInstance(Context context) {
        if (instance == null) {
            synchronized (ViewUtils.class) {
                if (instance == null) {
                    instance = new ViewUtils(context);
                }
            }
        }
        return instance;
    }

    public static float convertDpToPixel(float dp, Activity context) {
        try {
            if (context == null) return 0;
            Resources resources = context.getResources();
            DisplayMetrics metrics = resources.getDisplayMetrics();
            return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        } catch (NullPointerException e) {
            return 0;
        }
    }

    public static int getScreenHeightInPx(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Point s = new Point();
            activity.getWindowManager().getDefaultDisplay().getSize(s);
            return s.y;
        } else {
            return activity.getWindowManager().getDefaultDisplay().getHeight();
        }
    }

    private ViewUtils(Context context) {
        mContext = context;
    }



    public void setBackgroundDrawable(View view, int drawableId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(ContextCompat.getDrawable(mContext, drawableId));
        } else {
            view.setBackgroundResource(drawableId);
        }
    }

    public void setBackgroundColor(View view, int colorId) {
        view.setBackgroundColor(ContextCompat.getColor(mContext, colorId));
    }

    public float convertDpToPixel(float dp) {
        try {
            if (mContext == null) return 0;
            Resources resources = mContext.getResources();
            DisplayMetrics metrics = resources.getDisplayMetrics();
            return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        } catch (NullPointerException e) {
            return 0;
        }
    }

    public void setStroke(View v, int color) {
        GradientDrawable drawable = (GradientDrawable) v.getBackground();
        drawable.setStroke((int) convertDpToPixel(2), color); // set stroke width and stroke color
    }

    public float convertPixelsToDp(float px) {
        Resources resources = mContext.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static int getScreenWidthInPx(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Point s = new Point();
            activity.getWindowManager().getDefaultDisplay().getSize(s);
            return s.x;
        } else {
            return activity.getWindowManager().getDefaultDisplay().getWidth();
        }
    }

}
