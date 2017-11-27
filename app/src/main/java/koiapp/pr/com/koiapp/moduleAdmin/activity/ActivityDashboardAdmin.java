package koiapp.pr.com.koiapp.moduleAdmin.activity;

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


public class ActivityDashboardAdmin extends AppCompatActivity {
    ProgressBar progressBar;
    ViewPager pager;

    FragmentRegisterManager fragmentRegisterManager;
    FragmentRequestChangeRole fragmentRequestChangeRole;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_school_info);

        pager = (ViewPager) findViewById(R.id.pager_category);
        progressBar = (ProgressBar) findViewById(R.id.progress);

        addDetailInfo();
    }


    private void addDetailInfo() {
        fragmentRegisterManager = new FragmentRegisterManager();
        fragmentRegisterManager.setFragmentName("Đăng kí quản trị");
        fragmentRequestChangeRole = new FragmentRequestChangeRole();
        fragmentRequestChangeRole.setFragmentName("Thay đổi quyền");

        List<PrFragment> fragments = new ArrayList<>();
        fragments.add(fragmentRegisterManager);
        fragments.add(fragmentRequestChangeRole);
        PagerAdapterInfo adapter = new PagerAdapterInfo(getSupportFragmentManager(), fragments);
        pager.setAdapter(adapter);
    }


    @Override
    public void onBackPressed() {
//        viewOnMap.show(true);

        super.onBackPressed();
    }

}
