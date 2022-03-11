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

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.dtos.LoyaltyStatus;
import dev.wsgroup.main.models.dtos.Supplier;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.StringUtils;

public class APISupplierCaller {

    private static RequestQueue requestQueue;
    private static String url;
    private static List<Supplier> supplierList;


    public static void getSupplierById(String token, String supplierId,
                                       Application application, APIListener APIListener) {
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        url = StringUtils.USER_API_URL + "supplier/" + supplierId;
        try {
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject data = response.getJSONObject("data");
                        Supplier supplier = Supplier.getObjectFromJSON(data);
                        APIListener.onSupplierFound(supplier);
                    } catch (Exception e) {
                        e.printStackTrace();
                        APIListener.onFailedAPICall(IntegerUtils.ERROR_PARSING_JSON);
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



    public static void getCustomerLoyaltyStatus(String token, String supplierId,
                                                Application application, APIListener APIListener) {
        url = StringUtils.BASE_URL + "api/loyalcustomer/list/loyalCustomer?supplierId=" + supplierId;
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        LoyaltyStatus status = null;
                        JSONArray data = response.getJSONArray("data");
                        if (data.length() > 0) {
                            status = LoyaltyStatus.getObjectFromJSON(data.getJSONObject(0));
                        }
                        APIListener.onLoyaltyStatusFound(status);
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

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url,
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

    public static void getSupplierListByAccountId(Set<String> accountIdSet, List<Supplier> list,
                                                  Application application, APIListener APIListener) {
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        url = StringUtils.USER_API_URL + "getListAccountIdBySupplierId";
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            for (String string : accountIdSet) {
                jsonArray.put(string);
            }
            jsonObject.put("listAccountIds", jsonArray);
            System.out.println(jsonObject);
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        supplierList = (list == null) ? new ArrayList<>() : list;
                        JSONArray data = response.getJSONArray("data");
                        if (data.length() > 0) {
                            Supplier supplier;
                            for (int i = 0; i < data.length(); i++) {
                                supplier = Supplier.getObjectFromJSON(data.getJSONObject(i));
                                supplierList.add(supplier);
                            }
                        }
                        APIListener.onSupplierListFound(supplierList);
                    } catch (Exception e) {
                        e.printStackTrace();
                        APIListener.onFailedAPICall(IntegerUtils.ERROR_PARSING_JSON);
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

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
                    jsonObject, listener, errorListener);
            requestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
