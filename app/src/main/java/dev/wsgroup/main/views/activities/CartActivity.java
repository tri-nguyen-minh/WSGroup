package dev.wsgroup.main.views.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APICampaignCaller;
import dev.wsgroup.main.models.apis.callers.APICartCaller;
import dev.wsgroup.main.models.dtos.Campaign;
import dev.wsgroup.main.models.dtos.CartProduct;
import dev.wsgroup.main.models.navigationAdapters.NavigationAdapter;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.ObjectSerializer;
import dev.wsgroup.main.models.utils.StringUtils;
import dev.wsgroup.main.views.dialogbox.DialogBoxAlert;
import dev.wsgroup.main.views.fragments.tabs.cart.CampaignTab;
import dev.wsgroup.main.views.fragments.tabs.cart.RetailTab;

public class CartActivity extends AppCompatActivity {

    private ImageView imgBackFromCart, imgCartHome;
    private LinearLayout layoutFailedGettingCart, layoutNoShoppingCart;
    private ConstraintLayout layoutCart, layoutLoading;
    private TabLayout cartTabLayout;
    private ViewPager cartViewPager;
    private TextView lblRetryGetCart;
    private TabLayout.Tab tabCommon;

    private boolean retailCartCheck, campaignCartCheck,
            retailCartRemovalCheck, campaignCartRemovalCheck;
    private String token;
    private int campaignCount, retailCount;
    private SharedPreferences sharedPreferences;
    private List<CartProduct> tempRetailCartList, tempCampaignCartList,
            retailCartProductList, campaignCartProductList;
    private List<Campaign> campaignList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        this.getSupportActionBar().hide();

        imgBackFromCart = findViewById(R.id.imgBackFromCart);
        imgCartHome = findViewById(R.id.imgCartHome);
        layoutFailedGettingCart = findViewById(R.id.layoutFailedGettingCart);
        layoutNoShoppingCart = findViewById(R.id.layoutNoShoppingCart);
        layoutCart = findViewById(R.id.layoutCart);
        layoutLoading = findViewById(R.id.layoutLoading);
        cartTabLayout = findViewById(R.id.cartTabLayout);
        cartViewPager = findViewById(R.id.cartViewPager);
        lblRetryGetCart = findViewById(R.id.lblRetryGetCart);

        layoutLoading.setVisibility(View.VISIBLE);
        layoutFailedGettingCart.setVisibility(View.INVISIBLE);
        layoutNoShoppingCart.setVisibility(View.INVISIBLE);
        layoutCart.setVisibility(View.INVISIBLE);
        setUpShoppingCart();

        imgBackFromCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK, getIntent());
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
        token = sharedPreferences.getString("TOKEN", "");
        if (!token.isEmpty()) {
            APICartCaller.getCartList(token, getApplication(), new APIListener() {
            @Override
            public void onCartListFound(List<CartProduct> retailList,
                                        List<CartProduct> campaignList) {
                retailCartCheck = false; campaignCartCheck = false;
                retailCartRemovalCheck = false; campaignCartRemovalCheck = false;
                retailCartProductList = retailList;
                campaignCartProductList = campaignList;
//                checkValidCart(tempRetailCartList, false);
//                checkValidCart(tempCampaignCartList, true);
                putCartToSession();
                setupCustomerCart();
            }
            @Override
            public void onFailedAPICall(int code) {
                if (code == IntegerUtils.ERROR_NO_USER) {
                    MethodUtils.displayErrorAccountMessage(getApplicationContext(),
                            CartActivity.this);
                } else if (code == IntegerUtils.ERROR_API) {
                    setupFailedCustomerCart();
                } else {
                    retailCartProductList = new ArrayList<>();
                    campaignCartProductList = new ArrayList<>();
                    putCartToSession();
                    setupEmptyCustomerCart();
                }
            }
        });
        }
    }

    private void checkValidCart(List<CartProduct> cartProductList, boolean isCampaign) {
        if (isCampaign) {
            campaignCount = cartProductList.size();
            for (CartProduct cartProduct : cartProductList) {
                APICampaignCaller.getCampaignById(cartProduct.getCampaign().getId(),
                        getApplication(), new APIListener() {
                    @Override
                    public void onCampaignFound(Campaign campaign) {
                        if (!campaign.getStatus().equals("active")) {
                            APICartCaller.deleteCartItem(token, cartProduct.getId(),
                                    getApplication(), new APIListener() {
                                @Override
                                public void onUpdateSuccessful() {
                                    cartProductList.remove(cartProduct);
                                    campaignCartRemovalCheck = true;
                                    campaignCount--;
                                    if (campaignCount == 0) {
                                        campaignCartProductList = cartProductList;
                                        campaignCartCheck = true;
                                        finishShoppingCart();
                                    }
                                }

                                @Override
                                public void onFailedAPICall(int code) {
                                    if (code == IntegerUtils.ERROR_NO_USER) {
                                        MethodUtils.displayErrorAccountMessage(getApplicationContext(),
                                                CartActivity.this);
                                    } else {
                                        setupFailedCustomerCart();
                                    }
                                }
                            });
                        } else {
                            campaignCount--;
                            if (campaignCount == 0) {
                                campaignCartProductList = cartProductList;
                                campaignCartCheck = true;
                                finishShoppingCart();
                            }
                        }
                    }

                    @Override
                    public void onNoJSONFound() {
                        cartProductList.remove(cartProduct);
                        campaignCartRemovalCheck = true;
                        campaignCount--;
                        if (campaignCount == 0) {
                            campaignCartProductList = cartProductList;
                            campaignCartCheck = true;
                            finishShoppingCart();
                        }
                    }

                    @Override
                    public void onFailedAPICall(int code) {
                        setupFailedCustomerCart();
                    }
                });
            }
        } else {
            retailCartProductList = cartProductList;
            retailCartCheck = true;
            finishShoppingCart();
        }
    }

    private void finishShoppingCart() {
        if (retailCartRemovalCheck || campaignCartRemovalCheck) {
            DialogBoxAlert dialogBoxAlert = new DialogBoxAlert(CartActivity.this,
                    IntegerUtils.CONFIRM_ACTION_CODE_ALERT, StringUtils.MES_ALERT_INVALID_ORDER);
            dialogBoxAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogBoxAlert.show();
        }
        if (retailCartCheck && campaignCartCheck) {
            Comparator<CartProduct> comparator = new Comparator<CartProduct>() {
                @Override
                public int compare(CartProduct cart1, CartProduct cart2) {
                    return cart1.getProduct().getName().compareTo(cart2.getProduct().getName());
                }
            };
            Collections.sort(retailCartProductList, comparator);
            Collections.sort(campaignCartProductList, comparator);
            putCartToSession();
            setupCustomerCart();
        }
    }

    private void putCartToSession() {
        try {
            sharedPreferences.edit()
                    .putString("RETAIL_CART",
                            ObjectSerializer.serialize((Serializable) retailCartProductList))
                    .putString("CAMPAIGN_CART",
                            ObjectSerializer.serialize((Serializable) campaignCartProductList))
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
    public void onBackPressed() {
        imgBackFromCart.performClick();
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