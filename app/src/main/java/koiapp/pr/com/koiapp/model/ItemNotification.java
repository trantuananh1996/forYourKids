
package koiapp.pr.com.koiapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ItemNotification {

    public static final int TYPE_NEW_REGISTER_LEARN = 1;
    public static final int TYPE_REGISTER_LEARN_ACCEPTED = 2;
    public static final int TYPE_NEW_REGISTER_MANAGER = 3;
    public static final int TYPE_REGISTER_MANAGER_ACCEPTED = 4;
    public static final int TYPE_NEW_REQUEST_CHANGE_ROLE = 5;
    public static final int TYPE_REQUEST_CHANGE_ROLE_ACCEPTED = 6;
    public static final int TYPE_HAS_NEW_MESSAGE = 7;

    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("subtitle")
    @Expose
    private String subtitle;
    @SerializedName("time")
    @Expose
    private Long time;
    @SerializedName("type")
    @Expose
    private Integer type;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

}
