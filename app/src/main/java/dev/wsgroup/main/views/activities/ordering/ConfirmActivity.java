package dev.wsgroup.main.views.activities.ordering;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.Campaign;
import dev.wsgroup.main.models.dtos.CustomerDiscount;
import dev.wsgroup.main.models.dtos.Order;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewOrderSupplierListAdapter;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.StringUtils;
import dev.wsgroup.main.views.activities.MainActivity;
import dev.wsgroup.main.views.dialogbox.DialogBoxConfirm;

public class ConfirmActivity extends AppCompatActivity {

    private ImageView imgBackFromCheckout, imgCheckoutHome;
    private TextView txtCampaignDescription, txtCampaignPrice, txtCampaignOrderCount,
                    txtCampaignOrderQuantityCount, txtCampaignQuantityCount, txtCampaignTag;
    private ProgressBar progressBarQuantityCount;
    private RecyclerView recViewCheckoutOrderProduct;
    private Button btnConfirmOrder;
    private LinearLayout layoutCampaign, layoutOrderList;
    private RelativeLayout layoutLoading;

    private ArrayList<Order> orderList;
    private List<List<CustomerDiscount>> discountList;
    private int requestCode;
    private Campaign campaign;
    private RecViewOrderSupplierListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordering_confirm);
        this.getSupportActionBar().hide();

        imgBackFromCheckout = findViewById(R.id.imgBackFromCheckout);
        imgCheckoutHome = findViewById(R.id.imgCheckoutHome);
        txtCampaignDescription = findViewById(R.id.txtCampaignDescription);
        txtCampaignPrice = findViewById(R.id.txtCampaignPrice);
        txtCampaignOrderCount = findViewById(R.id.txtCampaignOrderCount);
        txtCampaignOrderQuantityCount = findViewById(R.id.txtCampaignOrderQuantityCount);
        txtCampaignQuantityCount = findViewById(R.id.txtCampaignQuantityCount);
        txtCampaignTag = findViewById(R.id.txtCampaignTag);
        progressBarQuantityCount = findViewById(R.id.progressBarQuantityCount);
        recViewCheckoutOrderProduct = findViewById(R.id.recViewCheckoutOrderProduct);
        btnConfirmOrder = findViewById(R.id.btnConfirmOrder);
        layoutCampaign = findViewById(R.id.layoutCampaign);
        layoutOrderList = findViewById(R.id.layoutOrderList);
        layoutLoading = findViewById(R.id.layoutLoading);

        orderList = (ArrayList<Order>) getIntent().getSerializableExtra("ORDER_LIST");
        requestCode = getIntent().getIntExtra("REQUEST_CODE", IntegerUtils.REQUEST_COMMON);
        layoutOrderList.setVisibility(View.INVISIBLE);
        layoutLoading.setVisibility(View.VISIBLE);

        if (requestCode == IntegerUtils.REQUEST_ORDER_RETAIL) {
            layoutCampaign.setVisibility(View.GONE);

            setupRecViewOrderList();
        } else {
            campaign = orderList.get(0).getCampaign();
            txtCampaignTag.setText(campaign.getShareFlag() ? "Sharing Campaign" : "Single Campaign");
            layoutCampaign.setVisibility(View.VISIBLE);
            txtCampaignDescription.setText(campaign.getDescription());
            txtCampaignPrice.setText(MethodUtils.formatPriceString(campaign.getSavingPrice()));
            txtCampaignOrderCount.setText(campaign.getOrderCount() + "");
            txtCampaignOrderQuantityCount.setText(campaign.getQuantityCount() + "");
            txtCampaignQuantityCount.setText(campaign.getMinQuantity() + "");
            progressBarQuantityCount.setMax(campaign.getMinQuantity());
            progressBarQuantityCount.setProgress(campaign.getQuantityCount());
            setupRecViewOrderList();
        }

        imgBackFromCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });

        imgCheckoutHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        btnConfirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogBoxConfirm dialogBoxConfirm
                        = new DialogBoxConfirm(ConfirmActivity.this, StringUtils.MES_CONFIRM_ORDER) {
                    @Override
                    public void onYesClicked() {
                        Intent orderInfoIntent = new Intent(getApplicationContext(), InfoActivity.class);
                        orderInfoIntent.putExtra("ORDER_LIST", orderList);
                        orderInfoIntent.putExtra("REQUEST_CODE", requestCode);
                        startActivityForResult(orderInfoIntent, requestCode);
                    }
                };
                dialogBoxConfirm.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBoxConfirm.show();
            }
        });
    }

    private void setupRecViewOrderList() {
        adapter = new RecViewOrderSupplierListAdapter(getApplicationContext(),
                ConfirmActivity.this, requestCode, IntegerUtils.REQUEST_ORDER_NOTE) {

        };
        adapter.setList(orderList);
        recViewCheckoutOrderProduct.setAdapter(adapter);
        recViewCheckoutOrderProduct.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                                            LinearLayoutManager.VERTICAL, false));
        recViewCheckoutOrderProduct.setNestedScrollingEnabled(false);
        layoutLoading.setVisibility(View.GONE);
        layoutOrderList.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }
}