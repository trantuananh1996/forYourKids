package koiapp.pr.com.koiapp.moduleSearch.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import koiapp.pr.com.koiapp.utils.view.PrFragment;

public class PagerAdapterInfo extends FragmentStatePagerAdapter {
    private final List<PrFragment> fragments;
    FragmentManager manager;

    public PagerAdapterInfo(FragmentManager supportFragmentManager, List<PrFragment> fm) {
        super(supportFragmentManager);
        this.manager = supportFragmentManager;
        fragments = fm;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments == null ? 0 : fragments.size();
    }

    @Override
    public int getItemPosition(Object object) {
        PrFragment f = (PrFragment) object;
        if (f != null) {
            f.update();
        }
        return POSITION_NONE;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        if (fragments.size() == 0) return "";
        return fragments.get(position).getFragmentName();
    }
}
