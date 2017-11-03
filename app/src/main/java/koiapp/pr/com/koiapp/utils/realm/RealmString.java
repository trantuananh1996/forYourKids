package koiapp.pr.com.koiapp.utils.realm;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by Tran Anh on 2/16/2017.
 */

public class RealmString extends RealmObject {
    @SerializedName("value")
    private String value;

    public RealmString() {
    }

    public RealmString(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
