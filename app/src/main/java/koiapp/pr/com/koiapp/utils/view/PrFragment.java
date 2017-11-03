package koiapp.pr.com.koiapp.utils.view;

import android.support.annotation.StringRes;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.flipboard.bottomsheet.commons.BottomSheetFragment;


public class PrFragment extends BottomSheetFragment{
    protected FragmentActivity parentActivity;
    protected String fragmentName = "";
    protected View rootView;

    public FragmentActivity getParentActivity() {
        return parentActivity;
    }

    public void setParentActivity(FragmentActivity parentActivity) {
        this.parentActivity = parentActivity;
    }

    public void findView() {
    }

    public void addListener() {
    }


    public String getStringFromRes(@StringRes int id) {
        return null;
    }

    public String getFragmentName() {
        return fragmentName;
    }

    public void setFragmentName(String fragmentName) {
        this.fragmentName = fragmentName;
    }


    public void update() {
    }
}