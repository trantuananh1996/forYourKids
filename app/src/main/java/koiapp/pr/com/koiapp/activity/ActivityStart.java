package koiapp.pr.com.koiapp.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import koiapp.pr.com.koiapp.R;
import koiapp.pr.com.koiapp.api.FirebaseAPI;
import koiapp.pr.com.koiapp.model.ItemNotification;
import koiapp.pr.com.koiapp.model.PostSendNotification;
import koiapp.pr.com.koiapp.model.User;
import koiapp.pr.com.koiapp.model.UserNotRealm;
import koiapp.pr.com.koiapp.moduleAdmin.activity.ActivityDashboardAdmin;
import koiapp.pr.com.koiapp.moduleChat.activity.ActivityContact;
import koiapp.pr.com.koiapp.moduleManager.activity.ActivityDashboardManager;
import koiapp.pr.com.koiapp.modulePost.activity.ActivityKidsOnlineCorner;
import koiapp.pr.com.koiapp.moduleSchoolInfo.activity.ActivitySchoolInfo;
import koiapp.pr.com.koiapp.moduleSearch.activity.ActivityFindNearby;
import koiapp.pr.com.koiapp.moduleSearch.activity.FragmentSchoolSearchResult;
import koiapp.pr.com.koiapp.moduleSearch.adapter.MySchoolListAdapter;
import koiapp.pr.com.koiapp.moduleSearch.model.placeDetail.ResultDetail;
import koiapp.pr.com.koiapp.utils.debug.Debug;
import koiapp.pr.com.koiapp.utils.realm.PrRealm;
import koiapp.pr.com.koiapp.utils.view.Config;

import static koiapp.pr.com.koiapp.utils.AppUtils.createDialogFromActivity;
import static koiapp.pr.com.koiapp.utils.FragmentUtils.showProgress;

