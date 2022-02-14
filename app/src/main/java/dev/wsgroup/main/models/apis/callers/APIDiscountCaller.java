package dev.wsgroup.main.models.apis.callers;

import android.app.Application;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.dtos.Discount;
import dev.wsgroup.main.models.dtos.Product;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.StringUtils;

public class APIDiscountCaller {

    private static RequestQueue requestQueue;
    private static List<Discount> discountList;

    public static void getDiscountListBySupplierId(String supplierId, Application application, APIListener APIListener) {
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONObject jsonObject = null;
                    try {
//                        JSONArray jsonArray = response.getJSONArray("data");
//                        if (jsonArray.length() > 0) {
//                            productList = (list == null) ? new ArrayList<>() : list;
//                            for (int i = 0; i < jsonArray.length();i++) {
//                                jsonObject = jsonArray.getJSONObject(i);
//                                product = Product.getProductFromJSON(jsonObject);
//                                productList.add(product);
//                            }
//                            APIListener.onProductListFound(productList);
//                        } else {
//                            APIListener.onProductListFound(new ArrayList<>());
//                        }
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
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, StringUtils.PRODUCT_API_URL,
                    new JSONObject(), listener, errorListener);
            requestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
