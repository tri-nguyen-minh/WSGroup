package dev.wsgroup.main.views.activities.ordering;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APIAddressCaller;
import dev.wsgroup.main.models.apis.callers.APICampaignCaller;
import dev.wsgroup.main.models.apis.callers.APIOrderCaller;
import dev.wsgroup.main.models.apis.callers.APIPaymentCaller;
import dev.wsgroup.main.models.apis.callers.APISupplierCaller;
import dev.wsgroup.main.models.dtos.Address;
import dev.wsgroup.main.models.dtos.Campaign;
import dev.wsgroup.main.models.dtos.CustomerDiscount;
import dev.wsgroup.main.models.dtos.Discount;
import dev.wsgroup.main.models.dtos.LoyaltyStatus;
import dev.wsgroup.main.models.dtos.Order;
import dev.wsgroup.main.models.dtos.OrderProduct;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewDiscountListSimpleAdapter;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewLoyaltyDiscountListAdapter;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewOrderingProductPriceAdapter;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.StringUtils;
import dev.wsgroup.main.views.activities.MainActivity;
import dev.wsgroup.main.views.activities.account.DeliveryAddressSelectActivity;
import dev.wsgroup.main.views.dialogbox.DialogBoxAlert;
import dev.wsgroup.main.views.dialogbox.DialogBoxConfirm;
import dev.wsgroup.main.views.dialogbox.DialogBoxPayment;
import dev.wsgroup.main.views.dialogbox.DialogBoxLoading;
import dev.wsgroup.main.views.dialogbox.DialogBoxSetupPayment;

public class InfoActivity extends AppCompatActivity {

    private ImageView imgBackFromCheckout, imgCheckoutMessage, imgCheckoutHome;
    private ConstraintLayout layoutAddress, layoutCampaignSaving;
    private EditText editPhoneNumber;
    private TextView txtDeliveryAddress, txtPaymentDescription, txtTotalPrice,
            txtCampaignSaving, txtFinalPrice, txtDeliveryPrice, lblDiscountPrice;
    private RecyclerView recViewOrderProductPrice, recViewLoyalStatusList, recViewDiscountList;
    private Button btnConfirmOrder;
    private Spinner spinnerPayment;
    private RelativeLayout layoutLoading;
    private LinearLayout layoutReceipt;

    private DialogBoxLoading dialogBoxLoading;
    private DialogBoxConfirm dialogBoxConfirm;
    private DialogBoxAlert dialogBoxAlert;
    private List<OrderProduct> orderProductList, tempList;
    private int orderCount;
    private String token, phone, description;
    private double totalPrice, discountPrice, finalPrice, deliveryPrice,
            loyaltyDiscountPrice, advancePrice;
    private SharedPreferences sharedPreferences;
    private Order order;
    private CustomerDiscount customerDiscount;
    private Address currentAddress;
    private boolean advancePaymentCancelled, addressLoaded, receiptLoaded;
    private List<LoyaltyStatus> loyaltyStatusList;
    private List<Discount> discountList;

