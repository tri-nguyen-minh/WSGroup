package dev.wsgroup.main.views.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.Order;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewOrderProductListAdapter;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;

public class OrderActivity extends AppCompatActivity {

    private ImageView imgBackFromOrderDetail, imgOrderDetailMessage, imgOrderDetailHome, imgSupplierAvatar;
    private TextView txtOrderCode, txtPhoneNumber, txtDeliveryAddress, txtPayment, txtStatus,
            txtSupplierName, txtSupplierAddress, txtTotalPrice;
    private RecyclerView recViewOrderProduct;

    private String phone, token;
    private int requestCode;
    private Order order;
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
//        txtNote = findViewById(R.id.txtNote);
        recViewOrderProduct = findViewById(R.id.recViewOrderProduct);

        order = (Order) getIntent().getSerializableExtra("ORDER");
        requestCode = getIntent().getIntExtra("REQUEST_CODE", IntegerUtils.REQUEST_COMMON);
        sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        phone = sharedPreferences.getString("PHONE", "");
        token = sharedPreferences.getString("TOKEN", "");

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
        setupRecViewOrderList();

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

    private void setupRecViewOrderList() {
        adapter = new RecViewOrderProductListAdapter(getApplicationContext(),
                OrderActivity.this, IntegerUtils.REQUEST_NOTE_READ_ONLY);
        adapter.setOrder(order);
        recViewOrderProduct.setAdapter(adapter);
        recViewOrderProduct.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL, false));
    }
}