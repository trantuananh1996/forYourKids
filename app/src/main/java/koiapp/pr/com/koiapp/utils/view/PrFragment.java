package koiapp.pr.com.koiapp.utils.view;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.flipboard.bottomsheet.commons.BottomSheetFragment;


public class PrFragment extends BottomSheetFragment {
    protected FragmentActivity parentActivity;
    protected String fragmentName = "";
    private Context mContext;
    protected View rootView;

    public Context getFragmentContext() {
        if (mContext == null) mContext = super.getContext();
        if (mContext == null) mContext = getActivity().getApplicationContext();
        if (mContext == null)
            if (parentActivity != null) mContext = parentActivity.getApplicationContext();
        return mContext;
    }

    public FragmentActivity getParentActivity() {
        return parentActivity;
    }

    public void setParentActivity(FragmentActivity parentActivity) {
        this.parentActivity = parentActivity;
    }

    @Override
    public void onAttach(Context context) {
        mContext = context;
        super.onAttach(context);
    }


    public void findView() {
    }

    /**
     * Đăng kí các sự kiện ở fragment
     */
    public void addListener() {
    }


    public String getStringFromRes(@StringRes int id) {
        if (parentActivity == null) {
            if (getActivity() != null) {
                parentActivity = getActivity();
                return parentActivity.getString(id);
            } else return "";
        } else return parentActivity.getString(id);
    }

    public String getFragmentName() {
        return fragmentName;
    }

    public void setFragmentName(String fragmentName) {
        this.fragmentName = fragmentName;
    }


    public boolean onBackButtonPressed() {
        return false;
    }

    public void update() {

    }
}