package datamodels;

/**
 * Created by Shamyyoun on 2/8/2015.
 */
public class Constants {
    // json response constants
    public static final String JSON_NULL_MSG = "null";
    public static final String JSON_FALSE_MSG = "false";
    public static final String JSON_SUCCESS_MSG = "success";
    public static final String JSON_USERNAME_EXISTS_MSG = "username_exists";

    // sp constants
    public static final String SP_RESPONSES = "responses";
    public static final String SP_KEY_ACTIVE_USER_RESPONSE = "active_user_response";
    public static final String SP_USER_CONFIG = "user_config";
    public static final String SP_KEY_CURRENT_LAT = "current_lat";
    public static final String SP_KEY_CURRENT_LONG = "current_long";

    // receivers request codes
    public static final int RECEIVER_LOCATION_UPDATER = 1;

    // notification request codes
    public static final int NOTIFICATION_OPEN_GPS = 1;

    // keys
    public static final String KEY_ACTIVE_USER = "active_user";
    public static final String KEY_OFFER = "offer";
    public static final String KEY_REQUEST = "request";
    public static final String KEY_SHOW_BACK = "show_back";
    public static final String KEY_CATEGORY_ID = "category_id";

    // push notification keys
    public static final String PUSHN_KEY_NEW_OFFER = "new_offer";
    public static final String PUSHN_KEY_REQUEST_ACCEPTED = "request_accepted";

    // activity request codes
    public static final int CODE_REQUEST_OFFER = 1;

    // transition views names
    public static final String TRANSITION_IMAGE = "image";

    // behaviour ids
    public static final String BEHAVIOUR_VIEW = "1";
    public static final String BEHAVIOUR_ADD = "3";
}