public class ActivityStart extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    EditText edtSearch;
    ProgressDialog progressDialog;
    ImageView ivNavAvatar;
    TextView tvNavName;
    TextView tvNavEmail;
    View headerView;
    private FirebaseAuth mAuth;
    NavigationView navigationView;
    FirebaseUser currentUser;
    public static String currentRole = "parent";
    ProgressBar progressBar;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        // Configure Google Sign In
        mAuth = FirebaseAuth.getInstance();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.findViewById(R.id.search_icon).setOnClickListener(v -> {
            openAutocompleteActivity();
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

        showProgress(true, progressBar);
        findView();
        RecyclerView rvNews = (RecyclerView) findViewById(R.id.rv_newest);
        List<ResultDetail> details = new ArrayList<>();
        MySchoolListAdapter mySchoolListAdapter = new MySchoolListAdapter(details, this);
        rvNews.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvNews.setAdapter(mySchoolListAdapter);
        FirebaseDatabase.getInstance().getReference("places").orderByChild("last_update").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
                    if (count > 79) showProgress(false, progressBar);
                    ResultDetail detail = dataSnapshot.getValue(ResultDetail.class);
                    details.add(0, detail);
                    mySchoolListAdapter.notifyItemInserted(0);
                    count++;
                } catch (Exception e) {
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        addListener();
    }

    private void addListener() {
        edtSearch.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (edtSearch.getRight() - edtSearch.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    UIUtil.hideKeyboard(ActivityStart.this);
                    search(edtSearch.getText().toString());
                    return true;
                }
            }
            return false;
        });

        edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                UIUtil.hideKeyboard(ActivityStart.this);
                search(edtSearch.getText().toString());
                return true;
            }
            return false;
        });
        findViewById(R.id.btn_search).setOnClickListener(view -> {
            UIUtil.hideKeyboard(ActivityStart.this);
            search(edtSearch.getText().toString());
        });
    }

    private void search(String query) {
        progressDialog = ProgressDialog.show(ActivityStart.this, "", "Đang tìm kiếm", true);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        runOnUiThread(() -> {
            String q2 = "mầm non " + query;
            GeoApiContext context = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_key))
                    .build();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try {
                LatLng latLng = new LatLng(21.021322500000004, 105.79484765625003);
                PlacesSearchResponse results = PlacesApi.nearbySearchQuery(context, latLng)
                        .type(PlaceType.SCHOOL)
                        .radius(50000)
                        .keyword(q2)
                        .await();
                handleSearchResult(results);
                Debug.prLog("Response", gson.toJson(results));

            } catch (ApiException e) {
                e.printStackTrace();
                Snackbar.make(edtSearch, e.getMessage(), Snackbar.LENGTH_LONG).show();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Snackbar.make(edtSearch, e.getMessage(), Snackbar.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Snackbar.make(edtSearch, e.getMessage(), Snackbar.LENGTH_LONG).show();
            } finally {
                progressDialog.dismiss();
            }
        });


    }


    private void findView() {
        edtSearch = (EditText) findViewById(R.id.edt_search);
        ivNavAvatar = (ImageView) headerView.findViewById(R.id.iv_nav_avatar);
        tvNavName = (TextView) headerView.findViewById(R.id.tv_nav_name);
        tvNavEmail = (TextView) headerView.findViewById(R.id.tv_nav_email);
    }

    private void handleSearchResult(PlacesSearchResponse dataSearchMap) {
        FragmentSchoolSearchResult fragmentSchoolSearchResult = new FragmentSchoolSearchResult();
        fragmentSchoolSearchResult.setSchools(dataSearchMap.results);
        fragmentSchoolSearchResult.show(getSupportFragmentManager(), R.id.bottom_sheet);
        PrRealm prRealm = PrRealm.getInstance(this);
        Realm realm = prRealm.getRealm(Config.REALM_NAME_SCHOOL_LIST);
        realm.beginTransaction();
//        realm.copyToRealmOrUpdate(dataSearchMap.getResults());
        realm.commitTransaction();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    private void openAutocompleteActivity() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .build(this);
            startActivityForResult(intent, 11);
        } catch (GooglePlayServicesRepairableException e) {
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);
            Log.e("Start activity", message);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 11) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                if (place != null) {
                    String placeName = place.getName().toString();
                    if (placeName.toLowerCase().contains("mầm non")
                            || placeName.toLowerCase().contains("kindergarten")
                            || placeName.toLowerCase().contains("mẫu giáo")) {
                        Log.i("Activity Search", "Place Selected: " + place.getName());
                        Intent intent = new Intent(this, ActivitySchoolInfo.class);
                        intent.putExtra("SchoolId", place.getId());
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Địa điểm bạn chọn không phải trường mầm non", Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.e("Activity Search", "Error: Status = " + status.toString());
            } else if (resultCode == RESULT_CANCELED) {
                Log.e("Activity Search", "Cancel search");

            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_search) {
            openAutocompleteActivity();
        } else if (id == R.id.nav_search_nearby) {
            Intent i = new Intent(ActivityStart.this, ActivityFindNearby.class);
            startActivity(i);
        } else if (id == R.id.nav_kidsonline_corner) {
            Intent i = new Intent(ActivityStart.this, ActivityKidsOnlineCorner.class);
            startActivity(i);
        } else if (id == R.id.nav_login) {
            Intent i = new Intent(ActivityStart.this, ActivityLogin.class);
            startActivity(i);

        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            updateUI(null);
        } else if (id == R.id.nav_register) {
            Intent i = new Intent(ActivityStart.this, ActivityRegister.class);
            startActivity(i);
        } else if (id == R.id.nav_switch_role) {
            if (mAuth.getCurrentUser() != null) {
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("admin/pending/role_switch").child(mAuth.getCurrentUser().getUid());
                showDialogChooseRole(dbRef, true);
            }
        } else if (id == R.id.nav_message) {
            Intent i = new Intent(ActivityStart.this, ActivityContact.class);
            startActivity(i);
        } else if (id == R.id.nav_admin_dashboard) {
            Intent i = new Intent(ActivityStart.this, ActivityDashboardAdmin.class);
            startActivity(i);
        } else if (id == R.id.nav_school_manager_dashboard) {
            Intent i = new Intent(ActivityStart.this, ActivityDashboardManager.class);
            startActivity(i);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onStart() {
        super.onStart();
        if (currentUser == null) {
            currentUser = mAuth.getCurrentUser();
            updateUI(currentUser);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    protected void onResume() {
        super.onResume();
        count = 0;
        if (currentUser == null) {
            currentUser = mAuth.getCurrentUser();
            updateUI(currentUser);
        }
    }

    private void showDialogChooseRole(final DatabaseReference dbRef, final boolean shouldValidate) {
        View view = View.inflate(this, R.layout.dialog_choose_role, null);
        final Dialog dialog = createDialogFromActivity(this);
        if (shouldValidate) {
            TextView tvC = (TextView) view.findViewById(R.id.tv_content);
            tvC.setText("Chọn vai trò bạn muốn đổi.\nThay đổi chỉ có hiệu lực sau khi quản trị viên xác nhận.\nBạn vẫn sẽ thao tác ở vai trò cũ");
        }
        Button btnYes = (Button) view.findViewById(R.id.btn_yes);
        View.OnClickListener clicked = v -> {
            dialog.dismiss();
            switch (v.getId()) {
                case R.id.btn_yes:
                    String role = "";
                    CheckBox rb = (CheckBox) dialog.findViewById(R.id.rb_parent);
                    if (rb.isChecked()) {
                        if (!shouldValidate) tvNavName.append(" - Phụ huynh");
                        role += "parent";
                        if (!shouldValidate) currentRole = "parent";
                    }
                    CheckBox rbManager = (CheckBox) dialog.findViewById(R.id.rb_manager);
                    if (rbManager.isChecked()) {
                        role += "schoolManager";
                        if (!shouldValidate) tvNavName.append(" - Quản trị trường");
                        if (!shouldValidate) currentRole = "schoolManager";
                    }
                    if (shouldValidate) {
                        requestChangeRole(dbRef, role);
                    } else {
                        dbRef.child("role").setValue(role);
                        User user = PrRealm.getInstance(getApplicationContext()).getCurrentUser();
                        if (user != null) {
                            user.setRole(role);
                            PrRealm.getInstance(getApplicationContext()).storeUser(user);
                        }
                        updateUI(currentUser);
                    }
                    break;
            }
        };
        btnYes.setOnClickListener(clicked);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(view);
        dialog.show();

    }

    private void requestChangeRole(DatabaseReference dbRef, String role) {
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            dbRef.child("name").setValue(firebaseUser.getDisplayName());
            dbRef.child("old_role").setValue(currentRole);
            dbRef.child("new_role").setValue(role);
            dbRef.child("request_time").setValue(System.currentTimeMillis() / 1000);
            if (firebaseUser.getPhotoUrl() != null)
                dbRef.child("user_avatar").setValue(firebaseUser.getPhotoUrl().toString());
            dbRef.child("user_request").setValue(firebaseUser.getUid());


            DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference("users");

            adminRef.orderByChild("role").equalTo("admin").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        UserNotRealm u = d.getValue(UserNotRealm.class);
                        if (u != null && u.getDevice_ids() != null && u.getDevice_ids().size() > 0 && u.getRole().equals("admin")) {
                            ItemNotification itemNotification = new ItemNotification();
                            itemNotification.setUserId(u.getuId());
                            itemNotification.setTitle("Có yêu cầu đổi quyền người dùng mới");
                            itemNotification.setSubtitle(firebaseUser.getDisplayName() + " yêu cầu đổi quyền người dùng");
                            itemNotification.setTime(System.currentTimeMillis() / 1000);
                            itemNotification.setType(ItemNotification.TYPE_NEW_REQUEST_CHANGE_ROLE);

                            PostSendNotification post = new PostSendNotification();
                            post.setData(itemNotification);
                            post.setRegistrationIds(u.getDevice_ids());
                            FirebaseAPI.sendNotification(post, getString(R.string.firebase_key_authorization), getString(R.string.firebase_content_type));
                            Toast.makeText(ActivityStart.this, "Yêu cầu đã được gửi đi", Toast.LENGTH_SHORT).show();
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private boolean checkListContainString(List<String> list, String value) {
        for (String str : list) if (str.equals(value)) return true;
        return false;
    }

    private void handlerUserInfo(DataSnapshot dataSnapshot, DatabaseReference dbRef) {
        String firebaseId = FirebaseInstanceId.getInstance().getToken();
        Debug.prLog("Firebase id ", "id " + firebaseId);
        UserNotRealm user = dataSnapshot.getValue(UserNotRealm.class);
        if (user == null) {
            user = new UserNotRealm(currentUser.getUid());
            user.setName(currentUser.getDisplayName());
            user.setEmail(currentUser.getEmail());
            if (currentUser.getPhotoUrl() != null)
                user.setPhotoUrl(currentUser.getPhotoUrl().toString());
        }
        String role = user.getRole();
        Debug.prLog("current role", "is " + role);
        if (progressDialog != null) progressDialog.dismiss();
        if (TextUtils.isEmpty(role) || role.equals("null")) {
            dbRef.setValue(user);
            List<String> deviceId = user.getDevice_ids();
            if (deviceId == null) deviceId = new ArrayList<>();
            if (!checkListContainString(deviceId, firebaseId))
                deviceId.add(firebaseId);
            HashMap<String, String> hMapIds = new HashMap<>();
            for (int i = 0; i < deviceId.size(); i++) {
                hMapIds.put(String.valueOf(i), deviceId.get(i));
            }
            dbRef.child("device_ids").removeValue();
            dbRef.child("device_ids").setValue(hMapIds);
            user.setDevice_ids(deviceId);
            showDialogChooseRole(dbRef, false);
        } else {
            List<String> deviceId = user.getDevice_ids();
            if (deviceId == null) deviceId = new ArrayList<>();
            if (!checkListContainString(deviceId, firebaseId))
                deviceId.add(firebaseId);
            HashMap<String, String> hMapIds = new HashMap<>();
            for (int i = 0; i < deviceId.size(); i++) {
                hMapIds.put(String.valueOf(i), deviceId.get(i));
            }
            dbRef.child("device_ids").removeValue();
            dbRef.child("device_ids").setValue(hMapIds);
            user.setDevice_ids(deviceId);
            currentRole = role;
            if (role.contains("parent")) {
                tvNavName.append(" - Phụ huynh");
                navigationView.getMenu().getItem(5).setVisible(false);
            }
            if (role.contains("schoolManager")) {
                tvNavName.append(" - Quản trị trường");
                navigationView.getMenu().getItem(5).setVisible(true);
                navigationView.getMenu().getItem(5).getSubMenu().getItem(0).setVisible(false);

            }
            if (role.contains("admin")) {
                tvNavName.append(" - Admin");
                navigationView.getMenu().getItem(4).getSubMenu().getItem(3).setVisible(false);
                navigationView.getMenu().getItem(5).setVisible(true);
                navigationView.getMenu().getItem(5).getSubMenu().getItem(1).setVisible(false);
            }
        }
        User uRealm = new User(user);

        PrRealm.getInstance(getApplicationContext()).storeUser(uRealm);
        progressDialog.dismiss();
    }

    private void updateUI(final FirebaseUser currentUser) {
        progressDialog = ProgressDialog.show(this, "", "Vui lòng đợi trong giây lát", true);
        if (currentUser != null) {
            navigationView.getMenu().getItem(5).setVisible(true);
            navigationView.getMenu().getItem(3).setVisible(true);
            String mFullName = currentUser.getDisplayName();
            String mEmail = currentUser.getEmail();
            tvNavName.setText(mFullName);
            tvNavEmail.setText(mEmail);
            navigationView.getMenu().getItem(4).getSubMenu().getItem(0).setVisible(false);
            navigationView.getMenu().getItem(4).getSubMenu().getItem(1).setVisible(true);
            navigationView.getMenu().getItem(4).getSubMenu().getItem(2).setVisible(false);
            navigationView.getMenu().getItem(4).getSubMenu().getItem(3).setVisible(true);
            try {
                if (currentUser.getPhotoUrl() != null)
                    Glide.with(this).load(currentUser.getPhotoUrl().toString()).diskCacheStrategy(DiskCacheStrategy.ALL).into(ivNavAvatar);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Debug.prLog("Current uId", currentUser.getUid());
            final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    handlerUserInfo(dataSnapshot, dbRef);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    progressDialog.dismiss();
                    navigationView.getMenu().getItem(5).setVisible(false);
                }
            });

        } else {
            progressDialog.dismiss();
            PrRealm.getInstance(getApplicationContext()).removeAllUser();
            currentRole = "none";
            tvNavName.setText("Chưa đăng nhập");
            tvNavEmail.setText("");
            navigationView.getMenu().getItem(3).setVisible(false);
            navigationView.getMenu().getItem(4).getSubMenu().getItem(0).setVisible(true);
            navigationView.getMenu().getItem(4).getSubMenu().getItem(1).setVisible(false);
            navigationView.getMenu().getItem(4).getSubMenu().getItem(2).setVisible(true);
            navigationView.getMenu().getItem(4).getSubMenu().getItem(3).setVisible(false);
            navigationView.getMenu().getItem(5).setVisible(false);
        }
    }
}
