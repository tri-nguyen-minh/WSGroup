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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.dtos.Campaign;
import dev.wsgroup.main.models.dtos.CartProduct;
import dev.wsgroup.main.models.dtos.Supplier;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.StringUtils;

public class APICartCaller {

    private static RequestQueue requestQueue;
    private static List<CartProduct> cartProductList;

    public static void getCartList(String token, Application application, APIListener APIListener) {
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    CartProduct cartProduct;
                    Supplier supplier;
                    cartProductList = new ArrayList<>();
                    List<CartProduct> retailList = new ArrayList<>();
                    List<CartProduct> campaignList = new ArrayList<>();
//                    HashMap<String, List<CartProduct>> retailCart = new HashMap<>();
//                    HashMap<String, List<CartProduct>> campaignCart = new HashMap<>();
//                    List<Supplier> supplierRetailList = new ArrayList<>();
//                    List<Supplier> supplierCampaignList = new ArrayList<>();
                    try {
                        JSONArray jsonArray = response.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length();i++) {
                            cartProduct = CartProduct.getObjectFromJSON(jsonArray.getJSONObject(i));
                            supplier = cartProduct.getProduct().getSupplier();
                            if (!cartProduct.getCampaignFlag()) {
                                retailList.add(cartProduct);
//                                cartProductList = retailCart.get(supplier.getId());
//                                if(cartProductList == null) {
//                                    supplierRetailList.add(supplier);
//                                    cartProductList = new ArrayList<>();
//                                }
//                                cartProductList.add(cartProduct);
//                                retailCart.put(supplier.getId(), cartProductList);
                            } else {
                                campaignList.add(cartProduct);
//                                cartProductList = campaignCart.get(supplier.getId());
//                                if(cartProductList == null) {
//                                    supplierCampaignList.add(supplier);
//                                    cartProductList = new ArrayList<>();
//                                }
//                                cartProductList.add(cartProduct);
//                                campaignCart.put(supplier.getId(), cartProductList);
                            }
                        }
                        APIListener.onCartListFound(retailList, campaignList);
                    } catch (Exception e) {
                        e.printStackTrace();
                        APIListener.onFailedAPICall(IntegerUtils.ERROR_PARSING_JSON);
                    }
                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
                }
            };
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, StringUtils.CART_API_URL,
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

    public  static void addCartItem(String token, CartProduct cartProduct, Application application, APIListener APIListener) {
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("productId", cartProduct.getProduct().getProductId());
            jsonObject.put("quantity", cartProduct.getQuantity());
            jsonObject.put("typeofproduct", cartProduct.getProductType());
            Campaign campaign = cartProduct.getCampaign();
            jsonObject.put("inCampaign", (campaign != null));
            if (campaign != null) {
                jsonObject.put("campaignId", campaign.getId());
            } else {
                jsonObject.put("campaignId", JSONObject.NULL);
            }
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject jsonObject = response.getJSONObject("data");
                        cartProduct.setId(jsonObject.getString("id"));
                        APIListener.onAddCartItemSuccessful(cartProduct);
                    } catch (JSONException e) {
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
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, StringUtils.CART_API_URL,
                    jsonObject, listener, errorListener) {

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

    public static void updateCartItem(String token, CartProduct cartProduct, Application application, APIListener APIListener) {
        String url = StringUtils.CART_API_URL + cartProduct.getId();
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("productId", cartProduct.getProduct().getProductId());
            jsonObject.put("quantity", cartProduct.getQuantity());
            jsonObject.put("typeofproduct", cartProduct.getProductType());
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    APIListener.onUpdateCartItemSuccessful();
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

    public static void deleteCartItem(String token, String cartId, Application application, APIListener APIListener) {
        String url = StringUtils.CART_API_URL + cartId;
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            JSONObject jsonObject = new JSONObject();
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    APIListener.onUpdateCartItemSuccessful();
                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
                }
            };
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url,
                    jsonObject, listener, errorListener) {

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
}
