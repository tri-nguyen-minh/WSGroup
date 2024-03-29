package dev.wsgroup.main.views.activities.order;

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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APICampaignCaller;
import dev.wsgroup.main.models.apis.callers.APIOrderCaller;
import dev.wsgroup.main.models.apis.callers.APIPaymentCaller;
import dev.wsgroup.main.models.apis.callers.APIReviewCaller;
import dev.wsgroup.main.models.dtos.Campaign;
import dev.wsgroup.main.models.dtos.CampaignMilestone;
import dev.wsgroup.main.models.dtos.Order;
import dev.wsgroup.main.models.dtos.OrderProduct;
import dev.wsgroup.main.models.dtos.Review;
import dev.wsgroup.main.models.dtos.User;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewCampaignMilestoneListAdapter;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewOrderProductListAdapter;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.StringUtils;
import dev.wsgroup.main.views.activities.MainActivity;
import dev.wsgroup.main.views.activities.SupplierActivity;
import dev.wsgroup.main.views.dialogbox.DialogBoxAlert;
import dev.wsgroup.main.views.dialogbox.DialogBoxConfirm;
import dev.wsgroup.main.views.dialogbox.DialogBoxLoading;
import dev.wsgroup.main.views.dialogbox.DialogBoxPayment;
import dev.wsgroup.main.views.dialogbox.DialogBoxSetupPayment;

public class OrderActivity extends AppCompatActivity {

    private ImageView imgBackFromOrderDetail, imgOrderDetailHome, imgSupplierAvatar, imgExpand;
    private TextView txtOrderCode, txtDeliveryAddress, txtPayment, txtStatus,
            txtSupplierName, txtSupplierAddress, txtTotalPrice, txtFinalPrice,
            txtTotalDiscount, txtAdvanced, lblRetryGetOrder, txtFullPayment,
            txtDiscountEndDate, txtMilestonePrice, txtCampaignOrderCount,
            txtCampaignQuantityCount, txtCampaignQuantityBar, txtCampaignNote,
            lblProductOrderCount, txtLoyaltyDiscount, txtShipping;
    private RelativeLayout layoutLoading;
    private LinearLayout layoutNoOrder, layoutAction, layoutCampaign;
    private RecyclerView recViewOrderProduct, recViewCampaignMilestone;
    private ConstraintLayout layoutDiscount, layoutAdvance, layoutStatus,
            layoutOrderSupplier, layoutLoyalty, layoutShipping;
    private NestedScrollView scrollViewMain;
    private Button btnFirstAction, btnSecondAction;
    private ProgressBar progressBarQuantityCount;

    private String token, userId, orderCode, status, description;
    private double totalPrice, discountPrice, advancedFee, shippingFee;
    private int productCount, index;
    private Intent intent;
    private Order order;
    private OrderProduct orderProduct;
    private Campaign sharingCampaign;
    private CampaignMilestone milestone;
    private List<OrderProduct> orderProductList;
    private SharedPreferences sharedPreferences;
    private RecViewOrderProductListAdapter adapter;
    private RecViewCampaignMilestoneListAdapter milestoneAdapter;
    private DialogBoxConfirm dialogBoxConfirm;
    private DialogBoxLoading dialogBoxLoading;
    private DialogBoxAlert dialogBoxAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        this.getSupportActionBar().hide();

