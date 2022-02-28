package dev.wsgroup.main.views.fragments.tabs.product;

import android.content.Context;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.Order;
import dev.wsgroup.main.models.dtos.Product;
import dev.wsgroup.main.models.dtos.Review;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewOrderListAdapter;
import dev.wsgroup.main.models.utils.MethodUtils;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class ReviewTab extends Fragment {

    private Spinner spinnerSorting;
    private TextView txtProductReviewCount, txtRatingProduct;
    private MaterialRatingBar ratingProduct;
    private RecyclerView recViewReview;
    private RelativeLayout layoutLoading, layoutNoReview;
    private ConstraintLayout layoutMain;
    private LinearLayout layoutReview;

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
        layoutNoReview = view.findViewById(R.id.layoutNoReview);
        layoutMain = view.findViewById(R.id.layoutMain);
        layoutReview = view.findViewById(R.id.layoutReview);

        sharedPreferences = getActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        intent = getActivity().getIntent();
        product = (Product)intent.getSerializableExtra("PRODUCT");

//        setMainLoadingState();
//
//        setupSpinner();
//        setupReviewList();
    }

    private void setupSpinner() {

        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_selected_item, sortData);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        spinnerSorting.setAdapter(adapter);
        spinnerSorting.setSelection(0);
        spinnerSorting.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int positionInt, long positionLong) {
                setListLoadingState();
                Collections.sort(reviewList, new Comparator<Review>() {
                    @Override
                    public int compare(Review review1, Review review2) {
                        int result = 0;
                        switch (positionInt) {
                            case 1: {
                                result = review1.getRating() > review2.getRating() ? -1 :
                                        (review1.getRating() < review2.getRating() ? 1 : 0);
                            }
                            default: {
                                Date date1 = null, date2 = null;
                                if (review1.getCreateDate() != null && review2.getCreateDate() != null) {
                                    try {
                                        date1 = MethodUtils.convertStringToDate(review1.getCreateDate());
                                        date2 = MethodUtils.convertStringToDate(review2.getCreateDate());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    result = date2.compareTo(date1);
                                }
                            }
                        }
                        return result;
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setupReviewList() {
    }

    private void setMainLoadingState() {
        layoutLoading.setVisibility(View.VISIBLE);
        layoutMain.setVisibility(View.INVISIBLE);
        layoutNoReview.setVisibility(View.INVISIBLE);
        layoutReview.setVisibility(View.INVISIBLE);
    }

    private void setListLoadingState() {
        layoutLoading.setVisibility(View.VISIBLE);
        layoutMain.setVisibility(View.VISIBLE);
        layoutNoReview.setVisibility(View.INVISIBLE);
        layoutReview.setVisibility(View.VISIBLE);
        recViewReview.setVisibility(View.INVISIBLE);
    }

    private void setReviewLoadedState() {
        layoutLoading.setVisibility(View.INVISIBLE);
        layoutMain.setVisibility(View.VISIBLE);
        layoutNoReview.setVisibility(View.INVISIBLE);
        layoutReview.setVisibility(View.VISIBLE);
        recViewReview.setVisibility(View.VISIBLE);
    }

    private void setNoReviewState() {
        layoutLoading.setVisibility(View.INVISIBLE);
        layoutMain.setVisibility(View.VISIBLE);
        layoutNoReview.setVisibility(View.VISIBLE);
        layoutReview.setVisibility(View.INVISIBLE);
    }
}