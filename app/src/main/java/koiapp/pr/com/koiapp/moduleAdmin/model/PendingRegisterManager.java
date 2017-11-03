package koiapp.pr.com.koiapp.moduleAdmin.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Tran Anh
 * on 6/13/2017.
 */

public class PendingRegisterManager {
    @SerializedName("request_time")
    private Long request_time;

    @SerializedName("uId_request")
    private String uId_request;

    @SerializedName("school_request")
    private String school_request;

    @SerializedName("approved")
    private Boolean approved;

    @SerializedName("user_name")
    private String user_name;

    @SerializedName("user_avatar")
    private String user_avatar;

    @SerializedName("school_name")
    private String school_name;

    public PendingRegisterManager() {
    }

    public String getSchool_name() {
        return school_name;
    }

    public void setSchool_name(String school_name) {
        this.school_name = school_name;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_avatar() {
        return user_avatar;
    }

    public void setUser_avatar(String user_avatar) {
        this.user_avatar = user_avatar;
    }

    public Long getRequest_time() {
        return request_time;
    }

    public void setRequest_time(Long request_time) {
        this.request_time = request_time;
    }

    public String getuId_request() {
        return uId_request;
    }

    public void setuId_request(String uId_request) {
        this.uId_request = uId_request;
    }

    public String getSchool_request() {
        return school_request;
    }

    public void setSchool_request(String school_request) {
        this.school_request = school_request;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }
}
