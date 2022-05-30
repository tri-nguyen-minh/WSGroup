package dev.wsgroup.main.models.apis.callers;

import android.app.Application;

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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.dtos.Category;
import dev.wsgroup.main.models.dtos.Product;
import dev.wsgroup.main.models.dtos.Review;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.StringUtils;

public class APIProductCaller {

    private static RequestQueue requestQueue;
    private static Product product;
    private static List<Product> productList;
    private static String url;

    public static void getAllProduct(Application application, APIListener APIListener) {
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        productList = new ArrayList<>();
                        JSONArray jsonArray = response.getJSONArray("data");
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length();i++) {
                                product = Product.getObjectFromJSON(jsonArray.getJSONObject(i),
                                                                                false);
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
                    System.out.println(error);
                    System.out.println(MethodUtils.getVolleyErrorMessage(error));
                    APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
                }
            };
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                    StringUtils.PRODUCT_API_URL, new JSONObject(), listener, errorListener);
            request.setRetryPolicy(new DefaultRetryPolicy(7000,
                    1, 2));
            requestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
            APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
        }
    }

    public static void getProductsByProductStatus(String status, Application application,
                                                  APIListener APIListener) {
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        url = StringUtils.PRODUCT_API_URL + "getAllProductByStatus";
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("status", status);
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        productList = new ArrayList<>();
                        JSONArray jsonArray = response.getJSONArray("data");
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length();i++) {
                                product = Product.getObjectFromJSON(jsonArray.getJSONObject(i),
                                        false);
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
                    System.out.println(error);
                    System.out.println(MethodUtils.getVolleyErrorMessage(error));
                    APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
                }
            };
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                    url, jsonObject, listener, errorListener);
            request.setRetryPolicy(new DefaultRetryPolicy(7000,
                    1, 2));
            requestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
            APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
        }
    }

    public static void getProductsWithCompletedOrders(Application application,
                                                      APIListener APIListener) {
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        url = StringUtils.PRODUCT_API_URL + "getProductWithOrderCompleted";
        try {
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        productList = new ArrayList<>();
                        JSONArray jsonArray = response.getJSONArray("data");
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length();i++) {
                                product = Product.getObjectFromJSON(jsonArray.getJSONObject(i),
                                        false);
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
                    System.out.println(error);
                    System.out.println(MethodUtils.getVolleyErrorMessage(error));
                    APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
                }
            };
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                    url, new JSONObject(), listener, errorListener);
            request.setRetryPolicy(new DefaultRetryPolicy(7000,
                    1, 2));
            requestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
            APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
        }
    }

    public static void getNewProductsCurrentWeek(Application application, APIListener APIListener) {
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        url = StringUtils.PRODUCT_API_URL + "getProductCreatedThisWeek";
        try {
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        productList = new ArrayList<>();
                        JSONArray jsonArray = response.getJSONArray("data");
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length();i++) {
                                product = Product.getObjectFromJSON(jsonArray.getJSONObject(i),
                                        false);
                                if (!product.getStatus().equals("deactivated")) {
                                    productList.add(product);
                                }
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
                    System.out.println(error);
                    System.out.println(MethodUtils.getVolleyErrorMessage(error));
                    APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
                }
            };
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                    url, new JSONObject(), listener, errorListener);
            request.setRetryPolicy(new DefaultRetryPolicy(7000,
                    1, 2));
            requestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
            APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
        }
    }

    public static void getProductById(String productId, Application application,
                                      APIListener APIListener) {
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
                        productList = new ArrayList<>();
                        if(jsonObject != null) {
                            product = Product.getObjectFromJSON(jsonObject, false);
                            productList.add(product);
                        }
                        APIListener.onProductListFound(productList);
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
                    jsonObject, listener, errorListener);
            request.setRetryPolicy(new DefaultRetryPolicy(7000,
                    1, 2));
            requestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
            APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
        }
    }

    public static void getProductListBySupplierId(String supplierId, Application application,
                                                  APIListener APIListener) {
        String url = StringUtils.PRODUCT_API_URL + "supplier/" + supplierId;
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONObject jsonObject;
                    try {
                        JSONArray jsonArray = response.getJSONArray("data");
                        productList = new ArrayList<>();
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length();i++) {
                                jsonObject = jsonArray.getJSONObject(i);
                                product = Product.getObjectFromJSON(jsonObject, true);
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

    public static void getOrderCountByProductIdList(List<String> productIdList,
                                                    Application application,
                                                    APIListener APIListener) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        String url = StringUtils.REVIEW_API_URL + "product/order";
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray array = new JSONArray();
            for (String id : productIdList) {
                array.put(id);
            }
            jsonObject.put("productIds", array);
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Map<String, Integer> countMap = new HashMap<>();
                    try {
                        JSONObject result = response.getJSONObject("data").getJSONObject("result");
                        if (result.length() > 0) {
                            JSONArray list;
                            String key;
                            Iterator keys = result.keys();
                            while (keys.hasNext()) {
                                key = (String) keys.next();
                                list = result.getJSONArray(key);
                                countMap.put(key, list.length());
                            }
                        }
                        APIListener.onProductOrderCountFound(countMap);
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

    public static void getOrderCountByProductList(List<Product> productList,
                                                    Application application, APIListener APIListener) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        String url = StringUtils.REVIEW_API_URL + "product/order";
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray array = new JSONArray();
            for (Product product : productList) {
                array.put(product.getProductId());
            }
            jsonObject.put("productIds", array);
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Map<String, Integer> countMap = new HashMap<>();
                    try {
                        JSONObject result = response.getJSONObject("data").getJSONObject("result");
                        if (result.length() > 0) {
                            JSONArray list;
                            String key;
                            Iterator keys = result.keys();
                            while (keys.hasNext()) {
                                key = (String) keys.next();
                                list = result.getJSONArray(key);
                                countMap.put(key, list.length());
                            }
                        }
                        APIListener.onProductOrderCountFound(countMap);
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

    public static void getRatingByProductIdList(List<Product> productList,
                                                Application application, APIListener APIListener) {
        String url = StringUtils.PRODUCT_API_URL + "products/rating";
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray array = new JSONArray();
            for (Product product : productList) {
                array.put(product.getProductId());
            }
            jsonObject.put("productIds", array);
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    product = null;
                    try {
                        JSONArray campaignArray = response.getJSONObject("data")
                                                          .getJSONArray("campaignOrder");
                        JSONArray retailArray = response.getJSONObject("data")
                                                        .getJSONArray("retailOrder");
                        Map<String, Review> reviewMap = new HashMap<>();
                        Map<String, Double> ratingMap = new HashMap<>();
                        Review review;
                        JSONObject data;
                        double rating;
                        int count;
                        if (campaignArray.length() > 0) {
                            for (int i = 0; i < campaignArray.length(); i++) {
                                data = campaignArray.getJSONObject(i);
                                rating = data.getDouble("rating");
                                count = data.getInt("count");
                                review = reviewMap.get(data.getString("productId"));
                                if (review == null) {
                                    review = new Review();
                                } else {
                                    rating += review.getRating();
                                    count += Integer.parseInt(review.getReview());
                                }
                                review.setRating(rating);
                                review.setReview(count + "");
                                reviewMap.put(data.getString("productId"), review);
                            }
                        }
                        if (retailArray.length() > 0) {
                            for (int i = 0; i < retailArray.length(); i++) {
                                data = retailArray.getJSONObject(i);
                                rating = data.getDouble("rating");
                                count = data.getInt("count");
                                review = reviewMap.get(data.getString("productId"));
                                if (review == null) {
                                    review = new Review();
                                } else {
                                    rating += review.getRating();
                                    count += Integer.parseInt(review.getReview());
                                }
                                review.setRating(rating);
                                review.setReview(count + "");
                                reviewMap.put(data.getString("productId"), review);
                            }
                        }
                        if (!reviewMap.isEmpty()) {
                            for (Product product : productList) {
                                review = reviewMap.get(product.getProductId());
                                if (review != null) {
                                    rating = review.getRating();
                                    rating /= Double.parseDouble(review.getReview());
                                    ratingMap.put(product.getProductId(), rating);
                                }
                            }
                        }
                        APIListener.onRatingListCount(ratingMap);
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
                    if(error.toString().contains("NoConnectionError")) {
                        APIListener.onFailedAPICall(IntegerUtils.ERROR_NO_CONNECTION);
                    } else {
                        APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
                    }
                }
            };
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                    url, jsonObject, listener, errorListener);
            request.setRetryPolicy(new DefaultRetryPolicy(7000,
                    1, 2));
            requestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
            APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
        }
    }

    public static void searchProductByNameOrSupplier(String search, Application application,
                                                     APIListener APIListener) {
        String url = StringUtils.PRODUCT_API_URL + "searchProduct";
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("value", search);
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    product = null;
                    productList = new ArrayList<>();
                    try {
                        JSONArray jsonArray = response.getJSONArray("data");
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length();i++) {
                                product = Product.getObjectFromJSON(jsonArray.getJSONObject(i), true);
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
                    System.out.println(error);
                    System.out.println(MethodUtils.getVolleyErrorMessage(error));
                    if(error.toString().contains("NoConnectionError")) {
                        APIListener.onFailedAPICall(IntegerUtils.ERROR_NO_CONNECTION);
                    } else {
                        APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
                    }
                }
            };
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                    url, jsonObject, listener, errorListener);
            request.setRetryPolicy(new DefaultRetryPolicy(7000,
                    1, 2));
            requestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
            APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
        }
    }

    public static void getProductByCategoryList(List<Category> categoryList,
                                                Application application, APIListener APIListener) {
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        String url = StringUtils.PRODUCT_API_URL + "getListProductByCates";
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray array = new JSONArray();
            for (Category category : categoryList) {
                array.put(category.getCategoryId());
            }
            jsonObject.put("listCategories", array);
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject productJSON;
                        productList = new ArrayList<>();
                        JSONArray data = response.getJSONArray("data");
                        if (data.length() > 0) {
                            for (int i = 0; i < data.length();i++) {
                                productJSON = data.getJSONObject(i);
                                product = Product.getObjectFromJSON(productJSON, true);
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
                    System.out.println(error);
                    System.out.println(MethodUtils.getVolleyErrorMessage(error));
                    APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
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
}
