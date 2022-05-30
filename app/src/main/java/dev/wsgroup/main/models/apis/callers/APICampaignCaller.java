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
import java.util.List;
import java.util.Map;

import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.dtos.Campaign;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.StringUtils;

public class APICampaignCaller {

    private static RequestQueue requestQueue;
    private static List<Campaign> campaignList;

    public static void getCampaignListByProductId(String productId, String status,
                                                  Application application,
                                                  APIListener APIListener) {
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        String url = StringUtils.CAMPAIGN_API_URL + "product";
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(productId);
            jsonObject.put("productIds", jsonArray);
            jsonObject.put("status", status);
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray data = response.getJSONArray("data");
                        campaignList = new ArrayList<>();
                        if(data.length() > 0) {
                            Campaign campaign;
                            for (int i = 0; i < data.length(); i++) {
                                try {
                                    campaign = Campaign.getObjectFromJSON(data.getJSONObject(i));
                                    campaignList.add(campaign);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        APIListener.onCampaignListFound(campaignList);
                    } catch (Exception e) {
                        e.printStackTrace();
                        APIListener.onNoJSONFound();
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

    public static void searchCampaign(String value, Application application,
                                      APIListener APIListener) {
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        String url = StringUtils.CAMPAIGN_API_URL + "searchCampaign";
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("value", value);
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray data = response.getJSONObject("data")
                                                 .getJSONArray("campaign");
                        campaignList = new ArrayList<>();
                        if(data.length() > 0) {
                            Campaign campaign;
                            for (int i = 0; i < data.length(); i++) {
                                try {
                                    campaign = Campaign.getSearchedObjectFromJSON(data.getJSONObject(i));
                                    if (campaign.getStatus().equals("active")) {
                                        campaignList.add(campaign);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        APIListener.onCampaignListFound(campaignList);
                    } catch (Exception e) {
                        e.printStackTrace();
                        APIListener.onNoJSONFound();
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

    public static void getCampaignById(String campaignId, Application application,
                                       APIListener APIListener) {
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        String url = StringUtils.CAMPAIGN_API_URL + campaignId;
        try {
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject data = response.getJSONObject("data");
                        campaignList = new ArrayList<>();
                        if(data != null) {
                            Campaign campaign = Campaign.getObjectFromJSON(data.getJSONObject("campaign"));
                            campaignList.add(campaign);
                        }
                        APIListener.onCampaignListFound(campaignList);
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
                    new JSONObject(), listener, errorListener) {

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
