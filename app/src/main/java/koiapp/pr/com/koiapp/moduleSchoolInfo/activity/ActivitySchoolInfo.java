package koiapp.pr.com.koiapp.moduleSchoolInfo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import koiapp.pr.com.koiapp.R;
import koiapp.pr.com.koiapp.api.FirebaseAPI;
import koiapp.pr.com.koiapp.model.ItemNotification;
import koiapp.pr.com.koiapp.model.PostSendNotification;
import koiapp.pr.com.koiapp.model.User;
import koiapp.pr.com.koiapp.model.UserNotRealm;
import koiapp.pr.com.koiapp.moduleSearch.adapter.PagerAdapterInfo;
import koiapp.pr.com.koiapp.moduleSearch.model.placeDetail.ResultDetail;
import koiapp.pr.com.koiapp.moduleSearch.utils.GetDetailCallBack;
import koiapp.pr.com.koiapp.moduleSearch.utils.GoogleMapApiHelper;
import koiapp.pr.com.koiapp.utils.AppUtils;
import koiapp.pr.com.koiapp.utils.FragmentUtils;
import koiapp.pr.com.koiapp.utils.realm.PrRealm;
import koiapp.pr.com.koiapp.utils.view.PrFragment;

import static koiapp.pr.com.koiapp.activity.ActivityStart.currentRole;

/**
 * Created by Tran Anh
 * on 4/19/2017.
 */

public class ActivitySchoolInfo extends AppCompatActivity {

