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

import java.util.HashMap;
import java.util.Map;

import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.dtos.Address;
import dev.wsgroup.main.models.dtos.Order;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.StringUtils;

public class APIPaymentCaller {

    private static RequestQueue requestQueue;
    private static String url;

    public static void getPaymentURL(Order order, double price, String bankString,
                                     String orderDescription, String orderType,
                                     Application application, APIListener APIListener) {
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("amount", (price * 100));
            jsonObject.put("bankCode", bankString);
            jsonObject.put("orderDescription", orderDescription);
            jsonObject.put("orderType", orderType);
            jsonObject.put("orderId", order.getId());
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String url = response.getString("data");
                        APIListener.onResultStringFound(url);
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
                    StringUtils.PAYMENT_API_URL, jsonObject, listener, errorListener) {

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

    public static void getShippingService(String fromDistrict, String toDistrict,
                                          Application application, APIListener APIListener) {
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        url = StringUtils.DELIVERY_API_URL + "v2/shipping-order/available-services";
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("shop_id", Integer.parseInt(StringUtils.DELIVERY_API_SHOP_ID));
            jsonObject.put("from_district", Integer.parseInt(fromDistrict));
            jsonObject.put("to_district", Integer.parseInt(toDistrict));
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray array = response.getJSONArray("data");
                        JSONObject serviceJSON;
                        for (int i = 0; i < array.length(); i++) {
                            serviceJSON = array.getJSONObject(i);
                            if (serviceJSON.getInt("service_type_id") == 2) {
                                APIListener.onResultStringFound(serviceJSON.getString("service_id"));
                            }
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
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                    url, jsonObject, listener, errorListener) {

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

    public static void getShippingFee(String serviceId, Address fromAddress,
                                      Address toAddress, double weight,
                                      Application application, APIListener APIListener) {
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        url = StringUtils.DELIVERY_API_URL + "v2/shipping-order/fee";
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("service_id", Integer.parseInt(serviceId));
            jsonObject.put("from_district_id", Integer.parseInt(fromAddress.getDistrictId()));
            jsonObject.put("to_district_id", Integer.parseInt(toAddress.getDistrictId()));
            jsonObject.put("to_ward_code", toAddress.getWardId());
            jsonObject.put("weight", (int) (weight * 1000));
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String message = response.getString("message");
                        if (message.equals("Success")) {
                            int shippingFee = response.getJSONObject("data").getInt("service_fee");
                            APIListener.onShippingFeeFound((int) Math.ceil((shippingFee/100) * 100));
                        } else {
                            APIListener.onShippingFeeFound(0);
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
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                    url, jsonObject, listener, errorListener) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put("token", StringUtils.DELIVERY_API_TOKEN);
                    header.put("shop_id", StringUtils.DELIVERY_API_SHOP_ID);
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
