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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.dtos.Address;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.StringUtils;

public class APIAddressCaller {

    private static RequestQueue requestQueue;
    private static String url;
    private static List<Address> addressList;

    public static void getAllAddress(String token, List<Address> list,
                                     Application application, APIListener APIListener) {
        url = StringUtils.ADDRESS_API_URL + "All";
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Address address;
                        JSONArray jsonArray = response.getJSONArray("data");
                        if (jsonArray.length() > 0) {
                            addressList = (list == null) ? new ArrayList<>() : list;
                            if (jsonArray.length() > 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    address = Address.getObjectFromJSON(jsonArray.getJSONObject(i));
                                    addressList.add(address);
                                }
                            }
                            APIListener.onAddressListFound(addressList);
                        } else {
                            APIListener.onAddressListFound(new ArrayList<>());
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
                    new JSONObject(), listener, errorListener) {

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

    public static void getDefaultAddress(String token,
                                         Application application, APIListener APIListener) {
        url = StringUtils.ADDRESS_API_URL + "default";
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Address address = null;
                        JSONArray jsonArray = response.getJSONArray("data");
                        if (jsonArray != null) {
                            if (jsonArray.length() > 0) {
                                address = Address.getObjectFromJSON(jsonArray.getJSONObject(0));
                            }
                        }
                        APIListener.onAddressFound(address);
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
                    new JSONObject(), listener, errorListener) {

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

    public static void AddAddress(String token, Address address,
                                  Application application, APIListener APIListener) {
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("province", address.getProvince());
            jsonObject.put("street", address.getStreet());
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject data = response.getJSONObject("data");
                        address.setId(data.getString("id"));
                        address.setDefaultFlag(data.getBoolean("isdefault"));
                        APIListener.onUpdateAddressSuccessful(address);
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
            JsonObjectRequest request
                    = new JsonObjectRequest(Request.Method.POST, StringUtils.ADDRESS_API_URL,
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

    public static void UpdateAddress(String token, Address address,
                                     Application application, APIListener APIListener) {
        url = StringUtils.ADDRESS_API_URL + address.getId();
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("province", address.getProvince());
            jsonObject.put("street", address.getStreet());
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.getInt("data") > 0) {
                            APIListener.onUpdateAddressSuccessful(address);
                        }
                        else {
                            APIListener.onFailedAPICall(IntegerUtils.ERROR_PARSING_JSON);
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
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url,
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

    public static void DeleteAddress(String token, Address address,
                                     Application application, APIListener APIListener) {
        url = StringUtils.ADDRESS_API_URL + address.getId();
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.getInt("data") > 0) {
                            APIListener.onUpdateAddressSuccessful(address);
                        }
                        else {
                            APIListener.onFailedAPICall(IntegerUtils.ERROR_PARSING_JSON);
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
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url,
                    new JSONObject(), listener, errorListener) {

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
}
