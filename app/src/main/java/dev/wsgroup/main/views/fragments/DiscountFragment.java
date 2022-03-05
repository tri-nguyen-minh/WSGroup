package dev.wsgroup.main.views.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import dev.wsgroup.main.R;

public class DiscountFragment extends Fragment {

    private LinearLayout layoutFailedGettingDiscount, layoutNoDiscount;
    private ConstraintLayout layoutLoading;
    private RecyclerView recViewDiscountList;

    private String tabString;

    public DiscountFragment(String tabString) {
        if (tabString.toLowerCase().equals("usable")) {
            this.tabString = "ready";
        } else {
            this.tabString = tabString.toLowerCase();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discount, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layoutFailedGettingDiscount = view.findViewById(R.id.layoutFailedGettingDiscount);
        layoutNoDiscount = view.findViewById(R.id.layoutNoDiscount);
        layoutLoading = view.findViewById(R.id.layoutLoading);
        recViewDiscountList = view.findViewById(R.id.recViewDiscountList);
    }

    private void getDiscountList() {

    }

    private void setLoadingState() {
        layoutLoading.setVisibility(View.VISIBLE);
        layoutFailedGettingDiscount.setVisibility(View.INVISIBLE);
        layoutNoDiscount.setVisibility(View.INVISIBLE);
        recViewDiscountList.setVisibility(View.INVISIBLE);
    }

    private void setListLoadedState() {
        layoutLoading.setVisibility(View.INVISIBLE);
        layoutFailedGettingDiscount.setVisibility(View.INVISIBLE);
        layoutNoDiscount.setVisibility(View.INVISIBLE);
        recViewDiscountList.setVisibility(View.VISIBLE);
    }

    private void setNoListState() {
        layoutLoading.setVisibility(View.INVISIBLE);
        layoutFailedGettingDiscount.setVisibility(View.INVISIBLE);
        layoutNoDiscount.setVisibility(View.VISIBLE);
        recViewDiscountList.setVisibility(View.INVISIBLE);
    }

    private void setFailedState() {
        layoutLoading.setVisibility(View.INVISIBLE);
        layoutFailedGettingDiscount.setVisibility(View.VISIBLE);
        layoutNoDiscount.setVisibility(View.INVISIBLE);
        recViewDiscountList.setVisibility(View.INVISIBLE);
    }
}