package dev.wsgroup.main.views.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APICartCaller;
import dev.wsgroup.main.models.apis.callers.APIUserCaller;
import dev.wsgroup.main.models.dtos.CartProduct;
import dev.wsgroup.main.models.dtos.User;
import dev.wsgroup.main.models.navigationAdapters.NavigationAdapter;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.views.activities.account.SignInActivity;
import dev.wsgroup.main.views.fragments.tabs.main.HistoryTab;
import dev.wsgroup.main.views.fragments.tabs.main.HomeTab;
import dev.wsgroup.main.views.fragments.tabs.main.ProfileTab;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private View viewCommon;
    private ConstraintLayout layoutMainPage, layoutLoading;
    private LinearLayout layoutFailed;
    private TextView lblRetry;

    private SharedPreferences sharedPreferences;
    private String userId, token;
    private int tabPosition, cartCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getSupportActionBar().hide();

        tabLayout = findViewById(R.id.mainTabLayout);
        viewPager = findViewById(R.id.mainViewPager);
        layoutMainPage = findViewById(R.id.layoutMainPage);
        layoutLoading = findViewById(R.id.layoutLoading);
        layoutFailed = findViewById(R.id.layoutFailed);
        lblRetry = findViewById(R.id.lblRetry);

        setupLayout();

        lblRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupLayout();
            }
        });
    }

    private void setupLayout() {
        setLoadingState();
        sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("USER_ID", "");
        tabPosition = getIntent().getIntExtra("MAIN_TAB_POSITION", 0);

        if(userId.isEmpty()) {
            setupTabLayout(0);
            setLoadedState();
        } else {
            token = sharedPreferences.getString("TOKEN", "");
            APIUserCaller.findUserByToken(token, getApplication(), new APIListener() {
                @Override
                public void onUserFound(User user, String message) {
                    user.setToken(token);
                    setUpShoppingCart(tabPosition);
                }

                @Override
                public void onFailedAPICall(int code) {
                    if (code == IntegerUtils.ERROR_NO_USER) {
                        MethodUtils.displayErrorAccountMessage(getApplicationContext(),
                                MainActivity.this);
                    } else {
                        setFailedState();
                    }
                }
            });
        }
    }

    private void setUpShoppingCart(int tabPosition) {
        APICartCaller.getCartList(token, getApplication(), new APIListener() {
            @Override
            public void onCartListFound(ArrayList<CartProduct> retailList,
                                        ArrayList<CartProduct> campaignList) {
                cartCount = retailList.size() + campaignList.size();
                getIntent().putExtra("CART_COUNT", cartCount);
                setupTabLayout(tabPosition);
                setLoadedState();
            }
            @Override
            public void onFailedAPICall(int code) {
                if (code == IntegerUtils.ERROR_NO_USER) {
                    MethodUtils.displayErrorAccountMessage(getApplicationContext(),
                            MainActivity.this);
                } else {
                    getIntent().putExtra("CART_COUNT", 0);
                    setupTabLayout(tabPosition);
                    setLoadedState();
                }
            }
        });
    }

    private void setupTabLayout(int tabPosition) {
        tabLayout.removeAllTabs();
        viewPager.setAdapter(null);

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

        NavigationAdapter adapter = new NavigationAdapter(getSupportFragmentManager()) {
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
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        tabLayout.selectTab(tabLayout.getTabAt(tabPosition));
        viewPager.setCurrentItem(tabLayout.getSelectedTabPosition());
    }

    private void setLoadingState() {
        layoutLoading.setVisibility(View.VISIBLE);
        layoutFailed.setVisibility(View.INVISIBLE);
        layoutMainPage.setVisibility(View.INVISIBLE);
    }

    private void setLoadedState() {
        layoutLoading.setVisibility(View.INVISIBLE);
        layoutFailed.setVisibility(View.INVISIBLE);
        layoutMainPage.setVisibility(View.VISIBLE);
    }

    private void setFailedState() {
        layoutLoading.setVisibility(View.INVISIBLE);
        layoutFailed.setVisibility(View.VISIBLE);
        layoutMainPage.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() { }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_CANCELED) {
            tabLayout.selectTab(tabLayout.getTabAt(0));
        } else if (resultCode == RESULT_OK) {
            tabPosition = 0;
            if(data != null) {
                getIntent().putExtra("MAIN_TAB_POSITION",
                        data.getIntExtra("MAIN_TAB_POSITION",0));
                getIntent().putExtra("HISTORY_TAB_POSITION",
                        data.getIntExtra("HISTORY_TAB_POSITION", 0));
            }
            setupLayout();
            if (!userId.isEmpty()) {
                setUpShoppingCart(tabPosition);
            }
        }
    }
}