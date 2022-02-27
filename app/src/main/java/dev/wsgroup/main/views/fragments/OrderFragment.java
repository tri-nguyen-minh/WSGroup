package dev.wsgroup.main.views.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APIOrderCaller;
import dev.wsgroup.main.models.dtos.Order;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewOrderListAdapter;
import dev.wsgroup.main.models.utils.MethodUtils;

public class OrderFragment extends Fragment {

    private RelativeLayout layoutLoading, layoutNoOrder;
    private LinearLayout layoutOrderView;
    private RecyclerView recViewOrderView;
    private Spinner spinnerSorting;

    private SharedPreferences sharedPreferences;
    private String orderStatus, token;
    private RecViewOrderListAdapter adapter;
    private List<Order> currentOrderList;

    private final String[] sortData = {"Last Ordered", "Last Updated"};

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
        spinnerSorting = view.findViewById(R.id.spinnerSorting);

        layoutLoading.setVisibility(View.VISIBLE);
        layoutNoOrder.setVisibility(View.INVISIBLE);
        layoutOrderView.setVisibility(View.INVISIBLE);
        
        setupSpinner();

        APIOrderCaller.getOrderByStatus(token, orderStatus,null, getActivity().getApplication(), new APIListener() {
            @Override
            public void onOrderFound(List<Order> orderList) {
                super.onOrderFound(orderList);
                if (orderList.size() > 0) {
                    currentOrderList = orderList;
                    layoutLoading.setVisibility(View.INVISIBLE);
                    layoutNoOrder.setVisibility(View.INVISIBLE);
                    layoutOrderView.setVisibility(View.VISIBLE);
                    sortByCreate(currentOrderList, false);
                    setupOrderList(currentOrderList);
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

    private void setupSpinner() {

        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_selected_item, sortData);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        spinnerSorting.setAdapter(adapter);
//        spinnerSorting.setSelection(0);
        spinnerSorting.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int positionInt, long positionLong) {
                layoutLoading.setVisibility(View.VISIBLE);
                recViewOrderView.setVisibility(View.GONE);
                switch (positionInt) {
                    case 1: {
//                        sortByUpdate(currentOrderList, false);
                        if (currentOrderList != null) {
                            sortByCreate(currentOrderList, true);
                            setupOrderList(currentOrderList);
                        }
                        break;
                    }
                    default: {
                        if (currentOrderList != null) {
                            sortByCreate(currentOrderList, false);
                            setupOrderList(currentOrderList);
                        }
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void sortByCreate(List<Order> currentOrderList, boolean sortAscending) {
        Collections.sort(currentOrderList, new Comparator<Order>() {
            @Override
            public int compare(Order order1, Order order2) {
                Date date1 = null, date2 = null;
                if (order1.getDateCreated() != null && order2.getDateCreated() != null) {
                    try {
                        date1 = MethodUtils.convertStringToDate(order1.getDateCreated());
                        date2 = MethodUtils.convertStringToDate(order2.getDateCreated());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (sortAscending) {
                    return date1.compareTo(date2);
                } else {
                    return date2.compareTo(date1);
                }
            }
        });
    }

    private void sortByUpdate(List<Order> currentOrderList, boolean sortAscending) {
        Collections.sort(currentOrderList, new Comparator<Order>() {
            @Override
            public int compare(Order order1, Order order2) {
                Date date1 = null, date2 = null;
                if (order1.getDateUpdated() != null && order2.getDateUpdated() != null) {
                    try {
                        date1 = MethodUtils.convertStringToDate(order1.getDateUpdated());
                        date2 = MethodUtils.convertStringToDate(order2.getDateUpdated());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (sortAscending) {
                    return date1.compareTo(date2);
                } else {
                    return date2.compareTo(date1);
                }
            }
        });
    }

    private void setupOrderList(List<Order> orderList) {
        adapter = new RecViewOrderListAdapter(getContext(), getActivity());
        adapter.setOrderList(orderList);
        recViewOrderView.setAdapter(adapter);
        recViewOrderView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recViewOrderView.setVisibility(View.VISIBLE);
    }
}