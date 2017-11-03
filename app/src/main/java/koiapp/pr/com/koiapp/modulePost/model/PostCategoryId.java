package koiapp.pr.com.koiapp.modulePost.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Tran Anh
 * on 5/3/2017.
 */

public class PostCategoryId {
    @SerializedName("category_id")
    private Integer categoryId;

    public PostCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }
}
