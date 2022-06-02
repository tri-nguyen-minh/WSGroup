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

import com.android.volley.VolleyError;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.SQLOutput;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import dev.wsgroup.main.models.dtos.Address;
import dev.wsgroup.main.models.dtos.Campaign;
import dev.wsgroup.main.models.dtos.CampaignMilestone;
import dev.wsgroup.main.views.activities.MainActivity;
import dev.wsgroup.main.views.dialogbox.DialogBoxAlert;

public class MethodUtils {

    private static SharedPreferences sharedPreferences;
    private static GoogleSignInOptions options;
    private static final SimpleDateFormat dateParse
            = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public static String formatDecimalString(double weight) {
        NumberFormat format = new DecimalFormat("#.#");
        return format.format(weight);
    }

    public static String formatWeightString(double weight) {
        if (weight < 1) {
            weight *= 1000;
            return ((int) weight) + "g";
        } else {
            return formatDecimalString(weight) + "kg";
        }
    }

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
        return phone.replaceFirst("(\\d{2})(\\d{3})(\\d{3})(\\d+)", "$1 $2 $3 $4");
    }


    public static String formatPhoneWithCountryCode(String phone) {
        if (phone.startsWith(StringUtils.VIETNAM_COUNTRY_CODE)) {
            phone = phone.substring(3);
        }
        if (phone.startsWith("0")) {
            phone = phone.substring(1);
        }
        return StringUtils.VIETNAM_COUNTRY_CODE + phone.replaceAll("\\s", "");
    }

    public static String formatDate(String dateString, boolean withTime) {
        try {
            SimpleDateFormat format;
            if (withTime) {
                format = new SimpleDateFormat("HH:mm MMM dd, yyyy");
            } else {
                format = new SimpleDateFormat("MMM dd, yyyy");
            }
            return format.format(dateParse.parse(dateString));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date convertToDate(String dateString) throws Exception {
        return dateParse.parse(dateString);
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
                logoutAction(context, activity);
            }
        };
        dialogBoxAlert.show();
    }

    public static void logoutAction(Context context, Activity activity) {
        sharedPreferences = activity.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
        options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
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

    public static CampaignMilestone getReachedCampaignMilestone(List<CampaignMilestone> milestoneList,
                                                                int quantity) {
        int count = -1;
        for (CampaignMilestone milestone : milestoneList) {
            if (quantity >= milestone.getQuantity()) {
                count = milestoneList.indexOf(milestone);
            }
        }
        return (count < 0) ? null : milestoneList.get(count);
    }

    public static CampaignMilestone getLastCampaignMilestone(List<CampaignMilestone> milestoneList) {
        return milestoneList.get(milestoneList.size() - 1);
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

    public static List<Address> filterDistrictList(List<Address> addressList) {
        List<Address> resultList = new ArrayList<>();
        for (Address address : addressList) {
            if (!address.getDistrict().contains("Vật Tư")
                    && !address.getDistrict().contains("Đặc Biệt")) {
                resultList.add(address);
            }
        }
        return resultList;
    }

    public static String getVolleyErrorMessage(VolleyError error) {
        String body = "";
        if (error == null) {
            return "No error ";
        }
        if (error.networkResponse == null) {
            return "no response ";
        }
        String statusCode = String.valueOf(error.networkResponse.statusCode);
        try {
            body += statusCode + " - " + new String(error.networkResponse.data,"UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return body;
    }
}
