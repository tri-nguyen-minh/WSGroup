package dev.wsgroup.main.views.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APIOrderCaller;
import dev.wsgroup.main.models.apis.callers.APIPaymentCaller;
import dev.wsgroup.main.models.apis.callers.APIReviewCaller;
import dev.wsgroup.main.models.dtos.Order;
import dev.wsgroup.main.models.dtos.OrderProduct;
import dev.wsgroup.main.models.dtos.Review;
import dev.wsgroup.main.models.dtos.User;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewOrderProductListAdapter;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.StringUtils;
import dev.wsgroup.main.views.activities.ordering.InfoActivity;
import dev.wsgroup.main.views.dialogbox.DialogBoxAlert;
import dev.wsgroup.main.views.dialogbox.DialogBoxConfirm;
import dev.wsgroup.main.views.dialogbox.DialogBoxLoading;
import dev.wsgroup.main.views.dialogbox.DialogBoxPayment;
import dev.wsgroup.main.views.dialogbox.DialogBoxSetupPayment;

public class OrderActivity extends AppCompatActivity {

    private ImageView imgBackFromOrderDetail, imgOrderDetailMessage, imgOrderDetailHome, imgSupplierAvatar;
    private TextView txtOrderCode, txtPhoneNumber, txtDeliveryAddress, txtPayment, txtStatus,
            txtSupplierName, txtSupplierAddress, txtTotalPrice, txtTotalDiscount, txtAdvanced;
    private RelativeLayout layoutLoading;
    private LinearLayout layoutProductList;
    private RecyclerView recViewOrderProduct;
    private Button btnAdditionalAction;
    private ConstraintLayout layoutAdvance;

    private String phone, token, userId, status;
    private double totalPrice, discountPrice, advancedFee;
    private int requestCode, productCount;
    private Order order;
    private OrderProduct orderProduct;
    private List<OrderProduct> orderProductList;
    private SharedPreferences sharedPreferences;
    private RecViewOrderProductListAdapter adapter;
    private DialogBoxConfirm dialogBoxConfirm;
    private DialogBoxLoading dialogBoxLoading;
    private DialogBoxAlert dialogBoxAlert;
    private View.OnClickListener listener;

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
        txtTotalDiscount = findViewById(R.id.txtTotalDiscount);
        txtAdvanced = findViewById(R.id.txtAdvanced);
        layoutLoading = findViewById(R.id.layoutLoading);
        layoutProductList = findViewById(R.id.layoutProductList);
        recViewOrderProduct = findViewById(R.id.recViewOrderProduct);
        btnAdditionalAction = findViewById(R.id.btnAdditionalAction);
        layoutAdvance = findViewById(R.id.layoutAdvance);

        totalPrice = 0; discountPrice = 0; advancedFee = 0;
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

        if (order.getPaymentMethod().equals("cod")) {
            txtPayment.setText("Payment on Delivery");
        } else {
            txtPayment.setText("Payment via VNPay");
        }
        txtStatus.setText(MethodUtils.displayStatus(order.getStatus()));
        txtSupplierName.setText(order.getSupplier().getName());
        txtSupplierAddress.setVisibility(View.GONE);
        discountPrice = order.getDiscountPrice();
        txtTotalDiscount.setText(MethodUtils.formatPriceString(order.getDiscountPrice()));
        if (order.getAdvanceFee() == 0) {
            layoutAdvance.setVisibility(View.GONE);
        } else {
            advancedFee = order.getAdvanceFee();
            layoutAdvance.setVisibility(View.VISIBLE);
        }
        txtAdvanced.setText(MethodUtils.formatPriceString(advancedFee));
        totalPrice = order.getTotalPrice() - discountPrice;
        txtTotalPrice.setText(MethodUtils.formatPriceString(order.getTotalPrice()));
        if (order.getStatus().equals("completed")) {
           checkOrderReview();
        } else {
            setupRecViewOrderList();
        }
        setupButton();

        imgBackFromOrderDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getIntent().putExtra("MAIN_TAB_POSITION", 1);
                int historyTabPosition = 0;
                switch (order.getStatus()) {
                    case "unpaid": {
                        historyTabPosition = 0;
                        break;
                    }
                    case "advanced": {
                        historyTabPosition = 1;
                        break;
                    }
                    case "created": {
                        historyTabPosition = 2;
                        break;
                    }
                    case "processing": {
                        historyTabPosition = 3;
                        break;
                    }
                    case "delivering": {
                        historyTabPosition = 4;
                        break;
                    }
                    case "delivered": {
                        historyTabPosition = 5;
                        break;
                    }
                    case "completed": {
                        historyTabPosition = 6;
                        break;
                    }
                    case "returned": {
                        historyTabPosition = 7;
                        break;
                    }
                    case "cancelled": {
                        historyTabPosition = 8;
                        break;
                    }
                }
                getIntent().putExtra("HISTORY_TAB_POSITION", historyTabPosition);
                setResult(RESULT_OK, getIntent());
                finish();
            }
        });

        imgOrderDetailHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }

    private void setupButton() {
        listener = null;
        if (checkPaymentCompleted()) {
            btnAdditionalAction.setVisibility(View.VISIBLE);
            btnAdditionalAction.setText("COMPLETE PAYMENT");
            listener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogBoxConfirm = new DialogBoxConfirm(OrderActivity.this,
                            StringUtils.MES_CONFIRM_COMPLETE_PAYMENT) {
                        @Override
                        public void onYesClicked() {
                            performPayment();
                        }
                    };
                    dialogBoxConfirm.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialogBoxConfirm.show();
                }
            };
        } else if (order.getStatus().equals("delivered")) {
            btnAdditionalAction.setVisibility(View.VISIBLE);
            btnAdditionalAction.setText("REQUEST RETURN & REFUND");
            listener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            };
        } else {
            btnAdditionalAction.setVisibility(View.GONE);
        }
        btnAdditionalAction.setOnClickListener(listener);
    }

    private boolean checkPaymentCompleted() {
        if (order.getStatus().equals("unpaid")) {
            status = "created";
            return true;
        } else if (order.getStatus().equals("advanced")
                    && order.getPaymentMethod().equals("online")
                    && order.getPaymentId().equals("null")) {
            status = "advanced";
            return true;
        }
        return false;
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

    private void performPayment() {
        dialogBoxLoading = new DialogBoxLoading(OrderActivity.this);
        dialogBoxLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBoxLoading.show();
        double paymentPrice = totalPrice - advancedFee;
        DialogBoxSetupPayment dialogBoxSetupPayment
                = new DialogBoxSetupPayment(OrderActivity.this, paymentPrice) {
            @Override
            public void onBankSelected(String bankString) {
                APIPaymentCaller.getPaymentURL(order, paymentPrice, bankString, status,
                        "topup", getApplication(), new APIListener() {
                            @Override
                            public void onGettingPaymentURL(String url) {
                                super.onGettingPaymentURL(url);
                                DialogBoxPayment dialogBox
                                        = new DialogBoxPayment(OrderActivity.this, url, order) {
                                    @Override
                                    public void onCompletedPayment(String vnpRef) {
                                        APIOrderCaller.updateOrderPaymentStatus(token, order,
                                                "advanced", true, paymentPrice, vnpRef,
                                                getApplication(), new APIListener() {
                                                    @Override
                                                    public void onUpdateOrderSuccessful() {
                                                        displayFinalMessage();
                                                    }
                                                });
                                    }

                                    @Override
                                    public void onCancelDialogBox() {
                                        displayCancelMessage();
                                    }
                                };
                                dialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialogBox.show();
                            }
                        });
            }
            @Override
            public void onCancel() {
                displayCancelMessage();
            }
        };
        dialogBoxSetupPayment.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBoxSetupPayment.show();
    }
    private void displayCancelMessage() {
        if (dialogBoxLoading.isShowing()) {
            dialogBoxLoading.dismiss();
        }
        dialogBoxAlert = new DialogBoxAlert(OrderActivity.this,
                IntegerUtils.CONFIRM_ACTION_CODE_FAILED, StringUtils.MES_ERROR_PAYMENT_FAILED, "");
        dialogBoxAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBoxAlert.show();
    }

    private void displayFinalMessage() {
        if (dialogBoxLoading.isShowing()) {
            dialogBoxLoading.dismiss();
        }
        dialogBoxAlert = new DialogBoxAlert(OrderActivity.this,
                IntegerUtils.CONFIRM_ACTION_CODE_SUCCESS, StringUtils.MES_SUCCESSFUL_PAYMENT, "") {
            @Override
            public void onClickAction() {
                fixLayout();
            }
        };
        dialogBoxAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBoxAlert.show();
    }

    private void fixLayout() {
        order.setStatus(status);
        txtStatus.setText(MethodUtils.displayStatus(status));
        btnAdditionalAction.setVisibility(View.GONE);
    }
}