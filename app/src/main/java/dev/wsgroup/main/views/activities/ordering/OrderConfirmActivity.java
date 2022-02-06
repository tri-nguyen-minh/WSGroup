package dev.wsgroup.main.views.activities.ordering;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.Campaign;
import dev.wsgroup.main.models.dtos.CustomerDiscount;
import dev.wsgroup.main.models.dtos.Discount;
import dev.wsgroup.main.models.dtos.Order;
import dev.wsgroup.main.models.dtos.Supplier;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewOrderProductListAdapter;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.StringUtils;
import dev.wsgroup.main.views.activities.MainActivity;
import dev.wsgroup.main.views.boxes.DialogBoxConfirm;

public class OrderConfirmActivity extends AppCompatActivity {

    private ImageView imgBackFromCheckout, imgCheckoutHome;
    private TextView txtSupplierName, txtSupplierAddress, lblOrderingProduct,
            txtPrice, txtCampaignOrderCount, txtCampaignOrderQuantityCount,
            txtCampaignQuantityCount, txtDiscountPrice, lblDiscountCode;
    private ProgressBar progressBarQuantityCount;
    private RecyclerView recViewCheckoutOrderProduct;
    private Button btnConfirmOrder;
    private LinearLayout layoutCampaign, layoutScreen;
    private EditText editDiscount;
    private ConstraintLayout layoutDiscountSelect;
    private LinearLayout layoutDiscountPrice;
    private ProgressBar progressBarLoading;

    private Supplier supplier;
    private Order order;
    private Campaign campaign;
    private RecViewOrderProductListAdapter adapter;
    private String discountCode;
    private int process;
    private double discountPrice;
    private CustomerDiscount customerDiscount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm);
        this.getSupportActionBar().hide();

        imgBackFromCheckout = findViewById(R.id.imgBackFromCheckout);
        imgCheckoutHome = findViewById(R.id.imgCheckoutHome);
        txtSupplierName = findViewById(R.id.txtSupplierName);
        txtSupplierAddress = findViewById(R.id.txtSupplierAddress);
        lblOrderingProduct = findViewById(R.id.lblOrderingProduct);
        txtPrice = findViewById(R.id.txtPrice);
        txtCampaignOrderCount = findViewById(R.id.txtCampaignOrderCount);
        txtCampaignOrderQuantityCount = findViewById(R.id.txtCampaignOrderQuantityCount);
        txtCampaignQuantityCount = findViewById(R.id.txtCampaignQuantityCount);
        txtDiscountPrice = findViewById(R.id.txtDiscountPrice);
//        lblDiscountCode = findViewById(R.id.lblDiscountCode);
        progressBarQuantityCount = findViewById(R.id.progressBarQuantityCount);
        recViewCheckoutOrderProduct = findViewById(R.id.recViewCheckoutOrderProduct);
        btnConfirmOrder = findViewById(R.id.btnConfirmOrder);
        layoutCampaign = findViewById(R.id.layoutCampaign);
        layoutScreen = findViewById(R.id.layoutScreen);
        editDiscount = findViewById(R.id.editDiscount);
        layoutDiscountSelect = findViewById(R.id.layoutDiscountSelect);
        layoutDiscountPrice = findViewById(R.id.layoutDiscountPrice);
        progressBarLoading = findViewById(R.id.progressBarLoading);

        discountPrice = 0;
        txtDiscountPrice.setText(setDiscountPrice());
        order = (Order) getIntent().getSerializableExtra("ORDER");
        process = getIntent().getIntExtra("PROCESS", 0);
        supplier = order.getSupplier();

        if(supplier != null) {
            txtSupplierName.setText(supplier.getName());
            txtSupplierAddress.setText(supplier.getAddress());
            if(order.getOrderProductList().size() > 1) {
                lblOrderingProduct.setText("Ordering Products");
            } else {
                lblOrderingProduct.setText("Ordering Product");
            }
            if (order.getCampaignId().isEmpty()) {
                layoutCampaign.setVisibility(View.GONE);
            } else {
                campaign = order.getOrderProductList().get(0).getProduct().getCampaign();;
                layoutCampaign.setVisibility(View.VISIBLE);
                txtPrice.setText(MethodUtils.convertPriceString(campaign.getPrice()));
                txtCampaignOrderCount.setText(campaign.getOrderCount() + "");
                txtCampaignOrderQuantityCount.setText(campaign.getQuantityCount() + "");
                txtCampaignQuantityCount.setText(campaign.getQuantity() + "");
                progressBarQuantityCount.setMax(campaign.getQuantity());
                progressBarQuantityCount.setProgress(campaign.getQuantityCount());
            }
            setupRecViewOrderList();
        }

