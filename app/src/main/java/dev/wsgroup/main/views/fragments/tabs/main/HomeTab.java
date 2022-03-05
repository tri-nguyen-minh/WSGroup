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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APIDiscountCaller;
import dev.wsgroup.main.models.apis.callers.APIProductCaller;
import dev.wsgroup.main.models.dtos.CartProduct;
import dev.wsgroup.main.models.dtos.CustomerDiscount;
import dev.wsgroup.main.models.dtos.Product;
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
    private List<CartProduct> retailList, campaignList;
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

        List<String> list = new ArrayList<>();
//        list.add("3c2623eb-f4ba-4fe9-8f33-c3e604250ab5");
        list.add("1bd7c4d0-80dd-4a33-a229-df536a621aed");
        list.add("b53602fc-fc91-483b-a528-799338cbe13f");
        list.add("84bb4393-be10-406c-93ec-cc92be9a6da0");

        imgProductDetailMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = "99ba5ad1-612c-493f-8cdb-2c2af92ae95a";
                APIDiscountCaller.getCustomerDiscountByCondition(sharedPreferences.getString("TOKEN", ""), list, 1000000, id,
                        null, getActivity().getApplication(), new APIListener() {
                            @Override
                            public void onDiscountListFound(List<CustomerDiscount> discountList) {
                                System.out.println("size " + discountList.size());
                            }
                        });
//                APIDiscountCaller.getCustomerDiscountByCondition(sharedPreferences.getString("TOKEN", ""), null, 100000, id,
//                        null, getActivity().getApplication(), new APIListener());
//                APIDiscountCaller.getCustomerDiscountByStatus(sharedPreferences.getString("TOKEN", ""), "ready", null,
//                        getActivity().getApplication(), new APIListener());
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
                if(!productList.isEmpty()) {
                    APIProductCaller.getOrderCountByProductList(productList, getActivity().getApplication(), new APIListener() {
                        @Override
                        public void onProductOrderCountFound(Map<String, Integer> countList) {
                            for (Product product : productList) {
                                if (countList.get(product.getProductId()) != null) {
                                    product.setOrderCount(countList.get(product.getProductId()));
                                }else {
                                    product.setOrderCount(0);
                                }
                            }
                            setupMostPopularView(productList);
                        }
                    });
                } else {
                    setupNoProductView();
                }
            }
            @Override
            public void onFailedAPICall(int errorCode) {
                setupNoProductView();
            }
        });
    }

    private void setupMostPopularView(List<Product> productList) {
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

    private void editCartCountByUser() {
        try {
            retailList = (List<CartProduct>) ObjectSerializer
                    .deserialize(sharedPreferences.getString("RETAIL_CART", ""));
            campaignList = (List<CartProduct>) ObjectSerializer
                    .deserialize(sharedPreferences.getString("CAMPAIGN_CART", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(retailList == null && campaignList == null) {
            cardViewProductDetailCartCount.setVisibility(View.INVISIBLE);
        } else {
            if (retailList.size() > 0 || campaignList.size() > 0) {
                cardViewProductDetailCartCount.setVisibility(View.VISIBLE);
                cartCount = retailList.size() + campaignList.size();
                txtProductDetailCartCount.setText(cartCount + "");
            } else {
                cardViewProductDetailCartCount.setVisibility(View.INVISIBLE);
            }
        }
    }
}