package koiapp.pr.com.koiapp.moduleChat;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import koiapp.pr.com.koiapp.moduleChat.activity.FragmentChat;
import koiapp.pr.com.koiapp.model.User;
import koiapp.pr.com.koiapp.moduleChat.model.UserContact;
import koiapp.pr.com.koiapp.utils.realm.PrRealm;
import koiapp.pr.com.koiapp.utils.FragmentUtils;

/**
 * Created by Tran Anh
 * on 3/3/2017.
 */

public class ChatUtils {
    public static final String KEY_LAST_UPDATE = "lastUpdate";
    public static final String KEY_USERS = "users";
    public static final String KEY_MESSAGES = "messages";


    public Context mContext;
    private static volatile ChatUtils instance = null;

    public static ChatUtils getInstance(Context context) {
        if (instance == null) {
            synchronized (ChatUtils.class) {
                if (instance == null) {
                    instance = new ChatUtils(context);
                }
            }
        }
        return instance;
    }

    private ChatUtils(Context context) {
        mContext = context;
    }

    public static String createChatroomPath(String id1, String id2) {
        String path = "";
        if (id1!=null && id2!=null && id1.compareTo(id2)<0) {
            path += id1 + "_" + id2;
        } else {
            path += id2 + "_" + id1;
        }
        return path;
    }

    public static void gotoChat(UserContact contactItem, FragmentActivity activity) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("chats");
        if (!TextUtils.isEmpty(contactItem.getLastMessageKey())) {
            User user = PrRealm.getInstance(activity.getApplicationContext()).getCurrentUser();
            String myId = user == null?"":user.getuId();
            String otherId = contactItem.getuId();
            DatabaseReference chatRoomRef = rootRef.child(createChatroomPath(myId, otherId));
            chatRoomRef.child("messages").child(contactItem.getLastMessageKey()).child("unread").setValue(0);
        }
        FragmentChat fragmentChat = new FragmentChat();
        fragmentChat.setOtherName(contactItem.getName());
        fragmentChat.setOtherId(contactItem.getuId());
        fragmentChat.setOtherAvatar(contactItem.getPhotoUrl());
        FragmentUtils.addNewFragment(fragmentChat, contactItem.getName(), activity);
    }
    public static String createUserPath(String uId) {
        if (uId==null) return "";
        return  uId;
    }
}
