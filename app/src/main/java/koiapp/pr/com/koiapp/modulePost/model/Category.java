
package koiapp.pr.com.koiapp.modulePost.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Category extends RealmObject {

    @SerializedName("id")
    @PrimaryKey
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("avatar")
    @Expose
    private String avatar;
    @SerializedName("childs")
    @Expose
    private RealmList<Category> childs = null;

    @SerializedName("posts")
    private RealmList<KidsCornerPost> posts;

    private int userId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public List<Category> getChilds() {
        return childs;
    }

    public void setChilds(RealmList<Category> childs) {
        this.childs = childs;
    }

    public List<KidsCornerPost> getPosts() {
        return posts;
    }

    public void setPosts(RealmList<KidsCornerPost> posts) {
        this.posts = posts;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
