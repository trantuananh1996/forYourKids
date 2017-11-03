package koiapp.pr.com.koiapp.modulePost.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import koiapp.pr.com.koiapp.R;


/**
 * Created by Tran Anh
 * on 6/10/2017.
 */

public class ActivityKidsOnlineCorner extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        FragmentKidsonlineCorner fragment = new FragmentKidsonlineCorner();
        fragment.setParentActivity(ActivityKidsOnlineCorner.this);
        FragmentManager manager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_up_enter,
                R.anim.slide_left_exit,
                R.anim.slide_down_enter,
                R.anim.slide_right_exit);
        transaction.replace(R.id.container, fragment
                , fragment.getClass().getName());
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
