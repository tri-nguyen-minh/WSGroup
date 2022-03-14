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
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.StringUtils;

public class APIProductCaller {

    private static RequestQueue requestQueue;
    private static Product product;
    private static List<Product> productList;

    public static void getAllProduct(List<Product> list,
                                     Application application, APIListener APIListener) {
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        try {
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        productList = (list == null) ? new ArrayList<>() : list;
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
                    if(error.toString().contains("NoConnectionError")) {
                        APIListener.onFailedAPICall(IntegerUtils.ERROR_NO_CONNECTION);
                    } else {
                        APIListener.onFailedAPICall(IntegerUtils.ERROR_API);
                    }
                }
            };
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                    StringUtils.PRODUCT_API_URL, new JSONObject(), listener, errorListener);
            requestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getProductById(String productId,
                                      Application application,APIListener APIListener) {
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
                            product = Product.getObjectFromJSON(jsonObject, false);
                            APIListener.onProductFound(product);
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

    public static void getProductListBySupplierId(String supplierId, List<Product> list,
                                                  Application application, APIListener APIListener) {
        String url = StringUtils.PRODUCT_API_URL + "?supplierId=" + supplierId;
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
                        productList = (list == null) ? new ArrayList<>() : list;
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length();i++) {
                                jsonObject = jsonArray.getJSONObject(i);
                                product = Product.getObjectFromJSON(jsonObject, false);
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
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
                    new JSONObject(), listener, errorListener);
            requestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getOrderCountByProductIdList(List<String> productIdList,
                                                    Application application, APIListener APIListener) {
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
            request.setRetryPolicy(new DefaultRetryPolicy(
                    20000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
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
            request.setRetryPolicy(new DefaultRetryPolicy(
                    20000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
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
                    JSONObject jsonObject = null;
                    product = null;
                    try {
                        JSONArray jsonArray = response.getJSONArray("data");
                        Map<String, Double> ratingList = new HashMap<>();
                        String id;
                        double rating;
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                id = jsonArray.getJSONObject(i).getString("productid");
                                rating = jsonArray.getJSONObject(i).getDouble("rating");
                                ratingList.put(id, rating);
                            }
                        }
                        APIListener.onRatingListCount(ratingList);
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
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                    url, jsonObject, listener, errorListener);
            requestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void searchProductByNameOrSupplier(String search, List<Product> list,
                                                     Application application, APIListener APIListener) {
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
                    productList = (list == null) ? new ArrayList<>() : list;
                    try {
                        JSONArray jsonArray = response.getJSONArray("data");
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length();i++) {
                                product = Product.getObjectFromJSON(jsonArray.getJSONObject(i), false);
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
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                    url, jsonObject, listener, errorListener);
            requestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getProductByCategoryList(List<Category> categoryList, List<Product> list,
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
            System.out.println(jsonObject);
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println(response);
                    try {
                        JSONObject productJSON;
                        productList = (list == null) ? new ArrayList<>() : list;
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
            request.setRetryPolicy(new DefaultRetryPolicy(
                    20000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
