package koiapp.pr.com.koiapp.moduleSearch.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.maps.model.PlacesSearchResult;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import koiapp.pr.com.koiapp.R;
import koiapp.pr.com.koiapp.utils.view.PrFragment;
import koiapp.pr.com.koiapp.moduleSearch.model.Result;
import koiapp.pr.com.koiapp.moduleSearch.adapter.SchoolListAdapter;

/**
 * Created by nguyetdtm
 * on 4/17/2017.
 */

public class FragmentSchoolSearchResult extends PrFragment {
    public static final String TAG = FragmentSchoolSearchResult.class.getName();
    SchoolListAdapter schoolListAdapter;
    RecyclerView rvSchools;
   private List<PlacesSearchResult> schools = new ArrayList<>();

    public void setSchools(PlacesSearchResult[] schools) {
        Collections.addAll(this.schools, schools);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_school_search_result, container, false);
        findView();

        Log.e(TAG, new Gson().toJson(schools));
        schoolListAdapter = new SchoolListAdapter(schools, getActivity());
        rvSchools.setAdapter(schoolListAdapter);
        rvSchools.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        addListener();
        return rootView;

    }


    @Override
    public void findView() {
        rvSchools = (RecyclerView) rootView.findViewById(R.id.rv_school_search_result);
    }

    @Override
    public void addListener() {

    }
}
