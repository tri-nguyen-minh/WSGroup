package dev.wsgroup.main.models.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;

import com.android.volley.VolleyError;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import dev.wsgroup.main.models.dtos.Campaign;
import dev.wsgroup.main.models.dtos.CampaignMilestone;
import dev.wsgroup.main.models.services.FirebaseDatabaseReferences;
import dev.wsgroup.main.views.activities.MainActivity;
import dev.wsgroup.main.views.dialogbox.DialogBoxAlert;

public class MethodUtils {

    private static SharedPreferences sharedPreferences;
    private static GoogleSignInOptions options;

    public static String formatPriceString(double price) {
        NumberFormat format = new DecimalFormat("#,###");
        return format.format(price) + "VND";
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

    public static String formatDate(String dateString) {
        String returnedDate;
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
        String returnedDate;
        SimpleDateFormat format = new SimpleDateFormat("HH:mm MMM dd, yyyy");
        try {
            returnedDate = format.format(convertToDate(dateString));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return returnedDate;
    }

    public static Date convertToDate(String dateString) throws Exception {
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
                return "Waiting on Campaign";
            }
            default: {
                return status.substring(0, 1).toUpperCase() + status.substring(1);
            }
        }
    }

    public static String getStatusChangedReason(String description) {
        if (description.indexOf(": ") > 0) {
            return description.substring(description.indexOf(": ") + 2);
        }
        return "";
    }

    public static Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images
                .Media.insertImage(context.getContentResolver(), bitmap, "image", null);
        return Uri.parse(path);
    }

    public static String getFirebaseImageProofLink(String uuid) {
        return "https://firebasestorage.googleapis.com/" +
                "v0/b/wsg-authen-144ba.appspot.com/o/images%2Fproof%2F" + uuid + "?alt=media";
    }

    public static String getVNPayRef(String url) {
        String vnpRef;
        vnpRef = url.substring(url.indexOf("vnp_TxnRef="), url.indexOf("&vnp_SecureHash"));
        vnpRef = vnpRef.substring(vnpRef.indexOf("=") + 1);
        return vnpRef;
    }

    public static String getVNPayResponseCode(String url) {
        String responseCode;
        responseCode = url.substring(url.indexOf("vnp_ResponseCode="), url.indexOf("&vnp_TmnCode"));
        responseCode = responseCode.substring(responseCode.indexOf("=") + 1);
        return responseCode;
    }

    public static String getFormattedCurrentDate() {
        SimpleDateFormat dateParse = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return dateParse.format(new Date());
    }

    public static void displayErrorAPIMessage(Activity activity) {
        DialogBoxAlert dialogBoxAlert = new DialogBoxAlert(activity,
                IntegerUtils.CONFIRM_ACTION_CODE_FAILED, StringUtils.MES_ERROR_FAILED_API_CALL);
        dialogBoxAlert.show();
    }

    public static void displayErrorAccountMessage(Context context, Activity activity) {
        DialogBoxAlert dialogBoxAlert = new DialogBoxAlert(activity,
                IntegerUtils.CONFIRM_ACTION_CODE_ALERT, StringUtils.MES_ERROR_UNAVAILABLE_ACCOUNT) {
            @Override
            public void onClickAction() {
                super.onClickAction();
                logoutAction(context, activity);
            }
        };
        dialogBoxAlert.show();
    }

    public static void logoutAction(Context context, Activity activity) {
        sharedPreferences = activity.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        System.out.println("id: " + sharedPreferences.getString("USER_ID", ""));
        sharedPreferences.edit().clear().commit();
        System.out.println("clear: " + sharedPreferences.getString("USER_ID", ""));
        options = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        GoogleSignIn.getClient(context, options).signOut();
        activity.startActivity(new Intent(context, MainActivity.class));
    }

    public static void hideKeyboard(View view, Context context) {
        InputMethodManager inputMethodManager
                = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean checkCampaignEndDate(Campaign campaign) throws Exception {
        SimpleDateFormat dateParse = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 3);
        Date currentDate = cal.getTime();
        return dateParse.parse(campaign.getEndDate()).before(currentDate);
    }

    public static int checkCampaignQuantity(Campaign campaign) {
        int orderedQuantity = (int)(campaign.getMinQuantity() * 0.8);
        return campaign.getQuantityCount() - orderedQuantity;
    }

    public static CampaignMilestone getReachedCampaignMilestone(List<CampaignMilestone> milestoneList, int quantity) {
        int count = -1;
        for (CampaignMilestone milestone : milestoneList) {
            if (quantity >= milestone.getQuantity()) {
                count = milestoneList.indexOf(milestone);
            }
        }
        if (count < 0) {
            return null;
        }
        return milestoneList.get(count);
    }

    public static void sortSharingCampaign(List<Campaign> list) {
        Collections.sort(list, new Comparator<Campaign>() {
            @Override
            public int compare(Campaign campaign1, Campaign campaign2) {
                int result = campaign2.getQuantityCount() - campaign1.getQuantityCount();
                if (result != 0) {
                    return result;
                }
                result = campaign1.getMilestoneList().size() - 1;
                double price1 = campaign1.getMilestoneList().get(result).getPrice()
                        / campaign1.getProduct().getRetailPrice();
                result = campaign2.getMilestoneList().size() - 1;
                double price2 = campaign2.getMilestoneList().get(result).getPrice()
                        / campaign2.getProduct().getRetailPrice();
                if ((price2 - price1) != 0) {
                    return (price1 > price2) ? 1 : -1;
                }
                Date date1 = null, date2 = null;
                if (campaign1.getEndDate() != null && campaign2.getEndDate() != null) {
                    try {
                        date1 = MethodUtils.convertToDate(campaign1.getEndDate());
                        date2 = MethodUtils.convertToDate(campaign2.getEndDate());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return date2.compareTo(date1);
            }
        });
    }

    public static void sortSingleCampaign(List<Campaign> list) {
        Collections.sort(list, new Comparator<Campaign>() {
            @Override
            public int compare(Campaign campaign1, Campaign campaign2) {
                Date date1 = null, date2 = null;
                if (campaign1.getEndDate() != null && campaign2.getEndDate() != null) {
                    try {
                        date1 = MethodUtils.convertToDate(campaign1.getEndDate());
                        date2 = MethodUtils.convertToDate(campaign2.getEndDate());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (date2.compareTo(date1) != 0) {
                    return date2.compareTo(date1);
                }
                double price1 = campaign1.getPrice() / campaign1.getProduct().getRetailPrice();
                double price2 = campaign2.getPrice() / campaign2.getProduct().getRetailPrice();
                return (price1 > price2) ? 1 : -1;
            }
        });
    }

    public static String getVolleyErrorMessage(VolleyError error) {
        String body = "";
        if (error == null) {
            return   "No error ";
        }
        if (error.networkResponse == null) {
            return  "no response ";
        }
        final String statusCode = String.valueOf(error.networkResponse.statusCode);
        try {
            body += statusCode + " - " + new String(error.networkResponse.data,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
        }
        return body;
    }


    public static void main(String[] args) throws Exception {
        String phone = "new code: SALEOFF";
        FirebaseDatabaseReferences references = new FirebaseDatabaseReferences();
        references.getCustomerServiceId().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String key = ds.getKey();
                    System.out.println(key);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });


        System.out.println("test"+phone.substring(phone.indexOf(":") + 1));
//        System.out.println(EmailValidator.getInstance().isValid(phone));
    }
}
