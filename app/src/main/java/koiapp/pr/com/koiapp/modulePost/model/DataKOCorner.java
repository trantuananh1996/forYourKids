
package koiapp.pr.com.koiapp.modulePost.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.realm.RealmList;

public class DataKOCorner {

    @SerializedName("categories")
    @Expose
    private RealmList<Category> categories = null;
    @SerializedName("latest_posts")
    @Expose
    private RealmList<KidsCornerPost> latestPosts = null;
    @SerializedName("popular_posts")
    @Expose
    private RealmList<KidsCornerPost> popularPosts = null;

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(RealmList<Category> categories) {
        this.categories = categories;
    }

    public List<KidsCornerPost> getLatestPosts() {
        return latestPosts;
    }

    public void setLatestPosts(RealmList<KidsCornerPost> latestPosts) {
        this.latestPosts = latestPosts;
    }

    public List<KidsCornerPost> getPopularPosts() {
        return popularPosts;
    }

    public void setPopularPosts(RealmList<KidsCornerPost> popularPosts) {
        this.popularPosts = popularPosts;
    }

}
