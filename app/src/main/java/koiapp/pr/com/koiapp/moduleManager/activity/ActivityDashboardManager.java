package koiapp.pr.com.koiapp.moduleManager.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import koiapp.pr.com.koiapp.R;
import koiapp.pr.com.koiapp.moduleSearch.adapter.PagerAdapterInfo;
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

        pager = (ViewPager) findViewById(R.id.pager_category);
        progressBar = (ProgressBar) findViewById(R.id.progress);

        addDetailInfo();
    }


    private void addDetailInfo() {
        fragmentRegisterManager = new FragmentDashboardManager();


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
