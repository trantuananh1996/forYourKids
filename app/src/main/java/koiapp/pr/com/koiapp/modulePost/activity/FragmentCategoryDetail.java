package koiapp.pr.com.koiapp.modulePost.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import koiapp.pr.com.koiapp.R;
import koiapp.pr.com.koiapp.modulePost.adapter.ListPostKCAdapter;
import koiapp.pr.com.koiapp.utils.view.PrFragment;
import koiapp.pr.com.koiapp.modulePost.model.Category;
import koiapp.pr.com.koiapp.utils.debug.Debug;


/**
 * Created by Tran Anh
 * on 4/6/2017.
 */

public class FragmentCategoryDetail extends PrFragment {
    public static final String TAG = FragmentCategoryDetail.class.getName();
    RecyclerView rvCategory;
    View rootView;
    private Category category;
    ListPostKCAdapter mAdapter;
    TextView tvNodata;

     public static FragmentCategoryDetail newInstance(Category category) {
        Bundle args = new Bundle();
        FragmentCategoryDetail fragment = new FragmentCategoryDetail();
         fragment.setCategory(category);
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        setRetainInstance(true);
        rootView = inflater.inflate(R.layout.category_detail, container, false);
        findView();
        update();
        addListener();
        return rootView;
    }

    @Override
    public void findView() {
        rvCategory = (RecyclerView) rootView.findViewById(R.id.rv_category);
        tvNodata = (TextView) rootView.findViewById(R.id.tv_nodata);
    }

    @Override
    public void addListener() {

    }

    @Override
    public void onDestroy() {
        Debug.prLog(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        Debug.prLog(TAG, "onResum");
    }

    @Override
    public void update() {
        Debug.prLog(TAG, "update Detail" + category.getName());
        if (category.getPosts().size() > 0) {
            tvNodata.setVisibility(View.GONE);
            mAdapter = new ListPostKCAdapter(this);
            rvCategory.setAdapter(mAdapter);
            rvCategory.setLayoutManager(new LinearLayoutManager(parentActivity, LinearLayoutManager.VERTICAL, false));
        } else {
            tvNodata.setVisibility(View.VISIBLE);
        }
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
