package dev.wsgroup.main.models.apis.callers;

import android.app.Application;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
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
import dev.wsgroup.main.models.dtos.Supplier;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.StringUtils;

public class APICampaignCaller {

    private static RequestQueue requestQueue;

    public static void getCampaignById(String productId, Application application, APIListener APIListener) {
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        String url = StringUtils.CAMPAIGN_API_URL + "product";
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(productId);
            jsonObject.put("productIds", jsonArray);
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray data = response.getJSONArray("data");
                        if(data.length() > 0) {
                            Campaign campaign = Campaign.getCampaignFromJSON(data.getJSONObject(0));
                            APIListener.onCampaignFound(campaign);
                        } else {
                            APIListener.onNoCampaignFound();
                        }
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
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url,
                   jsonObject, listener, errorListener) {

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

    public static void getCampaignListById(List<String> productIdList, Application application, APIListener APIListener) {
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        String url = StringUtils.CAMPAIGN_API_URL + "product";
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            for (String id : productIdList) {
                jsonArray.put(id);
            }
            jsonObject.put("productIds", jsonArray);
            Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray data = response.getJSONArray("data");
                        if(data.length() > 0) {
                            List<Campaign> campaignList = new ArrayList<>();
                            Campaign campaign;
                            for (int i = 0; i < data.length(); i++) {
                                campaign = Campaign.getCampaignFromJSON(data.getJSONObject(i));
                                campaignList.add(campaign);
                            }
                            APIListener.onCampaignListFound(campaignList);
                        } else {
                            APIListener.onNoCampaignFound();
                        }
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
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url,
                    jsonObject, listener, errorListener) {

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
