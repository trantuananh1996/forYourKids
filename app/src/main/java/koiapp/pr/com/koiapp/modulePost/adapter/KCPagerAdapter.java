package koiapp.pr.com.koiapp.modulePost.adapter;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import koiapp.pr.com.koiapp.R;


/**
 * Created by Tran Anh
 * on 4/6/2017.
 */

public class KCPagerAdapter extends PagerAdapter {
    Activity activity;

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view =View.inflate(activity, R.layout.item_news_kc,container);


        return view;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
