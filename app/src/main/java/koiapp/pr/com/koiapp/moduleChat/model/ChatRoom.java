
package koiapp.pr.com.koiapp.moduleChat.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChatRoom {

    @SerializedName("chatId")
    @Expose
    private String chatId;
    @SerializedName("createdAt")
    @Expose
    private Long createdAt;
    @SerializedName("lastSenderId")
    @Expose
    private Integer lastSenderId;
    @SerializedName("hasUnreadMessage")
    @Expose
    private Integer hasUnreadMessage;
   /* @SerializedName("users")
    @Expose
    private JsonObject users = null;

    private List<ChatUser> chatUser = null;

    @SerializedName("messages")
    @Expose
    private JsonObject messages;

    private List<ChatMessage> chatMessage = null;*/

    public ChatRoom() {
    }

    public ChatRoom(String chatId, Long createdAt) {
        this.chatId = chatId;
        this.createdAt = createdAt;
        this.hasUnreadMessage = 0;
        this.lastSenderId = 0;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getLastSenderId() {
        return lastSenderId;
    }

    public void setLastSenderId(Integer lastSenderId) {
        this.lastSenderId = lastSenderId;
    }

    public Integer getHasUnreadMessage() {
        return hasUnreadMessage;
    }

    public void setHasUnreadMessage(Integer hasUnreadMessage) {
        this.hasUnreadMessage = hasUnreadMessage;
    }

   /* public List<ChatUser> getChatUser() {
        chatUser = new ArrayList<>();
        if (users != null) {
            for (Map.Entry<String, JsonElement> entry : users.entrySet()) {
                chatUser.add(new Gson().fromJson(users.get(entry.getKey()), ChatUser.class));
            }
        }
        return chatUser;
    }

    public void setChatUser(List<ChatUser> chatUser) {
        this.chatUser = chatUser;
    }

    public List<ChatMessage> getChatMessage() {
        chatMessage = new ArrayList<>();
        if (messages != null) {
            for (Map.Entry<String, JsonElement> entry : messages.entrySet()) {
                chatMessage.add(new Gson().fromJson(messages.get(entry.getKey()), ChatMessage.class));
            }
        }
        return chatMessage;
    }

    public void setChatMessage(List<ChatMessage> chatMessage) {
        this.chatMessage = chatMessage;
    }

    public JsonObject getUsers() {
        return users;
    }

    public void setUsers(JsonObject users) {
        this.users = users;
    }

    public JsonObject getMessages() {
        return messages;
    }

    public void setMessages(JsonObject messages) {
        this.messages = messages;
    }*/
}
