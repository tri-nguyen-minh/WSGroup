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
import dev.wsgroup.main.models.dtos.Message;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.StringUtils;

public class APIChatCaller {

    private static RequestQueue requestQueue;
    private static String url;
    private static List<Message> messageList;
    private static int count;

    public static void getCustomerChatMessages(String token, Application application,
                                               APIListener APIListener) {
        url = StringUtils.CHAT_API_URL + "chatMessage/customerId";
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        messageList = new ArrayList<>();
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
                                            date1 = MethodUtils.convertToDate(msg1.getCreateDate());
                                            date2 = MethodUtils.convertToDate(msg2.getCreateDate());
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
                    System.out.println(error);
                    System.out.println(MethodUtils.getVolleyErrorMessage(error));
                    if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
                        APIListener.onFailedAPICall(IntegerUtils.ERROR_NO_USER);
                    } else {
                        APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
                    }
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
            request.setRetryPolicy(new DefaultRetryPolicy(7000,
                    1, 2));
            requestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
            APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
        }
    }

    public static void getConversation(String token, String userId, String supplierId,
                                               Application application, APIListener APIListener) {
        url = StringUtils.CHAT_API_URL + "getChatMessage/SenderOrReceiver";
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            count = 2;
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (count == 2) {
                            messageList = new ArrayList<>();
                        }
                        count--;
                        JSONArray data = response.getJSONArray("data");
                        Message message;
                        if (data.length() > 0) {
                            for (int i = 0; i < data.length(); i++) {
                                message = Message.getObjectFromJSON(data.getJSONObject(i));
                                messageList.add(message);
                            }
                        }
                        if (count == 0) {
                            Collections.sort(messageList, new Comparator<Message>() {
                                @Override
                                public int compare(Message msg1, Message msg2) {
                                    Date date1 = null, date2 = null;
                                    if (msg1.getCreateDate() != null && msg2.getCreateDate() != null) {
                                        try {
                                            date1 = MethodUtils.convertToDate(msg1.getCreateDate());
                                            date2 = MethodUtils.convertToDate(msg2.getCreateDate());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    return date2.compareTo(date1);
                                }
                            });
                            APIListener.onMessageListFound(messageList);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        APIListener.onMessageListFound(new ArrayList<>());
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
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("from", userId);
            jsonObject.put("to", supplierId);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url,
                    jsonObject, listener, errorListener) {
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
            request.setRetryPolicy(new DefaultRetryPolicy(7000,
                    1, 2));
            requestQueue.add(request);
            jsonObject = new JSONObject();
            jsonObject.put("from", supplierId);
            jsonObject.put("to", userId);
            request = new JsonObjectRequest(Request.Method.POST, url,
                    jsonObject, listener, errorListener) {

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
            request.setRetryPolicy(new DefaultRetryPolicy(7000,
                    1, 2));
            requestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
            APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
        }
    }

    public static void updateReadMessages(String token, String userId, String supplierId,
                                       Application application, APIListener APIListener) {
        url = StringUtils.CHAT_API_URL + "updateStatusToRead";
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                        APIListener.onUpdateSuccessful();
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
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("from", userId);
            jsonObject.put("to", supplierId);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url,
                    jsonObject, listener, errorListener) {
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
            request.setRetryPolicy(new DefaultRetryPolicy(7000,
                    1, 2));
            requestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
            APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
        }
    }
}