    private ArrayList<Order> orderList;
    private int requestCode;
    private Campaign campaign;
    private final String[] paymentData = {"Payment on Delivery", "Payment via VNPay"};

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordering_info);
        this.getSupportActionBar().hide();

        imgBackFromCheckout = findViewById(R.id.imgBackFromCheckout);
        imgCheckoutMessage = findViewById(R.id.imgCheckoutMessage);
        imgCheckoutHome = findViewById(R.id.imgCheckoutHome);
        layoutAddress = findViewById(R.id.layoutAddress);
        layoutCampaignSaving = findViewById(R.id.layoutCampaignSaving);
        editPhoneNumber = findViewById(R.id.editPhoneNumber);
        txtDeliveryAddress = findViewById(R.id.txtDeliveryAddress);
        txtPaymentDescription = findViewById(R.id.txtPaymentDescription);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);
        txtCampaignSaving = findViewById(R.id.txtCampaignSaving);
        txtFinalPrice = findViewById(R.id.txtFinalPrice);
        txtDeliveryPrice = findViewById(R.id.txtDeliveryPrice);
        lblDiscountPrice = findViewById(R.id.lblDiscountPrice);
        recViewOrderProductPrice = findViewById(R.id.recViewOrderProductPrice);
        recViewLoyalStatusList = findViewById(R.id.recViewLoyalStatusList);
        recViewDiscountList = findViewById(R.id.recViewDiscountList);
        btnConfirmOrder = findViewById(R.id.btnConfirmOrder);
        spinnerPayment = findViewById(R.id.spinnerPayment);
        layoutLoading = findViewById(R.id.layoutLoading);
        layoutReceipt = findViewById(R.id.layoutReceipt);

        sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        phone = sharedPreferences.getString("PHONE", "");
        token = sharedPreferences.getString("TOKEN", "");
        advancePaymentCancelled = false;
        addressLoaded = false;
        receiptLoaded = false;
        layoutLoading.setVisibility(View.VISIBLE);
        layoutReceipt.setVisibility(View.GONE);
        orderList = (ArrayList<Order>) getIntent().getSerializableExtra("ORDER_LIST");
        requestCode = getIntent().getIntExtra("REQUEST_CODE", IntegerUtils.REQUEST_COMMON);
        setupSpinner();

        APIAddressCaller.getDefaultAddress(token, getApplication(), new APIListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onAddressFound(Address address) {
                super.onAddressFound(address);
                currentAddress = address;
                setupConfirmAddress();
            }
        });

        orderProductList = new ArrayList<>();
        orderCount = orderList.size();

        for (Order order : orderList) {
            System.out.println("test: " + order.getTotalPrice());
            tempList = order.getOrderProductList();
            for (OrderProduct oProduct : tempList) {
                orderProductList.add(oProduct);
            }
            APISupplierCaller.getCustomerLoyaltyStatus(token, order.getSupplier().getId(), getApplication(), new APIListener() {
                @Override
                public void onLoyaltyStatusFound(LoyaltyStatus status) {
                    if (status != null) {
                        if (loyaltyStatusList == null) {
                            loyaltyStatusList = new ArrayList<>();
                        }
                        loyaltyStatusList.add(status);
                    }
                    orderCount--;
                    if (orderCount == 0) {
                        checkLoyaltyStatusList();
                    }
                }

                @Override
                public void onFailedAPICall(int code) {
                    orderCount--;
                    if (orderCount == 0) {
                        checkLoyaltyStatusList();
                    }
                }
            });
        }

        btnConfirmOrder.setEnabled(false);
        btnConfirmOrder.getBackground().setTint(getResources().getColor(R.color.gray_light));
        editPhoneNumber.setText(MethodUtils
                .formatPhoneNumberWithCountryCode(MethodUtils.formatPhoneNumber(phone)));
        editPhoneNumber.setEnabled(false);

        imgBackFromCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (advancePaymentCancelled) {
                    dialogBoxConfirm = new DialogBoxConfirm(InfoActivity.this,
                            StringUtils.MES_CONFIRM_CANCEL_ORDER) {
                        @Override
                        public void onYesClicked() {
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                    };
                    dialogBoxConfirm.setDescription(StringUtils.MES_CONFIRM_CANCEL_ORDER_DESC);
                    dialogBoxConfirm.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialogBoxConfirm.show();
                } else {
                    setResult(RESULT_CANCELED);
                    finish();
                }
            }
        });

        imgCheckoutHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        layoutAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addressIntent = new Intent(getApplicationContext(), DeliveryAddressSelectActivity.class);
                addressIntent.putExtra("ADDRESS", currentAddress);
                startActivityForResult(addressIntent, IntegerUtils.REQUEST_COMMON);
            }
        });

        btnConfirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBoxConfirm = new DialogBoxConfirm(InfoActivity.this, StringUtils.MES_CONFIRM_ORDER_METHOD) {
                    @Override
                    public void onYesClicked() {
                        super.onYesClicked();
                        processOrder();
                    }
                };
                dialogBoxConfirm.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBoxConfirm.show();
            }
        });
    }

    private void setupConfirmAddress() {
        if (currentAddress != null) {
            addressLoaded = true;
            txtDeliveryAddress.setText(currentAddress.getAddressString());
            txtDeliveryAddress.setTextColor(getResources().getColor(R.color.gray_dark));
        } else {
            addressLoaded = false;
            txtDeliveryAddress.setText("No Delivery Address");
            txtDeliveryAddress.setTextColor(getResources().getColor(R.color.gray));
        }
        checkValidConfirmButton();
    }

    private void checkLoyaltyStatusList() {
        if (loyaltyStatusList != null) {
            if (loyaltyStatusList.size() > 0) {
                for (LoyaltyStatus status : loyaltyStatusList) {
                    for (Order order : orderList) {
                        if (order.getSupplier().getId().equals(status.getSupplier().getId())) {
                            status.setDiscountPriceFromTotal(order.getTotalPrice());
                            status.setSupplier(order.getSupplier());
                        }
                    }
                }
                recViewLoyalStatusList.setVisibility(View.VISIBLE);
            } else {
                recViewLoyalStatusList.setVisibility(View.GONE);
            }
        } else {
            recViewLoyalStatusList.setVisibility(View.GONE);
        }
        loadReceipt();
        receiptLoaded = true;
        layoutLoading.setVisibility(View.GONE);
        layoutReceipt.setVisibility(View.VISIBLE);
        checkValidConfirmButton();
    }

    private void loadReceipt() {
        if (requestCode == IntegerUtils.REQUEST_ORDER_CAMPAIGN) {
            campaign = orderList.get(0).getCampaign();
        }
        if (orderProductList.size() > 0) {
            totalPrice = 0; discountPrice = 0; finalPrice = 0;
            deliveryPrice = 0; loyaltyDiscountPrice = 0;
            for (OrderProduct orderProduct : orderProductList) {
                totalPrice += orderProduct.getProduct().getRetailPrice() * orderProduct.getQuantity();
            }
            txtTotalPrice.setText(MethodUtils.formatPriceString(totalPrice));
            txtDeliveryPrice.setText(MethodUtils.formatPriceString(deliveryPrice));

            txtTotalPrice.setText(MethodUtils.formatPriceString(totalPrice));
            if (campaign == null) {
                layoutCampaignSaving.setVisibility(View.GONE);
                discountList = new ArrayList<>();
                for (int i = 0; i < orderList.size(); i++) {
                    customerDiscount = orderList.get(i).getCustomerDiscount();
                    if (customerDiscount != null) {
                        discountList.add(customerDiscount.getDiscount());
                        discountPrice += customerDiscount.getDiscount().getDiscountPrice();
                    }
                }
                if (discountList.size() > 0) {
                    RecViewDiscountListSimpleAdapter discountAdapter
                            = new RecViewDiscountListSimpleAdapter(getApplicationContext());
                    discountAdapter.setDiscountList(discountList);
                    recViewDiscountList.setAdapter(discountAdapter);
                    recViewDiscountList.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                            LinearLayoutManager.VERTICAL, false));
                }
            } else {
                layoutCampaignSaving.setVisibility(View.VISIBLE);
                lblDiscountPrice.setText("Campaign Saving");
                discountPrice = campaign.getSavingPrice() * orderProductList.get(0).getQuantity();
            }
            if (loyaltyStatusList != null) {
                if (loyaltyStatusList.size() > 0) {
                    for (LoyaltyStatus status : loyaltyStatusList) {
                        loyaltyDiscountPrice += status.getDiscountPrice();
                    }
                }
            }
            txtCampaignSaving.setText(MethodUtils.formatPriceString(discountPrice));
            finalPrice = totalPrice - discountPrice - deliveryPrice - loyaltyDiscountPrice;
            txtFinalPrice.setText(MethodUtils.formatPriceString(finalPrice));

            RecViewOrderingProductPriceAdapter productAdapter
                    = new RecViewOrderingProductPriceAdapter(getApplicationContext());
            productAdapter.setOrderProductList(orderProductList);
            recViewOrderProductPrice.setAdapter(productAdapter);
            recViewOrderProductPrice.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                    LinearLayoutManager.VERTICAL, false));

            RecViewLoyaltyDiscountListAdapter loyaltyAdapter
                    = new RecViewLoyaltyDiscountListAdapter(getApplicationContext());
            loyaltyAdapter.setLoyaltyStatusList(loyaltyStatusList);
            recViewLoyalStatusList.setAdapter(loyaltyAdapter);
            recViewLoyalStatusList.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                    LinearLayoutManager.VERTICAL, false));
        }
    }

    private void checkValidConfirmButton() {
        if (addressLoaded && receiptLoaded) {
            btnConfirmOrder.setEnabled(true);
            btnConfirmOrder.getBackground().setTint(getResources().getColor(R.color.blue_main));
        } else {
            btnConfirmOrder.setEnabled(false);
            btnConfirmOrder.getBackground().setTint(getResources().getColor(R.color.gray_light));
        }
    }

    private void setupSpinner() {
        ArrayAdapter adapter = new ArrayAdapter<String>(InfoActivity.this,
                R.layout.spinner_selected_item, paymentData);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerPayment.setAdapter(adapter);
        spinnerPayment.setSelection(0);
    }

    private void processOrder() {
            dialogBoxLoading = new DialogBoxLoading(InfoActivity.this);
            dialogBoxLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogBoxLoading.show();
            if (requestCode == IntegerUtils.REQUEST_ORDER_CAMPAIGN) {
                order = orderList.get(0);
                order.setDiscountPrice(loyaltyDiscountPrice);
                order.setAddress(currentAddress);
                if (spinnerPayment.getSelectedItemPosition() == 0) {
                    order.setPaymentMethod("cod");
                } else {
                    order.setPaymentMethod("online");
                }
                APIOrderCaller.addOrder(token, order, getApplication(), new APIListener() {
                    @Override
                    public void onOrderSuccessful(Order result) {
                        super.onOrderSuccessful(result);
                        onSuccessfulOrder();
                    }
                });
            } else {
                orderCount = orderList.size();
                for (int i = 0; i< orderList.size(); i++) {
                    Order currentOrder = orderList.get(i);
                    for (OrderProduct orderProduct : currentOrder.getOrderProductList()) {
                        System.out.println(orderProduct.getProduct().getName());
                    }
                    discountPrice = 0;
                    if (currentOrder.getCustomerDiscount() != null) {
                        discountPrice += currentOrder.getCustomerDiscount().getDiscount().getDiscountPrice();
                    }
                    LoyaltyStatus status = findLoyaltyStatus(currentOrder);
                    if (status != null) {
                        discountPrice += status.getDiscountPrice();
                    }
                    currentOrder.setDiscountPrice(discountPrice);
                    currentOrder.setAddress(currentAddress);
                    if (spinnerPayment.getSelectedItemPosition() == 0) {
                        currentOrder.setPaymentMethod("cod");
                    } else {
                        currentOrder.setPaymentMethod("online");
                    }
                    System.out.println("order " + orderCount);
                    APIOrderCaller.addOrder(token, currentOrder, getApplication(), new APIListener() {
                        @Override
                        public void onOrderSuccessful(Order result) {
                            System.out.println(currentOrder.getId() + " - " + orderCount);
                            orderCount--;
                            System.out.println("final " + orderCount);
                            onSuccessfulOrder();
                        }
                    });
                }
            }
    }

    private LoyaltyStatus findLoyaltyStatus(Order order) {
        for (LoyaltyStatus status : loyaltyStatusList) {
            if (order.getSupplier().getId().equals(status.getSupplier().getId())) {
                return status;
            }
        }
        return null;
    }

    private void onSuccessfulOrder() {
        if (requestCode == IntegerUtils.REQUEST_ORDER_RETAIL) {
            if (orderCount == 0) {
                processPayment();
            }
        } else {
            processPayment();
        }
    }

    private void processPayment() {
        if (requestCode == IntegerUtils.REQUEST_ORDER_RETAIL) {
            displayFinalMessage();
        } else {
            int advancePercentage = campaign.getAdvancePercentage();
            advancePrice = advancePercentage * finalPrice / 100;
            description = "Advance Fee: " + MethodUtils.formatPriceString(advancePrice);
            dialogBoxAlert = new DialogBoxAlert(InfoActivity.this,
                    IntegerUtils.CONFIRM_ACTION_CODE_ALERT, StringUtils.MES_CONFIRM_REQUIRE_ADVANCE, description) {
                @Override
                public void onClickAction() {
                    super.onClickAction();
                    performAdvancePayment();
                }
            };
            dialogBoxAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogBoxAlert.show();
        }
    }

    private void performAdvancePayment() {
        DialogBoxSetupPayment dialogBoxSetupPayment
                = new DialogBoxSetupPayment(InfoActivity.this, advancePrice) {
            @Override
            public void onBankSelected(String bankString) {
                APIPaymentCaller.getPaymentURL(order, advancePrice, bankString, "advance",
                        "topup", getApplication(), new APIListener() {
                    @Override
                    public void onGettingPaymentURL(String url) {
                        super.onGettingPaymentURL(url);
                        DialogBoxPayment dialogBox
                                = new DialogBoxPayment(InfoActivity.this, url, order) {
                            @Override
                            public void onCompletedPayment(String vnpRef) {
                                APIOrderCaller.updateOrderPaymentStatus(token, order,
                                        "advanced", true, advancePrice, vnpRef,
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
        description = "Failed to process order!";
        dialogBoxAlert = new DialogBoxAlert(InfoActivity.this,
                IntegerUtils.CONFIRM_ACTION_CODE_FAILED, StringUtils.MES_ERROR_PAYMENT_FAILED, description);
        dialogBoxAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBoxAlert.show();
    }

    private void displayFinalMessage() {
        if (dialogBoxLoading.isShowing()) {
            dialogBoxLoading.dismiss();
        }
        if (spinnerPayment.getSelectedItemPosition() == 0) {
            dialogBoxAlert = new DialogBoxAlert(InfoActivity.this,
                    IntegerUtils.CONFIRM_ACTION_CODE_SUCCESS, StringUtils.MES_SUCCESSFUL_ORDER, "") {
                @Override
                public void onClickAction() {
                    super.onClickAction();
//                    if (dialogBoxLoading.isShowing()) {
//                        dialogBoxLoading.dismiss();
//                    }
                    Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                    mainIntent.putExtra("MAIN_TAB_POSITION", 1);
                    if (requestCode == IntegerUtils.REQUEST_ORDER_RETAIL) {
                        if (spinnerPayment.getSelectedItemPosition() == 0) {
                            mainIntent.putExtra("HISTORY_TAB_POSITION", 2);
                        } else {
                            mainIntent.putExtra("HISTORY_TAB_POSITION", 0);
                        }
                        startActivity(mainIntent);
                    } else {
//                        re-check campaign
//                        APICampaignCaller.getCampaignById(campaign.getId(), getApplication(), new APIListener() {
//                            @Override
//                            public void onCampaignFound(Campaign campaign) {
//                                if (dialogBoxLoading.isShowing()) {
//                                    dialogBoxLoading.dismiss();
//                                }
//                                if (campaign.getStatus().equals("active")) {
//
//                                } else {
//
//                                }
//                            }
//                        });
                        if (spinnerPayment.getSelectedItemPosition() == 0) {
                            mainIntent.putExtra("HISTORY_TAB_POSITION", 2);
                        } else {
                            mainIntent.putExtra("HISTORY_TAB_POSITION", 0);
                        }
                        startActivity(mainIntent);
                    }
                }
            };
        } else {
            description = "Your order has been created!";
            dialogBoxAlert = new DialogBoxAlert(InfoActivity.this,
                    IntegerUtils.CONFIRM_ACTION_CODE_SUCCESS, description, StringUtils.MES_CONFIRM_REQUIRE_PAYMENT) {
                @Override
                public void onClickAction() {
                    super.onClickAction();
                    Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                    mainIntent.putExtra("MAIN_TAB_POSITION", 1);
                    if (requestCode == IntegerUtils.REQUEST_ORDER_RETAIL) {
                        mainIntent.putExtra("HISTORY_TAB_POSITION", 0);
                    } else {
                        mainIntent.putExtra("HISTORY_TAB_POSITION", 1);
                    }
                    startActivity(mainIntent);
                }
            };
        }
        dialogBoxAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBoxAlert.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data != null) {
                currentAddress = (Address) data.getSerializableExtra("ADDRESS");
                setupConfirmAddress();
            }
        }
    }
}