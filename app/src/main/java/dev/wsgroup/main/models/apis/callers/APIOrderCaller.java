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
import dev.wsgroup.main.models.dtos.Campaign;
import dev.wsgroup.main.models.dtos.CartProduct;
import dev.wsgroup.main.models.dtos.Order;
import dev.wsgroup.main.models.dtos.OrderProduct;
import dev.wsgroup.main.models.dtos.Product;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.StringUtils;

public class APIOrderCaller {

    private static RequestQueue requestQueue;
    private static List<Order> orderList;

    public  static void addOrder(String token, Order order,
                                 Application application, APIListener APIListener) {
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            JSONObject jsonObjectOrder = new JSONObject();
            JSONObject jsonObjectProduct;
            JSONArray productArray = new JSONArray();
            CartProduct cartProduct;
            Product product;
            Campaign campaign = order.getCampaign();
            for (OrderProduct orderProduct : order.getOrderProductList()) {
                cartProduct = orderProduct.getCartProduct();
                product = cartProduct.getProduct();
                jsonObjectProduct = new JSONObject();
                if (order.isInCart()) {
                    jsonObjectProduct.put("cartId", cartProduct.getId());
                } else {
                    jsonObjectProduct.put("cartId", JSONObject.NULL);
                }
                jsonObjectProduct.put("productId", product.getProductId());
                jsonObjectProduct.put("productName", product.getName());
                jsonObjectProduct.put("image", product.getImageLink());
                jsonObjectProduct.put("quantity", cartProduct.getQuantity());
                jsonObjectProduct.put("price", product.getRetailPrice());
                jsonObjectProduct.put("totalPrice", orderProduct.getTotalPrice());
                jsonObjectProduct.put("typeofproduct", cartProduct.getProductType());
                jsonObjectProduct.put("notes", orderProduct.getNote());
                productArray.put(jsonObjectProduct);
            }
            jsonObjectOrder.put("paymentMethod", order.getPaymentMethod());
            jsonObjectOrder.put("campaignId", (campaign == null)
                                                        ? JSONObject.NULL: campaign.getId());
            jsonObjectOrder.put("isWholesale", (campaign == null) ? false : true);
            jsonObjectOrder.put("addressId", order.getAddress().getId());
            jsonObjectOrder.put("discountPrice", order.getDiscountPrice());
            jsonObjectOrder.put("shippingFee", order.getShippingFee());
            jsonObjectOrder.put("supplierId", order.getSupplier().getId());
            jsonObjectOrder.put("inCart", order.isInCart());
            if (order.getCustomerDiscount() != null) {
                jsonObjectOrder.put("customerDiscountCodeId", order.getCustomerDiscount().getId());
            } else {
                jsonObjectOrder.put("customerDiscountCodeId", JSONObject.NULL);
            }

            jsonObjectOrder.put("products", productArray);
            System.out.println(jsonObjectOrder);
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject data = response.getJSONObject("data");
                        order.setId(data.getString("id"));
                        order.setStatus(data.getString("status"));
                        order.setTotalPrice(data.getDouble("totalprice"));
                        APIListener.onOrderSuccessful(order);
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


    public static void getOrderByStatus(String token, String status, List<Order> list,
                                        Application application, APIListener APIListener) {
        String url = StringUtils.ORDER_API_URL + "customer?status=" + status;
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray jsonArray = response.getJSONArray("data");
                        if (jsonArray.length() > 0) {
                            orderList = (list == null) ? new ArrayList<>() : list;
                            List<Integer> orderWithCampaignIndexList = new ArrayList<>();
                            Order order;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                order = Order.getObjectFromJSON(jsonArray.getJSONObject(i));
                                if (order.getStatus().equals(status)) {
                                    orderList.add(order);
                                    if (order.getCampaign() != null) {
                                        orderWithCampaignIndexList.add(orderList.indexOf(order));
                                    }
                                }
                            }
                            APIListener.onOrderFound(orderList);
                        } else {
                            APIListener.onOrderFound(new ArrayList<>());
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
                    header.put("cookie", token);
                    return header;
                }
            };
            requestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  static void updateOrderPaymentStatus(String token, Order order, String status,
                                                 boolean advanceFlag, double price, String vnpRef,
                                                 Application application, APIListener APIListener) {
        String url = StringUtils.ORDER_API_URL + "payment";
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("orderId", order.getId());
            jsonObject.put("status", status);
            jsonObject.put("isAdvanced", advanceFlag);
            jsonObject.put("amount", price);
            jsonObject.put("vnp_TxnRef", vnpRef);
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String message = response.getString("message");
                        if (message.equals("successful")) {
                            APIListener.onUpdateOrderSuccessful();
                        }
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
