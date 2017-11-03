package koiapp.pr.com.koiapp.utils.realm;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by Tran Anh on 2/16/2017.
 */

public class RealmInteger extends RealmObject {
    @SerializedName("value")
    private Integer value;

    public RealmInteger() {
    }

    public RealmInteger(Integer integer) {
        this.value = integer;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
