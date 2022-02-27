package dev.wsgroup.main.views.fragments.tabs.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APICampaignCaller;
import dev.wsgroup.main.models.apis.callers.APICartCaller;
import dev.wsgroup.main.models.apis.callers.APIProductCaller;
import dev.wsgroup.main.models.dtos.Campaign;
import dev.wsgroup.main.models.dtos.CartProduct;
import dev.wsgroup.main.models.dtos.Product;
import dev.wsgroup.main.models.dtos.Supplier;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewProductListAdapter;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.ObjectSerializer;
import dev.wsgroup.main.views.activities.CartActivity;
import dev.wsgroup.main.views.activities.account.SignInActivity;

public class HomeTab extends Fragment {

    private RelativeLayout layoutLoading;
    private LinearLayout layoutNoProductFound;
    private TextView lblRetryGetProduct, txtProductDetailCartCount, lblViewMorePopularProduct;
    private RecyclerView recViewHomePopularProduct;
    private CardView cardViewProductDetailCartCount;
    private ScrollView scrollViewHomeFragment;
    private ConstraintLayout constraintLayoutShoppingCart;
    private ImageView imgProductDetailMessage;

    private SharedPreferences sharedPreferences;
    private List<Supplier> supplierRetailList, supplierCampaignList;
    private HashMap<String, List<CartProduct>> retailCart, campaignCart;
    private List<CartProduct> productList;
    private String userId;
    private int cartCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sharedPreferences = getActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        return inflater.inflate(R.layout.fragment_main_home_tab, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layoutLoading = view.findViewById(R.id.layoutLoading);
        layoutNoProductFound = view.findViewById(R.id.layoutNoProductFound);
        lblRetryGetProduct = view.findViewById(R.id.lblRetryGetProduct);
        txtProductDetailCartCount = view.findViewById(R.id.txtProductDetailCartCount);
        lblViewMorePopularProduct = view.findViewById(R.id.lblViewMorePopularProduct);
        recViewHomePopularProduct = view.findViewById(R.id.recViewHomePopularProduct);
        cardViewProductDetailCartCount = view.findViewById(R.id.cardViewProductDetailCartCount);
        scrollViewHomeFragment = view.findViewById(R.id.scrollViewHomeFragment);
        constraintLayoutShoppingCart = view.findViewById(R.id.constraintLayoutShoppingCart);
        imgProductDetailMessage = view.findViewById(R.id.imgProductDetailMessage);

        cartCount = 0;
        try {
            userId = sharedPreferences.getString("USER_ID", "");
            editCartCountByUser();
        } catch (Exception e) {
            e.printStackTrace();
        }

        layoutNoProductFound.setVisibility(View.INVISIBLE);
        layoutLoading.setVisibility(View.VISIBLE);

        imgProductDetailMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                APICampaignCaller.getCampaignById(token,"ce179be8-c93a-4627-b3d1-0f7be42fcc22", getActivity().getApplication(), new APIListener());
            }
        });

//        List<Product> productList = getProductListByFilter("POPULAR");
        getProductList();

        constraintLayoutShoppingCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userId.isEmpty()) {
                    Intent signInIntent = new Intent(getActivity().getApplicationContext(), SignInActivity.class);
                    startActivityForResult(signInIntent, IntegerUtils.REQUEST_LOGIN_FOR_CART);
                } else {
                    Intent cartIntent = new Intent(getActivity().getApplicationContext(), CartActivity.class);
                    startActivityForResult(cartIntent, IntegerUtils.REQUEST_COMMON);
                }
            }
        });

        lblRetryGetProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutNoProductFound.setVisibility(View.INVISIBLE);
                layoutLoading.setVisibility(View.VISIBLE);
                getProductList();
            }
        });
    }

    private void getProductList() {
        APIProductCaller.getAllProduct(null, getActivity().getApplication(), new APIListener() {
            @Override
            public void onProductListFound(List<Product> productList) {
                super.onProductListFound(productList);
                if(!productList.isEmpty()) {
                    setupView(productList);
                } else {
                    setupNoProductView();
                }
            }
            @Override
            public void onFailedAPICall(int errorCode) {
                super.onFailedAPICall(errorCode);
                setupNoProductView();
            }
        });
    }

    private void setupView(List<Product> productList) {
        layoutNoProductFound.setVisibility(View.INVISIBLE);
        layoutLoading.setVisibility(View.INVISIBLE);
        scrollViewHomeFragment.setVisibility(View.VISIBLE);
        RecViewProductListAdapter adapter = new RecViewProductListAdapter(getContext(), getActivity());
        adapter.setProductsList(productList);
        recViewHomePopularProduct.setAdapter(adapter);
        recViewHomePopularProduct.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        lblViewMorePopularProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void setupNoProductView() {
        layoutNoProductFound.setVisibility(View.VISIBLE);
        layoutLoading.setVisibility(View.INVISIBLE);
        scrollViewHomeFragment.setVisibility(View.INVISIBLE);
    }

    private int getRetailCartCount() {
        int count = 0;
        for (Supplier supplier : supplierRetailList) {
            productList = retailCart.get(supplier.getId());
            count += productList.size();
        }
        return count;
    }

    private int getCampaignCartCount() {
        int count = 0;
        for (Supplier supplier : supplierCampaignList) {
            productList = campaignCart.get(supplier.getId());
            count += productList.size();
        }
        return count;
    }

    private void editCartCountByUser() {
        try {
            retailCart = (HashMap<String, List<CartProduct>>) ObjectSerializer
                    .deserialize(sharedPreferences.getString("RETAIL_CART", ""));
            campaignCart = (HashMap<String, List<CartProduct>>) ObjectSerializer
                    .deserialize(sharedPreferences.getString("CAMPAIGN_CART", ""));
            supplierRetailList = (ArrayList<Supplier>) ObjectSerializer
                    .deserialize(sharedPreferences.getString("SUPPLIER_RETAIL_LIST", ""));
            supplierCampaignList = (ArrayList<Supplier>) ObjectSerializer
                    .deserialize(sharedPreferences.getString("SUPPLIER_CAMPAIGN_LIST", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(supplierRetailList == null && supplierCampaignList == null) {
            cardViewProductDetailCartCount.setVisibility(View.INVISIBLE);
        } else {
            if (supplierRetailList.size() > 0 || supplierCampaignList.size() > 0) {
                cardViewProductDetailCartCount.setVisibility(View.VISIBLE);
                cartCount = 0;
                if (supplierRetailList.size() > 0) {
                    cartCount += getRetailCartCount();
                }
                if (supplierCampaignList.size() > 0) {
                    cartCount += getCampaignCartCount();
                }
                txtProductDetailCartCount.setText(cartCount + "");
            } else {
                cardViewProductDetailCartCount.setVisibility(View.INVISIBLE);
            }
        }
    }
}