package dev.wsgroup.main.views.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APIDiscountCaller;
import dev.wsgroup.main.models.dtos.CustomerDiscount;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewDiscountListAdapter;
import dev.wsgroup.main.models.utils.IntegerUtils;

public class DiscountFragment extends Fragment {

    private LinearLayout layoutFailedGettingDiscount, layoutNoDiscount, layoutList;
    private ConstraintLayout layoutLoading;
    private RecyclerView recViewDiscountList;
    private TextView lblRetryGetDiscount;

    private String status, token;
    private SharedPreferences sharedPreferences;
    private RecViewDiscountListAdapter adapter;

    public DiscountFragment(String tabString) {
        if (tabString.toLowerCase().equals("applicable")) {
            status = "ready";
        } else {
            status = tabString.toLowerCase();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sharedPreferences = getActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("TOKEN", "");
        return inflater.inflate(R.layout.fragment_discount, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layoutFailedGettingDiscount = view.findViewById(R.id.layoutFailedGettingDiscount);
        layoutNoDiscount = view.findViewById(R.id.layoutNoDiscount);
        layoutList = view.findViewById(R.id.layoutList);
        layoutLoading = view.findViewById(R.id.layoutLoading);
        recViewDiscountList = view.findViewById(R.id.recViewDiscountList);
        lblRetryGetDiscount = view.findViewById(R.id.lblRetryGetDiscount);

        getDiscountList();

        lblRetryGetDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDiscountList();
            }
        });
    }

    private void getDiscountList() {
        setLoadingState();
        APIDiscountCaller.getCustomerDiscountByStatus(token, status, null,
                getActivity().getApplication(), new APIListener() {
            @Override
            public void onDiscountListFound(List<CustomerDiscount> discountList) {
                super.onDiscountListFound(discountList);
                if (discountList.size() > 0) {
                    setupList(discountList);
                } else {
                    setNoListState();
                }
            }

                    @Override
                    public void onFailedAPICall(int code) {
                        setFailedState();
                    }
                });
    }

    private void setupList(List<CustomerDiscount> discountList) {
        adapter = new RecViewDiscountListAdapter(getContext(),
                                                IntegerUtils.IDENTIFIER_DISCOUNT_VIEW);
        adapter.setDiscountList(discountList);
        recViewDiscountList.setAdapter(adapter);
        recViewDiscountList.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        setListLoadedState();
    }

    private void setLoadingState() {
        layoutLoading.setVisibility(View.VISIBLE);
        layoutFailedGettingDiscount.setVisibility(View.INVISIBLE);
        layoutNoDiscount.setVisibility(View.INVISIBLE);
        layoutList.setVisibility(View.INVISIBLE);
    }

    private void setListLoadedState() {
        layoutLoading.setVisibility(View.INVISIBLE);
        layoutFailedGettingDiscount.setVisibility(View.INVISIBLE);
        layoutNoDiscount.setVisibility(View.INVISIBLE);
        layoutList.setVisibility(View.VISIBLE);
    }

    private void setNoListState() {
        layoutLoading.setVisibility(View.INVISIBLE);
        layoutFailedGettingDiscount.setVisibility(View.INVISIBLE);
        layoutNoDiscount.setVisibility(View.VISIBLE);
        layoutList.setVisibility(View.INVISIBLE);
    }

    private void setFailedState() {
        layoutLoading.setVisibility(View.INVISIBLE);
        layoutFailedGettingDiscount.setVisibility(View.VISIBLE);
        layoutNoDiscount.setVisibility(View.INVISIBLE);
        layoutList.setVisibility(View.INVISIBLE);
    }
}