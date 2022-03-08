package dev.wsgroup.main.models.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

import com.android.volley.VolleyError;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
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
import dev.wsgroup.main.models.dtos.Review;
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

    public static Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "image", null);
        return Uri.parse(path);
    }

    public static String getVNPayRef(String url) {
        String vnpRef = "";
        vnpRef = url.substring(url.indexOf("vnp_TxnRef="), url.indexOf("&vnp_SecureHash"));
        vnpRef = vnpRef.substring(vnpRef.indexOf("=") + 1);
        return vnpRef;
    }

    public static String getVolleyErrorMessage(VolleyError error) {
        String body = "";
        if (error == null) {
            return   "No error ";
        }
        if (error.networkResponse == null) {
            return  "no response ";
        }

        //get status code here
        final String statusCode = String.valueOf(error.networkResponse.statusCode);
        //get response body and parse with appropriate encoding
        try {
            body += statusCode + " - " + new String(error.networkResponse.data,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
        }
        return body;
    }

    public static void main(String[] args) {
        try {
//            List<Review> reviews = new ArrayList<>();
//            Review review = new Review();
//            review.setCreateDate("2022-02-12T14:36:49.958Z");
//            review.setRating(1);
//            reviews.add(review);
//            review = new Review();
//            review.setCreateDate("2022-01-27T17:25:59.996Z");
//            review.setRating(3);
//            reviews.add(review);
//            review = new Review();
//            review.setCreateDate("2022-02-12T07:05:33.288Z");
//            review.setRating(4.5);
//            reviews.add(review);
//            Collections.sort(reviews, new Comparator<Review>() {
//                @Override
//                public int compare(Review review1, Review review2) {
//                    Date date1 = null, date2 = null;
//                    int result = 0;
//                    if (review1.getCreateDate() != null && review2.getCreateDate() != null) {
//                        try {
//                            date1 = MethodUtils.convertStringToDate(review1.getCreateDate());
//                            date2 = MethodUtils.convertStringToDate(review2.getCreateDate());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        result = date1.compareTo(date2);
//                    }
//                    return result;
//                }
//            });
//            System.out.println("By Date");
//            for (Review r : reviews) {
//                System.out.println(r.getRating() + " - " + formatDateWithTime(r.getCreateDate()));
//            }
//            Collections.sort(reviews, new Comparator<Review>() {
//                @Override
//                public int compare(Review review1, Review review2) {
//                    return review1.getRating() > review2.getRating() ? -1 :
//                            (review1.getRating() < review2.getRating() ? 1 : 0);
//                }
//            });
//            System.out.println("By Rating");
//            for (Review r : reviews) {
//                System.out.println(r.getRating() + " - " + formatDateWithTime(r.getCreateDate()));
//            }
            String url = "https://server-wsg.herokuapp.com/api/order/payment?order_id=5f9c07de-699b-417d-b5e9-cfc345412da1&vnp_Amount=40000000&vnp_BankCode=NCB&vnp_BankTranNo=20220307123714&vnp_CardType=ATM&vnp_OrderInfo=topup+tesst&vnp_PayDate=20220307123709&vnp_ResponseCode=00&vnp_TmnCode=REKSYL10&vnp_TransactionNo=13700330&vnp_TransactionStatus=00&vnp_TxnRef=050350&vnp_SecureHash=982baa2e7c9cd6b60e3c87db510f6ecceb62a6f0c71b7fc1a3ee10364520d8dd5c109687e245ad83e7467effb97ef57534622028968c01a9193b63f2e30996d2";
            System.out.println(getVNPayRef(url));
//            List<String> sortData = new ArrayList<>();
//            sortData.add("2022-02-12T14:36:49.958Z");
//            sortData.add("2022-02-11T22:33:43.396Z");
//            sortData.add("2022-01-27T17:25:59.996Z");
//            sortData.add("2022-02-12T12:05:33.288Z");
//            Collections.sort(sortData, new Comparator<String>() {
//                @Override
//                public int compare(String s1, String s2) {
//                    Date date1 = null, date2 = null;
//                    try {
//                        date1 = convertStringToDate(s1);
//                        date2 = convertStringToDate(s2);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    if (false) {
//                        return date1.compareTo(date2);
//                    } else {
//                        return date2.compareTo(date1);
//                    }
//                }
//            });
//            for (String s : sortData) {
//                System.out.println(s);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
