package dev.wsgroup.main.models.apis.callers;

import android.app.Application;

import com.android.volley.AuthFailureError;
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
import dev.wsgroup.main.models.dtos.Campaign;
import dev.wsgroup.main.models.dtos.Message;
import dev.wsgroup.main.models.dtos.Order;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.StringUtils;

public class APIChatCaller {

    private static RequestQueue requestQueue;
    private static String url;
    private static List<Message> messageList;

    public static void getCustomerChatMessages(String token, List<Message> list,
                                                Application application, APIListener APIListener) {
        url = StringUtils.CHAT_API_URL + "chatMessage/customerId";
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        messageList = (list == null) ? new ArrayList<>() : list;
                        JSONArray data = response.getJSONArray("data");
                        Message message;
                        if (data.length() > 0) {
                            for (int i = 0; i < data.length(); i++) {
                                message = Message.getObjectFromJSON(data.getJSONObject(i));
                                messageList.add(message);
                            }
                            Collections.sort(messageList, new Comparator<Message>() {
                                @Override
                                public int compare(Message msg1, Message msg2) {
                                    Date date1 = null, date2 = null;
                                    if (msg1.getCreateDate() != null && msg2.getCreateDate() != null) {
                                        try {
                                            date1 = MethodUtils.convertStringToDate(msg1.getCreateDate());
                                            date2 = MethodUtils.convertStringToDate(msg2.getCreateDate());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    return date1.compareTo(date2);
                                }
                            });
                        }
                        APIListener.onMessageListFound(messageList);
                    } catch (Exception e) {
                        e.printStackTrace();
                        APIListener.onMessageListFound(new ArrayList<>());
                    }
                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
                }
            };
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
                    new JSONObject(), listener, errorListener) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put("cookie", token);
                    return header;
                }
                @Override
                public String getBodyContentType() {
                    return StringUtils.APPLICATION_JSON;
                }
            };
            requestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
