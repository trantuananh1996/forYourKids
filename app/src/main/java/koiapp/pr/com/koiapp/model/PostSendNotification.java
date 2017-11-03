package koiapp.pr.com.koiapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Tran Anh
 * on 6/15/2017.
 */

public class PostSendNotification {

    @SerializedName("data")
    @Expose
    private ItemNotification data;
    @SerializedName("registration_ids")
    @Expose
    private List<String> registrationIds = null;

    public ItemNotification getData() {
        return data;
    }

    public void setData(ItemNotification data) {
        this.data = data;
    }

    public List<String> getRegistrationIds() {
        return registrationIds;
    }

    public void setRegistrationIds(List<String> registrationIds) {
        this.registrationIds = registrationIds;
    }

}
