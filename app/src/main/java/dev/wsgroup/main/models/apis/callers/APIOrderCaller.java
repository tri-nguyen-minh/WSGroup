package dev.wsgroup.main.models.apis.callers;

import android.app.Application;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.util.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.dtos.Address;
import dev.wsgroup.main.models.dtos.CartProduct;
import dev.wsgroup.main.models.dtos.Order;
import dev.wsgroup.main.models.dtos.OrderProduct;
import dev.wsgroup.main.models.dtos.Product;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.StringUtils;

public class APIOrderCaller {

    private static RequestQueue requestQueue;

    public  static void addOrder(String token, Order order, Application application, APIListener APIListener) {
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            JSONObject jsonObjectOrder = new JSONObject();
            JSONObject jsonObjectProduct;
            JSONArray productArray = new JSONArray();
            CartProduct cartProduct;
            Product product;
            for (OrderProduct orderProduct : order.getOrderProductList()) {
                cartProduct = orderProduct.getCartProduct();
                product = cartProduct.getProduct();
                jsonObjectProduct = new JSONObject();
                jsonObjectProduct.put("productId", product.getProductId());
                jsonObjectProduct.put("productName", product.getName());
                jsonObjectProduct.put("quantity", cartProduct.getQuantity());
                double totalPrice = cartProduct.getQuantity();
                if(order.getCampaignId().isEmpty()) {
                    jsonObjectProduct.put("price", product.getRetailPrice());
                    totalPrice *= product.getRetailPrice();
                } else {
                    jsonObjectProduct.put("price", product.getCampaign().getPrice());
                    totalPrice *= product.getCampaign().getPrice();
                }
                jsonObjectProduct.put("totalPrice", totalPrice);
                jsonObjectProduct.put("typeofproduct", cartProduct.getProductType());
                jsonObjectProduct.put("image", product.getImageLink());
                jsonObjectProduct.put("notes", orderProduct.getNote());
                productArray.put(jsonObjectProduct);
            }
            jsonObjectOrder.put("campaignId", order.getCampaignId().isEmpty() ? null : order.getCampaignId());
            jsonObjectOrder.put("addressId", order.getAddressId());
            jsonObjectOrder.put("paymentId", order.getPaymentId());
            jsonObjectOrder.put("discountPrice", order.getDiscountPrice());
            jsonObjectOrder.put("shippingFee", order.getShippingFee());
            jsonObjectOrder.put("supplierId", order.getSupplier().getId());
//            if (order.getCustomerDiscount() != null) {
//                jsonObjectOrder.put("customerDiscountCodeId", order.getCustomerDiscount().getId());
//            } else {
                jsonObjectOrder.put("customerDiscountCodeId", null);
//            }
            jsonObjectOrder.put("isWholesale", order.getCampaignId().isEmpty() ? false : true);
            jsonObjectOrder.put("products", productArray);
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        APIListener.onOrderSuccessful();
                    } catch (Exception e) {
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
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, StringUtils.ORDER_API_URL,
                    jsonObjectOrder, listener, errorListener) {

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


    public static void getAllOrder(String token, String status, Application application, APIListener APIListener) {
        String url = StringUtils.ORDER_API_URL + "customer?status=" + status;
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        List<Order> orderList = new ArrayList<>();
                        JSONArray jsonArray = response.getJSONArray("data");
                        if (jsonArray.length() > 0) {
                            Order order;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                order = Order.getOrderFromJSON(jsonArray.getJSONObject(i));
                                orderList.add(order);
                            }
                        }
                        APIListener.onOrderFound(orderList);
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
}
