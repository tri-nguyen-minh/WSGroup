package dev.wsgroup.main.models.apis.callers;

import android.app.Application;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.dtos.Order;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.StringUtils;

public class APIPaymentCaller {

    private static RequestQueue requestQueue;

    public static void getPaymentURL(Order order, double price, String bankString,
                                     String orderDescription, String orderType,
                                     Application application, APIListener APIListener) {
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            System.out.println(price);
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
                        APIListener.onGettingPaymentURL(url);
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
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                    StringUtils.PAYMENT_API_URL, jsonObject, listener, errorListener) {

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
