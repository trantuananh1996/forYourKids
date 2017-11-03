package koiapp.pr.com.koiapp.model;

import android.support.annotation.NonNull;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Tran Anh
 * on 6/12/2017.
 */

public class User extends RealmObject implements Comparable<User> {
    @PrimaryKey
    private String uId;

    private String email;
    private String name;
    private String role;

    private String photoUrl;

    private String lastMessageKey;
    private boolean isHasMessage = false;
    private boolean isOnline = false;
    private Long lastUpdate;

    private String manager_at;
    private String school_name;

    public User() {
    }

    public User(String uId) {
        this.uId = uId;
    }

    public User(UserNotRealm user) {
        this.uId = user.getuId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.photoUrl = user.getPhotoUrl();
        this.manager_at = user.getManager_at();
        this.school_name = user.getSchool_name();
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public boolean isHasMessage() {
        return isHasMessage;
    }

    public void setHasMessage(boolean hasMessage) {
        isHasMessage = hasMessage;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public void setLastMessageKey(String lastMessageKey) {
        this.lastMessageKey = lastMessageKey;
    }

    public void setLastUpdate(Long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getLastMessageKey() {
        return lastMessageKey;
    }

    public Long getLastUpdate() {
        return lastUpdate;
    }

    @Override
    public int compareTo(@NonNull User user) {
        if (this.lastUpdate == null || user.getLastUpdate() == null) return 0;
        return this.lastUpdate.compareTo(user.lastUpdate);
    }

    public String getManager_at() {
        return manager_at;
    }

    public void setManager_at(String manager_at) {
        this.manager_at = manager_at;
    }

    public String getSchool_name() {
        return school_name;
    }

    public void setSchool_name(String school_name) {
        this.school_name = school_name;
    }
}
