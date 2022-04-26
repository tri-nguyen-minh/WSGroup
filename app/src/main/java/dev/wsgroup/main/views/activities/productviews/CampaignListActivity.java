package dev.wsgroup.main.views.activities.productviews;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APICampaignCaller;
import dev.wsgroup.main.models.dtos.Campaign;
import dev.wsgroup.main.models.dtos.CampaignMilestone;
import dev.wsgroup.main.models.dtos.Product;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewCampaignListAdapter;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewCampaignMilestoneListAdapter;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.views.activities.MainActivity;

public class CampaignListActivity extends AppCompatActivity {

    private ImageView imgBackFromCampaignList, imgCampaignListHome, imgExpand;
    private TextView txtProductRetailPrice, txtDiscountStartDate, txtDiscountEndDate,
            txtCampaignMaxQuantity, txtMilestonePrice, txtCampaignOrderCount,
            txtCampaignQuantityCount, txtCampaignQuantityBar, txtCampaignNote, lblProductOrderCount;
    private Button btnSelectBasePrice, btnSelectCampaign;
    private LinearLayout layoutFailed, layoutSharingCampaign, layoutSingleCampaign;
    private ConstraintLayout layoutMain, layoutCampaignMilestone;
    private RelativeLayout layoutNoCampaign, layoutLoading;
    private NestedScrollView scrollViewMain;
    private RecyclerView recViewCampaignView, recViewCampaignMilestone;
    private ProgressBar progressBarQuantityCount;

