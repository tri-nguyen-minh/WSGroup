package dev.wsgroup.main.models.utils;

public class StringUtils {
    public static final String BASE_URL = "http://13.215.133.39:3000/";
    public static final String USER_API_URL = BASE_URL + "api/users/";
    public static final String LOGIN_URL = USER_API_URL + "login";
    public static final String REGISTER_URL = USER_API_URL + "register";
    public static final String GET_PROFILE_URL = USER_API_URL + "profile/me";
    public static final String LOGIN_GOOGLE_URL = USER_API_URL + "login/google";
    public static final String PRODUCT_API_URL = BASE_URL + "api/products/";
    public static final String CATEGORY_API_URL = BASE_URL + "api/categories/";
    public static final String CART_API_URL = BASE_URL + "api/cart/";
    public static final String CAMPAIGN_API_URL = BASE_URL + "api/campaigns/";
    public static final String ORDER_API_URL = BASE_URL + "api/order/";
    public static final String ORDER_HISTORY_API_URL = BASE_URL + "api/history/";
    public static final String ADDRESS_API_URL = BASE_URL + "api/address/";
    public static final String REVIEW_API_URL = BASE_URL + "api/comment/";
    public static final String PAYMENT_API_URL = BASE_URL + "api/payment/";
    public static final String DISCOUNT_API_URL = BASE_URL + "api/customerdiscountcode/";
    public static final String CHAT_API_URL = BASE_URL + "api/chat/";
    public static final String NOTIFICATION_API_URL = BASE_URL + "api/notif/";
    public static final String DELIVERY_API_URL = "https://online-gateway.ghn.vn/shiip/public-api/";

    public static final String APPLICATION_JSON = "application/json";
    public static final String VIETNAM_COUNTRY_CODE = "+84";
    public static final String DELIVERY_API_TOKEN = "73457c0b-d0f5-11ec-ac32-0e0f5adc015a";
    public static final String DELIVERY_API_SHOP_ID = "2761103";
    public static final String EXCEPTION_API_NULL_ARRAY = "Value null at data of type org.json.JSONObject$1 cannot be converted to JSONArray";

