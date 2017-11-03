package koiapp.pr.com.koiapp.activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;

import com.shahroz.svlibrary.interfaces.onSearchListener;
import com.shahroz.svlibrary.interfaces.onSimpleSearchActionsListener;
import com.shahroz.svlibrary.widgets.MaterialSearchView;

/**
 * Created by Tran Anh
 * on 4/17/2017.
 */

public class MainActivity extends AppCompatActivity implements onSimpleSearchActionsListener, onSearchListener {
    ProgressDialog progressDialog;
    boolean doubleBackToExitPressedOnce = false;
    private MaterialSearchView mSearchView;
    private WindowManager mWindowManager;
    private Toolbar mToolbar;
    private MenuItem searchItem;
    private boolean mSearchViewAdded = false;
    private boolean searchActive = false;
    public static int count_fragment = 0;

    @Override
    public void onSearch(String query) {

    }

    @Override
    public void searchViewOpened() {

    }

    @Override
    public void searchViewClosed() {

    }

    @Override
    public void onCancelSearch() {

    }

    @Override
    public void onItemClicked(String item) {

    }

    @Override
    public void onScroll() {

    }

    @Override
    public void error(String localizedMessage) {

    }
/*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("MainActivity", "onCreate");
        setContentView(R.layout.main_activity);
        createSearchView();
        getListSchool();

        findViewById(R.id.main_activity).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mSearchView != null) {
                    mSearchView.hide();
                    UIUtil.hideKeyboard(MainActivity.this);
                }
                return false;
            }
        });

        findViewById(R.id.add_school).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentUtils.addNewFragment(FragmentAddSchool.TAG, "Thêm trường", MainActivity.this);
            }
        });
    }

    private void removeAllFragmentExceptMainFragment() {
        count_fragment = 0;
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        List<Fragment> fragments = manager.getFragments();
        if (fragments.isEmpty()) return;
        for (Fragment f : fragments) {
            if (f != null) {
                try {
                    transaction.remove(f);
                    f.onDestroy();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_school_info, menu);
        searchItem = menu.findItem(R.id.search);
        searchItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                mSearchView.display();
                openKeyboard();
                return true;
            }
        });
        if (searchActive)
            mSearchView.display();
        return true;

    }

    private void createSearchView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mSearchView = new MaterialSearchView(this);
        mSearchView.setOnSearchListener(this);
        mSearchView.setSearchResultsListener(this);
        mSearchView.setHintText("Search");

        if (mToolbar != null) {
            // Delay adding SearchView until Toolbar has finished loading
            mToolbar.post(new Runnable() {
                @Override
                public void run() {
                    if (!mSearchViewAdded && mWindowManager != null) {
                        mWindowManager.addView(mSearchView,
                                MaterialSearchView.getSearchViewLayoutParams(MainActivity.this));
                        mSearchViewAdded = true;
                    }
                }
            });
        }
    }

    private void getListSchool() {
        progressDialog = ProgressDialog.show(MainActivity.this, "", "Đang cập nhật dữ liệu", true);
        Call<JsonObject> call = KOAPI.getKOServices().getSchoolList();
        HTTPUtils.postDetail(call, null);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                progressDialog.dismiss();
                if (HTTPUtils.isSuccess(response)) {
                    if (HTTPUtils.getInstance(MainActivity.this).isSuccess(HTTPUtils.getStatus(response))) {
                        List<ItemSchool> schools = HTTPUtils.getDataList(ItemSchool.class, response);
                        handleSchoolList(schools);
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    private void handleSchoolList(List<ItemSchool> schools) {
        PrRealm prRealm = PrRealm.getInstance(MainActivity.this);
        Realm realm = prRealm.getRealm(Config.REALM_NAME_SCHOOL_LIST);
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(schools);
        realm.commitTransaction();
    }


    @Override
    public void onBackPressed() {
        Log.e("Main", count_fragment + "abc");
        if (count_fragment == 0) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                finish();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getString(R.string.str_press_back_again), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            count_fragment--;
            super.onBackPressed();
        }
    }

    private void openKeyboard() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                mSearchView.getSearchView().dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0));
//                mSearchView.getSearchView().dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0));
            }
        }, 200);
    }

    @Override
    public void onSearch(String query) {
        PrRealm prRealm = PrRealm.getInstance(this);
        Realm realm = prRealm.getRealm(Config.REALM_NAME_SCHOOL_LIST);
        RealmQuery<ItemSchool> schoolRealmQuery = realm.where(ItemSchool.class).contains("name", query);
//        Log.e("Main", new Gson().toJson(schoolRealmQuery));
    }

    @Override
    public void searchViewOpened() {

    }

    @Override
    public void searchViewClosed() {

    }

    @Override
    public void onCancelSearch() {
        searchActive = false;
        mSearchView.hide();
        UIUtil.hideKeyboard(MainActivity.this);
    }

    @Override
    public void onItemClicked(String item) {

    }

    @Override
    public void onScroll() {

    }

    @Override
    public void error(String localizedMessage) {

    }*/
}
