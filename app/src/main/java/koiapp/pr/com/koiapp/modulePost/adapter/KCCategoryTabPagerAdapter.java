package koiapp.pr.com.koiapp.modulePost.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import koiapp.pr.com.koiapp.modulePost.activity.FragmentCategoryDetail;

public class KCCategoryTabPagerAdapter extends FragmentPagerAdapter {
    final List<FragmentCategoryDetail> fragments;

    public KCCategoryTabPagerAdapter(FragmentManager fm, List<FragmentCategoryDetail> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public int getItemPosition(Object object) {
        FragmentCategoryDetail f = (FragmentCategoryDetail) object;
        if (f != null) {
            f.update();
        }
        return POSITION_NONE;
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
    public CharSequence getPageTitle(int position) {
        return fragments.get(position).getCategory().getName();
    }
}