//        discount
        List<CustomerDiscount> customerDiscountList = getDiscountList();
        discountCode = "";

        progressBarLoading.setVisibility(View.INVISIBLE);
        layoutDiscountPrice.setVisibility(View.INVISIBLE);



        editDiscount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                    String currentDiscountCode = editDiscount.getText().toString();
                    if (!discountCode.equals(currentDiscountCode)) {
                        if (!currentDiscountCode.isEmpty()) {
                            progressBarLoading.setVisibility(View.VISIBLE);
                            discountCode = currentDiscountCode;
                            customerDiscount = findDiscountByCode(discountCode, customerDiscountList);
                            if (customerDiscount != null) {
                                discountPrice = customerDiscount.getDiscount().getDiscountPrice();
                                txtDiscountPrice.setText(MethodUtils.convertPriceString(discountPrice));
                                progressBarLoading.setVisibility(View.INVISIBLE);
                                layoutDiscountPrice.setVisibility(View.VISIBLE);
                            } else {
                                progressBarLoading.setVisibility(View.INVISIBLE);
                                layoutDiscountPrice.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            progressBarLoading.setVisibility(View.INVISIBLE);
                            layoutDiscountPrice.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }
        });
        layoutScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDiscount.clearFocus();
            }
        });

        layoutDiscountSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

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
                DialogBoxConfirm dialogBoxConfirm = new DialogBoxConfirm(OrderConfirmActivity.this, StringUtils.MES_CONFIRM_CHECKOUT) {
                    @Override
                    public void onYesClicked() {
                        super.onYesClicked();
                        if (customerDiscount != null) {
                            order.setCustomerDiscount(customerDiscount);
                        }
                        Intent orderInfoIntent = new Intent(getApplicationContext(), OrderInfoActivity.class);
                        orderInfoIntent.putExtra("ORDER", order);
                        orderInfoIntent.putExtra("PROCESS", process);
                        startActivityForResult(orderInfoIntent, IntegerUtils.REQUEST_COMMON);
                    }
                };
                dialogBoxConfirm.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBoxConfirm.show();
            }
        });
    }

    private CustomerDiscount findDiscountByCode(String discountCode, List<CustomerDiscount> list) {
        for (CustomerDiscount discount : list) {
            if (discount.getDiscount().getCode().equals(discountCode)) {
                return discount;
            }
        }
        return null;
    }

    private String setDiscountPrice() {
        String price = "";
        if (discountPrice != 0) {
            price = MethodUtils.convertPriceString(discountPrice);
        }
        return price;
    }

    private void setupRecViewOrderList() {
        adapter = new RecViewOrderProductListAdapter(getApplicationContext(), OrderConfirmActivity.this);
        adapter.setOrder(order);
        recViewCheckoutOrderProduct.setAdapter(adapter);
        recViewCheckoutOrderProduct.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                                                        LinearLayoutManager.VERTICAL, false));
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private List<CustomerDiscount> getDiscountList() {
        List<CustomerDiscount> list = new ArrayList<>();
        CustomerDiscount customerDiscount = new CustomerDiscount();
        Discount discount = new Discount();
        discount.setCode("123456");
        discount.setDiscountPrice(300);
        customerDiscount.setDiscount(discount);
        customerDiscount.setId("1");
        list.add(customerDiscount);
        customerDiscount = new CustomerDiscount();
        discount = new Discount();
        discount.setCode("223456");
        discount.setDiscountPrice(100);
        customerDiscount.setDiscount(discount);
        customerDiscount.setId("2");
        list.add(customerDiscount);
        return list;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}