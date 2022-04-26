package dev.wsgroup.main.models.apis.callers;

import android.app.Application;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.dtos.Notification;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.StringUtils;

public class APINotificationCaller {

    private static RequestQueue requestQueue;
    private static String url;

    public static void getUsersNotifications(String token,
                                             Application application, APIListener APIListener) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            url = StringUtils.NOTIFICATION_API_URL + "getNotiForLoginUser";
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        List<Notification> notificationList = new ArrayList<>();
                        JSONArray array = response.getJSONArray("data");
                        Notification notification;
                        if (array.length() > 0) {
                            for (int i = 0; i < array.length(); i++) {
                                notification = Notification.getObjectFromJSON(array.getJSONObject(i));
                                notificationList.add(notification);
                            }
                        }
                        Collections.sort(notificationList, new Comparator<Notification>() {
                            @Override
                            public int compare(Notification not1, Notification not2) {
                                Date date1 = null, date2 = null;
                                if (not1.getCreatedDate() != null && not2.getCreatedDate() != null) {
                                    try {
                                        date1 = MethodUtils.convertToDate(not1.getCreatedDate());
                                        date2 = MethodUtils.convertToDate(not2.getCreatedDate());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                return date2.compareTo(date1);
                            }
                        });
                        APIListener.onNotificationListFound(notificationList);
                    } catch (Exception e) {
                        APIListener.onFailedAPICall(IntegerUtils.ERROR_PARSING_JSON);
                        e.printStackTrace();
                    }
                }
            };

            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println(error);
                    System.out.println(MethodUtils.getVolleyErrorMessage(error));
                    if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
                        APIListener.onFailedAPICall(IntegerUtils.ERROR_NO_USER);
                    } else {
                        APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
                    }
                }
            };

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                    url, new JSONObject(), listener, errorListener) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put("cookie", token);
                    return header;
                }
            };
            request.setRetryPolicy(new DefaultRetryPolicy(7000,
                    1, 2));
            requestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateReadNotification(String token, List<Notification> notificationList,
                                             Application application, APIListener APIListener) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            url = StringUtils.NOTIFICATION_API_URL + "updateNotif/notifs";
            JSONObject jsonObject = new JSONObject();
            JSONArray arrayString = new JSONArray();
            for (Notification notification : notificationList) {
                arrayString.put(notification.getId());
            }
            jsonObject.put("notifs", arrayString);
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String message = response.getString("message");
                        if (message.equals("successful")) {
                            APIListener.onUpdateSuccessful();
                        } else {
                            APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
                        }
                    } catch (Exception e) {
                        APIListener.onFailedAPICall(IntegerUtils.ERROR_PARSING_JSON);
                        e.printStackTrace();
                    }
                }
            };

            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println(error);
                    System.out.println(MethodUtils.getVolleyErrorMessage(error));
                    if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
                        APIListener.onFailedAPICall(IntegerUtils.ERROR_NO_USER);
                    } else {
                        APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
                    }
                }
            };

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT,
                    url, jsonObject, listener, errorListener) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put("cookie", token);
                    return header;
                }
            };
            request.setRetryPolicy(new DefaultRetryPolicy(7000,
                    1, 2));
            requestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