    private Product product;
    private String productId;
    private List<Campaign> campaignList;
    private Campaign sharingCampaign;
    private CampaignMilestone milestone;
    private RecViewCampaignListAdapter adapter;
    private RecViewCampaignMilestoneListAdapter milestoneAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign_list);
        this.getSupportActionBar().hide();

        imgBackFromCampaignList = findViewById(R.id.imgBackFromCampaignList);
        imgCampaignListHome = findViewById(R.id.imgCampaignListHome);
        imgExpand = findViewById(R.id.imgExpand);
        txtProductRetailPrice = findViewById(R.id.txtProductRetailPrice);
        txtDiscountStartDate = findViewById(R.id.txtDiscountStartDate);
        txtDiscountEndDate = findViewById(R.id.txtDiscountEndDate);
        txtCampaignMaxQuantity = findViewById(R.id.txtCampaignMaxQuantity);
        txtMilestonePrice = findViewById(R.id.txtMilestonePrice);
        txtCampaignOrderCount = findViewById(R.id.txtCampaignOrderCount);
        txtCampaignQuantityCount = findViewById(R.id.txtCampaignQuantityCount);
        txtCampaignQuantityBar = findViewById(R.id.txtCampaignQuantityBar);
        txtCampaignNote = findViewById(R.id.txtCampaignNote);
        lblProductOrderCount = findViewById(R.id.lblProductOrderCount);
        btnSelectBasePrice = findViewById(R.id.btnSelectBasePrice);
        btnSelectCampaign = findViewById(R.id.btnSelectCampaign);
        layoutFailed = findViewById(R.id.layoutFailed);
        layoutSharingCampaign = findViewById(R.id.layoutSharingCampaign);
        layoutSingleCampaign = findViewById(R.id.layoutSingleCampaign);
        layoutMain = findViewById(R.id.layoutMain);
        layoutCampaignMilestone = findViewById(R.id.layoutCampaignMilestone);
        layoutNoCampaign = findViewById(R.id.layoutNoCampaign);
        layoutLoading = findViewById(R.id.layoutLoading);
        scrollViewMain = findViewById(R.id.scrollViewMain);
        recViewCampaignView = findViewById(R.id.recViewCampaignView);
        recViewCampaignMilestone = findViewById(R.id.recViewCampaignMilestone);
        progressBarQuantityCount = findViewById(R.id.progressBarQuantityCount);

        setData();

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

    private void setData() {
        setLoadingState();
        product = (Product) getIntent().getSerializableExtra("PRODUCT");
        productId = product.getProductId();
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
            if (getAvailableQuantity() == 0) {
                System.out.println("test quantity");
                btnSelectBasePrice.setEnabled(false);
                btnSelectBasePrice.getBackground().setTint(getApplicationContext().getResources()
                        .getColor(R.color.gray_light));
            } else {
                btnSelectBasePrice.setEnabled(true);
                btnSelectBasePrice.getBackground().setTint(getApplicationContext().getResources()
                        .getColor(R.color.blue_main));
            }
        }
        getCampaignList();
    }

    private void getCampaignList() {
        layoutSharingCampaign.setVisibility(View.GONE);
        layoutSingleCampaign.setVisibility(View.GONE);
        APICampaignCaller.getCampaignListByProductId(productId, "active",
                campaignList, getApplication(), new APIListener() {
            @Override
            public void onCampaignListFound(List<Campaign> foundCampaignList) {
                if (foundCampaignList.size() > 0) {
                    campaignList = filterCampaignList(foundCampaignList);
                    setupCampaignList();
                } else {
                    setListLoadedState();
                    scrollViewMain.setVisibility(View.INVISIBLE);
                    layoutNoCampaign.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNoJSONFound() {
                setFailedState();
            }

            @Override
            public void onFailedAPICall(int code) {
                setFailedState();
            }
        });
    }

    private List<Campaign> filterCampaignList(List<Campaign> list) {
        List<Campaign> filteredList = new ArrayList<>();
        for (Campaign campaign : list) {
            if (campaign.getShareFlag()) {
                sharingCampaign = campaign;
            } else {
                filteredList.add(campaign);
            }
        }
        return filteredList;
    }

    private void setupCampaignList() {
        if (sharingCampaign != null) {
            layoutSharingCampaign.setVisibility(View.VISIBLE);
            recViewCampaignMilestone.setVisibility(View.GONE);
            txtCampaignNote.setText(sharingCampaign.getDescription());
            txtDiscountStartDate
                    .setText(MethodUtils.formatDate(sharingCampaign.getStartDate()));
            txtDiscountEndDate
                    .setText(MethodUtils.formatDate(sharingCampaign.getEndDate()));
            txtCampaignMaxQuantity.setText(sharingCampaign.getMaxQuantity() + "");
            imgExpand.setImageResource(R.drawable.ic_direction_down);
            milestone = MethodUtils.getReachedCampaignMilestone(
                    sharingCampaign.getMilestoneList(),
                    sharingCampaign.getQuantityCount());
            if (milestone == null) {
                txtMilestonePrice
                        .setText(MethodUtils.formatPriceString(sharingCampaign.getPrice()));
            } else {
                txtMilestonePrice
                        .setText(MethodUtils.formatPriceString(milestone.getPrice()));
            }
            txtCampaignOrderCount.setText(sharingCampaign.getOrderCount() + "");
            txtCampaignQuantityCount.setText(sharingCampaign.getQuantityCount() + "");
            txtCampaignQuantityBar.setText(sharingCampaign.getMaxQuantity() + "");
            lblProductOrderCount
                    .setText((sharingCampaign.getOrderCount() == 1) ? "order" : "orders");
            progressBarQuantityCount.setMax(sharingCampaign.getMaxQuantity());
            progressBarQuantityCount.setProgress(sharingCampaign.getQuantityCount());
            progressBarQuantityCount.getProgressDrawable()
                                    .setColorFilter(getResources().getColor(R.color.blue_main),
                                                    android.graphics.PorterDuff.Mode.SRC_IN);
            layoutCampaignMilestone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println("VISIBLE: " + (recViewCampaignMilestone.getVisibility() == View.VISIBLE));
                    System.out.println(recViewCampaignMilestone.getAdapter().getItemCount());
                    if (recViewCampaignMilestone.getVisibility() == View.GONE) {
                        recViewCampaignMilestone.setVisibility(View.VISIBLE);
                        imgExpand.setImageResource(R.drawable.ic_direction_up);
                    } else {
                        recViewCampaignMilestone.setVisibility(View.GONE);
                        imgExpand.setImageResource(R.drawable.ic_direction_down);
                    }
                }
            });
            milestoneAdapter = new RecViewCampaignMilestoneListAdapter(getApplicationContext(),
                    product.getRetailPrice());
            milestoneAdapter.setCampaign(sharingCampaign);
            recViewCampaignMilestone.setAdapter(milestoneAdapter);
            recViewCampaignMilestone.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                    LinearLayoutManager.VERTICAL,false));
            if (!sharingCampaign.getStatus().equals("active")) {
                btnSelectCampaign.setVisibility(View.GONE);
            } else {
                btnSelectCampaign.setVisibility(View.VISIBLE);
                btnSelectCampaign.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getIntent().putExtra("CAMPAIGN_SELECTED", sharingCampaign);
                        getIntent().putExtra("REQUEST_CODE", IntegerUtils.REQUEST_SELECT_CAMPAIGN);
                        setResult(Activity.RESULT_OK, getIntent());
                        finish();
                    }
                });
            }
        }
        if (campaignList != null && campaignList.size() > 0) {
            layoutSingleCampaign.setVisibility(View.VISIBLE);
            recViewCampaignView.setVisibility(View.VISIBLE);
            adapter = new RecViewCampaignListAdapter(getApplicationContext(), product.getRetailPrice()) {
                @Override
                public void executeOnCampaignSelected(Campaign campaign) {
                    getIntent().putExtra("CAMPAIGN_SELECTED", campaign);
                    getIntent().putExtra("REQUEST_CODE", IntegerUtils.REQUEST_SELECT_CAMPAIGN);
                    setResult(Activity.RESULT_OK, getIntent());
                    finish();
                }
            };
            adapter.setCampaignList(campaignList);
            recViewCampaignView.setAdapter(adapter);
            recViewCampaignView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                    LinearLayoutManager.VERTICAL, false));
        }
        setListLoadedState();
    }

    private int getAvailableQuantity() {
        int count = 0;
        if (product.getCampaignList().size() > 0) {
            for (Campaign campaign : product.getCampaignList()) {
                count += campaign.getMaxQuantity();
            }
        }
        count = product.getQuantity() - count;
        return Math.max(count, 0);
    }

    private void setLoadingState() {
        layoutLoading.setVisibility(View.VISIBLE);
        layoutFailed.setVisibility(View.INVISIBLE);
        layoutMain.setVisibility(View.INVISIBLE);
    }

    private void setListLoadedState() {
        layoutLoading.setVisibility(View.INVISIBLE);
        layoutFailed.setVisibility(View.INVISIBLE);
        layoutMain.setVisibility(View.VISIBLE);
        scrollViewMain.setVisibility(View.VISIBLE);
        layoutNoCampaign.setVisibility(View.INVISIBLE);
    }

    private void setFailedState() {
        layoutLoading.setVisibility(View.INVISIBLE);
        layoutFailed.setVisibility(View.VISIBLE);
        layoutMain.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        imgBackFromCampaignList.performClick();
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