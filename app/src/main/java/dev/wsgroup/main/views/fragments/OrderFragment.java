package dev.wsgroup.main.views.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APIOrderCaller;
import dev.wsgroup.main.models.dtos.Order;

public class OrderFragment extends Fragment {

    private RelativeLayout layoutLoading, layoutNoOrder, layoutOrderView;

    private SharedPreferences sharedPreferences;
    private String orderStatus, token;

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

        layoutLoading.setVisibility(View.VISIBLE);
        layoutNoOrder.setVisibility(View.INVISIBLE);
        layoutOrderView.setVisibility(View.INVISIBLE);

        APIOrderCaller.getAllOrder(token, orderStatus, getActivity().getApplication(), new APIListener() {
            @Override
            public void onOrderFound(List<Order> orderList) {
                super.onOrderFound(orderList);
                if (orderList.size() > 0) {
                    System.out.println("found " + orderStatus);
                    layoutLoading.setVisibility(View.INVISIBLE);
                    layoutNoOrder.setVisibility(View.INVISIBLE);
                    layoutOrderView.setVisibility(View.VISIBLE);
                } else {
                    System.out.println("found " + orderStatus);
                    layoutLoading.setVisibility(View.INVISIBLE);
                    layoutNoOrder.setVisibility(View.VISIBLE);
                    layoutOrderView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailedAPICall(int code) {
                super.onFailedAPICall(code);
                System.out.println("not found " + orderStatus);
                layoutLoading.setVisibility(View.INVISIBLE);
                layoutNoOrder.setVisibility(View.VISIBLE);
                layoutOrderView.setVisibility(View.INVISIBLE);
            }
        });
    }
}