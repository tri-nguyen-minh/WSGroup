package dev.wsgroup.main.views.activities.order;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APIAddressCaller;
import dev.wsgroup.main.models.apis.callers.APICampaignCaller;
import dev.wsgroup.main.models.apis.callers.APICartCaller;
import dev.wsgroup.main.models.apis.callers.APIDiscountCaller;
import dev.wsgroup.main.models.apis.callers.APIOrderCaller;
import dev.wsgroup.main.models.apis.callers.APIPaymentCaller;
import dev.wsgroup.main.models.apis.callers.APIProductCaller;
import dev.wsgroup.main.models.apis.callers.APISupplierCaller;
import dev.wsgroup.main.models.dtos.Address;
import dev.wsgroup.main.models.dtos.Campaign;
import dev.wsgroup.main.models.dtos.CampaignMilestone;
import dev.wsgroup.main.models.dtos.CustomerDiscount;
import dev.wsgroup.main.models.dtos.Discount;
import dev.wsgroup.main.models.dtos.LoyaltyStatus;
import dev.wsgroup.main.models.dtos.Order;
import dev.wsgroup.main.models.dtos.OrderProduct;
import dev.wsgroup.main.models.dtos.Product;
import dev.wsgroup.main.models.dtos.Supplier;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewDiscountListSimpleAdapter;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewLoyaltyDiscountListAdapter;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewOrderingProductPriceAdapter;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.StringUtils;
import dev.wsgroup.main.views.activities.CartActivity;
import dev.wsgroup.main.views.activities.MainActivity;
import dev.wsgroup.main.views.activities.account.DeliveryAddressSelectActivity;
import dev.wsgroup.main.views.dialogbox.DialogBoxAlert;
import dev.wsgroup.main.views.dialogbox.DialogBoxConfirm;
import dev.wsgroup.main.views.dialogbox.DialogBoxLoading;
import dev.wsgroup.main.views.dialogbox.DialogBoxPayment;
import dev.wsgroup.main.views.dialogbox.DialogBoxSetupPayment;

public class OrderInfoActivity extends AppCompatActivity {

    private ImageView imgBackFromCheckout, imgCheckoutHome;
    private ConstraintLayout layoutAddress, layoutCampaignSaving;
    private EditText editPhoneNumber;
    private TextView txtDeliveryAddress, txtTotalPrice,
            txtCampaignSaving, txtFinalPrice, txtDeliveryPrice, lblDiscountPrice;
    private RecyclerView recViewOrderProductPrice, recViewLoyalStatusList, recViewDiscountList;
    private Button btnConfirmOrder;
    private Spinner spinnerPayment;
    private RelativeLayout layoutLoading;
    private LinearLayout layoutReceipt;

