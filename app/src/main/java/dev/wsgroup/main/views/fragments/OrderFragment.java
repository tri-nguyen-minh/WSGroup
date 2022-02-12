package dev.wsgroup.main.views.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APIOrderCaller;
import dev.wsgroup.main.models.dtos.Order;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewOrderListAdapter;

public class OrderFragment extends Fragment {

    private RelativeLayout layoutLoading, layoutNoOrder;
    private LinearLayout layoutOrderView;
    private RecyclerView recViewOrderView;

    private SharedPreferences sharedPreferences;
    private String orderStatus, token;
    private RecViewOrderListAdapter adapter;

    public OrderFragment(String orderStatus) {
        if (orderStatus.toLowerCase().equals("ordered")) {
            this.orderStatus = "created";
        } else if (orderStatus.toLowerCase().equals("waiting")) {
            this.orderStatus = "advanced";
        } else {
            this.orderStatus = orderStatus.toLowerCase();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sharedPreferences = getActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("TOKEN", "");
        return inflater.inflate(R.layout.fragment_order, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layoutLoading = view.findViewById(R.id.layoutLoading);
        layoutNoOrder = view.findViewById(R.id.layoutNoOrder);
        layoutOrderView = view.findViewById(R.id.layoutOrderView);
        recViewOrderView = view.findViewById(R.id.recViewOrderView);

        layoutLoading.setVisibility(View.VISIBLE);
        layoutNoOrder.setVisibility(View.INVISIBLE);
        layoutOrderView.setVisibility(View.INVISIBLE);

        APIOrderCaller.getAllOrder(token, orderStatus,null, getActivity().getApplication(), new APIListener() {
            @Override
            public void onOrderFound(List<Order> orderList) {
                super.onOrderFound(orderList);
                System.out.println("size " + orderList.size());
                if (orderList.size() > 0) {
                    layoutLoading.setVisibility(View.INVISIBLE);
                    layoutNoOrder.setVisibility(View.INVISIBLE);
                    layoutOrderView.setVisibility(View.VISIBLE);
                    setupOrderList(orderList);
                } else {
                    layoutLoading.setVisibility(View.INVISIBLE);
                    layoutNoOrder.setVisibility(View.VISIBLE);
                    layoutOrderView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailedAPICall(int code) {
                super.onFailedAPICall(code);
                layoutLoading.setVisibility(View.INVISIBLE);
                layoutNoOrder.setVisibility(View.VISIBLE);
                layoutOrderView.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void setupOrderList(List<Order> orderList) {
        adapter = new RecViewOrderListAdapter(getContext(), getActivity());
        adapter.setOrderList(orderList);
        recViewOrderView.setAdapter(adapter);
        recViewOrderView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }
}