package koiapp.pr.com.koiapp.moduleChat.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Tran Anh
 * on 6/12/2017.
 */

public class UserContact implements Comparable<UserContact> {
    @SerializedName("uId")
    private String uId;

    @SerializedName("email")
    private String email;

    @SerializedName("name")
    private String name;
    @SerializedName("role")
    private String role;

    @SerializedName("photoUrl")
    private String photoUrl;

    private String lastMessageKey;
    private boolean isHasMessage = false;
    @SerializedName("online")
    private boolean isOnline = false;
    private Long lastUpdate;

    private String manager_at;
    private String school_name;
    public UserContact() {
    }

    public UserContact(String uId) {
        this.uId = uId;
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
    public int compareTo(@NonNull UserContact user) {
//        if (this.lastUpdate == null || user.getLastUpdate() == null) return 0;
//        return this.lastUpdate.compareTo(user.lastUpdate);
        try{return this.name.compareTo(user.name);}catch (Exception e){return 0;}
    }

    public String getSchool_name() {
        return school_name;
    }

    public void setSchool_name(String school_name) {
        this.school_name = school_name;
    }

    public String getManager_at() {
        return manager_at;
    }

    public void setManager_at(String manager_at) {
        this.manager_at = manager_at;
    }
}
