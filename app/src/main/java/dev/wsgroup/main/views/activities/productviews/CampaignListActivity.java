package dev.wsgroup.main.views.activities.productviews;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.Product;
import dev.wsgroup.main.models.navigationAdapters.NavigationAdapter;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.views.activities.MainActivity;
import dev.wsgroup.main.views.fragments.CampaignFragment;

public class CampaignListActivity extends AppCompatActivity {

    private ImageView imgBackFromCampaignList, imgCampaignListHome;
    private TextView txtProductRetailPrice;
    private Button btnSelectBasePrice;
    private ViewPager historyViewPager;
    private TabLayout historyTabLayout;
    private TabLayout.Tab tabCommon;

    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign_list);
        this.getSupportActionBar().hide();

        imgBackFromCampaignList = findViewById(R.id.imgBackFromCampaignList);
        imgCampaignListHome = findViewById(R.id.imgCampaignListHome);
        txtProductRetailPrice = findViewById(R.id.txtProductRetailPrice);
        btnSelectBasePrice = findViewById(R.id.btnSelectBasePrice);
        historyViewPager = findViewById(R.id.CampaignListViewPager);
        historyTabLayout = findViewById(R.id.CampaignListLayout);

        product = (Product) getIntent().getSerializableExtra("PRODUCT");
        if (product != null) {
            txtProductRetailPrice.setText(MethodUtils.formatPriceString(product.getRetailPrice()));
            btnSelectBasePrice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getIntent().putExtra("REQUEST_CODE", IntegerUtils.REQUEST_SELECT_CAMPAIGN);
                    setResult(Activity.RESULT_OK, getIntent());
                    finish();
                }
            });
        }
        setupTabLayout();

        imgBackFromCampaignList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        imgCampaignListHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

    }

    private void setupTabLayout() {
        tabCommon = historyTabLayout.newTab();
        tabCommon.setText("Ongoing");
        historyTabLayout.addTab(tabCommon);

        tabCommon = historyTabLayout.newTab();
        tabCommon.setText("Finished");
        historyTabLayout.addTab(tabCommon);

        tabCommon = historyTabLayout.newTab();
        tabCommon.setText("Upcoming");
        historyTabLayout.addTab(tabCommon);

        historyTabLayout.setTabTextColors(getResources().getColor(R.color.black),
                                            getResources().getColor(R.color.black));
        NavigationAdapter adapter = new NavigationAdapter(getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @Override
            public Fragment getItem(int position) {
                return  new CampaignFragment(historyTabLayout.getTabAt(position)
                                                             .getText().toString());
            }

            @Override
            public int getCount() {
                return historyTabLayout.getTabCount();
            }
        };
        historyViewPager.setAdapter(adapter);
        historyTabLayout.clearOnTabSelectedListeners();
        historyViewPager.clearOnPageChangeListeners();
        historyViewPager.setEnabled(false);
        historyViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(historyTabLayout));
        historyTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                historyViewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getIcon() != null)
                    tab.getIcon().setColorFilter(getResources()
                                 .getColor(R.color.black), PorterDuff.Mode.SRC_IN);
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        historyViewPager.setCurrentItem(historyTabLayout.getSelectedTabPosition());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }
}