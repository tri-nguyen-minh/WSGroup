package dev.wsgroup.main.models.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
            returnedDate = format.format(convertStringToDate(dateString));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return returnedDate;
    }

    public static Date convertStringToDate(String dateString) throws Exception {
        SimpleDateFormat dateParse = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return dateParse.parse(dateString);
    }

    public static boolean checkDeliveredOrder(String status) {
        return (status.equals("delivered") || status.equals("completed"));
    }

    public static boolean checkReturnedOrder(String status) {
        return status.equals("returned");
    }

    public static boolean checkCancelledOrder(String status) {
        return status.equals("cancelled");
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

    public static void main(String[] args) {
        try {
            List<String> sortData = new ArrayList<>();
            sortData.add("2022-02-12T14:36:49.958Z");
            sortData.add("2022-02-11T22:33:43.396Z");
            sortData.add("2022-01-27T17:25:59.996Z");
            sortData.add("2022-02-12T12:05:33.288Z");
            Collections.sort(sortData, new Comparator<String>() {
                @Override
                public int compare(String s1, String s2) {
                    Date date1 = null, date2 = null;
                    try {
                        date1 = convertStringToDate(s1);
                        date2 = convertStringToDate(s2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (false) {
                        return date1.compareTo(date2);
                    } else {
                        return date2.compareTo(date1);
                    }
                }
            });
            for (String s : sortData) {
                System.out.println(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
