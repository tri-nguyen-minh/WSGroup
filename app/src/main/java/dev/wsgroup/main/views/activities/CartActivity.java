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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APICartCaller;
import dev.wsgroup.main.models.dtos.CartProduct;
import dev.wsgroup.main.models.navigationAdapters.NavigationAdapter;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.ObjectSerializer;
import dev.wsgroup.main.views.fragments.tabs.cart.CampaignTab;
import dev.wsgroup.main.views.fragments.tabs.cart.RetailTab;

public class CartActivity extends AppCompatActivity {

    private ImageView imgBackFromCart, imgCartHome;
    private LinearLayout layoutFailedGettingCart;
    private RelativeLayout layoutNoShoppingCart;
    private ConstraintLayout layoutCart, layoutLoading;
    private TabLayout cartTabLayout;
    private ViewPager cartViewPager;
    private TextView lblRetryGetCart;
    private TabLayout.Tab tabCommon;

    private String token;
    private SharedPreferences sharedPreferences;
    private List<CartProduct> retailCartProductList, campaignCartProductList;

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
                setLoadingState();
                setUpShoppingCart();
            }
        });
    }

    private void setUpShoppingCart() {
        setLoadingState();
        sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("TOKEN", "");
        if (!token.isEmpty()) {
            APICartCaller.getCartList(token, getApplication(), new APIListener() {
            @Override
            public void onCartListFound(List<CartProduct> retailList,
                                        List<CartProduct> campaignList) {
                retailCartProductList = retailList;
                campaignCartProductList = campaignList;
                putCartToSession();
                setLoadedState();
            }
            @Override
            public void onFailedAPICall(int code) {
                if (code == IntegerUtils.ERROR_NO_USER) {
                    MethodUtils.displayErrorAccountMessage(getApplicationContext(),
                            CartActivity.this);
                } else if (code == IntegerUtils.ERROR_API) {
                    setFailedState();
                } else {
                    retailCartProductList = new ArrayList<>();
                    campaignCartProductList = new ArrayList<>();
                    putCartToSession();
                    setupEmptyState();
                }
            }
        });
        }
    }

    private void putCartToSession() {
        try {
            sharedPreferences.edit()
                    .putString("RETAIL_CART",
                            ObjectSerializer.serialize((Serializable) retailCartProductList))
                    .putString("CAMPAIGN_CART",
                            ObjectSerializer.serialize((Serializable) campaignCartProductList))
                    .apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        cartTabLayout.setTabTextColors(getResources()
                     .getColor(R.color.black), getResources().getColor(R.color.black));
        NavigationAdapter adapter = new NavigationAdapter(getSupportFragmentManager()) {
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

    private void setLoadingState() {
        layoutLoading.setVisibility(View.VISIBLE);
        layoutFailedGettingCart.setVisibility(View.INVISIBLE);
        layoutNoShoppingCart.setVisibility(View.INVISIBLE);
        layoutCart.setVisibility(View.INVISIBLE);
    }

    private void setLoadedState() {
        layoutLoading.setVisibility(View.INVISIBLE);
        layoutFailedGettingCart.setVisibility(View.INVISIBLE);
        layoutNoShoppingCart.setVisibility(View.INVISIBLE);
        layoutCart.setVisibility(View.VISIBLE);
        setupTabLayout();
    }

    private void setupEmptyState() {
        layoutLoading.setVisibility(View.INVISIBLE);
        layoutFailedGettingCart.setVisibility(View.INVISIBLE);
        layoutNoShoppingCart.setVisibility(View.VISIBLE);
        layoutCart.setVisibility(View.INVISIBLE);
    }

    private void setFailedState() {
        layoutLoading.setVisibility(View.INVISIBLE);
        layoutFailedGettingCart.setVisibility(View.VISIBLE);
        layoutNoShoppingCart.setVisibility(View.INVISIBLE);
        layoutCart.setVisibility(View.INVISIBLE);
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