        imgBackFromOrderDetail = findViewById(R.id.imgBackFromOrderDetail);
        imgOrderDetailHome = findViewById(R.id.imgOrderDetailHome);
        imgSupplierAvatar = findViewById(R.id.imgSupplierAvatar);
        imgExpand = findViewById(R.id.imgExpand);
        txtOrderCode = findViewById(R.id.txtOrderCode);
        txtDeliveryAddress = findViewById(R.id.txtDeliveryAddress);
        txtPayment = findViewById(R.id.txtPayment);
        txtStatus = findViewById(R.id.txtStatus);
        txtSupplierName = findViewById(R.id.txtSupplierName);
        txtSupplierAddress = findViewById(R.id.txtSupplierAddress);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);
        txtFinalPrice = findViewById(R.id.txtFinalPrice);
        txtTotalDiscount = findViewById(R.id.txtTotalDiscount);
        txtAdvanced = findViewById(R.id.txtAdvanced);
        lblRetryGetOrder = findViewById(R.id.lblRetryGetOrder);
        txtFullPayment = findViewById(R.id.txtFullPayment);
        txtDiscountEndDate = findViewById(R.id.txtDiscountEndDate);
        txtMilestonePrice = findViewById(R.id.txtMilestonePrice);
        txtFullPayment = findViewById(R.id.txtFullPayment);
        txtCampaignOrderCount = findViewById(R.id.txtCampaignOrderCount);
        txtCampaignQuantityCount = findViewById(R.id.txtCampaignQuantityCount);
        txtCampaignQuantityBar = findViewById(R.id.txtCampaignQuantityBar);
        txtCampaignNote = findViewById(R.id.txtCampaignNote);
        lblProductOrderCount = findViewById(R.id.lblProductOrderCount);
        txtLoyaltyDiscount = findViewById(R.id.txtLoyaltyDiscount);
        txtShipping = findViewById(R.id.txtShipping);
        layoutLoading = findViewById(R.id.layoutLoading);
        layoutNoOrder = findViewById(R.id.layoutNoOrder);
        recViewOrderProduct = findViewById(R.id.recViewOrderProduct);
        recViewCampaignMilestone = findViewById(R.id.recViewCampaignMilestone);
        layoutDiscount = findViewById(R.id.layoutDiscount);
        layoutAdvance = findViewById(R.id.layoutAdvance);
        layoutStatus = findViewById(R.id.layoutStatus);
        layoutOrderSupplier = findViewById(R.id.layoutOrderSupplier);
        layoutLoyalty = findViewById(R.id.layoutLoyalty);
        layoutShipping = findViewById(R.id.layoutShipping);
        layoutAction = findViewById(R.id.layoutAction);
        layoutCampaign = findViewById(R.id.layoutCampaign);
        scrollViewMain = findViewById(R.id.scrollViewMain);
        btnFirstAction = findViewById(R.id.btnFirstAction);
        btnSecondAction = findViewById(R.id.btnSecondAction);
        progressBarQuantityCount = findViewById(R.id.progressBarQuantityCount);

        setLoadingState();
        setupOrder();

        imgBackFromOrderDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getIntent().putExtra("MAIN_TAB_POSITION", 1);
                int historyTabPosition = 0;
                try {
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
                        case "returning": {
                            historyTabPosition = 7;
                            break;
                        }
                        case "returned": {
                            historyTabPosition = 8;
                            break;
                        }
                        case "cancelled": {
                            historyTabPosition = 9;
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
        lblRetryGetOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLoadingState();
                setupOrder();
            }
        });
        layoutStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplicationContext(), OrderHistoryActivity.class);
                intent.putExtra("ORDER_CODE", order.getCode());
                startActivityForResult(intent, IntegerUtils.REQUEST_COMMON);
            }
        });
        layoutOrderSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent supplierIntent = new Intent(getApplicationContext(), SupplierActivity.class);
                supplierIntent.putExtra("SUPPLIER_ID", order.getSupplier().getId());
                startActivity(supplierIntent);
            }
        });
    }

    private void setupOrder() {
        totalPrice = 0; discountPrice = 0; advancedFee = 0;
        orderCode = getIntent().getStringExtra("ORDER_CODE");
        sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("TOKEN", "");
        userId = sharedPreferences.getString("USER_ID", "");
        APIOrderCaller.getOrderByOrderCode(orderCode, getApplication(), new APIListener() {
            @Override
            public void onOrderFound(List<Order> orderList) {
                if (orderList.size() > 0) {
                    order = orderList.get(0);
                    if (order.getCampaign() != null) {
                        APICampaignCaller.getCampaignById(order.getCampaign().getId(),
                                getApplication(), new APIListener() {
                            @Override
                            public void onCampaignListFound(List<Campaign> campaignList) {
                                if (campaignList.size() > 0) {
                                    order.setCampaign(campaignList.get(0));
                                    setupData();
                                } else {
                                    setNoOrderState();
                                }
                            }

                            @Override
                            public void onFailedAPICall(int code) {
                                setNoOrderState();
                            }
                        });
                    } else {
                        setupData();
                    }
                } else {
                    setNoOrderState();
                }
            }

            @Override
            public void onFailedAPICall(int code) {
                setNoOrderState();
            }
        });
    }

    private void setupData() {
        txtOrderCode.setText(order.getCode());
        txtDeliveryAddress.setText(order.getAddress().getAddressString());
        if (order.getPaymentMethod().equals("cod")) {
            txtPayment.setText("Payment on Delivery");
        } else {
            txtPayment.setText("Payment via VNPAY");
        }
        txtStatus.setText(MethodUtils.displayStatus(order.getStatus()));
        txtSupplierName.setText(order.getSupplier().getName());
        if (!order.getSupplier().getAvatarLink().equals("null")) {
            Glide.with(getApplicationContext())
                 .load(order.getSupplier().getAvatarLink())
                 .into(imgSupplierAvatar);
        } else {
            Glide.with(getApplicationContext())
                 .load(R.drawable.ic_profile_circle)
                 .into(imgSupplierAvatar);
            imgSupplierAvatar.setScaleX((float) 1.1);
            imgSupplierAvatar.setScaleY((float) 1.1);
        }
        txtSupplierAddress.setText(order.getSupplier().getAddress().getFullAddressString());
        discountPrice = order.getDiscountPrice();
        if (discountPrice == 0) {
            layoutDiscount.setVisibility(View.GONE);
        } else {
            layoutDiscount.setVisibility(View.VISIBLE);
        }
        txtTotalDiscount.setText(MethodUtils.formatPriceString(order.getDiscountPrice()));
        advancedFee = order.getAdvanceFee();
        if (advancedFee == 0) {
            layoutAdvance.setVisibility(View.GONE);
        } else {
            layoutAdvance.setVisibility(View.VISIBLE);
        }
        shippingFee = order.getShippingFee();
        layoutShipping.setVisibility(View.VISIBLE);
        layoutCampaign.setVisibility(View.GONE);
        if (order.getStatus().equals("advanced")) {
            sharingCampaign = order.getCampaign();
            layoutCampaign.setVisibility(View.VISIBLE);
            txtCampaignNote.setText(sharingCampaign.getDescription());
            txtDiscountEndDate.setText(MethodUtils.formatDate(sharingCampaign.getEndDate(), false));
            imgExpand.setImageResource(R.drawable.ic_direction_down);
            milestone = MethodUtils.getReachedCampaignMilestone(
                    sharingCampaign.getMilestoneList(),
                    sharingCampaign.getQuantityCount());
            if (milestone == null) {
                txtMilestonePrice.setText(MethodUtils.formatPriceString(sharingCampaign.getPrice()));
            } else {
                txtMilestonePrice.setText(MethodUtils.formatPriceString(milestone.getPrice()));
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
            milestoneAdapter = new RecViewCampaignMilestoneListAdapter(getApplicationContext(),
                    order.getOrderProductList().get(0).getPrice());
            milestoneAdapter.setCampaign(sharingCampaign);
            recViewCampaignMilestone.setAdapter(milestoneAdapter);
            recViewCampaignMilestone.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                    LinearLayoutManager.VERTICAL,false));
        } else {
            layoutCampaign.setVisibility(View.GONE);
        }
        if (order.getLoyaltyDiscountPercent() != 0) {
            layoutLoyalty.setVisibility(View.VISIBLE);
            txtLoyaltyDiscount.setText(order.getLoyaltyDiscountPercent() + "%");
        } else {
            layoutLoyalty.setVisibility(View.GONE);
        }
        txtTotalPrice.setText(MethodUtils.formatPriceString(order.getTotalPrice()));
        txtAdvanced.setText(MethodUtils.formatPriceString(advancedFee));
        txtShipping.setText(MethodUtils.formatPriceString(shippingFee));
        totalPrice = order.getTotalPrice() - discountPrice - advancedFee + shippingFee;
        txtFinalPrice.setText(MethodUtils.formatPriceString(totalPrice));
        if (order.getStatus().equals("completed")) {
            checkOrderReview();
        } else {
            setupRecViewOrderList();
        }
        setupExtraActionLayout();
    }

    private void setupExtraActionLayout() {
        layoutAction.setVisibility(View.GONE);
        if (order.getStatus().equals("delivered")){
            btnFirstAction.setVisibility(View.VISIBLE);
            btnFirstAction.setText("COMPLETE ORDER");
            btnFirstAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogBoxConfirm = new DialogBoxConfirm(OrderActivity.this,
                            StringUtils.MES_CONFIRM_COMPLETE_ORDER) {
                        @Override
                        public void onYesClicked() {
                            completeOrder();
                        }
                    };
                    dialogBoxConfirm.setDescription(StringUtils.DESC_IRREVERSIBLE_ACTION);
                    dialogBoxConfirm.getWindow()
                            .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialogBoxConfirm.show();
                }
            });
        } else {
            btnFirstAction.setVisibility(View.GONE);
        }
        if (checkPaymentCompleted()) {
            btnSecondAction.setText("Complete Payment");
            btnSecondAction.setVisibility(View.VISIBLE);
            btnSecondAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogBoxConfirm = new DialogBoxConfirm(OrderActivity.this,
                            StringUtils.MES_CONFIRM_COMPLETE_PAYMENT) {
                        @Override
                        public void onYesClicked() {
                            performPayment();
                        }
                    };
                    dialogBoxConfirm.getWindow()
                            .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialogBoxConfirm.show();
                }
            });
        } else if (order.getStatus().equals("delivered")) {
            btnSecondAction.setText("REQUEST RETURN ORDER");
            btnSecondAction.setVisibility(View.VISIBLE);
            btnSecondAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    intent = new Intent(getApplicationContext(), RequestActivity.class);
                    intent.putExtra("ORDER_CODE", order.getCode());
                    intent.putExtra("REQUEST_CODE", IntegerUtils.REQUEST_RETURN_ORDER);
                    startActivityForResult(intent, IntegerUtils.REQUEST_RETURN_ORDER);
                }
            });
        } else {
            btnSecondAction.setVisibility(View.GONE);
        }
        if (btnFirstAction.getVisibility() == View.VISIBLE
                || btnSecondAction.getVisibility() == View.VISIBLE) {
            layoutAction.setVisibility(View.VISIBLE);
        }
    }

    private boolean checkPaymentCompleted() {
        txtFullPayment.setVisibility(View.GONE);
        if (order.getStatus().equals("unpaid")) {
            txtFullPayment.setVisibility(View.VISIBLE);
            status = "created";
            return true;
        }
        return false;
    }

    private void checkOrderReview() {
        if (order.getCampaign() != null) {
            APIReviewCaller.getReviewByOrderId(order.getId(), true,
                    getApplication(), new APIListener() {
                @Override
                public void onReviewListFound(List<Review> reviewList) {
                    if (reviewList.size() > 0) {
                        order.getOrderProductList().get(0).setReview(reviewList.get(0));
                    }
                    setupRecViewOrderList();
                }

                @Override
                public void onFailedAPICall(int code) {
                    setupRecViewOrderList();
                }
            });
        } else {
            orderProductList = order.getOrderProductList();
            productCount = orderProductList.size();
            for (OrderProduct orderProduct : orderProductList) {
                APIReviewCaller.getReviewByOrderId(orderProduct.getId(), false,
                        getApplication(), new APIListener() {
                    @Override
                    public void onReviewListFound(List<Review> reviewList) {
                        if (reviewList.size() > 0) {
                            orderProduct.setReview(reviewList.get(0));
                        }
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
    }

    private void setupRecViewOrderList() {
        adapter = new RecViewOrderProductListAdapter(getApplicationContext(),
                OrderActivity.this, IntegerUtils.REQUEST_NOTE_READ_ONLY) {
            @Override
            public void addingReview(Review review, int position) {
                dialogBoxLoading = new DialogBoxLoading(OrderActivity.this);
                dialogBoxLoading.getWindow()
                                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBoxLoading.show();
                User user = new User();
                user.setUserId(userId);
                review.setUser(user);
                boolean isCampaign = (order.getCampaign() != null);
                if (isCampaign) {
                    review.setOrderId(order.getId());
                } else {
                    review.setOrderId(order.getOrderProductList().get(position).getId());
                }
                APIReviewCaller.addReview(review, token, isCampaign,
                        getApplication(), new APIListener() {
                    @Override
                    public void onReviewAdded(Review review) {
                        dialogBoxAlert = new DialogBoxAlert(OrderActivity.this,
                                IntegerUtils.CONFIRM_ACTION_CODE_SUCCESS,
                                StringUtils.MES_SUCCESSFUL_ADD_REVIEW,"") {
                            @Override
                            public void onClickAction() {
                                applyReview(review);
                            }
                        };
                        dialogBoxAlert.getWindow()
                                      .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialogBoxAlert.show();
                    }

                    @Override
                    public void onFailedAPICall(int code) {
                        if (dialogBoxLoading.isShowing()) {
                            dialogBoxLoading.dismiss();
                        }
                        if (code == IntegerUtils.ERROR_NO_USER) {
                            MethodUtils.displayErrorAccountMessage(getApplicationContext(),
                                    OrderActivity.this);
                        } else {
                            MethodUtils.displayErrorAPIMessage(OrderActivity.this);
                        }
                    }
                });
            }
        };
        adapter.setOrder(order);
        recViewOrderProduct.setAdapter(adapter);
        recViewOrderProduct.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL, false));
        setOrderFoundState();
    }

    private void applyReview(Review review) {
        if (order.getCampaign() != null) {
            APIReviewCaller.getReviewByOrderId(order.getId(), true, getApplication(), new APIListener() {
                @Override
                public void onReviewListFound(List<Review> reviewList) {
                    if (reviewList.size() > 0) {
                        order.getOrderProductList().get(0).setReview(reviewList.get(0));
                    }
                    adapter.notifyDataSetChanged();
                    if (dialogBoxLoading.isShowing()) {
                        dialogBoxLoading.dismiss();
                    }
                }

                @Override
                public void onFailedAPICall(int code) {
                    MethodUtils.displayErrorAPIMessage(OrderActivity.this);
                    if (dialogBoxLoading.isShowing()) {
                        dialogBoxLoading.dismiss();
                    }
                }
            });
            order.getOrderProductList().get(0).setReview(review);
        } else {
            orderProductList = order.getOrderProductList();
            for (index = 0; index < orderProductList.size(); index++) {
                orderProduct = orderProductList.get(index);
                if (review.getOrderId().equals(orderProduct.getId())) {
                    break;
                }
            }
            APIReviewCaller.getReviewByOrderId(orderProduct.getId(), true,
                    getApplication(), new APIListener() {
                @Override
                public void onReviewListFound(List<Review> reviewList) {
                    if (reviewList.size() > 0) {
                        orderProduct.setReview(reviewList.get(0));
                        orderProductList.set(index, orderProduct);
                    }
                    adapter.notifyDataSetChanged();
                    if (dialogBoxLoading.isShowing()) {
                        dialogBoxLoading.dismiss();
                    }
                }

                @Override
                public void onFailedAPICall(int code) {
                    MethodUtils.displayErrorAPIMessage(OrderActivity.this);
                    adapter.notifyDataSetChanged();
                    if (dialogBoxLoading.isShowing()) {
                        dialogBoxLoading.dismiss();
                    }
                }
            });
            order.setOrderProductList(orderProductList);
        }
    }

    private void performPayment() {
        dialogBoxLoading = new DialogBoxLoading(OrderActivity.this);
        dialogBoxLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBoxLoading.show();
        DialogBoxSetupPayment dialogBoxSetupPayment;
        dialogBoxSetupPayment = new DialogBoxSetupPayment(OrderActivity.this,
                                                getApplicationContext(), totalPrice) {
            @Override
            public void onBankSelected(String bankString) {
                APIPaymentCaller.getPaymentURL(order, totalPrice, bankString,
                        "online payment", "online payment",
                        getApplication(), new APIListener() {
                    @Override
                    public void onResultStringFound(String url) {
                        DialogBoxPayment dialogBox = new DialogBoxPayment(OrderActivity.this, url) {
                            @Override
                            public void onCompletedPayment(String vnpRef) {
                                APIOrderCaller.updateOrderPaymentStatus(token, order,
                                        status,false, totalPrice, vnpRef,
                                        getApplication(), new APIListener() {
                                    @Override
                                    public void onUpdateSuccessful() {
                                        displayFinalMessage(StringUtils.MES_SUCCESSFUL_PAYMENT);
                                    }
                                    @Override
                                    public void onFailedAPICall(int code) {
                                        if (dialogBoxLoading.isShowing()) {
                                            dialogBoxLoading.dismiss();
                                        }
                                        if (code == IntegerUtils.ERROR_NO_USER) {
                                            MethodUtils.displayErrorAccountMessage(getApplicationContext(),
                                                    OrderActivity.this);
                                        } else {
                                            MethodUtils.displayErrorAPIMessage(OrderActivity.this);
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onStartPaymentFailed() {
                                if (dialogBoxLoading.isShowing()) {
                                    dialogBoxLoading.dismiss();
                                }
                                MethodUtils.displayErrorAPIMessage(OrderActivity.this);
                            }

                            @Override
                            public void onCancelDialogBox() {
                                displayCancelMessage();
                            }
                        };
                        dialogBox.getWindow()
                                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialogBox.show();
                    }

                    @Override
                    public void onFailedAPICall(int code) {
                        if (dialogBoxLoading.isShowing()) {
                            dialogBoxLoading.dismiss();
                        }
                        MethodUtils.displayErrorAPIMessage(OrderActivity.this);
                    }
                });
            }
            @Override
            public void onCancel() {
                displayCancelMessage();
            }
        };
        dialogBoxSetupPayment.getWindow()
                             .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBoxSetupPayment.show();
    }

    private void completeOrder() {
        dialogBoxLoading = new DialogBoxLoading(OrderActivity.this);
        dialogBoxLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBoxLoading.show();
        description = "set as completed by Customer";
        APIOrderCaller.updateOrderStatus(token, order, description, null,
                "completed", getApplication(), new APIListener() {
            @Override
            public void onUpdateSuccessful() {
                displayFinalMessage(StringUtils.MES_SUCCESSFUL_UPDATE_ORDER);
            }

            @Override
            public void onFailedAPICall(int code) {
                if (dialogBoxLoading.isShowing()) {
                    dialogBoxLoading.dismiss();
                }
                if (code == IntegerUtils.ERROR_NO_USER) {
                    MethodUtils.displayErrorAccountMessage(getApplicationContext(),
                            OrderActivity.this);
                } else {
                    MethodUtils.displayErrorAPIMessage(OrderActivity.this);
                }
            }
        });
    }

    private void displayCancelMessage() {
        if (dialogBoxLoading.isShowing()) {
            dialogBoxLoading.dismiss();
        }
        dialogBoxAlert = new DialogBoxAlert(OrderActivity.this,
                IntegerUtils.CONFIRM_ACTION_CODE_FAILED,
                StringUtils.MES_ERROR_PAYMENT_FAILED, "");
        dialogBoxAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBoxAlert.show();
    }

    private void displayFinalMessage(String message) {
        if (dialogBoxLoading.isShowing()) {
            dialogBoxLoading.dismiss();
        }
        dialogBoxAlert = new DialogBoxAlert(OrderActivity.this,
                IntegerUtils.CONFIRM_ACTION_CODE_SUCCESS, message, "") {
            @Override
            public void onClickAction() {
                setLoadingState();
                setupOrder();
            }
        };
        dialogBoxAlert.getWindow()
                      .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBoxAlert.show();
    }

    private void setLoadingState() {
        layoutLoading.setVisibility(View.VISIBLE);
        scrollViewMain.setVisibility(View.GONE);
        layoutNoOrder.setVisibility(View.GONE);
    }

    private void setOrderFoundState() {
        layoutLoading.setVisibility(View.GONE);
        scrollViewMain.setVisibility(View.VISIBLE);
        layoutNoOrder.setVisibility(View.GONE);
    }

    private void setNoOrderState() {
        layoutLoading.setVisibility(View.GONE);
        scrollViewMain.setVisibility(View.GONE);
        layoutNoOrder.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        imgBackFromOrderDetail.performClick();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setLoadingState();
        setupOrder();
    }
}