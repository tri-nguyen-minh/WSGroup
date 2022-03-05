package dev.wsgroup.main.views.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APIReviewCaller;
import dev.wsgroup.main.models.dtos.Order;
import dev.wsgroup.main.models.dtos.OrderProduct;
import dev.wsgroup.main.models.dtos.Review;
import dev.wsgroup.main.models.dtos.User;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewOrderProductListAdapter;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.StringUtils;
import dev.wsgroup.main.views.dialogbox.DialogBoxAlert;
import dev.wsgroup.main.views.dialogbox.DialogBoxConfirm;

public class OrderActivity extends AppCompatActivity {

    private ImageView imgBackFromOrderDetail, imgOrderDetailMessage, imgOrderDetailHome, imgSupplierAvatar;
    private TextView txtOrderCode, txtPhoneNumber, txtDeliveryAddress, txtPayment, txtStatus,
            txtSupplierName, txtSupplierAddress, txtTotalPrice;
    private RelativeLayout layoutLoading;
    private LinearLayout layoutProductList;
    private RecyclerView recViewOrderProduct;

    private String phone, token, userId;
    private int requestCode, productCount;
    private Order order;
    private OrderProduct orderProduct;
    private List<OrderProduct> orderProductList;
    private SharedPreferences sharedPreferences;
    private RecViewOrderProductListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        this.getSupportActionBar().hide();

        imgBackFromOrderDetail = findViewById(R.id.imgBackFromOrderDetail);
        imgOrderDetailMessage = findViewById(R.id.imgOrderDetailMessage);
        imgOrderDetailHome = findViewById(R.id.imgOrderDetailHome);
        imgSupplierAvatar = findViewById(R.id.imgSupplierAvatar);
        txtOrderCode = findViewById(R.id.txtOrderCode);
        txtPhoneNumber = findViewById(R.id.txtPhoneNumber);
        txtDeliveryAddress = findViewById(R.id.txtDeliveryAddress);
        txtPayment = findViewById(R.id.txtPayment);
        txtStatus = findViewById(R.id.txtStatus);
        txtSupplierName = findViewById(R.id.txtSupplierName);
        txtSupplierAddress = findViewById(R.id.txtSupplierAddress);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);
        layoutLoading = findViewById(R.id.layoutLoading);
        layoutProductList = findViewById(R.id.layoutProductList);
        recViewOrderProduct = findViewById(R.id.recViewOrderProduct);

        order = (Order) getIntent().getSerializableExtra("ORDER");
        requestCode = getIntent().getIntExtra("REQUEST_CODE", IntegerUtils.REQUEST_COMMON);
        sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        phone = sharedPreferences.getString("PHONE", "");
        token = sharedPreferences.getString("TOKEN", "");
        userId = sharedPreferences.getString("USER_ID", "");

        txtOrderCode.setText(order.getCode());
        txtPhoneNumber.setText(MethodUtils
                .formatPhoneNumberWithCountryCode(MethodUtils.formatPhoneNumber(phone)));
        txtDeliveryAddress.setText(order.getAddress().getAddressString());

        if (order.getPayment().getId().isEmpty()) {
            txtPayment.setText("Payment on Delivery");
        } else {

        }
        txtStatus.setText(MethodUtils.displayStatus(order.getStatus()));
        txtSupplierName.setText(order.getSupplier().getName());
        txtSupplierAddress.setVisibility(View.GONE);
        txtTotalPrice.setText(MethodUtils.formatPriceString(order.getTotalPrice()));
        if (order.getStatus().equals("completed")) {
           checkOrderReview();
        } else {
            setupRecViewOrderList();
        }

        imgBackFromOrderDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (requestCode == IntegerUtils.REQUEST_ORDER_AFTER_CHECKOUT) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                } else {
                    finish();
                }
            }
        });

        imgOrderDetailHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }

    private void checkOrderReview() {
        layoutLoading.setVisibility(View.VISIBLE);
        layoutProductList.setVisibility(View.INVISIBLE);
        orderProductList = order.getOrderProductList();
        productCount = orderProductList.size();
        for (OrderProduct orderProduct : orderProductList) {
            APIReviewCaller.getReviewByOrderProductId(orderProduct.getId(), getApplication(), new APIListener() {
                @Override
                public void onReviewFound(Review review) {
                    orderProduct.setReview(review);
                    productCount--;
                    if (productCount == 0) {
                        setupRecViewOrderList();
                    }
                }

                @Override
                public void onFailedAPICall(int code) {
                    productCount--;
                    if (productCount == 0) {
                        setupRecViewOrderList();
                    }
                }
            });
        }
    }

    private void setupRecViewOrderList() {
        layoutLoading.setVisibility(View.INVISIBLE);
        layoutProductList.setVisibility(View.VISIBLE);
        adapter = new RecViewOrderProductListAdapter(getApplicationContext(),OrderActivity.this,
                IntegerUtils.REQUEST_NOTE_READ_ONLY, IntegerUtils.REQUEST_ORDER_REVIEW) {
            @Override
            public void addingReview(Review review) {
                layoutLoading.setVisibility(View.VISIBLE);
                layoutProductList.setVisibility(View.INVISIBLE);
                User user = new User();
                user.setUserId(userId);
                review.setUser(user);
                APIReviewCaller.addReview(review, token, getApplication(), new APIListener() {
                    @Override
                    public void onReviewFound(Review review) {
                        DialogBoxAlert dialogBoxAlert = new DialogBoxAlert(OrderActivity.this,
                                IntegerUtils.CONFIRM_ACTION_CODE_SUCCESS, StringUtils.MES_SUCCESSFUL_ADD_REVIEW,"") {
                            @Override
                            public void onClickAction() {
                                applyReview(review);
                                notifyDataSetChanged();
                                layoutLoading.setVisibility(View.INVISIBLE);
                                layoutProductList.setVisibility(View.VISIBLE);
                            }
                        };
                        dialogBoxAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialogBoxAlert.show();
                    }

                    @Override
                    public void onFailedAPICall(int code) {
                        String errorMessage = StringUtils.MES_ERROR_FAILED_API_CALL;
                        DialogBoxAlert dialogBoxAlert = new DialogBoxAlert(OrderActivity.this,
                                IntegerUtils.CONFIRM_ACTION_CODE_SUCCESS, errorMessage,"");
                        dialogBoxAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialogBoxAlert.show();
                        layoutLoading.setVisibility(View.INVISIBLE);
                        layoutProductList.setVisibility(View.VISIBLE);
                    }
                });
            }
        };
        adapter.setOrder(order);
        recViewOrderProduct.setAdapter(adapter);
        recViewOrderProduct.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL, false));
    }

    private void applyReview(Review review) {
        orderProductList = order.getOrderProductList();
        int count;
        for (count = 0; count < orderProductList.size(); count++) {
            orderProduct = orderProductList.get(count);
            if (review.getOrderId().equals(orderProduct.getId())) {
                orderProduct.setReview(review);
                break;
            }
        }
        orderProduct.setReview(review);
        orderProductList.set(count, orderProduct);
        order.setOrderProductList(orderProductList);
    }
}