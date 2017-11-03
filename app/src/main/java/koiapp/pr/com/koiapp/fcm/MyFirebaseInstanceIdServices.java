package koiapp.pr.com.koiapp.fcm;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import koiapp.pr.com.koiapp.utils.debug.Debug;

/**
 * Created by Tran Anh
 * on 10/28/2016.
 */
public class MyFirebaseInstanceIdServices extends FirebaseInstanceIdService {
    public static final String TAG = MyFirebaseInstanceIdServices.class.getName();

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//        Log.d(TAG, "Refreshed token: " + refreshedToken);
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        Debug.prLog(TAG, token);
    }
}
