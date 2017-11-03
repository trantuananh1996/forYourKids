package koiapp.pr.com.koiapp.utils.realm;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import koiapp.pr.com.koiapp.model.User;
import koiapp.pr.com.koiapp.utils.NameConfigs;

/**
 * Created by Tran Anh on 2/7/2017.
 */

public class PrRealm {
    public Context mContext;
    private static volatile PrRealm instance = null;

    public static PrRealm getInstance(Context context) {
        if (instance == null) {
            synchronized (PrRealm.class) {
                if (instance == null) {
                    instance = new PrRealm(context);
                }
            }
        }
        return instance;
    }

    private PrRealm(Context context) {
        mContext = context;
    }


    public Realm getRealm(String fileName) {
        Realm.init(mContext);
        RealmConfiguration config1 = new RealmConfiguration.Builder()
                .schemaVersion(3)
                .name(fileName)
                .deleteRealmIfMigrationNeeded()
                .build();
        return Realm.getInstance(config1);
    }

    public User getCurrentUser() {
        Realm realm = getRealm(NameConfigs.DB_NAME_USER);
        User user = realm.where(User.class).findFirst();
        if (user == null) return null;
        return realm.copyFromRealm(user);
    }

    public void storeUser(User user) {
        Realm realm = getRealm(NameConfigs.DB_NAME_USER);
        realm.beginTransaction();
        realm.delete(User.class);
        realm.copyToRealmOrUpdate(user);
        realm.commitTransaction();
    }

    public void removeAllUser() {
        Realm realm = getRealm(NameConfigs.DB_NAME_USER);
        realm.beginTransaction();
        realm.delete(User.class);
        realm.commitTransaction();
    }
}
