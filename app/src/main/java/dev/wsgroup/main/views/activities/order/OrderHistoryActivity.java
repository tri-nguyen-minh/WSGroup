package dev.wsgroup.main.views.activities.order;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APIOrderCaller;
import dev.wsgroup.main.models.dtos.Order;
import dev.wsgroup.main.models.dtos.OrderHistory;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewOrderHistoryAdapter;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.views.activities.MainActivity;

public class OrderHistoryActivity extends AppCompatActivity {

    private ImageView imgBack, imgHome;
    private TextView txtOrderCode, txtStatus, lblRetry;
    private LinearLayout layoutOrder, layoutFailed;
    private RelativeLayout layoutLoading;
    private RecyclerView recViewOrderHistory;

    private SharedPreferences sharedPreferences;
    private String token, orderCode;
    private Order order;
    private RecViewOrderHistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);
        this.getSupportActionBar().hide();

        imgBack = findViewById(R.id.imgBack);
        imgHome = findViewById(R.id.imgHome);
        txtOrderCode = findViewById(R.id.txtOrderCode);
        txtStatus = findViewById(R.id.txtStatus);
        lblRetry = findViewById(R.id.lblRetry);
        layoutOrder = findViewById(R.id.layoutOrder);
        layoutFailed = findViewById(R.id.layoutFailed);
        layoutLoading = findViewById(R.id.layoutLoading);
        recViewOrderHistory = findViewById(R.id.recViewOrderHistory);

        setupOrder();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });
        imgHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
        lblRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupOrder();
            }
        });
    }

    private void setupOrder() {
        setLoadingState();
        sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("TOKEN", "");
        orderCode = getIntent().getStringExtra("ORDER_CODE");
        APIOrderCaller.getOrderByOrderCode(orderCode, getApplication(), new APIListener() {
            @Override
            public void onOrderFound(List<Order> orderList) {
                if (orderList.size() > 0) {
                    order = orderList.get(0);
                    txtOrderCode.setText(order.getCode());
                    txtStatus.setText(MethodUtils.displayStatus(order.getStatus()));
                    APIOrderCaller.getOrderHistoryByOrderCode(order.getCode(),
                            getApplication(), new APIListener() {
                        @Override
                        public void onOrderHistoryFound(List<OrderHistory> historyList) {
                            if (historyList.size() > 0) {

                                setupList(historyList);
                                setListViewState();
                            } else {
                                setFailedState();
                            }
                        }

                        @Override
                        public void onFailedAPICall(int code) {
                            setFailedState();
                        }
                    });
                } else {
                    setFailedState();
                }
            }

            @Override
            public void onFailedAPICall(int code) {
                setFailedState();
            }
        });
    }

    private void setupList(List<OrderHistory> historyList) {
        adapter = new RecViewOrderHistoryAdapter(getApplicationContext(),
                OrderHistoryActivity.this);
        adapter.setOrderHistoryList(historyList);
        recViewOrderHistory.setAdapter(adapter);
        recViewOrderHistory.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL, false));
//        recViewOrderHistory.scrollToPosition(historyList.size() - 1);
        recViewOrderHistory.scrollToPosition(0);
    }

    private void setLoadingState() {
        layoutLoading.setVisibility(View.VISIBLE);
        layoutFailed.setVisibility(View.GONE);
        layoutOrder.setVisibility(View.GONE);
    }

    private void setFailedState() {
        layoutLoading.setVisibility(View.GONE);
        layoutFailed.setVisibility(View.VISIBLE);
        layoutOrder.setVisibility(View.GONE);
    }

    private void setListViewState() {
        layoutLoading.setVisibility(View.GONE);
        layoutFailed.setVisibility(View.GONE);
        layoutOrder.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        imgBack.performClick();
    }
}