    private DialogBoxLoading dialogBoxLoading;
    private DialogBoxConfirm dialogBoxConfirm;
    private DialogBoxAlert dialogBoxAlert;
    private int orderCount, orderProductCount;
    private String token, phone, description;
    private double totalPrice, discountPrice, finalPrice, deliveryPrice,
            loyaltyDiscountPrice, advancePrice;
    private SharedPreferences sharedPreferences;
    private Order order;
    private OrderProduct orderProduct;
    private CustomerDiscount customerDiscount;
    private Address currentAddress;
    private boolean advancePaymentCancelled, addressLoaded, receiptLoaded;
    private List<LoyaltyStatus> loyaltyStatusList;
    private List<Discount> discountList;
    private ArrayList<Order> orderList;
    private List<OrderProduct> orderProductList, tempList;
    private Map<Order, String> invalidOrderMap;
    private int requestCode;
    private Campaign campaign;
    private CampaignMilestone milestone;
    private final String[] paymentData = {"Payment on Delivery", "Payment via VNPAY"};

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordering_info);
        this.getSupportActionBar().hide();

        imgBackFromCheckout = findViewById(R.id.imgBackFromCheckout);
        imgCheckoutHome = findViewById(R.id.imgCheckoutHome);
        layoutAddress = findViewById(R.id.layoutAddress);
        layoutCampaignSaving = findViewById(R.id.layoutCampaignSaving);
        editPhoneNumber = findViewById(R.id.editPhoneNumber);
        txtDeliveryAddress = findViewById(R.id.txtDeliveryAddress);
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

            @Override
            public void onFailedAPICall(int code) {
                if (code == IntegerUtils.ERROR_NO_USER) {
                    MethodUtils.displayErrorAccountMessage(getApplicationContext(),
                            OrderInfoActivity.this);
                } else {
                    setupConfirmAddress();
                }
            }
        });

        orderProductList = new ArrayList<>();
        orderCount = orderList.size();

        loadData();

        btnConfirmOrder.setEnabled(false);
        btnConfirmOrder.getBackground().setTint(getResources().getColor(R.color.gray_light));
        editPhoneNumber.setText(MethodUtils
                .formatPhoneNumberWithCountryCode(MethodUtils.formatPhoneNumber(phone)));
        editPhoneNumber.setEnabled(false);

        imgBackFromCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (advancePaymentCancelled) {
                    dialogBoxConfirm = new DialogBoxConfirm(OrderInfoActivity.this,
                            StringUtils.MES_CONFIRM_ABANDON_ORDER) {
                        @Override
                        public void onYesClicked() {
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                    };
                    dialogBoxConfirm.setDescription(StringUtils.DESC_ABANDON_ORDER);
                    dialogBoxConfirm.getWindow()
                                    .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
                Intent addressIntent = new Intent(getApplicationContext(),
                                                    DeliveryAddressSelectActivity.class);
                addressIntent.putExtra("ADDRESS", currentAddress);
                startActivityForResult(addressIntent, IntegerUtils.REQUEST_COMMON);
            }
        });

        btnConfirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBoxConfirm = new DialogBoxConfirm(OrderInfoActivity.this,
                                                        StringUtils.MES_CONFIRM_ORDER_METHOD) {
                    @Override
                    public void onYesClicked() {
                        super.onYesClicked();
                        processOrder();
                    }
                };
                dialogBoxConfirm.getWindow()
                                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBoxConfirm.show();
            }
        });
    }

    private void loadData() {
        for (Order order : orderList) {
            tempList = order.getOrderProductList();
            for (OrderProduct oProduct : tempList) {
                orderProductList.add(oProduct);
            }
            APISupplierCaller.getCustomerLoyaltyStatus(token, order.getSupplier().getId(),
                    getApplication(), new APIListener() {
                @Override
                public void onLoyaltyStatusListFound(List<LoyaltyStatus> list) {
                    if (!list.isEmpty()) {
                        if (loyaltyStatusList == null) {
                            loyaltyStatusList = new ArrayList<>();
                        }
                        loyaltyStatusList.add(list.get(0));
                    }
                    orderCount--;
                    if (orderCount == 0) {
                        checkLoyaltyStatusList();
                    }
                }

                @Override
                public void onFailedAPICall(int code) {
                    if (code == IntegerUtils.ERROR_NO_USER) {
                        MethodUtils.displayErrorAccountMessage(getApplicationContext(),
                                OrderInfoActivity.this);
                    } else {
                        orderCount--;
                        if (orderCount == 0) {
                            checkLoyaltyStatusList();
                        }
                    }
                }
            });
        }
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
                            totalPrice = order.getTotalPrice();
                            if (order.getCustomerDiscount() != null) {
                                totalPrice -= order.getCustomerDiscount()
                                                   .getDiscount()
                                                   .getDiscountPrice();
                            } else if (order.getCampaign() != null) {
                                campaign = order.getCampaign();
                                orderProduct = order.getOrderProductList().get(0);
                                totalPrice -= (campaign.getPrice() * orderProduct.getQuantity());
                                if (campaign.getShareFlag()) {
                                     milestone = MethodUtils.getReachedCampaignMilestone(campaign.getMilestoneList(),
                                            orderProduct.getQuantity() + campaign.getQuantityCount());
                                    if (milestone != null) {
                                        discountPrice = (orderProduct.getQuantity() * milestone.getPrice());
                                        totalPrice -= discountPrice;
                                    }
                                }
                            }
                            status.setDiscountPriceFromTotal(totalPrice);
                            status.setSupplier(order.getSupplier());
                            order.setLoyaltyDiscountPercent(status.getDiscountPercent());
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
            deliveryPrice = 5000;
            txtDeliveryPrice.setText(MethodUtils.formatPriceString(deliveryPrice));
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
                order = orderList.get(0);
                orderProduct = order.getOrderProductList().get(0);
                discountPrice = orderProduct.getProduct().getRetailPrice();
                if (campaign.getShareFlag()) {
                    milestone = MethodUtils.getReachedCampaignMilestone(campaign.getMilestoneList(),
                            orderProduct.getQuantity() + campaign.getQuantityCount());
                    if (milestone != null) {
                        discountPrice -= milestone.getPrice();
                    } else {
                        discountPrice -= campaign.getPrice();
                    }
                } else {
                    discountPrice -= campaign.getPrice();
                }
                discountPrice *= orderProductList.get(0).getQuantity();
            }
            if (loyaltyStatusList != null) {
                if (loyaltyStatusList.size() > 0) {
                    for (LoyaltyStatus status : loyaltyStatusList) {
                        loyaltyDiscountPrice += status.getDiscountPrice();
                    }
                }
            }
            txtCampaignSaving.setText(MethodUtils.formatPriceString(discountPrice));
            finalPrice = totalPrice - discountPrice + deliveryPrice - loyaltyDiscountPrice;
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
        ArrayAdapter adapter = new ArrayAdapter<String>(OrderInfoActivity.this,
                R.layout.spinner_selected_item, paymentData);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerPayment.setAdapter(adapter);
        spinnerPayment.setSelection(0);
    }

    private void processOrder() {
            dialogBoxLoading = new DialogBoxLoading(OrderInfoActivity.this);
            dialogBoxLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogBoxLoading.show();
            if (requestCode == IntegerUtils.REQUEST_ORDER_CAMPAIGN) {
                order = orderList.get(0);
                orderProduct = order.getOrderProductList().get(0);
                discountPrice = orderProduct.getProduct().getRetailPrice();
                if (campaign.getShareFlag()) {
                    milestone = MethodUtils.getReachedCampaignMilestone(campaign.getMilestoneList(),
                            orderProduct.getQuantity() + campaign.getQuantityCount());
                    if (milestone != null) {
                        discountPrice -= milestone.getPrice();
                    } else {
                        discountPrice -= campaign.getPrice();
                    }
                } else {
                    discountPrice -= campaign.getPrice();
                }
                discountPrice *= orderProductList.get(0).getQuantity();
                discountPrice += loyaltyDiscountPrice;
                order.setDiscountPrice(discountPrice);
                order.setAddress(currentAddress);
                if (spinnerPayment.getSelectedItemPosition() == 0) {
                    order.setPaymentMethod("cod");
                } else {
                    order.setPaymentMethod("online");
                }
                checkSupplierCampaign(order);
            } else {
                tempList = new ArrayList<>();
                invalidOrderMap = new HashMap<>();
                orderProductCount = orderProductList.size();
                System.out.println("start: " + orderProductCount);
                for (OrderProduct orderProduct : orderProductList) {
                    checkSupplierRetail(orderProduct);
                }
            }
    }

    private Order findOrderByOrderProduct(OrderProduct orderProduct) {
        for (Order order : orderList) {
            if (order.getOrderProductList().contains(orderProduct)) {
                return order;
            }
        }
        return null;
    }

    private LoyaltyStatus findLoyaltyStatus(Order order) {
        if (loyaltyStatusList != null) {
            for (LoyaltyStatus status : loyaltyStatusList) {
                if (order.getSupplier().getId().equals(status.getSupplier().getId())) {
                    return status;
                }
            }
        }
        return null;
    }

    private void finishCampaignOrder(Order newOrder) {
        APIOrderCaller.addOrder(token, newOrder, getApplication(), new APIListener() {
            @Override
            public void onOrderSuccessful(Order result) {
                order = result;
                onSuccessfulOrder();
            }

            @Override
            public void onFailedAPICall(int code) {
                if (code == IntegerUtils.ERROR_NO_USER) {
                    MethodUtils.displayErrorAccountMessage(getApplicationContext(),
                            OrderInfoActivity.this);
                } else {
                    if (dialogBoxLoading.isShowing()) {
                        dialogBoxLoading.dismiss();
                    }
                    MethodUtils.displayErrorAPIMessage(OrderInfoActivity.this);
                }
            }
        });
    }

    private void finishRetailOrder() {
        orderCount = orderList.size();
        for (Order currentOrder : orderList) {
            discountPrice = 0;
            if (currentOrder.getCustomerDiscount() != null) {
                discountPrice += currentOrder.getCustomerDiscount()
                        .getDiscount()
                        .getDiscountPrice();
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
            APIOrderCaller.addOrder(token, currentOrder, getApplication(), new APIListener() {
                @Override
                public void onOrderSuccessful(Order result) {
                    if (currentOrder.getCustomerDiscount() != null) {
                        APIDiscountCaller.useDiscountCode(token,
                                currentOrder.getCustomerDiscount().getId(),
                                getApplication(), new APIListener() {
                            @Override
                            public void onUpdateSuccessful() {
                                orderCount--;
                                onSuccessfulOrder();
                            }

                            @Override
                            public void onFailedAPICall(int code) {
                                if (code == IntegerUtils.ERROR_NO_USER) {
                                    MethodUtils.displayErrorAccountMessage(getApplicationContext(),
                                            OrderInfoActivity.this);
                                } else {
                                    if (dialogBoxLoading.isShowing()) {
                                        dialogBoxLoading.dismiss();
                                    }
                                    displayFailedAlert(StringUtils.MES_ERROR_FAILED_API_CALL,
                                            "", IntegerUtils.ERROR_CHECKOUT_ORDER);
                                }
                            }
                        });
                    } else {
                        orderCount--;
                        onSuccessfulOrder();
                    }
                }

                @Override
                public void onFailedAPICall(int code) {
                    if (code == IntegerUtils.ERROR_NO_USER) {
                        MethodUtils.displayErrorAccountMessage(getApplicationContext(),
                                OrderInfoActivity.this);
                    } else {
                        if (dialogBoxLoading.isShowing()) {
                            dialogBoxLoading.dismiss();
                        }
                        displayFailedAlert(StringUtils.MES_ERROR_FAILED_API_CALL,
                                "", IntegerUtils.ERROR_CHECKOUT_ORDER);
                    }
                }
            });
        }
    }

    private void checkSupplierCampaign(Order order) {
        APISupplierCaller.getSupplierById(order.getSupplier().getId(),
                getApplication(), new APIListener() {
            @Override
            public void onSupplierFound(Supplier supplier) {
                if (supplier.getStatus()) {
                    description = "Supplier " + order.getSupplier().getName();
                    description += StringUtils.DESC_INVALID_ORDER_SUPPLIER_SUSPENDED;
                    removeOrderForCampaign(order, description);
                } else {
                    checkCampaign(order);
                }
            }

            @Override
            public void onFailedAPICall(int code) {
                if (code == IntegerUtils.ERROR_NO_USER) {
                    description = "Supplier " + order.getSupplier().getName();
                    description += StringUtils.DESC_INVALID_ORDER_SUPPLIER_SUSPENDED;
                    removeOrderForCampaign(order, description);
                } else {
                    displayFailedAlert(StringUtils.MES_ERROR_FAILED_API_CALL,
                            "", IntegerUtils.ERROR_CHECKOUT_ORDER);
                }
            }
        });
    }

    private void checkSupplierRetail(OrderProduct orderProduct) {
        APISupplierCaller.getSupplierById(orderProduct.getProduct().getSupplier().getId(),
                getApplication(), new APIListener() {
            @Override
            public void onSupplierFound(Supplier supplier) {
                if (supplier.getStatus()) {
                    order = findOrderByOrderProduct(orderProduct);
                    if (invalidOrderMap.get(order) == null) {
                        invalidOrderMap.put(order, StringUtils.DESC_INVALID_ORDER_SUPPLIER_SUSPENDED);
                    }
                    tempList.add(orderProduct);
                    orderProductCount--;
                    finishCheckingInvalidRetailOrder();
                } else {
                    checkProduct(orderProduct);
                }
            }

            @Override
            public void onFailedAPICall(int code) {
                if (code == IntegerUtils.ERROR_NO_USER) {
                    order = findOrderByOrderProduct(orderProduct);
                    if (invalidOrderMap.get(order) == null) {
                        invalidOrderMap.put(order, StringUtils.DESC_INVALID_ORDER_SUPPLIER_SUSPENDED);
                    }
                    tempList.add(orderProduct);
                    orderProductCount--;
                    finishCheckingInvalidRetailOrder();
                } else {
                    displayFailedAlert(StringUtils.MES_ERROR_FAILED_API_CALL,
                            "", IntegerUtils.ERROR_CHECKOUT_ORDER);
                }
            }
        });
    }

    private void checkCampaign(Order order) {
        APICampaignCaller.getCampaignById(order.getCampaign().getId(), getApplication(), new APIListener() {
            @Override
            public void onCampaignFound(Campaign campaign) {
                if (!campaign.getStatus().equals("active")) {
                    removeOrderForCampaign(order, StringUtils.DESC_INVALID_ORDER_CAMPAIGN_COMPLETED);
                } else {
                    int amount = campaign.getMaxQuantity() - campaign.getQuantityCount();
                    if (order.getOrderProductList().get(0).getQuantity() > amount) {
                        removeOrderForCampaign(order, StringUtils.DESC_INVALID_ORDER_QUANTITY);
                    } else {
                        finishCampaignOrder(order);
                    }
                }
            }

            @Override
            public void onNoJSONFound() {
                removeOrderForCampaign(order, StringUtils.DESC_INVALID_ORDER_CAMPAIGN_COMPLETED);
            }

            @Override
            public void onFailedAPICall(int code) {
                displayFailedAlert(StringUtils.MES_ERROR_FAILED_API_CALL,
                        "", IntegerUtils.ERROR_CHECKOUT_ORDER);
            }
        });
    }

    private void checkProduct(OrderProduct orderProduct) {
        APIProductCaller.getProductById(orderProduct.getProduct().getProductId(),
                getApplication(), new APIListener() {
            @Override
            public void onProductFound(Product product) {
                if (product.getStatus().equals("deactivated")) {
                    order = findOrderByOrderProduct(orderProduct);
                    if (invalidOrderMap.get(order) == null) {
                        invalidOrderMap.put(order, StringUtils.DESC_INVALID_ORDER_PRODUCT);
                    }
                    tempList.add(orderProduct);
                    orderProductCount--;
                    finishCheckingInvalidRetailOrder();
                } else if (product.getStatus().equals("active")) {
                    orderProductCount--;
                    finishCheckingInvalidRetailOrder();
                } else {
                    checkCampaignList(orderProduct);
                }
            }

            @Override
            public void onFailedAPICall(int code) {
                displayFailedAlert(StringUtils.MES_ERROR_FAILED_API_CALL,
                        "", IntegerUtils.ERROR_CHECKOUT_ORDER);
            }
        });
    }

    private void checkCampaignList(OrderProduct orderProduct) {
        APICampaignCaller.getCampaignListByProductId(orderProduct.getProduct().getProductId(),
                "active", null, getApplication(), new APIListener() {
            @Override
            public void onCampaignListFound(List<Campaign> campaignList) {
                if (campaignList.size() > 0) {
                    int quantity = orderProduct.getProduct().getQuantity();
                    for (Campaign campaign : campaignList) {
                        quantity -= campaign.getMaxQuantity();
                    }
                    if (orderProduct.getQuantity() > quantity) {
                        order = findOrderByOrderProduct(orderProduct);
                        if (invalidOrderMap.get(order) == null) {
                            invalidOrderMap.put(order, StringUtils.DESC_INVALID_ORDER_QUANTITY);
                        }
                        tempList.add(orderProduct);
                    }
                }
                orderProductCount--;
                finishCheckingInvalidRetailOrder();
            }

            @Override
            public void onFailedAPICall(int code) {
                displayFailedAlert(StringUtils.MES_ERROR_FAILED_API_CALL,
                        "", IntegerUtils.ERROR_CHECKOUT_ORDER);
            }
        });
    }

    private void finishCheckingInvalidRetailOrder() {
        if (orderProductCount == 0) {
            if (invalidOrderMap.size() == 0) {
                finishRetailOrder();
            } else {
                if (!orderList.get(0).isInCart()) {
                    order = orderList.get(0);
                    description = invalidOrderMap.get(order);
                    switch (description) {
                        case StringUtils.DESC_INVALID_ORDER_SUPPLIER_SUSPENDED: {
                            description = "Supplier " + order.getSupplier().getName() + description;
                            break;
                        }
                        case StringUtils.DESC_INVALID_ORDER_PRODUCT: {
                            description = "Supplier "+ order.getOrderProductList()
                                                            .get(0)
                                                            .getProduct()
                                                            .getName() + description;
                            break;
                        }
                    }
                    displayFailedAlert(StringUtils.MES_ALERT_INVALID_ORDER,
                            description, IntegerUtils.ERROR_CHECKOUT_ORDER_REMOVAL_SINGLE);
                } else {
                    removeOrderForRetail();
                }
            }
        }
    }

    private void removeOrderForCampaign(Order order, String description) {
        if (order.isInCart()) {
            orderProduct = order.getOrderProductList().get(0);
            APICartCaller.deleteCartItem(token, orderProduct.getCartProduct().getId(),
                    getApplication(), new APIListener() {
                @Override
                public void onUpdateSuccessful() {
                    displayFailedAlert(StringUtils.MES_ALERT_INVALID_ORDER,
                            description, IntegerUtils.ERROR_CHECKOUT_ORDER_REMOVAL_SINGLE);
                }
                @Override
                public void onFailedAPICall(int code) {
                    if (code == IntegerUtils.ERROR_NO_USER) {
                        MethodUtils.displayErrorAccountMessage(getApplicationContext(),
                                OrderInfoActivity.this);
                    } else {
                        displayFailedAlert(StringUtils.MES_ERROR_FAILED_API_CALL,
                                "", IntegerUtils.ERROR_CHECKOUT_ORDER);
                    }
                }
            });
        } else {
            displayFailedAlert(StringUtils.MES_ALERT_INVALID_ORDER,
                    description, IntegerUtils.ERROR_CHECKOUT_ORDER_REMOVAL_SINGLE);
        }
    }

    private void removeOrderForRetail() {
        orderProductCount = tempList.size();
        for (OrderProduct orderProduct : tempList) {
            APICartCaller.deleteCartItem(token, orderProduct.getCartProduct().getId(),
                    getApplication(), new APIListener() {
                @Override
                public void onUpdateSuccessful() {
                    orderProductCount--;
                    if (orderProductCount == 0) {
                        displayFailedAlert(StringUtils.MES_ALERT_INVALID_FULL_ORDER,
                                StringUtils.DESC_CART_REMOVAL,
                                IntegerUtils.ERROR_CHECKOUT_ORDER_REMOVAL_MULTIPLE);
                    }
                }
                @Override
                public void onFailedAPICall(int code) {
                    if (code == IntegerUtils.ERROR_NO_USER) {
                        MethodUtils.displayErrorAccountMessage(getApplicationContext(),
                                OrderInfoActivity.this);
                    } else {
                        displayFailedAlert(StringUtils.MES_ERROR_FAILED_API_CALL,
                                "", IntegerUtils.ERROR_CHECKOUT_ORDER);
                    }
                }
            });
        }
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
            if (campaign.getShareFlag()) {
                if (campaign.getMilestoneList() != null && campaign.getMilestoneList().size() > 0) {
                    milestone = campaign.getMilestoneList()
                                        .get(campaign.getMilestoneList().size() - 1);
                    discountPrice = milestone.getPrice();
                } else {
                    discountPrice = campaign.getPrice();
                }
            } else {
                discountPrice = campaign.getPrice();
            }
            discountPrice *= orderProductList.get(0).getQuantity();
            discountPrice -= loyaltyDiscountPrice;
            advancePrice = advancePercentage * discountPrice / 100;
            description = "Advance Fee: " + MethodUtils.formatPriceString(advancePrice);
            dialogBoxAlert = new DialogBoxAlert(OrderInfoActivity.this,
                    IntegerUtils.CONFIRM_ACTION_CODE_ALERT,
                    StringUtils.MES_ALERT_ADVANCE_REQUIRE, description) {
                @Override
                public void onClickAction() {
                    super.onClickAction();
                    performAdvancePayment();
                }
            };
            dialogBoxAlert.getWindow()
                          .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogBoxAlert.show();
        }
    }

    private void performAdvancePayment() {
        DialogBoxSetupPayment dialogBoxSetupPayment
                = new DialogBoxSetupPayment(OrderInfoActivity.this,
                                                getApplicationContext(), advancePrice) {
            @Override
            public void onBankSelected(String bankString) {
                APIPaymentCaller.getPaymentURL(order, advancePrice, bankString,
                        "online payment", "online payment",
                        getApplication(), new APIListener() {
                    @Override
                    public void onGettingPaymentURL(String url) {
                        super.onGettingPaymentURL(url);
                        DialogBoxPayment dialogBox
                                = new DialogBoxPayment(OrderInfoActivity.this, url) {
                            @Override
                            public void onCompletedPayment(String vnpRef) {
                                if (milestone != null) {
                                    discountPrice = milestone.getPrice();
                                } else {
                                    discountPrice = campaign.getPrice();
                                }
                                APIOrderCaller.updateOrderPaymentStatus(token, order,
                                        "advanced", true, advancePrice, discountPrice,
                                        vnpRef, getApplication(), new APIListener() {
                                    @Override
                                    public void onUpdateSuccessful() {
                                        displayFinalMessage();
                                    }

                                    @Override
                                    public void onFailedAPICall(int code) {
                                        if (code == IntegerUtils.ERROR_NO_USER) {
                                            MethodUtils.displayErrorAccountMessage(getApplicationContext(),
                                                    OrderInfoActivity.this);
                                        } else {
                                            displayFailedAlert(StringUtils.MES_ERROR_FAILED_API_CALL,
                                                    "", IntegerUtils.ERROR_CHECKOUT_ORDER);
                                            advancePaymentCancelled = true;
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onStartPaymentFailed() {
                                displayFailedAlert(StringUtils.MES_ERROR_FAILED_API_CALL,
                                        "", IntegerUtils.ERROR_CHECKOUT_ORDER);
                                advancePaymentCancelled = true;
                            }

                            @Override
                            public void onCancelDialogBox() {
                                displayCancelPaymentMessage();
                            }
                        };
                        dialogBox.getWindow()
                                 .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialogBox.show();
                    }
                    @Override
                    public void onFailedAPICall(int code) {
                        displayFailedAlert(StringUtils.MES_ERROR_FAILED_API_CALL,
                                "", IntegerUtils.ERROR_CHECKOUT_ORDER);
                        advancePaymentCancelled = true;
                    }
                });
            }

            @Override
            public void onCancel() {
                displayCancelPaymentMessage();
            }
        };
        dialogBoxSetupPayment.getWindow()
                             .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBoxSetupPayment.show();
    }

    private void displayFailedAlert(String message, String description, int identifier) {
        if (dialogBoxLoading.isShowing()) {
            dialogBoxLoading.dismiss();
        }
        if (dialogBoxAlert == null || !dialogBoxAlert.isShowing()) {
            dialogBoxAlert = new DialogBoxAlert(OrderInfoActivity.this,
                    IntegerUtils.CONFIRM_ACTION_CODE_FAILED, message, description) {
                @Override
                public void onClickAction() {
                    switch (identifier) {
                        case IntegerUtils.ERROR_CHECKOUT_ORDER_REMOVAL_SINGLE: {
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            break;
                        }
                        case IntegerUtils.ERROR_CHECKOUT_ORDER_REMOVAL_MULTIPLE: {
                            startActivity(new Intent(getApplicationContext(), CartActivity.class));
                            break;
                        }
                    }
                }
            };
            dialogBoxAlert.getWindow()
                    .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogBoxAlert.show();
        }
    }

    private void displayCancelPaymentMessage() {
        if (dialogBoxLoading.isShowing()) {
            dialogBoxLoading.dismiss();
        }
        advancePaymentCancelled = true;
        description = "Failed to process order!";
        dialogBoxAlert = new DialogBoxAlert(OrderInfoActivity.this,
                IntegerUtils.CONFIRM_ACTION_CODE_FAILED,
                StringUtils.MES_ERROR_PAYMENT_FAILED, description);
        dialogBoxAlert.getWindow()
                      .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBoxAlert.show();
    }

    private void displayFinalMessage() {
        if (spinnerPayment.getSelectedItemPosition() == 0) {
            dialogBoxAlert = new DialogBoxAlert(OrderInfoActivity.this,
                    IntegerUtils.CONFIRM_ACTION_CODE_SUCCESS,
                    StringUtils.MES_SUCCESSFUL_ORDER, "") {
                @Override
                public void onClickAction() {
                    super.onClickAction();
                    Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                    mainIntent.putExtra("MAIN_TAB_POSITION", 1);
                    if (requestCode == IntegerUtils.REQUEST_ORDER_RETAIL) {
                        if (dialogBoxLoading.isShowing()) {
                            dialogBoxLoading.dismiss();
                        }
                        mainIntent.putExtra("HISTORY_TAB_POSITION", 2);
                        startActivity(mainIntent);
                    } else {
                        APICampaignCaller.getCampaignById(campaign.getId(),
                                getApplication(), new APIListener() {
                            @Override
                            public void onCampaignFound(Campaign campaign) {
                                if (dialogBoxLoading.isShowing()) {
                                    dialogBoxLoading.dismiss();
                                }
                                if (campaign.getStatus().equals("active")) {
                                    mainIntent.putExtra("HISTORY_TAB_POSITION", 1);
                                } else {
                                    mainIntent.putExtra("HISTORY_TAB_POSITION", 2);
                                }
                                startActivity(mainIntent);
                            }
                        });
                    }
                }
            };
        } else {
            description = "Your order has been created!";
            dialogBoxAlert = new DialogBoxAlert(OrderInfoActivity.this,
                    IntegerUtils.CONFIRM_ACTION_CODE_SUCCESS, description,
                    StringUtils.MES_ALERt_REQUIRE_PAYMENT) {
                @Override
                public void onClickAction() {
                    super.onClickAction();
                    Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                    mainIntent.putExtra("MAIN_TAB_POSITION", 1);
                    if (requestCode == IntegerUtils.REQUEST_ORDER_RETAIL) {
                        mainIntent.putExtra("HISTORY_TAB_POSITION", 0);
                        startActivity(mainIntent);
                    } else {
                        APICampaignCaller.getCampaignById(campaign.getId(),
                                getApplication(), new APIListener() {
                            @Override
                            public void onCampaignFound(Campaign campaign) {
                                if (dialogBoxLoading.isShowing()) {
                                    dialogBoxLoading.dismiss();
                                }
                                if (campaign.getStatus().equals("active")) {
                                    mainIntent.putExtra("HISTORY_TAB_POSITION", 1);
                                } else {
                                    mainIntent.putExtra("HISTORY_TAB_POSITION", 0);
                                }
                                startActivity(mainIntent);
                            }
                        });
                    }
                }
            };
        }
        dialogBoxAlert.getWindow()
                      .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBoxAlert.show();
    }

    @Override
    public void onBackPressed() {
        imgBackFromCheckout.performClick();
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