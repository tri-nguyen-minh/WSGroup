package dev.wsgroup.main.views.fragments.tabs.product;

import android.content.Context;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.Order;
import dev.wsgroup.main.models.dtos.Product;
import dev.wsgroup.main.models.dtos.Review;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewOrderListAdapter;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class ReviewTab extends Fragment {

    private Spinner spinnerSorting;
    private TextView txtProductReviewCount, txtRatingProduct;
    private MaterialRatingBar ratingProduct;
    private RecyclerView recViewReview;
    private RelativeLayout layoutLoading;

    private SharedPreferences sharedPreferences;
    private Intent intent;
    private Product product;
    private List<Review> reviewList;
    private final String[] sortData = {"Latest", "Highest Rating"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_review_tab, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinnerSorting = view.findViewById(R.id.spinnerSorting);
        txtProductReviewCount = view.findViewById(R.id.txtProductReviewCount);
        txtRatingProduct = view.findViewById(R.id.txtRatingProduct);
        ratingProduct = view.findViewById(R.id.ratingProduct);
        recViewReview = view.findViewById(R.id.recViewReview);
        layoutLoading = view.findViewById(R.id.layoutLoading);

        sharedPreferences = getActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
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