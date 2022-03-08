package dev.wsgroup.main.models.utils;

public class StringUtils {
    public static final String BASE_URL = "https://server-wsg.herokuapp.com/";
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
    public static final String ADDRESS_API_URL = BASE_URL + "api/address/";
    public static final String REVIEW_API_URL = BASE_URL + "api/comment/";
    public static final String PAYMENT_API_URL = BASE_URL + "api/payment/";
    public static final String DISCOUNT_API_URL = BASE_URL + "api/customerdiscountcode/";

    public static final String APPLICATION_JSON = "application/json";

    public static final String PHONE_REGEX = "0[1-9][0-9]{8,9}";
    public static final String USERNAME_REGEX = "[a-zA-z0-9_]{6,12}";
    public static final String PASSWORD_REGEX = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*_-]).{8,20}$";
    public static final String MAIL_REGEX = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'" +
            "*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]" +
            "|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)" +
            "+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))" +
            "\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:" +
            "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]" +
            "|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

    public static final String MES_CONFIRM_LOG_OUT = "Sign out?";
    public static final String MES_CONFIRM_DELETE_CART = "Remove this product from\nyour shopping cart?";
    public static final String MES_CONFIRM_CHECKOUT = "Proceed to checkout?";
    public static final String MES_CONFIRM_IMMEDIATE_CHECKOUT = "Checkout immediately?";
    public static final String MES_CONFIRM_ORDER = "Confirm ordering products?";
    public static final String MES_CONFIRM_DUPLICATE_CART_PRODUCT = "This product is already\nin your shopping cart.\nReplace it?";
    public static final String MES_CONFIRM_ADD_REVIEW = "Submit your Review?";
    public static final String MES_CONFIRM_ADD_REVIEW_DESCRIPTION = "Your review cannot be changed or removed later.";
    public static final String MES_CONFIRM_ORDER_METHOD = "Confirm order's delivery and payment method?";
    public static final String MES_CONFIRM_CANCEL_ORDER = "Your order has not been processed.\nAre you sure you want to leave?";
    public static final String MES_CONFIRM_CANCEL_ORDER_DESC = "Your current order will be lost!";
    public static final String MES_CONFIRM_REQUIRE_PAYMENT = "Please complete the final payment to process the order!";
    public static final String MES_CONFIRM_REQUIRE_ADVANCE = "You must complete a required advanced payment before your order can be finalized.";
    public static final String MES_CONFIRM_COMPLETE_PAYMENT = "Complete payment for this order?";

    public static final String MES_ERROR_FAILED_API_CALL = "An error has occurred.\nPlease try again later.";
    public static final String MES_ERROR_FAILED_LOG_IN = "Your Username or Password is incorrect";
    public static final String MES_ERROR_DUPLICATE_USERNAME = "This username has already been taken.";
    public static final String MES_ERROR_DUPLICATE_NUMBER = "Your number is already connected to an account.";
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

    public static final String MES_ALERT_CAMPAIGN_CART_ORDER = "Order within Campaign can only be completed individually.";

    public static final String DES_INVALID_USERNAME = "Your Username should be:\n" +
            "";
    public static final String MES_SUCCESSFUL_REGISTRATION = "Your new account is ready!";
    public static final String MES_SUCCESSFUL_UPDATE_PASSWORD = "Your password has been reset!";
    public static final String MES_SUCCESSFUL_UPDATE_PROFILE = "Your profile has been updated!";
    public static final String MES_SUCCESSFUL_ORDER = "Your order has been processed!";
    public static final String MES_SUCCESSFUL_OTP = "Your number has been successfully confirmed!";
    public static final String MES_SUCCESSFUL_ADD_CART = "The product has been add to your shopping cart!";
    public static final String MES_SUCCESSFUL_ADD_REVIEW = "Your review has been submitted.";
    public static final String MES_SUCCESSFUL_PAYMENT = "Your payment has been completed.";
}
