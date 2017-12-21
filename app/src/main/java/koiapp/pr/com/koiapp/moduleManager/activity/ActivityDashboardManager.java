package koiapp.pr.com.koiapp.moduleManager.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import koiapp.pr.com.koiapp.R;
import koiapp.pr.com.koiapp.moduleSearch.adapter.PagerAdapterInfo;
import koiapp.pr.com.koiapp.utils.FragmentUtils;
import koiapp.pr.com.koiapp.utils.view.PrFragment;

/**
 * Created by nguyentt
 * on 4/19/2017.
 */

public class ActivityDashboardManager extends AppCompatActivity {
    ProgressBar progressBar;
    ViewPager pager;

    FragmentDashboardManager fragmentRegisterManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_school_info);
        findViewById(R.id.tab_strip).setVisibility(View.GONE);
        pager = (ViewPager) findViewById(R.id.pager_category);
        progressBar = (ProgressBar) findViewById(R.id.progress);

        addDetailInfo();
    }


    private void addDetailInfo() {
        fragmentRegisterManager = new FragmentDashboardManager();
        fragmentRegisterManager.setCallback(s -> {
            findViewById(R.id.pager_category).setVisibility(View.GONE);
            TextView tv = (TextView) findViewById(R.id.tv_nodata);
            tv.setVisibility(View.VISIBLE);
            tv.setText(s);
            FragmentUtils.getInstance(ActivityDashboardManager.this).showToast(s);
        });

        List<PrFragment> fragments = new ArrayList<>();
        fragments.add(fragmentRegisterManager);
        PagerAdapterInfo adapter = new PagerAdapterInfo(getSupportFragmentManager(), fragments);
        pager.setAdapter(adapter);
    }


    @Override
    public void onBackPressed() {
//        viewOnMap.show(true);

        super.onBackPressed();
    }

}
