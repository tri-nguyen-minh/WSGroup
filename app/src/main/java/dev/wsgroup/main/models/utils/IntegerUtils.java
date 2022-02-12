package dev.wsgroup.main.models.utils;

public class IntegerUtils {
    public static final int REQUEST_COMMON = 1;
    public static final int REQUEST_LOGIN = 1;
    public static final int REQUEST_LOGIN_FOR_CART = 2;
    public static final int REQUEST_SELECT_ADDRESS = 2;
    public static final int REQUEST_INSTANT_CHECKOUT = 2;
    public static final int REQUEST_NOTE_READ_ONLY = 2;
    public static final int REQUEST_ORDER_AFTER_CHECKOUT = 2;

    public static final int ERROR_API = 1;
    public static final int ERROR_PARSING_JSON = 2;
    public static final int ERROR_NO_USER = 3;
    public static final int ERROR_NO_CONNECTION = 4;
    public static final int ERROR_OTP_REQUEST_INVALID = 1;
    public static final int ERROR_OTP_REQUEST_FULL = 2;

    public static final int REQUEST_REGISTER = 1;
    public static final int REQUEST_CHANGE_PASSWORD = 2;

    public static final int ADDRESS_ACTION_UPDATE = 1;
    public static final int ADDRESS_ACTION_DELETE = 2;

    public static final int CONFIRM_ACTION_CODE_SUCCESS = 1;
    public static final int CONFIRM_ACTION_CODE_FAILED = 2;
    public static final int CONFIRM_ACTION_CODE_ALERT = 3;

    public static final int IDENTIFIER_PRODUCT_TYPE_COMMON = 1;
    public static final int IDENTIFIER_PRODUCT_TYPE_SELECTED = 2;

    public static final int CAMPAIGN_LIST_VIEW = 1;
    public static final int CAMPAIGN_LIST_SELECT = 1;
}
