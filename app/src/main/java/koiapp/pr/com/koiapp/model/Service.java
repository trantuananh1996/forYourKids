
package koiapp.pr.com.koiapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Service extends RealmObject {

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
    @SerializedName("category")
    @Expose
    private Integer category;
    @SerializedName("type")
    @Expose
    private Integer type;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("register_type")
    @Expose
    private String registerType;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("deadline")
    @Expose
    private String deadline;
    @SerializedName("detail")
    @Expose
    private String detail;
    @SerializedName("can_register")
    @Expose
    private Integer canRegister;

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

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRegisterType() {
        return registerType;
    }

    public void setRegisterType(String registerType) {
        this.registerType = registerType;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Integer getCanRegister() {
        return canRegister;
    }

    public void setCanRegister(Integer canRegister) {
        this.canRegister = canRegister;
    }

}
