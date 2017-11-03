package koiapp.pr.com.koiapp.utils.debug;

import android.util.Log;

/**
 * Created by Tran Anh
 * on 3/17/2017.
 */

public class Debug {
    public static void prLog(String TAG, String content) {
       Log.e(TAG, content);
    }
}
