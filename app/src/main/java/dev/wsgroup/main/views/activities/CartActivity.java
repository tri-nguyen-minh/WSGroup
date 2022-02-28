package dev.wsgroup.main.views.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APICampaignCaller;
import dev.wsgroup.main.models.apis.callers.APICartCaller;
import dev.wsgroup.main.models.dtos.Campaign;
import dev.wsgroup.main.models.dtos.CartProduct;
import dev.wsgroup.main.models.dtos.Product;
import dev.wsgroup.main.models.dtos.Supplier;
import dev.wsgroup.main.models.navigationAdapters.NavigationAdapter;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.ObjectSerializer;
import dev.wsgroup.main.views.fragments.tabs.cart.CampaignTab;
import dev.wsgroup.main.views.fragments.tabs.cart.RetailTab;

public class CartActivity extends AppCompatActivity {

    private ImageView imgBackFromCart, imgCartMessage, imgCartHome;
    private LinearLayout layoutFailedGettingCart, layoutNoShoppingCart;
    private ConstraintLayout layoutCart, layoutLoading;
    private TabLayout cartTabLayout;
    private ViewPager cartViewPager;
    private TextView txtCartDescription, lblRetryGetCart;
    private TabLayout.Tab tabCommon;

    private SharedPreferences sharedPreferences;
    private List<Supplier> supplierRetailList, supplierCampaignList;
    private HashMap<String, List<CartProduct>> retailCart, campaignCart;
    private List<CartProduct> retailCartProductList, campaignCartProductList;
    private int retailCount, campaignCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        this.getSupportActionBar().hide();

        imgBackFromCart = findViewById(R.id.imgBackFromCart);
        imgCartMessage = findViewById(R.id.imgCartMessage);
        imgCartHome = findViewById(R.id.imgCartHome);
        layoutFailedGettingCart = findViewById(R.id.layoutFailedGettingCart);
        layoutNoShoppingCart = findViewById(R.id.layoutNoShoppingCart);
        layoutCart = findViewById(R.id.layoutCart);
        layoutLoading = findViewById(R.id.layoutLoading);
        cartTabLayout = findViewById(R.id.cartTabLayout);
        cartViewPager = findViewById(R.id.cartViewPager);
        txtCartDescription = findViewById(R.id.txtCartDescription);
        lblRetryGetCart = findViewById(R.id.lblRetryGetCart);

        layoutLoading.setVisibility(View.VISIBLE);
        layoutFailedGettingCart.setVisibility(View.INVISIBLE);
        layoutNoShoppingCart.setVisibility(View.INVISIBLE);
        layoutCart.setVisibility(View.INVISIBLE);
        setUpShoppingCart();

        imgBackFromCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });

        imgCartHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        lblRetryGetCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutLoading.setVisibility(View.VISIBLE);
                layoutFailedGettingCart.setVisibility(View.INVISIBLE);
                layoutNoShoppingCart.setVisibility(View.INVISIBLE);
                layoutCart.setVisibility(View.INVISIBLE);
                setUpShoppingCart();
            }
        });
    }

    private void setUpShoppingCart() {
        sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("TOKEN", "");
        APICartCaller.getCartList(token, getApplication(), new APIListener() {
            @Override
            public void onCartListFound(HashMap<String, List<CartProduct>> rCart, List<Supplier> rList,
                                        HashMap<String, List<CartProduct>> cCart, List<Supplier> cList) {
                retailCart = rCart;
                campaignCart = cCart;
                supplierRetailList = rList;
                supplierCampaignList = cList;
                if (supplierRetailList.size() > 0 || supplierCampaignList.size() > 0) {
                    finishRetailCart();
                } else {
                    retailCart = new HashMap<>();
                    campaignCart = new HashMap<>();
                    supplierRetailList = new ArrayList<>();
                    supplierCampaignList = new ArrayList<>();
                    putCartToSession();
                    setupEmptyCustomerCart();
                }
            }
            @Override
            public void onFailedAPICall(int code) {
                super.onFailedAPICall(code);
                if (code == IntegerUtils.ERROR_API) {
                    setupFailedCustomerCart();
                } else {
                    retailCart = new HashMap<>();
                    campaignCart = new HashMap<>();
                    supplierRetailList = new ArrayList<>();
                    supplierCampaignList = new ArrayList<>();
                    putCartToSession();
                    setupEmptyCustomerCart();
                }
            }
        });
    }

    private void finishRetailCart() {
        if (supplierRetailList.size() > 0) {
            retailCount = 0;
            for (Supplier supplier : supplierRetailList) {
                retailCartProductList = retailCart.get(supplier.getId());
                retailCount += retailCartProductList.size();
            }
            for (Supplier supplier : supplierRetailList) {
                retailCartProductList = retailCart.get(supplier.getId());
                for (CartProduct cartProduct : retailCartProductList) {
                    APICampaignCaller.getCampaignListByProductId(cartProduct.getProduct().getProductId(),
                            "active", null, getApplication(), new APIListener() {
                                @Override
                                public void onCampaignListFound(List<Campaign> campaignList) {
                                    super.onCampaignListFound(campaignList);
                                    retailCount--;
                                    Product product = cartProduct.getProduct();
                                    product.setCampaignList(campaignList);
                                    cartProduct.setProduct(product);
                                    if (retailCount == 0) {
                                        finishCampaignCart();
                                    }
                                }
                                @Override
                                public void onNoJSONFound() {
                                    super.onNoJSONFound();
                                    retailCount--;
                                    Product product = cartProduct.getProduct();
                                    product.setCampaignList(new ArrayList<Campaign>());
                                    cartProduct.setProduct(product);
                                    if (retailCount == 0) {
                                        finishCampaignCart();
                                    }
                                }
                            });
                }
            }
        } else {
            finishCampaignCart();
        }
    }

    private void finishCampaignCart() {
        if (supplierCampaignList.size() > 0) {
            campaignCount = 0;
            for (Supplier supplier : supplierCampaignList) {
                campaignCartProductList = campaignCart.get(supplier.getId());
                campaignCount += campaignCartProductList.size();
            }
            for (Supplier supplier : supplierCampaignList) {
                campaignCartProductList = campaignCart.get(supplier.getId());
                for (CartProduct cartProduct : campaignCartProductList) {
                    APICampaignCaller.getCampaignById(cartProduct.getCampaign().getId(),
                            getApplication(), new APIListener() {
                                @Override
                                public void onCampaignFound(Campaign campaign) {
                                    cartProduct.setCampaign(campaign);
                                    campaignCount--;
                                    if (campaignCount == 0) {
                                        putCartToSession();
                                        setupCustomerCart();
                                    }
                                }

                                @Override
                                public void onNoJSONFound() {
                                    campaignCount--;
                                    if (campaignCount == 0) {
                                        putCartToSession();
                                        setupCustomerCart();
                                    }
                                }

                                @Override
                                public void onFailedAPICall(int code) {
                                    super.onFailedAPICall(code);
                                    setupFailedCustomerCart();
                                }
                            });
                }
            }
        }
    }

    private void putCartToSession() {
        try {
            sharedPreferences.edit()
                    .putString("RETAIL_CART", ObjectSerializer.serialize((Serializable) retailCart))
                    .putString("SUPPLIER_RETAIL_LIST", ObjectSerializer.serialize((Serializable) supplierRetailList))
                    .putString("CAMPAIGN_CART", ObjectSerializer.serialize((Serializable) campaignCart))
                    .putString("SUPPLIER_CAMPAIGN_LIST", ObjectSerializer.serialize((Serializable) supplierCampaignList))
                    .commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupCustomerCart() {
        layoutLoading.setVisibility(View.INVISIBLE);
        layoutFailedGettingCart.setVisibility(View.INVISIBLE);
        layoutNoShoppingCart.setVisibility(View.INVISIBLE);
        layoutCart.setVisibility(View.VISIBLE);
        setupTabLayout();
    }

    private void setupEmptyCustomerCart() {
        layoutLoading.setVisibility(View.INVISIBLE);
        layoutFailedGettingCart.setVisibility(View.INVISIBLE);
        layoutNoShoppingCart.setVisibility(View.VISIBLE);
        layoutCart.setVisibility(View.INVISIBLE);
    }

    private void setupFailedCustomerCart() {
        layoutLoading.setVisibility(View.INVISIBLE);
        layoutFailedGettingCart.setVisibility(View.VISIBLE);
        layoutNoShoppingCart.setVisibility(View.INVISIBLE);
        layoutCart.setVisibility(View.INVISIBLE);
    }

    private void setupTabLayout() {
        cartTabLayout.removeAllTabs();
        cartViewPager.setAdapter(null);

        tabCommon = cartTabLayout.newTab();
        tabCommon.setText("RETAIL");
        cartTabLayout.addTab(tabCommon);

        tabCommon = cartTabLayout.newTab();
        tabCommon.setText("CAMPAIGN");
        cartTabLayout.addTab(tabCommon);

        cartTabLayout.setTabTextColors(getResources().getColor(R.color.black), getResources().getColor(R.color.black));
        NavigationAdapter adapter = new NavigationAdapter(getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0: {
                        return new RetailTab();
                    }
                    case 1: {
                        return new CampaignTab();
                    }
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return cartTabLayout.getTabCount();
            }
        };
        cartViewPager.setAdapter(adapter);
        cartTabLayout.clearOnTabSelectedListeners();
        cartViewPager.clearOnPageChangeListeners();
        cartViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(cartTabLayout));
        cartTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                cartViewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getIcon() != null)
                    tab.getIcon().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_IN);
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        cartTabLayout.selectTab(cartTabLayout.getTabAt(0));
        cartViewPager.setCurrentItem(cartTabLayout.getSelectedTabPosition());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        layoutLoading.setVisibility(View.VISIBLE);
        layoutFailedGettingCart.setVisibility(View.INVISIBLE);
        layoutNoShoppingCart.setVisibility(View.INVISIBLE);
        layoutCart.setVisibility(View.INVISIBLE);
        setUpShoppingCart();
    }
}