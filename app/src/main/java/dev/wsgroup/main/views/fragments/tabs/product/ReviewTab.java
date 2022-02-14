package dev.wsgroup.main.views.fragments.tabs.product;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.Order;
import dev.wsgroup.main.models.dtos.Product;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewOrderListAdapter;

public class ReviewTab extends Fragment {

    private Spinner spinnerSorting;

    private SharedPreferences sharedPreferences;
    private Intent intent;
    private Product product;
    private String userId;
    private final String[] sortData = {"Latest", "Highest Rating", "Lowest Rating", "Most Liked"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_review_tab, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinnerSorting = view.findViewById(R.id.spinnerSorting);

        sharedPreferences = getActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("USER_ID", "");
        intent = getActivity().getIntent();
        product = (Product)intent.getSerializableExtra("PRODUCT");

        setupSpinner();

    }

    private void setupSpinner() {

        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_selected_item, sortData);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        spinnerSorting.setAdapter(adapter);
        spinnerSorting.setSelection(0);
        spinnerSorting.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int positionInt, long positionLong) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}