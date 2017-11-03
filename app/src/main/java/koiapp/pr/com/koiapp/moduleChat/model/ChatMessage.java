
package koiapp.pr.com.koiapp.moduleChat.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import net.yazeed44.imagepicker.model.ImageEntry;

public class ChatMessage {

    @SerializedName("photo")
    @Expose
    private String photo = "";
    @SerializedName("senderId")
    @Expose
    private String senderId;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("time")
    @Expose
    private Long time;

    @SerializedName("unread")
    private Integer unread;

    private ImageEntry imageEntry;

    public ChatMessage() {
    }

    public ChatMessage(String senderId, String content, Long time) {
        this.senderId = senderId;
        this.content = content;
        this.time = time;
        this.unread = 1;
    }

    public ChatMessage(String senderId, String content, String photo, Long time) {
        this.senderId = senderId;
        this.content = content;
        this.time = time;
        this.photo = photo;
        this.unread = 1;

    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public ImageEntry getImageEntry() {
        return imageEntry;
    }

    public void setImageEntry(ImageEntry imageEntry) {
        this.imageEntry = imageEntry;
    }

    public Integer getUnread() {
        return unread;
    }

    public void setUnread(Integer unread) {
        this.unread = unread;
    }
}
