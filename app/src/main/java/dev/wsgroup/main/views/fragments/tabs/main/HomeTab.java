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
    private SharedPreferences.Editor editor;
    private List<Supplier> supplierList;
    private HashMap<String, List<CartProduct>> shoppingCart;
    private List<CartProduct> productList;

    private String userId, token;
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
        userId = sharedPreferences.getString("USER_ID", "");
        token = sharedPreferences.getString("TOKEN", "");

        layoutNoProductFound.setVisibility(View.INVISIBLE);
        layoutLoading.setVisibility(View.VISIBLE);

        imgProductDetailMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                APICampaignCaller.getCampaignById(token,"ce179be8-c93a-4627-b3d1-0f7be42fcc22", getActivity().getApplication(), new APIListener());
            }
        });

        editCartCountByUser();

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

    private int getShoppingCartCount() {
        cartCount = 0;
        for (Supplier supplier : supplierList) {
            productList = shoppingCart.get(supplier.getId());
            cartCount += productList.size();
        }
        return cartCount;
    }

    private void setUpShoppingCart(String token) {
        APICartCaller.getCartList(token, getActivity().getApplication(), new APIListener() {
            @Override
            public void onCartListFound(HashMap<String, List<CartProduct>> shoppingCart, List<Supplier> supplierList) {
                super.onCartListFound(shoppingCart, supplierList);
                List<String> productIdList = new ArrayList<>();
                for (Supplier supplier : supplierList) {
                    productList = shoppingCart.get(supplier.getId());
                    for (CartProduct product : productList) {
                        productIdList.add(product.getProduct().getProductId());
                    }
                }
                APICampaignCaller.getCampaignListByProductId(productIdList,null, getActivity().getApplication(), new APIListener() {
                    @Override
                    public void onCampaignListFound(List<Campaign> campaignList) {
                        super.onCampaignListFound(campaignList);
                        for (Campaign campaign : campaignList) {
                            for (Supplier supplier : supplierList) {
                                productList = shoppingCart.get(supplier.getId());
                                for (CartProduct product : productList) {
                                    if(product.getProduct().getProductId().equals(campaign.getProductId())) {
                                        product.getProduct().setCampaign(campaign);
                                    }
                                }
                            }
                        }
                        putShoppingCartToSession(shoppingCart, supplierList);
                    }

                    @Override
                    public void onNoCampaignFound() {
                        super.onNoCampaignFound();
                        putShoppingCartToSession(shoppingCart, supplierList);
                    }

                    @Override
                    public void onFailedAPICall(int code) {
                        super.onFailedAPICall(code);
                        putShoppingCartToSession(shoppingCart, supplierList);
                    }
                });
            }
            @Override
            public void onFailedAPICall(int code) {
                super.onFailedAPICall(code);
                shoppingCart = new HashMap<>();
                supplierList = new ArrayList<>();
                putShoppingCartToSession(shoppingCart, supplierList);
            }
        });
    }

    private void putShoppingCartToSession(HashMap<String, List<CartProduct>> shoppingCart, List<Supplier> supplierList) {
        try {
            sharedPreferences.edit()
                    .putString("SHOPPING_CART", ObjectSerializer.serialize((Serializable) shoppingCart))
                    .commit();
            sharedPreferences.edit()
                    .putString("SUPPLIER_LIST", ObjectSerializer.serialize((Serializable) supplierList))
                    .commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
        editCartCountByUser();
    }

    private void editCartCountByUser() {
        try {
            supplierList = (ArrayList<Supplier>) ObjectSerializer
                    .deserialize(sharedPreferences.getString("SUPPLIER_LIST", ""));
            shoppingCart = (HashMap<String, List<CartProduct>>) ObjectSerializer
                    .deserialize(sharedPreferences.getString("SHOPPING_CART", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(supplierList == null) {
            cardViewProductDetailCartCount.setVisibility(View.INVISIBLE);
        } else if (supplierList.size() != 0) {
            cardViewProductDetailCartCount.setVisibility(View.VISIBLE);
            txtProductDetailCartCount.setText(getShoppingCartCount() + "");
        } else {
            cardViewProductDetailCartCount.setVisibility(View.INVISIBLE);
        }
    }
}