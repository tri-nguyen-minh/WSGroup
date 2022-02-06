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

    public static String convertPriceString(double price) {
        NumberFormat format = new DecimalFormat("#,###");
        return format.format(price) + "d";
    }

    public static String convertOrderOrReviewCount(int count) {
        String countString = count + "";
        if(count > 9999) {
            countString = (count/1000) + "K";
        }
        return countString;
    }

    public static String formatPhoneNumber(String phone) {
        return phone.replaceFirst("(\\d{3})(\\d{3})(\\d+)", "$1 $2 $3");
    }

    public static String formatDate(String dateString){
        String returnedDate = "";
        SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy");
        SimpleDateFormat dateParse = new SimpleDateFormat("yyyy-dd-MM'T'HH:mm:ss.SSS'Z'");
        try {
            returnedDate = format.format(dateParse.parse(dateString));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return returnedDate;
    }

    public static void main(String[] args) {
        try {
            String fullDate = "2022-10-01T00:00:00.000Z";
            String date = "2021-08-01";
            System.out.println(formatDate(fullDate));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
