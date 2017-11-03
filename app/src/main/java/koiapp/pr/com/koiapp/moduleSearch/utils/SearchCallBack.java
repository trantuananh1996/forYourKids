package koiapp.pr.com.koiapp.moduleSearch.utils;

import koiapp.pr.com.koiapp.moduleSearch.model.DataSearchMap;

public interface SearchCallBack {
    void onSearchCompleted(DataSearchMap dataSearchMap);

    void onSearchFailed(String status, String message);
}