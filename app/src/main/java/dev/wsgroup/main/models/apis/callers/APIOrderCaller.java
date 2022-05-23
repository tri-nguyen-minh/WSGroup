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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.dtos.Campaign;
import dev.wsgroup.main.models.dtos.CartProduct;
import dev.wsgroup.main.models.dtos.Order;
import dev.wsgroup.main.models.dtos.OrderHistory;
import dev.wsgroup.main.models.dtos.OrderProduct;
import dev.wsgroup.main.models.dtos.Product;
import dev.wsgroup.main.models.dtos.Supplier;
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
                jsonObjectProduct.put("notes", orderProduct.getNote());
                productArray.put(jsonObjectProduct);
            }
            jsonObjectOrder.put("paymentMethod", order.getPaymentMethod());
            jsonObjectOrder.put("campaignId", (campaign == null)
                                                        ? JSONObject.NULL : campaign.getId());
            jsonObjectOrder.put("addressId", order.getAddress().getId());
            jsonObjectOrder.put("discountPrice", order.getDiscountPrice());
            jsonObjectOrder.put("shippingFee", order.getShippingFee());
            jsonObjectOrder.put("supplierId", order.getSupplier().getId());
            if (order.getCustomerDiscount() != null) {
                jsonObjectOrder.put("customerDiscountCodeId", order.getCustomerDiscount().getId());
            } else {
                jsonObjectOrder.put("customerDiscountCodeId", JSONObject.NULL);
            }
            jsonObjectOrder.put("products", productArray);
            jsonObjectOrder.put("loyalcustomerdiscountpercent", order.getLoyaltyDiscountPercent());
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject data = response.getJSONObject("data");
                        order.setId(data.getString("id"));
                        order.setStatus(data.getString("status"));
                        order.setTotalPrice(data.getDouble("totalPrice"));
                        order.setCode(data.getString("orderCode"));
                        APIListener.onOrderSuccessful(order);
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
                    if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
                        APIListener.onFailedAPICall(IntegerUtils.ERROR_NO_USER);
                    } else {
                        APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
                    }
                }
            };
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                    StringUtils.ORDER_API_URL, jsonObjectOrder, listener, errorListener) {

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
            APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
        }
    }


    public static void getOrderByStatus(String token, String status,
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
                        orderList = new ArrayList<>();
                        if (jsonArray.length() > 0) {
                            Order order; Supplier supplier;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                order = Order.getObjectFromJSON(jsonArray.getJSONObject(i));
                                supplier = Supplier.getObjectFromJSON(jsonArray.getJSONObject(i));
                                order.setSupplier(supplier);
                                if (order.getStatus().equals(status)) {
                                    orderList.add(order);
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
            jsonObject.put("orderCode", order.getCode());
            jsonObject.put("type", order.getCampaign() == null ? "retail" : "campaign");
            jsonObject.put("status", status);
            jsonObject.put("isAdvanced", advanceFlag);
            jsonObject.put("amount", price);
            jsonObject.put("vnp_TxnRef", vnpRef);
            if (order.getCampaign() != null) {
                jsonObject.put("isShare", order.getCampaign().getShareFlag());
            }
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String message = response.getString("message");
                        if (message.equals("successful")) {
                            APIListener.onUpdateSuccessful();
                        } else {
                            APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
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
                    if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
                        APIListener.onFailedAPICall(IntegerUtils.ERROR_NO_USER);
                    } else {
                        APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
                    }
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
            request.setRetryPolicy(new DefaultRetryPolicy(7000,
                    1, 2));
            requestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
            APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
        }
    }

    public static void getOrderByOrderCode(String orderCode,
                                           Application application, APIListener APIListener) {
        String url = StringUtils.ORDER_API_URL + "getOrderByCode?orderCode=" + orderCode;
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject data = response.getJSONObject("data");
                        JSONObject orderJSON = data.getJSONObject("order");
                        JSONObject supplierJSON = data.getJSONObject("supplierId");
                        Supplier supplier = Supplier.getObjectFromJSON(supplierJSON);
                        orderList = new ArrayList<>();
                        Order order = Order.getObjectFromJSON(orderJSON);
                        order.setSupplier(supplier);
                        orderList.add(order);
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
                    new JSONObject(), listener, errorListener);
            request.setRetryPolicy(new DefaultRetryPolicy(7000,
                    1, 2));
            requestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
            APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
        }
    }

    public  static void getOrderHistoryByOrderCode(String orderCode,
                                                   Application application, APIListener APIListener) {
        String url = StringUtils.ORDER_HISTORY_API_URL + "orderCode";
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("orderCode", orderCode);
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        List<OrderHistory> list = new ArrayList<>();
                        OrderHistory orderHistory;
                        JSONArray array = response.getJSONArray("data");
                        if (array.length() > 0) {
                            for (int i = 0; i < array.length(); i++) {
                                orderHistory = OrderHistory.getObjectFromJSON(array.getJSONObject(i));
                                list.add(orderHistory);
                            }
                        }
                        Collections.sort(list, new Comparator<OrderHistory>() {
                            @Override
                            public int compare(OrderHistory order1, OrderHistory order2) {
                                Date date1 = null, date2 = null;
                                if (order1.getCreateDate() != null && order2.getCreateDate() != null) {
                                    try {
                                        date1 = MethodUtils.convertToDate(order1.getCreateDate());
                                        date2 = MethodUtils.convertToDate(order2.getCreateDate());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                return date2.compareTo(date1);
                            }
                        });
                        APIListener.onOrderHistoryFound(list);
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
                    if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
                        APIListener.onFailedAPICall(IntegerUtils.ERROR_NO_USER);
                    } else {
                        APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
                    }
                }
            };
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url,
                    jsonObject, listener, errorListener) {
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

    public static void updateOrderStatus(String token, Order order, String description,
                                         List<String> imageList, String status,
                                         Application application, APIListener APIListener) {
        String url = StringUtils.ORDER_API_URL + "status/customer/" + status;
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("orderCode", order.getCode());
            jsonObject.put("orderId", order.getId());
            jsonObject.put("type", order.getCampaign() == null ? "retail" : "campaign");
            jsonObject.put("description", description);
            if (!status.equals("completed")) {
                JSONArray array = new JSONArray();
                JSONObject imageJSON;
                for (String string : imageList) {
                    imageJSON = new JSONObject();
                    imageJSON.put("url", string);
                    array.put(imageJSON);
                }
                jsonObject.put("image", array);
            }
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String result = response.getString("message");
                        if (result.equals("successful")) {
                            APIListener.onUpdateSuccessful();
                        } else {
                            APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
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
}
