package dev.wsgroup.main.models.apis.callers;

import android.app.Application;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.dtos.User;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.StringUtils;
import dev.wsgroup.main.views.dialogbox.DialogBoxAlert;

public class APIUserCaller {

    private static RequestQueue requestQueue;

    public static void logInWithUsernameAndPassword(String username, String password,
                                                    Application application, APIListener APIListener) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
            jsonObject.put("password", password);
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String message = response.getString("status");
                        JSONObject data = response.getJSONObject("data");
                        User user = User.getAccountFromJSON(data.getJSONObject("user"),
                                                            data.getJSONObject("info"));
                        user.setToken(data.getString("token"));
                        APIListener.onUserFound(user, message);
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
                    if (error.toString().contains("NoConnectionError")) {
                        APIListener.onFailedAPICall(IntegerUtils.ERROR_NO_CONNECTION);
                    } else if (error.toString().contains("AuthFailureError")) {
                        APIListener.onNoJSONFound();
                    } else {
                        APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
                    }
                }
            };

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                    StringUtils.LOGIN_URL, jsonObject, listener, errorListener);
            request.setRetryPolicy(new DefaultRetryPolicy(7000,
                    1, 2));
            requestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
            APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
        }
    }

    public static void loginWithGoogle(User user, Application application,
                                       APIListener APIListener) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("googleId", user.getGoogleId());
            jsonObject.put("firstName", user.getFirstName());
            jsonObject.put("lastName", user.getLastName());
            jsonObject.put("phone", user.getPhoneNumber());
            jsonObject.put("email", user.getMail());
            jsonObject.put("roleName", "Customer");
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String message = response.getString("status");
                        JSONObject data = response.getJSONObject("data");
                        User user = User.getAccountFromJSON(data.getJSONObject("user"),
                                                            data.getJSONObject("info"));
                        user.setToken(data.getString("token"));
                        APIListener.onUserFound(user, message);
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
                    APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
                }
            };
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                    StringUtils.LOGIN_GOOGLE_URL, jsonObject, listener, errorListener) {
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

    public static void registerNewUser(User user, Application application,
                                       APIListener APIListener) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", user.getUsername());
            jsonObject.put("password", user.getPassword());
            jsonObject.put("firstName", user.getFirstName());
            jsonObject.put("lastName", user.getLastName());
            jsonObject.put("phone", user.getPhoneNumber());
            jsonObject.put("email", user.getMail());
            jsonObject.put("roleName", "Customer");
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            JSONObject data = response.getJSONObject("data");
                            if (data != null) {
                                APIListener.onUpdateSuccessful();
                            }
                        } catch (JSONException e) {
                            APIListener.onFailedAPICall(IntegerUtils.ERROR_PARSING_JSON);
                            e.printStackTrace();
                        }
                    }
                }
            };

            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println(error);
                    System.out.println(MethodUtils.getVolleyErrorMessage(error));
                    APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
                }
            };
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                    StringUtils.REGISTER_URL, jsonObject, listener, errorListener) {
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

    public static void findUserByPhoneNumber(String phoneNumber, Application application,
                                             APIListener APIListener) {
        String url = StringUtils.USER_API_URL + phoneNumber;
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String message = response.getString("message");
                        JSONArray jsonArray = response.getJSONArray("data");
                        if (jsonArray.length() > 0) {
                            User user = new User();
                            user.setAccountId(jsonArray.getJSONObject(0)
                                                       .getString("id"));
                            APIListener.onUserFound(user, message);
                        } else {
                            APIListener.onFailedAPICall(IntegerUtils.ERROR_NO_USER);
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
                    APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
                }
            };

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
                    new JSONObject(), listener, errorListener);
            request.setRetryPolicy(new DefaultRetryPolicy(7000,
                    1, 2));
            requestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
            APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
        }
    }

    public static void findUserByMail(String mail, Application application,
                                      APIListener APIListener) {
        String url = StringUtils.BASE_URL + "api/supplier/existEmail?email=" + mail;
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String message = response.getString("message");
                        JSONArray jsonArray = response.getJSONObject("data")
                                .getJSONArray("cusData");
                        if (jsonArray.length() > 0) {
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            User user = new User();
                            user.setAccountId(jsonObject.getString("id"));
                            user.setGoogleId(jsonObject.getString("googleid"));
                            user.setPhoneNumber(jsonObject.getString("phone"));
                            APIListener.onUserFound(user, message);
                        } else {
                            APIListener.onFailedAPICall(IntegerUtils.ERROR_NO_USER);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        APIListener.onFailedAPICall(IntegerUtils.ERROR_PARSING_JSON);
                    }
                }
            };

            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println(error);
                    System.out.println(MethodUtils.getVolleyErrorMessage(error));
                    APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
                }
            };

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
                    new JSONObject(), listener, errorListener);
            request.setRetryPolicy(new DefaultRetryPolicy(7000,
                    1, 2));
            requestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
            APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
        }
    }

    public static void findUserByToken(String token, Application application,
                                       APIListener APIListener) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String message = response.getString("message");
                        JSONArray jsonArray = response.getJSONObject("data")
                                                      .getJSONArray("customerData");
                        if (jsonArray.length() > 0) {
                            User user = User.getObjectFromJSON(jsonArray.getJSONObject(0));
                            APIListener.onUserFound(user, message);
                        } else {
                            APIListener.onFailedAPICall(IntegerUtils.ERROR_NO_USER);
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

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                    StringUtils.GET_PROFILE_URL, new JSONObject(), listener, errorListener) {
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
            APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
        }
    }

    public static void updateUserProfile(User user, Application application,
                                         APIListener APIListener) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        String url = StringUtils.USER_API_URL + "customer";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("firstName", user.getFirstName());
            jsonObject.put("lastName", user.getLastName());
            jsonObject.put("email", user.getMail());
            jsonObject.put("avt", user.getAvatarLink());
            jsonObject.put("phone", user.getPhoneNumber());
            jsonObject.put("eWalletCode", user.getWalletCode());
            jsonObject.put("eWalletSecret", user.getWalletSecret());
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String result = response.getString("message");
                        if (result.equals("successful")) {
                            APIListener.onUpdateSuccessful();
                        } else {
                            APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        APIListener.onFailedAPICall(IntegerUtils.ERROR_PARSING_JSON);
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

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url,
                    jsonObject, listener, errorListener) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put("cookie", user.getToken());
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


    public static void updatePassword(String accountId, String password,
                                      Application application, APIListener APIListener) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        String url = StringUtils.USER_API_URL + "user/resetPassword";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("password", password);
            jsonObject.put("accountId", accountId);
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        int data = response.getInt("data");
                        if (data == 1) {
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
                    if (error.toString().contains("NoConnectionError")) {
                        APIListener.onFailedAPICall(IntegerUtils.ERROR_NO_CONNECTION);
                    } else {
                        APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
                    }
                }
            };

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url,
                    jsonObject, listener, errorListener) {

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
