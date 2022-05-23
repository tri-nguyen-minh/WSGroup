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
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APIOrderCaller;
import dev.wsgroup.main.models.dtos.Order;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewOrderListAdapter;
import dev.wsgroup.main.models.services.FirebaseReferences;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;

public class OrderFragment extends Fragment {

    private RelativeLayout layoutLoading;
    private LinearLayout layoutOrderView, layoutNoOrder;
    private RecyclerView recViewOrderView;
    private Spinner spinnerSorting;
    private TextView lblRetry;

    private SharedPreferences sharedPreferences;
    private String orderStatus, token, accountId;
    private RecViewOrderListAdapter adapter;
    private List<Order> currentOrderList;
    private FirebaseReferences firebaseReferences;
    private boolean notificationLoading;
    private final List<String> sortData;

//    private final String[] sortData = {"Last Ordered", "Last Updated"};

    public OrderFragment(String orderStatus) {
        if (orderStatus.toLowerCase().equals("ordered")) {
            this.orderStatus = "created";
        } else if (orderStatus.toLowerCase().equals("waiting")) {
            this.orderStatus = "advanced";
        } else {
            this.orderStatus = orderStatus.toLowerCase();
        }
        sortData = new ArrayList<>();
        sortData.add("Last Ordered");
        sortData.add("Last Updated");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        lblRetry = view.findViewById(R.id.lblRetry);

        sharedPreferences = getActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("TOKEN", "");
        accountId = sharedPreferences.getString("ACCOUNT_ID", "");
        setupList();
        setRealtimeFirebase();

        lblRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupList();
            }
        });
    }

    private void setupList() {
        setLoadingState();
        if (getActivity() == null) {
            setFailedState();
        } else {
            setupSpinner();
            if (!token.isEmpty()) {
                currentOrderList = new ArrayList<>();
                APIOrderCaller.getOrderByStatus(token, orderStatus,
                        getActivity().getApplication(), new APIListener() {
                    @Override
                    public void onOrderFound(List<Order> orderList) {
                        if (orderList.size() > 0) {
                            currentOrderList = orderList;
                            sortByCreate(false);
                            setupOrderList();
                        } else {
                            setFailedState();
                        }
                    }

                    @Override
                    public void onFailedAPICall(int code) {
                        if (code == IntegerUtils.ERROR_NO_USER) {
                            MethodUtils.displayErrorAccountMessage(getContext(), getActivity());
                        } else {
                            setFailedState();
                        }
                    }
                });
            }
        }
    }

    private void setupSpinner() {
        ArrayAdapter adapter = new ArrayAdapter<>(getActivity(),
                R.layout.spinner_selected_item, sortData);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerSorting.setAdapter(adapter);
        spinnerSorting.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int positionInt, long positionLong) {
                setLoadingState();
                switch (positionInt) {
                    case 1: {
                        if (currentOrderList != null) {
                            sortByUpdate(false);
                            setupOrderList();
                        }
                        break;
                    }
                    default: {
                        if (currentOrderList != null) {
                            sortByCreate(false);
                            setupOrderList();
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

    private void sortByCreate(boolean sortAscending) {
        Collections.sort(currentOrderList, new Comparator<Order>() {
            @Override
            public int compare(Order order1, Order order2) {
                Date date1 = null, date2 = null;
                if (order1.getDateCreated() != null && order2.getDateCreated() != null) {
                    try {
                        date1 = MethodUtils.convertToDate(order1.getDateCreated());
                        date2 = MethodUtils.convertToDate(order2.getDateCreated());
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

    private void sortByUpdate(boolean sortAscending) {
        Collections.sort(currentOrderList, new Comparator<Order>() {
            @Override
            public int compare(Order order1, Order order2) {
                Date date1 = null, date2 = null;
                if (order1.getDateUpdated() != null && order2.getDateUpdated() != null) {
                    try {
                        date1 = MethodUtils.convertToDate(order1.getDateUpdated());
                        date2 = MethodUtils.convertToDate(order2.getDateUpdated());
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

    private void setupOrderList() {
        adapter = new RecViewOrderListAdapter(getContext(), getActivity());
        adapter.setOrderList(currentOrderList);
        recViewOrderView.setAdapter(adapter);
        recViewOrderView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        setReadyState();
    }

    private void setRealtimeFirebase() {
        firebaseReferences = new FirebaseReferences();
        firebaseReferences.getUserNotifications(accountId)
                          .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!notificationLoading) {
                    setupList();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) { }
        });
    }

    private void setLoadingState() {
        notificationLoading = true;
        layoutLoading.setVisibility(View.VISIBLE);
        layoutNoOrder.setVisibility(View.GONE);
        layoutOrderView.setVisibility(View.GONE);
    }

    private void setFailedState() {
        notificationLoading = false;
        layoutLoading.setVisibility(View.GONE);
        layoutNoOrder.setVisibility(View.VISIBLE);
        layoutOrderView.setVisibility(View.GONE);
    }

    private void setReadyState() {
        notificationLoading = false;
        layoutLoading.setVisibility(View.GONE);
        layoutNoOrder.setVisibility(View.GONE);
        layoutOrderView.setVisibility(View.VISIBLE);
    }
}