package koiapp.pr.com.koiapp.modulePost.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import koiapp.pr.com.koiapp.R;
import koiapp.pr.com.koiapp.api.KOAPI;
import koiapp.pr.com.koiapp.modulePost.adapter.KidsCategoryCoverAdapter;
import koiapp.pr.com.koiapp.modulePost.adapter.ListPostHorzAdapter;
import koiapp.pr.com.koiapp.modulePost.adapter.ListPostKCAdapter;
import koiapp.pr.com.koiapp.modulePost.adapter.PostViewHolder;
import koiapp.pr.com.koiapp.modulePost.model.Category;
import koiapp.pr.com.koiapp.modulePost.model.DataKOCorner;
import koiapp.pr.com.koiapp.modulePost.model.KidsCornerPost;
import koiapp.pr.com.koiapp.utils.DateTimeUtils;
import koiapp.pr.com.koiapp.utils.FragmentUtils;
import koiapp.pr.com.koiapp.utils.HTTPUtils;
import koiapp.pr.com.koiapp.utils.ViewUtils;
import koiapp.pr.com.koiapp.utils.customVP.KCViewpager;
import koiapp.pr.com.koiapp.utils.debug.Debug;
import koiapp.pr.com.koiapp.utils.view.PrFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static koiapp.pr.com.koiapp.utils.Constants.URL_BASE_KOMT;

/**
 * Created by Tran Anh
 * on 4/4/2017.
 */

public class FragmentKidsonlineCorner extends PrFragment {
    public static final String TAG = FragmentKidsonlineCorner.class.getName();
    View rootView;
    KCViewpager vpCategory;
//    ViewPager vpNewest;
//    ViewPager vpCare;

    ImageView ibNextCategory;
    ImageView ibNextLastest;
    ImageView ibNextPopular;

    ImageView ibPrevCategory;
    ImageView ibPrevLastest;
    ImageView ibPrevPopular;
    View vContent;
    ProgressBar progressBar;

    RecyclerView rvLastest;
    ListPostKCAdapter adapterLastest;

