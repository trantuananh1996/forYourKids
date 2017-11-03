package koiapp.pr.com.koiapp.modulePost.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Tran Anh
 * on 4/21/2017.
 */

public class KidsCornerPost extends RealmObject {
    @SerializedName("id")
    @Expose
    @PrimaryKey
    private Integer id;
    @SerializedName("avatar")
    @Expose
    private String avatar;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("time")
    @Expose
    private Long time;
    @SerializedName("short_content")
    @Expose
    private String shortContent;
    @SerializedName("views")
    @Expose
    private Integer views;
    @SerializedName("url")
    @Expose
    private String url;
    private int postType = 0;
    private int userId;

    @Ignore
    public static final int TYPE_LASTEST = 2;
    @Ignore
    public static final int TYPE_POPULAR = 3;
    @Ignore
    public static final int TYPE_ALL = 4;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getShortContent() {
        return shortContent;
    }

    public void setShortContent(String shortContent) {
        this.shortContent = shortContent;
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPostType() {
        return postType;
    }

    public void setPostType(int postType) {
        this.postType = postType;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
