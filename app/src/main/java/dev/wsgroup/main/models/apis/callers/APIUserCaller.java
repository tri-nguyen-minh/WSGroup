package dev.wsgroup.main.models.apis.callers;

import android.app.Application;

import com.android.volley.AuthFailureError;
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
import dev.wsgroup.main.models.utils.StringUtils;

public class APIUserCaller {

    private static RequestQueue requestQueue;

    public static void findUserByUsernameAndPassword(String username, String password, Application application, APIListener APIListener) {
        if(requestQueue == null) {
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
                        JSONObject data = response.getJSONObject("data");
                        User user = User.getUserAccountFromJSON(data.getJSONObject("user"), data.getJSONObject("info"));
                        user.setToken(data.getString("token"));
                        APIListener.onUserFound(user);
                    } catch (Exception e) {
                        APIListener.onFailedAPICall(IntegerUtils.ERROR_PARSING_JSON);
                        e.printStackTrace();
                    }
                }
            };

            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error.toString().contains("NoConnectionError")) {
                        APIListener.onFailedAPICall(IntegerUtils.ERROR_NO_CONNECTION);
                    } else {
                        APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
                    }
                }
            };

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, StringUtils.LOGIN_URL,
                                                                jsonObject, listener, errorListener);
            requestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void registerNewUser(User user, Application application, APIListener APIListener) {
        if(requestQueue == null) {
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
                                APIListener.onCompletingRegistrationRequest();
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
                    APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
                }
            };
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, StringUtils.REGISTER_URL,
                    jsonObject, listener, errorListener) {
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

    public static void findUserByPhoneNumber(String phoneNumber, Application application, APIListener APIListener) {
        String url = StringUtils.USER_API_URL + phoneNumber;
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        JSONObject jsonObject = new JSONObject();
        try {
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray jsonArray = response.getJSONArray("data");
                        if(jsonArray.length() > 0) {
                            APIListener.onUserFound(new User());
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
                    APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
                }
            };

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
                    jsonObject, listener, errorListener);
            requestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void findUserByToken(String token, Application application, APIListener APIListener) {
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        JSONObject jsonObject = new JSONObject();
        try {
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        User user = User.getUserFromJSON(response.getJSONObject("data"));
                        APIListener.onUserFound(user);
                    } catch (Exception e) {
                        APIListener.onFailedAPICall(IntegerUtils.ERROR_PARSING_JSON);
                        e.printStackTrace();
                    }
                }
            };

            Response.ErrorListener errorListener = new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
                }
            };

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, StringUtils.GET_PROFILE_URL,
                    jsonObject, listener, errorListener) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put("cookie", token);
                    return header;
                }
            };
            requestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateUserProfile(User user, Application application, APIListener APIListener) {
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        String url = StringUtils.USER_API_URL + user.getUserId();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("password", user.getPassword());
            jsonObject.put("firstName", user.getFirstName());
            jsonObject.put("lastName", user.getLastName());
            jsonObject.put("email", user.getMail());
            Response.Listener<String> listener = new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    APIListener.onUpdateProfileSuccessful();
                }
            };

            Response.ErrorListener errorListener = new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
                }
            };

            StringRequest request = new StringRequest(Request.Method.PUT, url,
                    listener, errorListener) {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("password", user.getPassword());
                    params.put("firstName", user.getFirstName());
                    params.put("lastName", user.getLastName());
                    params.put("email", user.getMail());
                    return params;
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
