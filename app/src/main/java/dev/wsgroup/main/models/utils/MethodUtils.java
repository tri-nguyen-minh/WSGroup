package dev.wsgroup.main.models.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import dev.wsgroup.main.models.dtos.CartProduct;
import dev.wsgroup.main.models.dtos.Supplier;

public class MethodUtils {

    public static String formatPriceString(double price) {
        NumberFormat format = new DecimalFormat("#,###");
        return format.format(price) + "d";
    }

    public static String formatOrderOrReviewCount(int count) {
        String countString = count + "";
        if(count > 9999) {
            countString = (count/1000) + "K";
        }
        return countString;
    }

    public static String formatPhoneNumber(String phone) {
        return phone.replaceFirst("(\\d{3})(\\d{3})(\\d+)", "$1 $2 $3");
    }

    public static String formatPhoneNumberWithCountryCode(String phone) {
        return "+84 " + phone.substring(1);
    }

    public static String revertPhoneNumber(String phone) {
        phone = phone.replaceAll("\\s", "");
        if (phone.contains("+84")) {
            phone = phone.substring(3);
        }
        return "0" + phone;
    }

    public static String formatDate(String dateString){
        String returnedDate = "";
        SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy");
        SimpleDateFormat dateParse = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            returnedDate = format.format(dateParse.parse(dateString));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return returnedDate;
    }

    public static String formatDateWithTime(String dateString){
        String returnedDate = "";
        SimpleDateFormat format = new SimpleDateFormat("HH:mm MMM dd, yyyy");
        SimpleDateFormat dateParse = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            returnedDate = format.format(dateParse.parse(dateString));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return returnedDate;
    }

    public static boolean checkDeliveredOrder(String status) {
        return (status.equals("delivered") || status.equals("completed"));
    }

    public static String displayStatus(String status) {
        switch (status) {
            case "created": {
                return "Ordered";
            }
            case "advanced": {
                return "Waiting";
            }
            default: {
                return status.substring(0, 1).toUpperCase() + status.substring(1);
            }
        }
    }

    public static boolean checkReturnedOrder(String status) {
        return status.equals("returned");
    }

    public static boolean checkCancelledOrder(String status) {
        return status.equals("cancelled");
    }

    public static void main(String[] args) {
        System.out.println(revertPhoneNumber("+8496 0029 29312"));
//        try {
//            String fullDate = "2022-10-01T12:42:10.000Z";
//            String date = "2021-08-01";
//            System.out.println(formatDateWithTime(fullDate));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
