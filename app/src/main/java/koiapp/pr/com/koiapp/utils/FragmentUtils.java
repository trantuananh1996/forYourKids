package koiapp.pr.com.koiapp.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.Calendar;
import java.util.TimeZone;

import koiapp.pr.com.koiapp.R;
import koiapp.pr.com.koiapp.activity.MainActivity;
import koiapp.pr.com.koiapp.utils.view.PrFragment;

/**
 * Created by Tran Anh
 * on 4/18/2017.
 */

public class FragmentUtils {

    public Context mContext;
    private static volatile FragmentUtils instance = null;

    public static Dialog createDialog(Activity activity) {
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }

    public DatePickerDialog createDateDialog(DatePickerDialog.OnDateSetListener datePickerListener, Activity activity) {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date
        DatePickerDialog datePicker = new DatePickerDialog(activity,
                datePickerListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));
        datePicker.setCancelable(false);
        datePicker.setCanceledOnTouchOutside(true);
        datePicker.setTitle("Từ ngày");
        return datePicker;
    }
    public static FragmentUtils getInstance(Context context) {
        if (instance == null) {
            synchronized (FragmentUtils.class) {
                if (instance == null) {
                    instance = new FragmentUtils(context);
                }
            }
        }
        return instance;
    }

    private FragmentUtils(Context context) {
        mContext = context;
    }

    public static void showSnackBarMessage(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static void showProgress(final boolean show, final ProgressBar mProgressView) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = 200;
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    public static void addNewFragment(PrFragment fragment, String intentname, FragmentActivity activity) {
        Log.e("abc", "add new fragment");
        fragment.setParentActivity(activity);
        FragmentManager manager = activity.getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_up_enter,
                R.anim.slide_left_exit,
                R.anim.slide_down_enter,
                R.anim.slide_right_exit);
        transaction.add(R.id.container, fragment
                , fragment.getClass().getName());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public static void replaceNewFragment(PrFragment fragment, String intentname, FragmentActivity activity) {
        Log.e("abc", "add new fragment");
        MainActivity.count_fragment++;
        fragment.setParentActivity(activity);
        FragmentManager manager = activity.getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_up_enter,
                R.anim.slide_left_exit,
                R.anim.slide_down_enter,
                R.anim.slide_right_exit);
        transaction.replace(R.id.container, fragment
                , fragment.getClass().getName());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public static void addNewFragment(String classname, String intentname, FragmentActivity activity) {
        addNewFragment((PrFragment) PrFragment.instantiate(activity, classname), intentname, activity);
    }

    public static void addNewFragment(String classname, @StringRes int intentnameId, FragmentActivity activity) {
        addNewFragment(classname, activity.getString(intentnameId), activity);
    }

    public static void addNewFragment(PrFragment fragment, @StringRes int intentnameId, FragmentActivity activity) {
        addNewFragment(fragment, activity.getString(intentnameId), activity);
    }

    public void showToast(@StringRes int stringId) {
        Toast.makeText(mContext, mContext.getResources().getString(stringId), Toast.LENGTH_SHORT).show();
    }

    public void showToast(String text) {
        Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
    }

}
