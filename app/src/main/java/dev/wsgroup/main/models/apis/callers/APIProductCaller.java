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
import dev.wsgroup.main.models.dtos.Campaign;
import dev.wsgroup.main.models.dtos.Product;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.StringUtils;

public class APIProductCaller {

    private static RequestQueue requestQueue;
    private static Product product;

    public static void getAllProduct(Application application, APIListener APIListener) {
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    JSONObject jsonObject = null;
                    product = null;
                    List<Product> productList = new ArrayList<>();
                    try {
                        JSONArray jsonArray = response.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length();i++) {
                            jsonObject = jsonArray.getJSONObject(i);
                            product = Product.getProductFromJSON(jsonObject);
                            if(product.getStatus().equals("active")) {
                                productList.add(product);
                            }
                        }
                        APIListener.onProductListFound(productList);
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

    public static void getProductById(String productId, Application application, APIListener APIListener) {
        String url = StringUtils.PRODUCT_API_URL + productId;
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            JSONObject jsonObject = new JSONObject();
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject jsonObject = response.getJSONObject("data");
                        if(jsonObject != null) {
                            product = Product.getProductFromJSON(jsonObject);
                            APICampaignCaller.getCampaignById(productId, application, new APIListener() {
                                @Override
                                public void onCampaignFound(Campaign campaign) {
                                    super.onCampaignFound(campaign);
                                    product.setCampaign(campaign);
                                    APIListener.onProductFound(product);
                                }

                                @Override
                                public void onNoCampaignFound() {
                                    super.onNoCampaignFound();
                                    product.setCampaign(null);
                                    APIListener.onProductFound(product);
                                }
                            });
                        } else {
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

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
                    jsonObject, listener, errorListener);
            requestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