    public static final String PHONE_REGEX = "\\+84[1-9][0-9]{7,9}";
    public static final String USERNAME_REGEX = "[a-zA-z0-9_]{6,20}";
    public static final String PASSWORD_REGEX = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,20}$";

    public static final String MES_CONFIRM_LOG_OUT = "Log out?";
    public static final String MES_CONFIRM_DELETE_CART = "Remove this product from\nyour shopping cart?";
    public static final String MES_CONFIRM_CHECKOUT = "Proceed to checkout?";
    public static final String MES_CONFIRM_IMMEDIATE_CHECKOUT = "Checkout immediately?";
    public static final String MES_CONFIRM_ORDER = "Confirm ordering products?";
    public static final String MES_CONFIRM_DUPLICATE_CART_PRODUCT = "This product is already\nin your shopping cart.\nReplace it?";
    public static final String MES_CONFIRM_ADD_REVIEW = "Submit your Review?";
    public static final String MES_CONFIRM_ORDER_METHOD = "Confirm order's delivery and payment method?";
    public static final String MES_CONFIRM_ABANDON_ORDER = "Your order has not been processed.\nAre you sure you want to leave?";
    public static final String MES_ALERt_REQUIRE_PAYMENT = "Please complete the final payment to process your order!";
    public static final String MES_CONFIRM_COMPLETE_PAYMENT = "Complete payment for this order?";
    public static final String MES_CONFIRM_COMPLETE_ORDER = "Set this order as completed?";
    public static final String MES_CONFIRM_SUBMIT_REQUEST = "Submit return request?";
    public static final String MES_CONFIRM_NOTIFICATION = "Mark all new notifications as read?";
    public static final String MES_CONFIRM_NO_WALLET = "Update your profile without E-Wallet?";

    public static final String MES_ERROR_FAILED_API_CALL = "An error has occurred.\nPlease try again later.";
    public static final String MES_ERROR_FAILED_LOG_IN = "Your Username or Password is incorrect";
    public static final String MES_ERROR_DUPLICATE_USERNAME = "This username has already been taken.";
    public static final String MES_ERROR_UNAVAILABLE_ACCOUNT = "This account is no longer available.\nPlease login with a different account.";
    public static final String MES_ERROR_DUPLICATE_NUMBER = "This phone number is already linked to an account.";
    public static final String MES_ERROR_DUPLICATE_MAIL = "This Gmail is already linked to an account.";
    public static final String MES_ERROR_NO_NUMBER_FOUND = "We cannot find any account with your number.";
    public static final String MES_ERROR_FIELD_EMPTY = "Please enter all the required fields.";
    public static final String MES_ERROR_INVALID_USERNAME = "Your username is invalid.";
    public static final String MES_ERROR_INVALID_NUMBER = "Your phone number is invalid.";
    public static final String MES_ERROR_INVALID_MAIL = "Your Email is invalid.";
    public static final String MES_ERROR_INVALID_PASSWORD = "Your password is invalid.";
    public static final String MES_ERROR_INCORRECT_CONFIRM_PASSWORD = "Please re-enter your password correctly.";
    public static final String MES_ERROR_INVALID_OTP = "The OTP is incorrect.";
    public static final String MES_ERROR_OUT_OF_STOCK = "There are not enough products in stock to make this purchase.";
    public static final String MES_ERROR_WRONG_OLD_PASSWORD = "Your old password is incorrect.";
    public static final String MES_ERROR_DUPLICATE_OLD_PASSWORD = "Your new password cannot be the same as your old password.";
    public static final String MES_ERROR_NO_DISCOUNT = "There is no applicable discount for this order.";
    public static final String MES_ERROR_PAYMENT_FAILED = "The payment has not been completed.";
    public static final String MES_ERROR_UNAVAILABLE_ADDRESS = "Your delivery address is currently unavailable.";

    public static final String MES_ALERT_ADVANCE_REQUIRE = "You must complete a required advanced payment before your order can be finalized.";
    public static final String MES_ALERT_OTP_REQUIRE = "We will send you an OTP to confirm your new phone number.";
    public static final String MES_ALERT_INVALID_ORDER = "Your order cannot be placed.";
    public static final String MES_ALERT_INVALID_FULL_ORDER = "Your order cannot be placed because some products are unavailable.";
    public static final String MES_ALERT_REVIEW_REMOVED = "This review has been disabled.";
    public static final String MES_ALERT_INCOMPLETE_ACCOUNT = "Your account is not ready.";
    public static final String MES_ALERT_INCOMPLETE_WALLET = "We need your E-Wallet information in order to process your refund.";
    public static final String MES_ALERT_ADD_REVIEW_DESCRIPTION = "Your review cannot be changed or removed later.";
    public static final String MES_ALERT_PHONE_LINKED_GOOGLE = "This number is linked to a Google Account.";

    public static final String DESC_PHONE_LINKED_GOOGLE = "Please login using your Google Account.";
    public static final String DESC_IRREVERSIBLE_ACTION = "This action cannot be undone.";
    public static final String DESC_ABANDON_ORDER = "Your current order will be lost!";
    public static final String DESC_INVALID_ORDER_SUPPLIER_SUSPENDED = "'s account has been suspended.";
    public static final String DESC_INVALID_ORDER_CAMPAIGN_COMPLETED = "The campaign you're joining is already over.";
    public static final String DESC_INVALID_ORDER_QUANTITY = "The quantity in this order exceeds the available amount.";
    public static final String DESC_INVALID_ORDER_PRODUCT = " is no longer available.";
    public static final String DESC_CART_REMOVAL = "Unavailable products have been removed from your cart.\nPlease try again.";
    public static final String DESC_REQUIRE_COMPLETE_ACCOUNT = "You must complete your account before you can log in.";
    public static final String DESC_UNAVAILABLE_ADDRESS = "Please select a different address.";

    public static final String MES_SUCCESSFUL_REGISTRATION = "Your new account is ready!";
    public static final String MES_SUCCESSFUL_UPDATE_PASSWORD = "Your password has been reset!";
    public static final String MES_SUCCESSFUL_UPDATE_PROFILE = "Your profile has been updated!";
    public static final String MES_SUCCESSFUL_UPDATE_ORDER = "Your order has been updated!";
    public static final String MES_SUCCESSFUL_ORDER = "Your order has been processed!";
    public static final String MES_SUCCESSFUL_OTP = "Your number has been successfully confirmed!";
    public static final String MES_SUCCESSFUL_ADD_CART = "The product has been add to your shopping cart!";
    public static final String MES_SUCCESSFUL_ADD_REVIEW = "Your review has been submitted.";
    public static final String MES_SUCCESSFUL_PAYMENT = "Your payment has been completed.";
    public static final String MES_SUCCESSFUL_ADD_ADDRESS = "The address has been added.";
    public static final String MES_SUCCESSFUL_UPDATE_ADDRESS = "The address has been updated.";
    public static final String MES_SUCCESSFUL_DELETE_ADDRESS = "The address has been deleted.";
    public static final String MES_SUCCESSFUL_REQUEST_SENT = "Return request has been submitted!";
}
