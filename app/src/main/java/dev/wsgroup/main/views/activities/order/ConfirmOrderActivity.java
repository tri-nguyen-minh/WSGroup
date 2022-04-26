package dev.wsgroup.main.views.activities.order;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.Campaign;
import dev.wsgroup.main.models.dtos.CampaignMilestone;
import dev.wsgroup.main.models.dtos.CustomerDiscount;
import dev.wsgroup.main.models.dtos.Order;
import dev.wsgroup.main.models.dtos.OrderProduct;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewOrderSupplierListAdapter;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.StringUtils;
import dev.wsgroup.main.views.activities.MainActivity;
import dev.wsgroup.main.views.dialogbox.DialogBoxConfirm;

public class ConfirmOrderActivity extends AppCompatActivity {

    private ImageView imgBackFromCheckout, imgCheckoutHome;
    private TextView txtCampaignNote, txtProductPrice, txtCampaignPrice, txtCampaignOrderCount,
            lblProductOrderCount, lblQuantityCountSeparator, txtCampaignOrderQuantityCount,
            txtCampaignQuantityCount, txtCampaignTag;
    private ProgressBar progressBarQuantityCount;
    private RecyclerView recViewOrderProduct;
    private Button btnConfirmOrder;
    private LinearLayout layoutCampaign, layoutOrderList;
    private RelativeLayout layoutLoading;
    private ConstraintLayout layoutCampaignCount;

    private SharedPreferences sharedPreferences;
    private ArrayList<Order> orderList;
    private Order order;
    private OrderProduct orderProduct;
    private int requestCode;
    private Campaign campaign;
    private CampaignMilestone currentDetail;
    private RecViewOrderSupplierListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordering_confirm);
        this.getSupportActionBar().hide();

        imgBackFromCheckout = findViewById(R.id.imgBackFromCheckout);
        imgCheckoutHome = findViewById(R.id.imgCheckoutHome);
        txtCampaignNote = findViewById(R.id.txtCampaignNote);
        txtProductPrice = findViewById(R.id.txtProductPrice);
        txtCampaignPrice = findViewById(R.id.txtCampaignPrice);
        txtCampaignOrderCount = findViewById(R.id.txtCampaignOrderCount);
        lblProductOrderCount = findViewById(R.id.lblProductOrderCount);
        lblQuantityCountSeparator = findViewById(R.id.lblQuantityCountSeparator);
        txtCampaignOrderQuantityCount = findViewById(R.id.txtCampaignOrderQuantityCount);
        txtCampaignQuantityCount = findViewById(R.id.txtCampaignQuantityCount);
        txtCampaignTag = findViewById(R.id.txtCampaignTag);
        progressBarQuantityCount = findViewById(R.id.progressBarQuantityCount);
        recViewOrderProduct = findViewById(R.id.recViewCheckoutOrderProduct);
        btnConfirmOrder = findViewById(R.id.btnConfirmOrder);
        layoutCampaign = findViewById(R.id.layoutCampaign);
        layoutOrderList = findViewById(R.id.layoutOrderList);
        layoutLoading = findViewById(R.id.layoutLoading);
        layoutCampaignCount = findViewById(R.id.layoutCampaignCount);


        sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        orderList = (ArrayList<Order>) getIntent().getSerializableExtra("ORDER_LIST");
        requestCode = getIntent().getIntExtra("REQUEST_CODE", IntegerUtils.REQUEST_COMMON);
        layoutOrderList.setVisibility(View.INVISIBLE);
        layoutLoading.setVisibility(View.VISIBLE);

        if (requestCode == IntegerUtils.REQUEST_ORDER_RETAIL) {
            layoutCampaign.setVisibility(View.GONE);
        } else {
            order = orderList.get(0);
            orderProduct = order.getOrderProductList().get(0);
            campaign = order.getCampaign();
            layoutCampaign.setVisibility(View.VISIBLE);
            txtCampaignNote.setText(campaign.getDescription());
            if (campaign.getShareFlag()) {
                txtCampaignTag.setText("Sharing Campaign");
                currentDetail = MethodUtils.getReachedCampaignMilestone(campaign.getMilestoneList(),
                        orderProduct.getQuantity() + campaign.getQuantityCount());
                if (currentDetail == null) {
                    txtCampaignPrice.setText(MethodUtils.formatPriceString(campaign.getPrice()));
                } else {
                    txtCampaignPrice.setText(MethodUtils.formatPriceString(currentDetail.getPrice()));
                }
                layoutCampaignCount.setVisibility(View.VISIBLE);
                progressBarQuantityCount.setVisibility(View.VISIBLE);
                txtCampaignOrderCount.setText(campaign.getOrderCount() + "");
                txtCampaignOrderQuantityCount.setText(campaign.getQuantityCount() + "");
                txtCampaignQuantityCount.setText(campaign.getMaxQuantity() + "");
                progressBarQuantityCount.setMax(campaign.getMaxQuantity());
                progressBarQuantityCount.setProgress(campaign.getQuantityCount());
                lblProductOrderCount.setText((campaign.getOrderCount() == 1) ? "order" : "orders");
                lblQuantityCountSeparator.setText("/");
            } else {
                txtCampaignTag.setText("One-time Campaign");
                txtCampaignPrice.setText(MethodUtils.formatPriceString(campaign.getPrice()));
                layoutCampaignCount.setVisibility(View.GONE);
                progressBarQuantityCount.setVisibility(View.GONE);
            }
            txtProductPrice.setText(MethodUtils.formatPriceString(orderProduct.getPrice()));
            txtProductPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }
        setupRecViewOrderList();

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
                        = new DialogBoxConfirm(ConfirmOrderActivity.this,
                        StringUtils.MES_CONFIRM_ORDER) {
                    @Override
                    public void onYesClicked() {
                        Intent orderInfoIntent
                                = new Intent(getApplicationContext(), OrderInfoActivity.class);
                        orderInfoIntent.putExtra("ORDER_LIST", orderList);
                        orderInfoIntent.putExtra("REQUEST_CODE", requestCode);
                        startActivityForResult(orderInfoIntent, requestCode);
                    }
                };
                dialogBoxConfirm.getWindow()
                        .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBoxConfirm.show();
            }
        });
    }

    private void setupRecViewOrderList() {
        adapter = new RecViewOrderSupplierListAdapter(getApplicationContext(),
                ConfirmOrderActivity.this, requestCode) {
            @Override
            public void setCustomerDiscount(int position, CustomerDiscount customerDiscount) {
                if (customerDiscount != null) {
                    customerDiscount.getDiscount()
                            .setSupplier(orderList.get(position).getSupplier());
                }
                orderList.get(position).setCustomerDiscount(customerDiscount);
            }
        };
        adapter.setList(orderList);
        recViewOrderProduct.setAdapter(adapter);
        recViewOrderProduct.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL, false));
        recViewOrderProduct.setNestedScrollingEnabled(false);
        layoutLoading.setVisibility(View.GONE);
        layoutOrderList.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        imgBackFromCheckout.performClick();
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