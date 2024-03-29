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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.dtos.Address;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.StringUtils;

public class APIAddressCaller {

    private static RequestQueue requestQueue;
    private static String url;
    private static List<Address> addressList;

    public static void getAllAddress(String token,
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
                            addressList = new ArrayList<>();
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
            };
            request.setRetryPolicy(new DefaultRetryPolicy(7000,
                    1, 2));
            requestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
            APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
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
                        if (jsonArray != null && jsonArray.length() > 0) {
                            address = Address.getObjectFromJSON(jsonArray.getJSONObject(0));
                        }
                        APIListener.onAddressAPICompleted(address);
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
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
                    new JSONObject(), listener, errorListener) {

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

    public static void addAddress(String token, Address address,
                                  Application application, APIListener APIListener) {
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("provinceId", address.getProvinceId());
            jsonObject.put("province", address.getProvince());
            jsonObject.put("districtId", address.getDistrictId());
            jsonObject.put("district", address.getDistrict());
            jsonObject.put("wardId", address.getWardId());
            jsonObject.put("ward", address.getWard());
            jsonObject.put("street", address.getStreet());
            System.out.println();
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject data = response.getJSONObject("data");
                        address.setId(data.getString("id"));
                        address.setDefaultFlag(data.getBoolean("isDefault"));
                        APIListener.onAddressAPICompleted(address);
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
            request.setRetryPolicy(new DefaultRetryPolicy(7000,
                    1, 2));
            requestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
            APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
        }
    }

    public static void updateAddress(String token, Address address,
                                     Application application, APIListener APIListener) {
        url = StringUtils.ADDRESS_API_URL + address.getId();
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("provinceId", address.getProvinceId());
            jsonObject.put("province", address.getProvince());
            jsonObject.put("districtId", address.getDistrictId());
            jsonObject.put("district", address.getDistrict());
            jsonObject.put("wardId", address.getWardId());
            jsonObject.put("ward", address.getWard());
            jsonObject.put("street", address.getStreet());
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.getInt("data") > 0) {
                            APIListener.onAddressAPICompleted(address);
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

    public static void deleteAddress(String token, Address address,
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
                            APIListener.onAddressAPICompleted(address);
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
                    System.out.println(error);
                    System.out.println(MethodUtils.getVolleyErrorMessage(error));
                    if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
                        APIListener.onFailedAPICall(IntegerUtils.ERROR_NO_USER);
                    } else {
                        APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
                    }
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
            request.setRetryPolicy(new DefaultRetryPolicy(7000,
                    1, 2));
            requestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
            APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
        }
    }

    public static void getProvinceList(Application application, APIListener APIListener) {
        url = StringUtils.DELIVERY_API_URL + "master-data/province";
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray array = response.getJSONArray("data");
                        if (array.length() > 0) {
                            addressList = new ArrayList<>();
                            for (int i = 0; i < array.length(); i++) {
                                Address address = Address.getProvinceFromAPI(array.getJSONObject(i));
                                addressList.add(address);
                            }
                            APIListener.onAddressListFound(addressList);
                        } else {
                            APIListener.onNoJSONFound();
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
                    new JSONObject(), listener, errorListener) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put("token", StringUtils.DELIVERY_API_TOKEN);
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

    public static void getDistrictList(String provinceId,
                                       Application application, APIListener APIListener) {
        url = StringUtils.DELIVERY_API_URL + "master-data/district";
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("province_id", Integer.parseInt(provinceId));
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray array = response.getJSONArray("data");
                        if (array.length() > 0) {
                            addressList = new ArrayList<>();
                            Address address;
                            for (int i = 0; i < array.length(); i++) {
                                address = Address.getDistrictFromAPI(array.getJSONObject(i));
                                addressList.add(address);
                            }
                            addressList = MethodUtils.filterDistrictList(addressList);
                            APIListener.onAddressListFound(addressList);
                        } else {
                            APIListener.onNoJSONFound();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (e.getLocalizedMessage().equals(StringUtils.EXCEPTION_API_NULL_ARRAY)) {
                            APIListener.onAddressListFound(new ArrayList<>());
                        } else {
                            APIListener.onFailedAPICall(IntegerUtils.ERROR_PARSING_JSON);
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
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url,
                    jsonObject, listener, errorListener) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put("token", StringUtils.DELIVERY_API_TOKEN);
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

    public static void getWardList(String districtId,
                                   Application application, APIListener APIListener) {
        url = StringUtils.DELIVERY_API_URL + "master-data/ward";
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("district_id", Integer.parseInt(districtId));
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray array = response.getJSONArray("data");
                        if (array.length() > 0) {
                            addressList = new ArrayList<>();
                            Address address;
                            for (int i = 0; i < array.length(); i++) {
                                address = Address.getWardFromAPI(array.getJSONObject(i));
                                addressList.add(address);
                            }
                            APIListener.onAddressListFound(addressList);
                        } else {
                            APIListener.onNoJSONFound();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (e.getLocalizedMessage().equals(StringUtils.EXCEPTION_API_NULL_ARRAY)) {
                            APIListener.onAddressListFound(new ArrayList<>());
                        } else {
                            APIListener.onFailedAPICall(IntegerUtils.ERROR_PARSING_JSON);
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
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url,
                    jsonObject, listener, errorListener) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put("token", StringUtils.DELIVERY_API_TOKEN);
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
}
