
package koiapp.pr.com.koiapp.moduleChat.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChatUser {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("fullName")
    @Expose
    private String fullName;
    @SerializedName("typing")
    @Expose
    private Integer typing;

    public ChatUser() {
    }

    public ChatUser(String id, String fullName,  Integer typing) {
        this.id = id;
        this.fullName = fullName;
        this.typing = typing;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


    public Integer getTyping() {
        return typing;
    }

    public void setTyping(Integer typing) {
        this.typing = typing;
    }

}
