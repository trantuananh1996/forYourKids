package koiapp.pr.com.koiapp.moduleSearch.utils;

import koiapp.pr.com.koiapp.moduleSearch.model.placeDetail.ResultDetail;

/**
 * Created by nguyetdtm
 * on 6/13/2017.
 */

public interface GetDetailCallBack {
    void onSearchCompleted(ResultDetail detail);

    void onSearchFailed(String status, String message);
}
