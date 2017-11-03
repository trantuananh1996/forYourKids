package koiapp.pr.com.koiapp.modulePost.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.gson.JsonObject;

import io.realm.Realm;
import koiapp.pr.com.koiapp.R;
import koiapp.pr.com.koiapp.modulePost.adapter.PagerAdapterKC;
import koiapp.pr.com.koiapp.api.KOAPI;
import koiapp.pr.com.koiapp.utils.view.PrFragment;
import koiapp.pr.com.koiapp.modulePost.model.Category;
import koiapp.pr.com.koiapp.modulePost.model.PostCategoryId;
import koiapp.pr.com.koiapp.utils.realm.PrRealm;
import koiapp.pr.com.koiapp.utils.FragmentUtils;
import koiapp.pr.com.koiapp.utils.HTTPUtils;
import koiapp.pr.com.koiapp.utils.NameConfigs;
import koiapp.pr.com.koiapp.utils.debug.Debug;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Tran Anh
 * on 4/6/2017.
 */

public class FragmentCategory extends PrFragment {
    public static final String TAG = FragmentCategory.class.getName();
//    ArrayList<Fragment> fragments = new ArrayList<>();
    View rootView;
    private int categoryId;
    ProgressBar progressBar;
    ViewPager pager;
    Realm realm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        realm = PrRealm.getInstance(getActivity().getApplicationContext()).getRealm(NameConfigs.DB_KIDSONLINE_CORNER_NAME);
        rootView = inflater.inflate(R.layout.fragment_kc_category, container, false);
        findView();

        getCategoryDetail();
        return rootView;
    }

    @Override
    public void onDestroy() {
        Debug.prLog(TAG, "onDestroy");
        pager.removeAllViews();
        pager = null;
//        for (Fragment fragment : fragments) fragment.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onDetach() {
//        for (Fragment fragment : fragments) fragment.onDestroy();
        super.onDetach();
    }

    private void addCategoryInfo(Category category) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(category);
        realm.commitTransaction();
//        fragments.clear();
    /*    for (Category categoryChild : category.getChilds()) {
            FragmentCategoryDetail f = new FragmentCategoryDetail();
            f.setCategory(categoryChild);
            fragments.add(f);
        }*/
        PagerAdapterKC adapter = new PagerAdapterKC(getChildFragmentManager(), category.getChilds());
        pager.setAdapter(adapter);
        if (category.getChilds().size() > 1)
            pager.setCurrentItem(category.getChilds().size() * (PagerAdapterKC.LOOPS_COUNT / 2), false);
    }

    private void getCategoryDetail() {
        FragmentUtils.showProgress(true, progressBar);
        PostCategoryId post = new PostCategoryId(categoryId);
        Call<JsonObject> call = KOAPI.getKOServices().getSubCategories(post, getActivity().getString(R.string.parent_token_key));
        HTTPUtils.postDetail(call, post);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                FragmentUtils.showProgress(false, progressBar);
                if (HTTPUtils.isSuccess(response)) {
                    if (HTTPUtils.getInstance(parentActivity).isSuccess(HTTPUtils.getStatus(response))) {
                        Category category = HTTPUtils.getData(Category.class, response);
                        addCategoryInfo(category);

                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                FragmentUtils.showProgress(false, progressBar);
                Category category = realm.where(Category.class).equalTo("id", categoryId).findFirst();
                if (category != null) addCategoryInfo(category);
                FragmentUtils.getInstance(parentActivity).showToast(R.string.str_connect_error);
                t.printStackTrace();
            }
        });
    }

    @Override
    public void findView() {
        pager = (ViewPager) rootView.findViewById(R.id.pager_category);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress);
    }


    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}