    RecyclerView rvPopular;
    ListPostHorzAdapter adapterPopular;
    List<KidsCornerPost> lastestPosts = new ArrayList<>();
    List<KidsCornerPost> popularPosts = new ArrayList<>();
    public List<Category> categories = new ArrayList<>();
    int userId = 0;
    LinearLayout llLastest;
    FrameLayout frameCategory;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        userId = 0;//TODO ActivityMain.getInstance().dataLogin.getUser().getId();
        rootView = inflater.inflate(R.layout.fragment_kidsonline_corner, container, false);
        findView();
        ibNextLastest.setVisibility(View.GONE);
        ibNextPopular.setVisibility(View.GONE);
        ibPrevLastest.setVisibility(View.GONE);
        ibPrevPopular.setVisibility(View.GONE);
        getDataFromDB();
        getDataKOCorner();
        addListener();
        return rootView;
    }

    private void getDataFromDB() {


    }

    AsyncTask<String, Void, Void> as = new AsyncTask<String, Void, Void>() {
        @Override
        protected Void doInBackground(String... params) {
            parentActivity.runOnUiThread(() -> {
                addLastestPost(lastestPosts);
                int viewWidth = ViewUtils.getScreenWidthInPx(parentActivity) / 5 * 3;
                frameCategory.getLayoutParams().height = (9 * viewWidth) / 16;
                frameCategory.requestLayout();
            });
            return null;
        }
    };

    private void addLastestPost(List<KidsCornerPost> lastestPosts) {
        llLastest.removeAllViews();
        for (final KidsCornerPost post : lastestPosts) {
            View view = LayoutInflater.from(parentActivity).inflate(R.layout.item_kc_post, null);
            PostViewHolder holder = new PostViewHolder(view);
           loadImage(post.getAvatar(), holder.avatar);
            holder.title.setText(post.getTitle());
            holder.description.setText(post.getShortContent());
            holder.view.setText(post.getViews() + " lượt xem");
            holder.time.setText(DateTimeUtils.getFormatedDateTime(DateTimeUtils.dd_MM_yyyy, post.getTime()));
            view.setOnClickListener(v -> {
                Intent intent = new Intent(parentActivity, ActivityViewPostWebView.class);
                intent.putExtra("POST_ID", post.getId());
                parentActivity.startActivity(intent);
            });
            llLastest.addView(view);
        }
    }
    public void loadImage(String url, ImageView iv) {
        if (url == null || iv == null) return;
//        if (mContext == null) return;
        if (url.toLowerCase().contains("noimage")) {
            Glide.with(getActivity())
                    .load(R.drawable.no_image)
                    .thumbnail(0.5f)
                    .crossFade()
                    .into(iv);
        } else
            Glide.with(getActivity()).load(URL_BASE_KOMT + url)
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .signature(new StringSignature(url))
                    .fallback(ContextCompat.getDrawable(getActivity(), R.drawable.no_image))
                    .error(ContextCompat.getDrawable(getActivity(), R.drawable.no_image))
                    .into(iv);
    }


    private void getDataKOCorner() {
        Call<JsonObject> call = KOAPI.getKOServices().getDataKidsonlineCorner(getActivity().getString(R.string.parent_token_key));
        HTTPUtils.postDetail(call, null);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (HTTPUtils.isSuccess(response)) {
                    if (HTTPUtils.getInstance(getActivity()).isSuccess(HTTPUtils.getStatus(response))) {
                        handleResponse(HTTPUtils.getData(DataKOCorner.class, response));
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                FragmentUtils.getInstance(getActivity()).showToast(R.string.str_connect_error);
                t.printStackTrace();
            }
        });
    }

    private void handleResponse(DataKOCorner data) {

        lastestPosts.clear();
        lastestPosts.addAll(data.getLatestPosts());

        popularPosts.clear();
        popularPosts.addAll(data.getPopularPosts());

//        adapterLastest = new ListPostKCAdapter(parentActivity, lastestPosts);
//        rvLastest.setAdapter(adapterLastest);
//        rvLastest.setLayoutManager(new LinearLayoutManager(parentActivity, LinearLayoutManager.VERTICAL, false));
//        rvLastest.setNestedScrollingEnabled(false);

        adapterPopular = new ListPostHorzAdapter(parentActivity, popularPosts);
        adapterPopular.setShowView(true);
        rvPopular.setAdapter(adapterPopular);
        rvPopular.setLayoutManager(new LinearLayoutManager(parentActivity, LinearLayoutManager.HORIZONTAL, false));

        categories.clear();
        categories.addAll(data.getCategories());
        addListCategory();

        as.execute("");
    }

    int lastPosition = 0;

    private void addListCategory() {
        mHandler.removeCallbacks(mRunnable);
        final KidsCategoryCoverAdapter mAdapter = new KidsCategoryCoverAdapter(this);
        lastPosition = categories.size() - 1;
        vpCategory.setAdapter(mAdapter);
        vpCategory.setCurrentItem((categories.size() * (KidsCategoryCoverAdapter.KBDTLG / 2)), false); // set current item in the adapter to middle
        flagScrollCat = true;
        mHandler.postDelayed(mRunnable, DELAY);
        vpCategory.setScrollDurationFactor(3);
    }

    @Override
    public void onResume() {
        super.onResume();
        Debug.prLog(TAG, "onResum");
        // mHandler.postDelayed(mRunnable, DELAY);
    }

    private boolean flagScrollCat = false;
    private Handler mHandler = new Handler();
    public static int DELAY = 5000;
    boolean flag2 = false;
    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (vpCategory == null) return;
            if (vpCategory.getCurrentItem() + 1 < categories.size() * KidsCategoryCoverAdapter.KBDTLG) {
                if (flagScrollCat) {
                    DELAY = 0;
                    if (flag2) {
                        vpCategory.setCurrentItem(vpCategory.getCurrentItem() + 2, true);
                        flag2 = false;
                        flagScrollCat = false;
                    } else
                        vpCategory.setCurrentItem(vpCategory.getCurrentItem() - 1, false);
                    flag2 = true;
                } else {
                    DELAY = 5000;
                    vpCategory.setCurrentItem(vpCategory.getCurrentItem() + 1, true);
                }
            } else vpCategory.setCurrentItem(0, true);
            Debug.prLog("Current category", " at " + vpCategory.getCurrentItem());
            mHandler.postDelayed(this, DELAY);
        }
    };

    public void sharePost(String postStr) {
        KidsCornerPost post = new Gson().fromJson(postStr, KidsCornerPost.class);
        ShareDialog shareDialog = new ShareDialog(getActivity());
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent content = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(post.getUrl()))
                    .setContentTitle(post.getTitle())
                    .build();

            shareDialog.show(content);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mRunnable);
    }

    @Override
    public void findView() {
        vpCategory = (KCViewpager) rootView.findViewById(R.id.vp_category);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress);
        vContent = rootView.findViewById(R.id.ll_content);
        ibNextPopular = (ImageView) rootView.findViewById(R.id.next_care);
        ibNextCategory = (ImageView) rootView.findViewById(R.id.next_category);
        ibNextLastest = (ImageView) rootView.findViewById(R.id.next_newest);

        ibPrevPopular = (ImageView) rootView.findViewById(R.id.prev_care);
        ibPrevCategory = (ImageView) rootView.findViewById(R.id.prev_category);
        ibPrevLastest = (ImageView) rootView.findViewById(R.id.prev_newest);

        rvLastest = (RecyclerView) rootView.findViewById(R.id.rv_lastest);
        rvPopular = (RecyclerView) rootView.findViewById(R.id.rv_popular);
        llLastest = (LinearLayout) rootView.findViewById(R.id.ll_lastest);
        frameCategory = (FrameLayout) rootView.findViewById(R.id.frame_category);
    }

    @Override
    public void addListener() {

    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mRunnable);
    }
}
