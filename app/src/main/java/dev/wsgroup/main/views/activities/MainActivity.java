package dev.wsgroup.main.views.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.callers.APICampaignCaller;
import dev.wsgroup.main.models.apis.callers.APICartCaller;
import dev.wsgroup.main.models.apis.callers.APIUserCaller;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.dtos.Campaign;
import dev.wsgroup.main.models.dtos.CartProduct;
import dev.wsgroup.main.models.dtos.Supplier;
import dev.wsgroup.main.models.dtos.User;
import dev.wsgroup.main.models.navigationAdapters.NavigationAdapter;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.ObjectSerializer;
import dev.wsgroup.main.views.activities.account.SignInActivity;
import dev.wsgroup.main.views.fragments.tabs.main.HistoryTab;
import dev.wsgroup.main.views.fragments.tabs.main.HomeTab;
import dev.wsgroup.main.views.fragments.tabs.main.ProfileTab;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private View viewCommon;
    private ConstraintLayout layoutMainPage, layoutLoading;

    private SharedPreferences sharedPreferences;
    private List<Supplier> supplierRetailList, supplierCampaignList;
    private HashMap<String, List<CartProduct>> retailCart, campaignCart;

    private String userId, token;
    private int tabPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getSupportActionBar().hide();

        tabLayout = findViewById(R.id.mainTabLayout);
        viewPager = findViewById(R.id.mainViewPager);
        layoutMainPage = findViewById(R.id.layoutMainPage);
        layoutLoading = findViewById(R.id.layoutLoading);

        sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("USER_ID", "");
        tabPosition = getIntent().getIntExtra("MAIN_TAB_POSITION", 0);

        layoutMainPage.setVisibility(View.INVISIBLE);
        layoutLoading.setVisibility(View.VISIBLE);

        if(userId.isEmpty()) {
            setupTabLayout(0);
        } else {
            token = sharedPreferences.getString("TOKEN", "");
            APIUserCaller.findUserByToken(token, getApplication(), new APIListener() {
                @Override
                public void onUserFound(User user) {
                    user.setToken(token);
                    sharedPreferences.edit().putString("TOKEN", user.getToken()).commit();
                    setUpShoppingCart(tabPosition);
                }

                @Override
                public void onFailedAPICall(int errorCode) {
                    super.onFailedAPICall(errorCode);
                    sharedPreferences.edit().clear().commit();
                    userId = "";
                    setUpShoppingCart(tabPosition);
                }
            });
        }
    }

    private void setUpShoppingCart(int tabPosition) {
        token = sharedPreferences.getString("TOKEN", "");
        APICartCaller.getCartList(token, getApplication(), new APIListener() {
            @Override
            public void onCartListFound(HashMap<String, List<CartProduct>> rCart, List<Supplier> rList,
                                        HashMap<String, List<CartProduct>> cCart, List<Supplier> cList) {
                retailCart = rCart;
                campaignCart = cCart;
                supplierRetailList = rList;
                supplierCampaignList = cList;
                putSessionCart(tabPosition);
            }
            @Override
            public void onFailedAPICall(int code) {
                super.onFailedAPICall(code);
                retailCart = new HashMap<>();
                campaignCart = new HashMap<>();
                supplierRetailList = new ArrayList<>();
                supplierCampaignList = new ArrayList<>();
                putSessionCart(tabPosition);
            }
        });
    }

    private void putSessionCart(int tabPosition) {
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
        setupTabLayout(tabPosition);
    }

    private void setupTabLayout(int tabPosition) {
        layoutMainPage.setVisibility(View.VISIBLE);
        layoutLoading.setVisibility(View.INVISIBLE);
        tabLayout.removeAllTabs();
        viewPager.setAdapter(null);

//        tabCommon.setText("Home");
//        tabCommon.setIcon(R.drawable.ic_home);
//        tabCommon.getIcon().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_IN);
//        tabLayout.addTab(tabCommon);

//        tabCommon = tabLayout.newTab();
//        tabCommon.setText("History");
//        tabCommon.setIcon(R.drawable.ic_history);
//        tabCommon.getIcon().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_IN);
//        tabLayout.addTab(tabCommon);

//        tabCommon = tabLayout.newTab();
//        tabCommon.setText("Profile");
//        tabCommon.setIcon(R.drawable.ic_profile);
//        tabCommon.getIcon().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_IN);
//        tabLayout.addTab(tabCommon);

        viewCommon = getLayoutInflater().inflate(R.layout.custom_tab, null);
        viewCommon.findViewById(R.id.icon).setBackgroundResource(R.drawable.ic_home);
        tabLayout.addTab(tabLayout.newTab().setCustomView(viewCommon));

        viewCommon = getLayoutInflater().inflate(R.layout.custom_tab, null);
        viewCommon.findViewById(R.id.icon).setBackgroundResource(R.drawable.ic_history);
        tabLayout.addTab(tabLayout.newTab().setCustomView(viewCommon));

        viewCommon = getLayoutInflater().inflate(R.layout.custom_tab, null);
        viewCommon.findViewById(R.id.icon).setBackgroundResource(R.drawable.ic_profile);
        tabLayout.addTab(tabLayout.newTab().setCustomView(viewCommon));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabTextColors(getResources().getColor(R.color.black), getResources().getColor(R.color.black));

        NavigationAdapter adapter = new NavigationAdapter(getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0: {
                        return new HomeTab();
                    }
                    case 1: {
                        return new HistoryTab();
                    }
                    case 2: {
                        return new ProfileTab();
                    }
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return tabLayout.getTabCount();
            }
        };
        viewPager.setAdapter(adapter);
        tabLayout.clearOnTabSelectedListeners();
        viewPager.clearOnPageChangeListeners();
        viewPager.setEnabled(false);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(!userId.isEmpty()) {
                    viewPager.setCurrentItem(tab.getPosition());

//                    if (tab.getIcon() != null)
//                        tab.getIcon().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_IN);
                } else {
                    int selectedTab = tab.getPosition();
                    if (selectedTab > 0) {
                        Intent SignInIntent = new Intent(getApplicationContext(), SignInActivity.class);
                        SignInIntent.putExtra("MAIN_TAB_POSITION", selectedTab);
                        startActivityForResult(SignInIntent, IntegerUtils.REQUEST_LOGIN);
                    }
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
//                if (tab.getIcon() != null)
//                    tab.getIcon().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_IN);
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        tabLayout.selectTab(tabLayout.getTabAt(tabPosition));
        viewPager.setCurrentItem(tabLayout.getSelectedTabPosition());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_CANCELED) {
            tabLayout.selectTab(tabLayout.getTabAt(0));
        } else if (resultCode == RESULT_OK) {
            layoutMainPage.setVisibility(View.INVISIBLE);
            layoutLoading.setVisibility(View.VISIBLE);
            sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
            userId = sharedPreferences.getString("USER_ID", "");
            tabPosition = 0;
            if(data != null) {
                tabPosition = data.getIntExtra("MAIN_TAB_POSITION",0);
            }
            setUpShoppingCart(tabPosition);
        }
    }
}