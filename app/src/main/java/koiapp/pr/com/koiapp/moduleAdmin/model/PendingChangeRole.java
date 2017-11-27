package koiapp.pr.com.koiapp.moduleAdmin.model;

import com.google.gson.annotations.SerializedName;


public class PendingChangeRole {
    @SerializedName("request_time")
    private Long request_time;

    private String name;
    private String new_role;
    private String old_role;

    private String user_avatar;

    private Boolean approved;

    private String user_request;

    public Long getRequest_time() {
        return request_time;
    }

    public void setRequest_time(Long request_time) {
        this.request_time = request_time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNew_role() {
        return new_role;
    }

    public void setNew_role(String new_role) {
        this.new_role = new_role;
    }

    public String getOld_role() {
        return old_role;
    }

    public void setOld_role(String old_role) {
        this.old_role = old_role;
    }

    public String getUser_avatar() {
        return user_avatar;
    }

    public void setUser_avatar(String user_avatar) {
        this.user_avatar = user_avatar;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public String getUser_request() {
        return user_request;
    }

    public void setUser_request(String user_request) {
        this.user_request = user_request;
    }
}
