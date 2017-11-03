package koiapp.pr.com.koiapp.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Random;

import koiapp.pr.com.koiapp.R;
import koiapp.pr.com.koiapp.activity.ActivityStart;
import koiapp.pr.com.koiapp.model.ItemNotification;
import koiapp.pr.com.koiapp.moduleAdmin.activity.ActivityDashboardAdmin;
import koiapp.pr.com.koiapp.moduleManager.activity.ActivityDashboardManager;
import koiapp.pr.com.koiapp.utils.DateTimeUtils;
import koiapp.pr.com.koiapp.utils.debug.Debug;

import static koiapp.pr.com.koiapp.model.ItemNotification.TYPE_HAS_NEW_MESSAGE;
import static koiapp.pr.com.koiapp.model.ItemNotification.TYPE_NEW_REGISTER_LEARN;
import static koiapp.pr.com.koiapp.model.ItemNotification.TYPE_NEW_REGISTER_MANAGER;
import static koiapp.pr.com.koiapp.model.ItemNotification.TYPE_NEW_REQUEST_CHANGE_ROLE;
import static koiapp.pr.com.koiapp.model.ItemNotification.TYPE_REGISTER_LEARN_ACCEPTED;
import static koiapp.pr.com.koiapp.model.ItemNotification.TYPE_REGISTER_MANAGER_ACCEPTED;
import static koiapp.pr.com.koiapp.model.ItemNotification.TYPE_REQUEST_CHANGE_ROLE_ACCEPTED;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Debug.prLog(TAG, "From: " + remoteMessage.getFrom());
        Debug.prLog(TAG, "Notification Message Body: " + remoteMessage.getData());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {//User hiện đang đăng nhập
            ItemNotification itemNotification = createItemNotification(remoteMessage.getData());
            if (itemNotification != null) {//Lấy được notification về
                if (itemNotification.getUserId().equals(user.getUid())) {//Notification này là của user này
                    prepareNotification(itemNotification);
                }
            }
        }

    }

    private void prepareNotification(ItemNotification remoteMessage) {
        Intent launchIntent = new Intent(this, ActivityStart.class);
        switch (remoteMessage.getType()) {
            case TYPE_NEW_REGISTER_LEARN:
                launchIntent = new Intent(this, ActivityDashboardManager.class);
                break;
            case TYPE_NEW_REGISTER_MANAGER:
                launchIntent = new Intent(this, ActivityDashboardAdmin.class);
                break;
            case TYPE_NEW_REQUEST_CHANGE_ROLE:
                launchIntent = new Intent(this, ActivityDashboardAdmin.class);
                break;
            case TYPE_REGISTER_LEARN_ACCEPTED:
                break;
            case TYPE_REGISTER_MANAGER_ACCEPTED:
                break;
            case TYPE_REQUEST_CHANGE_ROLE_ACCEPTED:
                break;
            case TYPE_HAS_NEW_MESSAGE:
                break;
            default:
                break;
        }

        sendNotification(launchIntent, remoteMessage);
    }

    private RemoteViews createRemoteView(ItemNotification itemNotification) {
        RemoteViews remoteViews = new RemoteViews(getPackageName(),
                R.layout.item_notification_builder);
        remoteViews.setTextViewText(R.id.tv_title, itemNotification.getTitle());
        if (TextUtils.isEmpty(itemNotification.getSubtitle()))
            remoteViews.setViewVisibility(R.id.tv_content, View.GONE);
        else {
            remoteViews.setViewVisibility(R.id.tv_content, View.VISIBLE);
            remoteViews.setTextViewText(R.id.tv_content, itemNotification.getSubtitle());
        }
        String time = DateTimeUtils.getFormatedDateTime(DateTimeUtils.HH_mm, itemNotification.getTime());
        remoteViews.setTextViewText(R.id.tv_time, time);

        remoteViews.setImageViewResource(R.id.iv_icon, R.mipmap.ic_launcher);
        return remoteViews;
    }

    private void sendNotification(Intent launchIntent, final ItemNotification remoteMessage) {
        PendingIntent pendingIntent;
        pendingIntent = PendingIntent.getActivity(getApplicationContext(), new Random().nextInt(), launchIntent, Intent.FILL_IN_ACTION);

        RemoteViews remoteViews = createRemoteView(remoteMessage);

        final int notificationId;
        notificationId = new Random().nextInt();
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContent(remoteViews)
                .setCustomBigContentView(remoteViews)
                .setAutoCancel(true)
                .setLights(0xff82c3e3, 1000, 500)
                .setContentIntent(pendingIntent);
        notificationBuilder.setSound(defaultSoundUri);
        final NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        final Notification notification = notificationBuilder.build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        }

        notificationManager.notify(notificationId, notification);

        Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(new long[]{0, 300, 100, 300}, -1);

    }

    private ItemNotification createItemNotification(Map<String, String> remoteMessage) {
        ItemNotification item;
        Type item_type = new TypeToken<Map<String, Object>>() {
        }.getType();
        String json = new Gson().toJson(remoteMessage, item_type);
        item = new Gson().fromJson(json, ItemNotification.class);

        return item;
    }
}