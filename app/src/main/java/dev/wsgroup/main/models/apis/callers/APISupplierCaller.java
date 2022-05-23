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

    public static void getSupplierById(String supplierId,
                                       Application application, APIListener APIListener) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        url = StringUtils.USER_API_URL + "supplier/" + supplierId;
        try {
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject data = response.getJSONObject("data");
                        supplierList = new ArrayList<>();
                        if (data != null) {
                            Supplier supplier = Supplier.getObjectFromJSON(data);
                            supplierList.add(supplier);
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

    public static void getCustomerLoyaltyStatus(String token, String supplierId,
                                                Application application, APIListener APIListener) {
        url = StringUtils.BASE_URL + "api/loyalcustomer/list/loyalCustomer?supplierId=" + supplierId;
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        List<LoyaltyStatus> list = new ArrayList<>();
                        LoyaltyStatus status;
                        JSONArray data = response.getJSONArray("data");
                        if (data.length() > 0) {
                            status = LoyaltyStatus.getObjectFromJSON(data.getJSONObject(0));
                            list.add(status);
                        }
                        APIListener.onLoyaltyStatusListFound(list);
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
            request.setRetryPolicy(new DefaultRetryPolicy(7000,
                    1, 2));
            requestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
            APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
        }
    }

    public static void getSupplierListByAccountId(Set<String> accountIdSet, List<Supplier> list,
                                                  Application application, APIListener APIListener) {
        if (requestQueue == null) {
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
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        supplierList = list == null ? new ArrayList<>() : list;
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
                    System.out.println(error);
                    System.out.println(MethodUtils.getVolleyErrorMessage(error));
                    APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
                }
            };

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url,
                    jsonObject, listener, errorListener);
            request.setRetryPolicy(new DefaultRetryPolicy(7000,
                    1, 2));
            requestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
            APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
        }
    }

    public static void getSupplierByIdList(Set<String> idSet,
                                           Application application, APIListener APIListener) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        url = StringUtils.BASE_URL + "api/supplier/getSuppInforByListSuppId";
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            for (String string : idSet) {
                jsonArray.put(string);
            }
            jsonObject.put("supplierIds", jsonArray);
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        List<Supplier> supplierList = new ArrayList<>();
                        Supplier supplier;
                        JSONArray data = response.getJSONArray("data");
                        if (data.length() > 0) {
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
                    System.out.println(error);
                    System.out.println(MethodUtils.getVolleyErrorMessage(error));
                    APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
                }
            };

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url,
                    jsonObject, listener, errorListener);
            request.setRetryPolicy(new DefaultRetryPolicy(5000,
                    1, 2));
            requestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
            APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
        }
    }
}
