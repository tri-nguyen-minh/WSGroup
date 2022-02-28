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
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APIAddressCaller;
import dev.wsgroup.main.models.apis.callers.APIOrderCaller;
import dev.wsgroup.main.models.apis.callers.APIPaymentCaller;
import dev.wsgroup.main.models.dtos.Address;
import dev.wsgroup.main.models.dtos.Campaign;
import dev.wsgroup.main.models.dtos.CustomerDiscount;
import dev.wsgroup.main.models.dtos.Order;
import dev.wsgroup.main.models.dtos.OrderProduct;
import dev.wsgroup.main.models.dtos.Payment;
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

public class InfoActivity extends AppCompatActivity {

    private ImageView imgBackFromCheckout, imgCheckoutMessage, imgCheckoutHome;
    private ConstraintLayout layoutDeliveryInfo, layoutPayment, layoutAddress;
    private EditText editPhoneNumber;
    private TextView txtDeliveryAddress, txtPaymentDescription, txtTotalPrice,
            txtDiscountPrice, txtFinalPrice, txtDeliveryPrice, lblDiscountPrice;
    private RecyclerView recViewOrderProductPrice;
    private Button btnConfirmOrder;
    private Spinner spinnerPayment;

    private DialogBoxLoading dialogBoxLoading;
    private DialogBoxConfirm dialogBoxConfirm;
    private List<OrderProduct> orderProductList, tempList;
    private int orderCount;
    private String token, phone;
    private double totalPrice, discountPrice, finalPrice, deliveryPrice;
    private SharedPreferences sharedPreferences;
    private Order order;
    private CustomerDiscount customerDiscount;
    private Address currentAddress;
    private Payment currentPayment;

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
        layoutDeliveryInfo = findViewById(R.id.layoutDeliveryInfo);
        layoutPayment = findViewById(R.id.layoutPayment);
        layoutAddress = findViewById(R.id.layoutAddress);
        editPhoneNumber = findViewById(R.id.editPhoneNumber);
        txtDeliveryAddress = findViewById(R.id.txtDeliveryAddress);
        txtPaymentDescription = findViewById(R.id.txtPaymentDescription);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);
        txtDiscountPrice = findViewById(R.id.txtDiscountPrice);
        txtFinalPrice = findViewById(R.id.txtFinalPrice);
        txtDeliveryPrice = findViewById(R.id.txtDeliveryPrice);
        lblDiscountPrice = findViewById(R.id.lblDiscountPrice);
        recViewOrderProductPrice = findViewById(R.id.recViewOrderProductPrice);
        btnConfirmOrder = findViewById(R.id.btnConfirmOrder);
        spinnerPayment = findViewById(R.id.spinnerPayment);

        sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        phone = sharedPreferences.getString("PHONE", "");
        token = sharedPreferences.getString("TOKEN", "");

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
        for (Order order : orderList) {
            tempList = order.getOrderProductList();
            for (OrderProduct oProduct : tempList) {
                orderProductList.add(oProduct);
            }
        }
        if (requestCode == IntegerUtils.REQUEST_ORDER_CAMPAIGN) {
            campaign = orderList.get(0).getCampaign();
        }
        if (orderProductList.size() > 0) {
            totalPrice = 0; discountPrice = 0; finalPrice = 0; deliveryPrice = 0;
            for (OrderProduct orderProduct : orderProductList) {
                totalPrice += orderProduct.getProduct().getRetailPrice() * orderProduct.getQuantity();
            }
            txtTotalPrice.setText(MethodUtils.formatPriceString(totalPrice));
            txtDeliveryPrice.setText(MethodUtils.formatPriceString(deliveryPrice));

            txtTotalPrice.setText(MethodUtils.formatPriceString(totalPrice));
            if (campaign == null) {
                lblDiscountPrice.setText("Discount");
                for (int i = 0; i < orderList.size(); i++) {
                    customerDiscount = orderList.get(i).getCustomerDiscount();
                    if (customerDiscount != null) {
                        discountPrice += customerDiscount.getDiscount().getDiscountPrice();
                    }
                }
            } else {
                lblDiscountPrice.setText("Campaign Saving");
                discountPrice = campaign.getSavingPrice() * orderProductList.get(0).getQuantity();
            }
            txtDiscountPrice.setText(MethodUtils.formatPriceString(discountPrice));
            finalPrice = totalPrice - discountPrice - deliveryPrice;
            txtFinalPrice.setText(MethodUtils.formatPriceString(finalPrice));

            RecViewOrderingProductPriceAdapter adapter = new RecViewOrderingProductPriceAdapter(getApplicationContext());
            adapter.setOrderProductList(orderProductList);
            recViewOrderProductPrice.setAdapter(adapter);
            recViewOrderProductPrice.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                    LinearLayoutManager.VERTICAL, false));
        }

        btnConfirmOrder.setEnabled(false);
        btnConfirmOrder.getBackground().setTint(getResources().getColor(R.color.gray_light));
        editPhoneNumber.setText(MethodUtils
                .formatPhoneNumberWithCountryCode(MethodUtils.formatPhoneNumber(phone)));
        editPhoneNumber.setEnabled(false);

        imgBackFromCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
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
                dialogBoxConfirm = new DialogBoxConfirm(InfoActivity.this, StringUtils.MES_CONFIRM_ORDER) {
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
            txtDeliveryAddress.setText(currentAddress.getAddressString());
            txtDeliveryAddress.setTextColor(getResources().getColor(R.color.gray_dark));
            btnConfirmOrder.setEnabled(true);
            btnConfirmOrder.getBackground().setTint(getResources().getColor(R.color.blue_main));
        } else {
            txtDeliveryAddress.setText("No Delivery Address");
            txtDeliveryAddress.setTextColor(getResources().getColor(R.color.gray));
            btnConfirmOrder.setEnabled(false);
            btnConfirmOrder.getBackground().setTint(getResources().getColor(R.color.gray_light));
        }
    }
    private void setupSpinner() {

        ArrayAdapter adapter =
                new ArrayAdapter<String>(InfoActivity.this, R.layout.spinner_selected_item, paymentData);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        spinnerPayment.setAdapter(adapter);
        spinnerPayment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int positionInt, long positionLong) {
                paymentMethodSelected(positionInt);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerPayment.setSelection(0);
    }

    private void paymentMethodSelected(int position) {
        if (position == 0) {
            btnConfirmOrder.setText("PLACE ORDER");
        } else {
            btnConfirmOrder.setText("CHECKOUT WITH VNPAY");
        }
    }

    private void processOrder() {
            dialogBoxLoading = new DialogBoxLoading(InfoActivity.this);
            dialogBoxLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogBoxLoading.show();
            if (requestCode == IntegerUtils.REQUEST_ORDER_CAMPAIGN) {
                order = orderList.get(0);
                order.setAddress(currentAddress);

//            set Payment
                currentPayment = new Payment();
                currentPayment.setId("");
                order.setPayment(currentPayment);

                APIOrderCaller.addOrder(token, order, getApplication(), new APIListener() {
                    @Override
                    public void onOrderSuccessful(Order result) {
                        super.onOrderSuccessful(result);
                        onSuccessfulOrder();
                    }
                });
            } else {
                orderCount = orderList.size();
                for (Order currentOrder : orderList) {
                    order = currentOrder;
                    order.setAddress(currentAddress);

//            set Payment
                    currentPayment = new Payment();
                    currentPayment.setId("");
                    order.setPayment(currentPayment);

                    APIOrderCaller.addOrder(token, order, getApplication(), new APIListener() {
                        @Override
                        public void onOrderSuccessful(Order result) {
                            super.onOrderSuccessful(result);
                            order = result;
                            onSuccessfulOrder();
                        }
                    });
                }
            }
    }

    private void onSuccessfulOrder() {
        if (requestCode == IntegerUtils.REQUEST_ORDER_RETAIL) {
            System.out.println("checking");
            orderCount--;
            if (orderCount == 0) {
                processPayment();
            }
        } else {
            processPayment();
        }
    }

    private void processPayment() {
        if (spinnerPayment.getSelectedItemPosition() == 1) {
            APIPaymentCaller.getPaymentURL(order, getApplication(), new APIListener() {
                @Override
                public void onGettingPaymentURL(String url) {
                    super.onGettingPaymentURL(url);
                    DialogBoxPayment dialogBox
                            = new DialogBoxPayment(InfoActivity.this, url, order) {
                        @Override
                        public void onCompletedPayment() {
                            super.onCompletedPayment();
                            displaySuccessfulMessage();
                        }
                    };
                    dialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialogBox.show();
                }
            });
        } else {
            displaySuccessfulMessage();
        }
    }

    private void displaySuccessfulMessage() {
        if (dialogBoxLoading.isShowing()) {
            dialogBoxLoading.dismiss();
        }
        DialogBoxAlert dialogBoxAlert = new DialogBoxAlert(InfoActivity.this,
                IntegerUtils.CONFIRM_ACTION_CODE_SUCCESS, StringUtils.MES_SUCCESSFUL_ORDER, "") {
            @Override
            public void onClickAction() {
                super.onClickAction();
                setResult(RESULT_OK);
                finish();
            }
        };
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