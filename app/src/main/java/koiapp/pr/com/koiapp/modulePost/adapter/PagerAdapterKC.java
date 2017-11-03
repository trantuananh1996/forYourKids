package koiapp.pr.com.koiapp.modulePost.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import koiapp.pr.com.koiapp.utils.view.PrFragment;
import koiapp.pr.com.koiapp.modulePost.activity.FragmentCategoryDetail;
import koiapp.pr.com.koiapp.modulePost.model.Category;

public class PagerAdapterKC extends FragmentStatePagerAdapter {
    private final List<Category> categories;
    FragmentManager manager;
    public static int LOOPS_COUNT = 1000;

    public PagerAdapterKC(FragmentManager supportFragmentManager, List<Category> fm) {
        super(supportFragmentManager);
        this.manager = supportFragmentManager;
        categories = fm;
    }

    @Override
    public Fragment getItem(int position) {
        if (categories != null && categories.size() > 0) {
            position = position % categories.size(); // use modulo for infinite cycling
            return FragmentCategoryDetail.newInstance(categories.get(position));
        } else {
            return FragmentCategoryDetail.newInstance(null);
        }

    }

    @Override
    public int getCount() {
        if (categories != null && categories.size() > 0) {
            if (categories.size() == 1) return 1;
            else
                return categories.size() * LOOPS_COUNT; // simulate infinite by big number of products
        } else {
            return 1;
        }
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
        if (categories.size() == 0) return "";
        return categories.get(position % categories.size()).getName();
    }
}
