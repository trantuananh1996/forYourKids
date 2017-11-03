package koiapp.pr.com.koiapp.utils;

import android.Manifest;

/**
 * Created by Tran Anh
 * on 9/27/2016.
 */
public class Constants {
    public static final String URL_BASE = "http://dev.ko.edu.vn/";
    public static final String URL_BASE_KOMT = "http://komt.kidsonline.edu.vn/";

    /**
     * Key for notification
     */
    public static final String KEY_NOTIFICATION_TYPE = "notification_type";
    public static final String KEY_NOTIFICATION_TITLE = "title";
    public static final String KEY_NOTIFICATION_POST_ID = "post_id";
    public static final String KEY_NOTIFICATION_ALBUM_ID = "album_id";
    public static final String KEY_NOTIFICATION_DATE = "date";
    public static final String KEY_NOTIFICATION_STUDENT_ID = "student_id";
    public static final String KEY_NOTIFICATION_CLASS_ID = "class_id";
    public static final String KEY_NOTIFICATION_SCHOOL_ID = "school_id";
    public static final String KEY_NOTIFICATION_NOTE_ID = "note_id";
    public static final int NOTIFICATION_TYPE_SCHOOL_POST = 1;
    public static final int NOTIFICATION_TYPE_CLASS_POST = 2;
    public static final int NOTIFICATION_TYPE_CLASS_ALBUM = 3;
    public static final int NOTIFICATION_TYPE_STUDENT_ASSESSMENT = 4;
    public static final int NOTIFICATION_TYPE_STUDENT_HYGENIC = 5;
    public static final int NOTIFICATION_TYPE_RESIGNED_ACCEPTED = 6;
    public static final int NOTIFICATION_TYPE_NEW_HW = 7;
    public static final int NOTIFICATION_TYPE_NEW_SICK_HISTORY = 8;
    public static final int NOTIFICATION_TYPE_MEDICINE_ACCEPTED = 9;
    public static final int NOTIFICATION_TYPE_STUDENT_MEDICINE = 10;
    public static final int NOTIFICATION_TYPE_SCHOOL_ALBUM = 11;
    public static final int NOTIFICATION_TYPE_STUDENT_LEARN = 12;
    public static final int NOTIFICATION_TYPE_STUDENT_SLEEP = 13;
    public static final int NOTIFICATION_TYPE_NEW_BILL = 14;
    public static final int NOTIFICATION_TYPE_NEW_EXTRA = 15;
    public static final int NOTIFICATION_TYPE_BEGINNING_DAY_MESS_ACCEPTED = 16;
    public static final int NOTIFICATION_TYPE_BEGINNING_DAY_MESS_REPLIED = 17;
    public static final int NOTIFICATION_TYPE_REGISTER_EXTRA_ACCEPTED = 18;
    public static final int NOTIFICATION_TYPE_NEW_MESSAGE = 60;

    /**
     * Constants for modules
     */
    public static final Integer MODULE_POST = 1;
    public static final Integer MODULE_DAILY_ACTIVITY = 2;
    public static final Integer MODULE_CHILD_DIARY = 3;
    public static final Integer MODULE_ALBUM = 4;
    public static final Integer MODULE_MEDICINE_NOTE = 5;
    public static final Integer MODULE_RESIGNED = 6;
    public static final Integer MODULE_HEIGHT_WEIGHT = 7;
    public static final Integer MODULE_SICK_HISTORY = 8;
    public static final Integer MODULE_GENERAL_HEALTH = 9;
    public static final Integer MODULE_REGISTER_EAT = 10;
    public static final Integer MODULE_REGISTER_TRANSFER = 11;
    public static final Integer MODULE_REGISTER_EXTRA = 12;
    public static final Integer MODULE_BILLS = 13;
    public static final Integer MODULE_TEACHER_INFO = 14;
    public static final Integer MODULE_MESSAGE = 15;
    public static final Integer MODULE_CAMERA = 16;
    public static final Integer MODULE_MORNING_MESSAGE = 17;


    public static final String PREF_KEY_CUURENT_STUDENT_ID = "current_student_id";

    public static final String PREF_KEY_STORED_NOTIFICATION = "stored_notification";
    public static final String KEY_NOTIFICATION_ID = "notification_id";


    public static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
    public static final int SELECT_PICTURE_GALLERY = 1;
    public static final int PIC_CROP = 3;
    public static final int SELECT_PICTURE_CAMERA = 2;
    public static final int REQUEST_EXTERNAL_STORAGE = 1;
    public static final String PREF_KEY_INVALIDATE_TIME = "img_invalidate_time";
    public static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    //* TAG RESPONSE FROM SERVER*/
    public static final String TAG_DATA = "data";
    public static final String TAG_JSON_STATUS = "status";
    public static final String TAG_JSON_DATA = "data";
    public static final String TAG_JSON_VERSION = "version";

    public static final int CATEGORY_EXTRACURRICULAR = 1;
    public static final int CATEGORY_TRANSPORT = 2;
    public static final int CATEGORY_EAT = 3;

    public static final int REGISTER_ONE_TIME = 1;
    public static final int REGISTER_BY_TURN = 4;


    /**
     * Preference
     */
    public static final String PREF_NAME = "saved_state";
    public static final String PREF_KEY_MEDICINE_USER = "curr_user_med_ava";
    public static final String PREF_KEY_AUTHORIZATION = "autho";
    public static final String PREF_KEY_SCHOOL_ID = "school_id";
    public static final String PREF_KEY_CLASS_ID = "class_id";
    public static final String PREF_KEY_CURRENT_MEDICINE_ID = "curr_med_id";
    public static final String PREF_KEY_EMAIL = "email";
    public static final String PREF_KEY_CURRENT_STUDENT_ID = "curr_student_id";
    public static final String PREF_NAME_LOGIN_DATA = "stored_login_data";
    public static final String PREF_KEY_STUDENT_DATA = "student_data";
    public static final String PREF_KEY_SCHOOL_DATA = "school_data";
    public static final String PREF_KEY_CLASS_DATA = "class_data";
    public static final String PREF_KEY_AUTO_LOGIN = "auto_login";
    public static final String PREF_KEY_CAN_CHAT = "can_chat";
    public static final String PREF_KEY_CURRENT_USER_ID = "curr_user_id";
    public static final String PREF_KEY_LAST_TIME_GET_NOTIFICATION = "lt_get_noti";

    /**
     * Token
     */
    public static final String KEY_TOKEN = "token";
    /**
     * Login
     */
    public static final String KEY_SAVE_PASSWORD = "save_password";
    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_FULL_NAME = "full_name";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_AVATAR = "user_avatar";
    public static final String KEY_USER_EMAIL = "user_email";
    public static final String KEY_ALL_STUDENT = "all_student";
    public static final String KEY_USERNAME = "username";

    /**
     * Settings
     */
    public static final String KEY_DISPAY_VIEW_STATUS = "view_status";
    public static final String KEY_BIRTHDAY_REMIND = "birthday_remind";
    public static final String KEY_SOUND = "sound";
    public static final String KEY_VIBRATE = "vibrate";
}
