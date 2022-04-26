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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.dtos.CustomerDiscount;
import dev.wsgroup.main.models.dtos.OrderProduct;
import dev.wsgroup.main.models.dtos.Supplier;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.StringUtils;

public class APIDiscountCaller {

    private static RequestQueue requestQueue;
    private static List<CustomerDiscount> discountList;
    private static int count;

    public static void getCustomerDiscountByStatus(String token, String status,
                                                   List<CustomerDiscount> list,
                                                   Application application,
                                                   APIListener APIListener) {
        String url = StringUtils.DISCOUNT_API_URL + "getByStatus?status=" + status;
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        discountList = (list == null) ? new ArrayList<>() : list;
                        JSONArray data = response.getJSONArray("data");
                        CustomerDiscount discount;
                        if (data.length() > 0) {
                            for (int i = 0; i < data.length(); i++) {
                                discount = CustomerDiscount.getObjectFromJSON(data.getJSONObject(i));
                                discount.getDiscount()
                                        .setSupplier(Supplier.getObjectFromDiscountJSON(data.getJSONObject(i)));
                                if (!checkDuplicateCustomerDiscount(discount)) {
                                    discountList.add(discount);
                                }
                            }
                        }
                        APIListener.onDiscountListFound(discountList);
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
        }
    }

    public static void getCustomerDiscountByConditionV1(String token, List<String> productIdList,
                                                      double price, String supplierId,
                                                      List<CustomerDiscount> list,
                                                      Application application,
                                                      APIListener APIListener) {
        String url = StringUtils.DISCOUNT_API_URL + "productIds";
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            JSONObject jsonObject1 = new JSONObject();
            JSONObject jsonObject2 = null;
            if (productIdList != null) {
                count = 2;
                JSONArray jsonArray = new JSONArray();
                for (String id : productIdList) {
                    jsonArray.put(id);
                }
                jsonObject1.put("productIds", jsonArray);
                jsonObject2 = new JSONObject();
                jsonObject2.put("productIds", JSONObject.NULL);
                jsonObject2.put("minPriceCondition", price);
                jsonObject2.put("suppId", supplierId);
            } else {
                count = 1;
                jsonObject1.put("productIds", JSONObject.NULL);
            }
            jsonObject1.put("minPriceCondition", price);
            jsonObject1.put("suppId", supplierId);
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        count--;
                        discountList = (list == null) ? new ArrayList<>() : list;
                        JSONArray data = response.getJSONArray("data");
                        CustomerDiscount customerDiscount;
                        if (data.length() > 0) {
                            for (int i = 0; i < data.length(); i++) {
                                customerDiscount
                                        = CustomerDiscount.getObjectFromJSON(data.getJSONObject(i));
                                if (!checkDuplicateCustomerDiscount(customerDiscount)) {
                                    discountList.add(customerDiscount);
                                }
                            }
                        }
                        if (count == 0) {
                            APIListener.onDiscountListFound(discountList);
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
            JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.POST, url,
                    jsonObject1, listener, errorListener) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put("cookie", token);
                    return header;
                }
            };
            request1.setRetryPolicy(new DefaultRetryPolicy(7000,
                    1, 2));
            requestQueue.add(request1);
            if (jsonObject2 != null) {
                JsonObjectRequest request2 = new JsonObjectRequest(Request.Method.POST, url,
                        jsonObject2, listener, errorListener) {

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> header = new HashMap<>();
                        header.put("cookie", token);
                        return header;
                    }
                };
                request2.setRetryPolicy(new DefaultRetryPolicy(7000,
                        1, 2));
                requestQueue.add(request2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getCustomerDiscountByCondition(String token, List<OrderProduct> orderProductList,
                                                      double price, String supplierId,
                                                      List<CustomerDiscount> list,
                                                      Application application,
                                                      APIListener APIListener) {
        String url = StringUtils.DISCOUNT_API_URL + "productIds";
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            JSONObject jsonObject1 = new JSONObject();
            JSONObject jsonObject2 = null;
            if (orderProductList != null) {
                count = 2;
                JSONArray jsonArray = new JSONArray();
                for (OrderProduct product : orderProductList) {
                    jsonArray.put(product.getProduct().getProductId());
                }
                jsonObject1.put("productIds", jsonArray);
                jsonObject2 = new JSONObject();
                jsonObject2.put("productIds", JSONObject.NULL);
                jsonObject2.put("minPriceCondition", price);
                jsonObject2.put("suppId", supplierId);
            } else {
                count = 1;
                jsonObject1.put("productIds", JSONObject.NULL);
            }
            jsonObject1.put("minPriceCondition", price);
            jsonObject1.put("suppId", supplierId);
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        count--;
                        discountList = (list == null) ? new ArrayList<>() : list;
                        JSONArray data = response.getJSONArray("data");
                        CustomerDiscount customerDiscount;
                        if (data.length() > 0) {
                            for (int i = 0; i < data.length(); i++) {
                                customerDiscount
                                        = CustomerDiscount.getObjectFromJSON(data.getJSONObject(i));
                                if (!checkDuplicateCustomerDiscount(customerDiscount)) {
                                    discountList.add(customerDiscount);
                                }
                            }
                        }
                        if (count == 0) {
                            APIListener.onDiscountListFound(discountList);
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
            JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.POST, url,
                    jsonObject1, listener, errorListener) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put("cookie", token);
                    return header;
                }
            };
            request1.setRetryPolicy(new DefaultRetryPolicy(7000,
                    1, 2));
            requestQueue.add(request1);
            if (jsonObject2 != null) {
                JsonObjectRequest request2 = new JsonObjectRequest(Request.Method.POST, url,
                        jsonObject2, listener, errorListener) {

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> header = new HashMap<>();
                        header.put("cookie", token);
                        return header;
                    }
                };
                request2.setRetryPolicy(new DefaultRetryPolicy(7000,
                        1, 2));
                requestQueue.add(request2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getCustomerDiscountByDiscountCode(String token, String supplierId, String code,
                                                         Application application, APIListener APIListener) {
        String url = StringUtils.DISCOUNT_API_URL + "customerDiscountCodeAndSuppId";
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("discountCode", code);
            jsonObject.put("supplierId", supplierId);
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        List<CustomerDiscount> discountList = new ArrayList<>();
                        JSONArray data = response.getJSONArray("data");
                        CustomerDiscount discount;
                        if (data.length() > 0) {
                            for (int i = 0; i < data.length(); i++) {
                                discount = CustomerDiscount.getObjectFromJSON(data.getJSONObject(i));
                                discount.getDiscount()
                                        .setSupplier(Supplier.getObjectFromDiscountJSON(data.getJSONObject(i)));
                                discountList.add(discount);
                            }
                        }
                        APIListener.onDiscountListFound(discountList);
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
            };
            request.setRetryPolicy(new DefaultRetryPolicy(7000,
                    1, 2));
            requestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void useDiscountCode(String token, String customerDiscountId,
                                       Application application, APIListener APIListener) {
        String url = StringUtils.DISCOUNT_API_URL + "usedOneDiscountCode";
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("customerDiscountCodeId", customerDiscountId);
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        int data = response.getInt("data");
                        if (data == 1) {
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
            };
            request.setRetryPolicy(new DefaultRetryPolicy(7000,
                    1, 2));
            requestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean checkDuplicateCustomerDiscount(CustomerDiscount customerDiscount) {
        for (CustomerDiscount discount : discountList) {
            if (discount.getId().equals(customerDiscount.getId())) {
                return true;
            }
        }
        return false;
    }
}