    FragmentSchoolInfo fragmentSchoolInfo;
    ProgressBar progressBar;
    ViewPager pager;
    ResultDetail detail;
    FragmentReview fragmentReview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_school_info);

        pager = (ViewPager) findViewById(R.id.pager_category);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        String sId = getIntent().getExtras().getString("SchoolId");
        if (!TextUtils.isEmpty(sId)) {
            getDetail(sId);
        }
    }

    private void getDetail(String sId) {
        new GoogleMapApiHelper(ActivitySchoolInfo.this).getDetail(sId, new GetDetailCallBack() {
            @Override
            public void onSearchCompleted(ResultDetail detail) {
                addDetailInfo(detail);
            }

            @Override
            public void onSearchFailed(String status, String message) {

            }
        });
    }

    private void addDetailInfo(ResultDetail detail) {
        this.detail = detail;
        fragmentSchoolInfo = new FragmentSchoolInfo();
        fragmentSchoolInfo.setDetail(detail);
        fragmentSchoolInfo.setFragmentName("Thông tin");

        fragmentReview = new FragmentReview();
        fragmentReview.setFragmentName("Review");
        fragmentReview.setDetail(detail);

        List<PrFragment> fragments = new ArrayList<>();
        fragments.add(fragmentSchoolInfo);
        fragments.add(fragmentReview);
        PagerAdapterInfo adapter = new PagerAdapterInfo(getSupportFragmentManager(), fragments);
        pager.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_school_info, menu);

        if (currentRole.equals("parent")) {
            menu.getItem(1).setVisible(false);
            menu.getItem(2).setVisible(false);

        } else if (currentRole.equals("schoolManager")) {
            menu.getItem(2).setVisible(false);
            menu.getItem(0).setVisible(false);

        } else if (currentRole.equals("admin")) {
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(false);

        }
        // return true so that the menu pop up is opened
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_register_manager:
                if (AppUtils.alertGetYesNo("", "Bạn có muốn đăng kí làm quản trị viên trường này không?", ActivitySchoolInfo.this)) {
                    registerManager();
                }
                break;
            case R.id.action_register_learn:
                if (AppUtils.alertGetYesNo("", "Bạn có muốn đăng kí cho con học ở trường này không?", ActivitySchoolInfo.this)) {
                    registerLearn();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void registerLearn() {
        final User user = PrRealm.getInstance(getApplicationContext()).getCurrentUser();
        if (user == null) {
            FragmentUtils.getInstance(this).showToast("Bạn chưa đăng nhập");
            return;
        }
        if (!user.getRole().contains("parent")) {
            FragmentUtils.getInstance(this).showToast("Tài khoản hiện không có quyền phụ huynh");
            return;
        }

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("schoolManager/pending/register_learn").child(detail.getPlaceId()).child(user.getuId());
        dbRef.removeValue();
        dbRef.child("request_time").setValue(System.currentTimeMillis() / 1000);
        dbRef.child("uId_request").setValue(user.getuId());
        dbRef.child("school_request").setValue(detail.getPlaceId());
        dbRef.child("user_avatar").setValue(user.getPhotoUrl());
        dbRef.child("user_name").setValue(user.getName());
        dbRef.child("school_name").setValue(detail.getName());
        if (TextUtils.isEmpty(detail.getManaged_by()))
            AppUtils.alertGetYesNo("", "Yêu cầu của bạn đã được gửi đi.\nHiện tại trường chưa có quản trị viên.\nBạn vui lòng liên hệ trực tiếp", "Đồng ý", "", ActivitySchoolInfo.this);
        else {
            AppUtils.alertGetYesNo("", "Yêu cầu của bạn đã được gửi đi.\nVui lòng chờ quản trị trường duyệt yêu cầu", "Đồng ý", "", ActivitySchoolInfo.this);
            DatabaseReference uRef = FirebaseDatabase.getInstance().getReference("users").child(detail.getManaged_by());
            uRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserNotRealm u = dataSnapshot.getValue(UserNotRealm.class);
                    if (u != null && u.getDevice_ids() != null && u.getDevice_ids().size() > 0) {
                        ItemNotification itemNotification = new ItemNotification();
                        itemNotification.setUserId(u.getuId());
                        itemNotification.setTitle("Có đơn đăng kí học mới");
                        itemNotification.setSubtitle("Từ " + user.getName());
                        itemNotification.setTime(System.currentTimeMillis() / 1000);
                        itemNotification.setType(ItemNotification.TYPE_NEW_REGISTER_LEARN);

                        PostSendNotification post = new PostSendNotification();
                        post.setData(itemNotification);
                        post.setRegistrationIds(u.getDevice_ids());
                        sendNotification(post);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    private void sendNotification(PostSendNotification post) {
        FirebaseAPI.sendNotification(post, getString(R.string.firebase_key_authorization), getString(R.string.firebase_content_type));
    }

    private void registerManager() {
        final User user = PrRealm.getInstance(getApplicationContext()).getCurrentUser();
        if (user == null) {
            FragmentUtils.getInstance(this).showToast("Bạn chưa đăng nhập");
            return;
        }
        if (!user.getRole().contains("schoolManager")) {
            FragmentUtils.getInstance(this).showToast("Tài khoản hiện không có quyền quản trị trường");
            return;
        }

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("admin/pending/register_manager").child(user.getuId());
        dbRef.removeValue();
        dbRef.child("request_time").setValue(System.currentTimeMillis() / 1000);
        dbRef.child("uId_request").setValue(user.getuId());
        dbRef.child("school_request").setValue(detail.getPlaceId());
        dbRef.child("user_avatar").setValue(user.getPhotoUrl());
        dbRef.child("user_name").setValue(user.getName());
        dbRef.child("school_name").setValue(detail.getName());

        AppUtils.alertGetYesNo("", "Yêu cầu của bạn đã được gửi đi.\nVui lòng chờ quản trị hệ thống duyệt yêu cầu", "Đồng ý", "", ActivitySchoolInfo.this);


        DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference("users");

        adminRef.orderByChild("role").equalTo("admin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    UserNotRealm u = d.getValue(UserNotRealm.class);
                    if (u != null && u.getDevice_ids() != null && u.getDevice_ids().size() > 0 && u.getRole().equals("admin")) {
                        ItemNotification itemNotification = new ItemNotification();
                        itemNotification.setUserId(u.getuId());
                        itemNotification.setTitle("Có đơn đăng kí quản lí mới");
                        itemNotification.setSubtitle(user.getName() + " đăng kí quản lí " + detail.getName());
                        itemNotification.setTime(System.currentTimeMillis() / 1000);
                        itemNotification.setType(ItemNotification.TYPE_NEW_REGISTER_MANAGER);

                        PostSendNotification post = new PostSendNotification();
                        post.setData(itemNotification);
                        post.setRegistrationIds(u.getDevice_ids());
                        sendNotification(post);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onBackPressed() {
//        viewOnMap.show(true);

        super.onBackPressed();
    }

}
