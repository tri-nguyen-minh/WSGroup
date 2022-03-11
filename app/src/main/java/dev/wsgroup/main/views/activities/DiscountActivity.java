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

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.CustomerDiscount;
import dev.wsgroup.main.models.dtos.Supplier;
import dev.wsgroup.main.models.navigationAdapters.NavigationAdapter;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.views.fragments.DiscountFragment;
import dev.wsgroup.main.views.fragments.tabs.cart.CampaignTab;
import dev.wsgroup.main.views.fragments.tabs.cart.RetailTab;

public class DiscountActivity extends AppCompatActivity {

    private ImageView imgBackFromDiscount, imgDiscountHome;
    private TabLayout discountTabLayout;
    private ViewPager discountViewPager;
    private TabLayout.Tab tabCommon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discount);
        this.getSupportActionBar().hide();


        imgBackFromDiscount = findViewById(R.id.imgBackFromDiscount);
        imgDiscountHome = findViewById(R.id.imgDiscountHome);
        discountTabLayout = findViewById(R.id.discountTabLayout);
        discountViewPager = findViewById(R.id.discountViewPager);

        setupDiscountList();

        imgBackFromDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        imgDiscountHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }

    private void setupDiscountList() {
        discountTabLayout.removeAllTabs();
        discountViewPager.setAdapter(null);

        tabCommon = discountTabLayout.newTab();
        tabCommon.setText("APPLICABLE");
        discountTabLayout.addTab(tabCommon);

        tabCommon = discountTabLayout.newTab();
        tabCommon.setText("USED");
        discountTabLayout.addTab(tabCommon);

        tabCommon = discountTabLayout.newTab();
        tabCommon.setText("EXPIRED");
        discountTabLayout.addTab(tabCommon);

        discountTabLayout.setTabTextColors(getResources().getColor(R.color.black), getResources().getColor(R.color.black));
        NavigationAdapter adapter = new NavigationAdapter(getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @Override
            public Fragment getItem(int position) {
                Fragment fragment
                        = new DiscountFragment(discountTabLayout.getTabAt(position).getText().toString());
                return fragment;
            }

            @Override
            public int getCount() {
                return discountTabLayout.getTabCount();
            }
        };
        discountViewPager.setAdapter(adapter);
        discountTabLayout.clearOnTabSelectedListeners();
        discountViewPager.clearOnPageChangeListeners();
        discountViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(discountTabLayout));
        discountTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                discountViewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        discountTabLayout.selectTab(discountTabLayout.getTabAt(0));
        discountViewPager.setCurrentItem(discountTabLayout.getSelectedTabPosition());
    }
}