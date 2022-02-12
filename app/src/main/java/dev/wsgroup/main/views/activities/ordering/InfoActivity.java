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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APIAddressCaller;
import dev.wsgroup.main.models.apis.callers.APICartCaller;
import dev.wsgroup.main.models.apis.callers.APIOrderCaller;
import dev.wsgroup.main.models.dtos.Address;
import dev.wsgroup.main.models.dtos.CustomerDiscount;
import dev.wsgroup.main.models.dtos.Order;
import dev.wsgroup.main.models.dtos.OrderProduct;
import dev.wsgroup.main.models.dtos.Payment;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewOrderProductPriceAdapter;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.StringUtils;
import dev.wsgroup.main.views.activities.MainActivity;
import dev.wsgroup.main.views.activities.account.DeliveryAddressSelectActivity;
import dev.wsgroup.main.views.dialogbox.DialogBoxAlert;
import dev.wsgroup.main.views.dialogbox.DialogBoxConfirm;
import dev.wsgroup.main.views.dialogbox.DialogBoxLoading;

public class InfoActivity extends AppCompatActivity {

    private ImageView imgBackFromCheckout, imgCheckoutMessage, imgCheckoutHome;
    private ConstraintLayout layoutDeliveryInfo, layoutPayment, layoutAddress;
    private EditText editPhoneNumber;
    private TextView txtDeliveryAddress, txtPaymentDescription,
                txtTotalPrice, txtDiscountPrice, txtFinalPrice, txtDeliveryPrice;
    private RecyclerView recViewOrderProductPrice;
    private Button btnConfirmOrder;

    private DialogBoxLoading dialogBoxLoading;
    private DialogBoxConfirm dialogBoxConfirm;
    private List<OrderProduct> orderProductList;
    private int orderCount, process;
    private String token, phone;
    private double totalPrice, discountPrice, finalPrice, deliveryPrice;
    private SharedPreferences sharedPreferences;
    private Order order;
    private CustomerDiscount customerDiscount;
    private Address currentAddress;
    private Payment currentPayment;

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
        recViewOrderProductPrice = findViewById(R.id.recViewOrderProductPrice);
        btnConfirmOrder = findViewById(R.id.btnConfirmOrder);

        sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        phone = sharedPreferences.getString("PHONE", "");
        token = sharedPreferences.getString("TOKEN", "");
        order = (Order) getIntent().getSerializableExtra("ORDER");
        process = getIntent().getIntExtra("PROCESS", IntegerUtils.REQUEST_COMMON);

        if (order != null) {
            totalPrice = 0; discountPrice = 0; finalPrice = 0; deliveryPrice = 0;
            orderProductList = order.getOrderProductList();
            for (OrderProduct orderProduct : orderProductList) {
                totalPrice += orderProduct.getPrice();
            }
            totalPrice -= deliveryPrice;
            txtDeliveryPrice.setText(MethodUtils.formatPriceString(deliveryPrice));

            txtTotalPrice.setText(MethodUtils.formatPriceString(totalPrice));
            customerDiscount = order.getCustomerDiscount();
            if (customerDiscount != null) {
                discountPrice = customerDiscount.getDiscount().getDiscountPrice();
            }
            txtDiscountPrice.setText(MethodUtils.formatPriceString(discountPrice));
            finalPrice = totalPrice - discountPrice;
            txtFinalPrice.setText(MethodUtils.formatPriceString(finalPrice));

            RecViewOrderProductPriceAdapter adapter = new RecViewOrderProductPriceAdapter();
            adapter.setOrderProductList(orderProductList);
            recViewOrderProductPrice.setAdapter(adapter);
            recViewOrderProductPrice.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                    LinearLayoutManager.VERTICAL, false));
        }

        APIAddressCaller.getDefaultAddress(token, getApplication(), new APIListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onAddressFound(Address address) {
                super.onAddressFound(address);
                currentAddress = address;
                setupConfirmAddress();
            }
        });

        btnConfirmOrder.setEnabled(false);
        btnConfirmOrder.getBackground().setTint(getResources().getColor(R.color.gray_light));
        editPhoneNumber.setText(MethodUtils
                .formatPhoneNumberWithCountryCode(MethodUtils.formatPhoneNumber(phone)));
        editPhoneNumber.setEnabled(false);

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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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

    private void processOrder() {
            dialogBoxLoading = new DialogBoxLoading(InfoActivity.this);
            dialogBoxLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogBoxLoading.show();
            order.setAddress(currentAddress);

//            set Payment
            currentPayment = new Payment();
            currentPayment.setId("");
            order.setPayment(currentPayment);

            APIOrderCaller.addOrder(token, order, getApplication(), new APIListener() {
                @Override
                public void onOrderSuccessful() {
                    super.onOrderSuccessful();
                    if (process == IntegerUtils.REQUEST_COMMON) {
                        orderCount = 0;
                        for (OrderProduct orderProduct : order.getOrderProductList()) {
                            APICartCaller.deleteCartItem(token, orderProduct.getCartProduct().getId(), getApplication(), new APIListener() {
                                @Override
                                public void onUpdateCartItemSuccessful() {
                                    super.onUpdateCartItemSuccessful();
                                    orderCount++;
                                    if (orderCount == order.getOrderProductList().size()) {
                                        dialogBoxLoading.dismiss();
                                        onSuccessfulOrdering();
                                    }
                                }
                            });
                        }
                    } else {
                        dialogBoxLoading.dismiss();
                        onSuccessfulOrdering();
                    }
                }
            });
    }

    private void onSuccessfulOrdering() {
        DialogBoxAlert dialogBoxAlert = new DialogBoxAlert(InfoActivity.this,
                IntegerUtils.CONFIRM_ACTION_CODE_SUCCESS, StringUtils.MES_SUCCESSFUL_ORDER, "") {
            @Override
            public void onClickAction() {
                super.onClickAction();
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                mainIntent.putExtra("MAIN_TAB_POSITION", 1);
                startActivity(mainIntent);
            }
        };
        dialogBoxAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBoxAlert.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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