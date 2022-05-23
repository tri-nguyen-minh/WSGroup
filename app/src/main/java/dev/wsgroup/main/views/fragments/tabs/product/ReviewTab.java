package dev.wsgroup.main.views.fragments.tabs.product;

import android.content.Context;
import android.content.Intent;
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

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.Product;
import dev.wsgroup.main.models.dtos.Review;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewReviewAdapter;
import dev.wsgroup.main.models.utils.MethodUtils;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class ReviewTab extends Fragment {

    private TextView txtProductReviewCount, txtRatingProduct;
    private MaterialRatingBar ratingProduct;
    private RecyclerView recViewReview;
    private RelativeLayout layoutLoading, layoutNoReview;
    private LinearLayout layoutReview;

    private Intent intent;
    private Product product;
    private List<Review> reviewList;
    private RecViewReviewAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_review_tab, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtProductReviewCount = view.findViewById(R.id.txtProductReviewCount);
        txtRatingProduct = view.findViewById(R.id.txtRatingProduct);
        ratingProduct = view.findViewById(R.id.ratingProduct);
        recViewReview = view.findViewById(R.id.recViewReview);
        layoutLoading = view.findViewById(R.id.layoutLoading);
        layoutNoReview = view.findViewById(R.id.layoutNoReview);
        layoutReview = view.findViewById(R.id.layoutReview);

        setMainLoadingState();
        intent = getActivity().getIntent();
        product = (Product) intent.getSerializableExtra("PRODUCT");
        reviewList = product.getReviewList();

        if (reviewList != null && reviewList.size() > 0) {
            setReviewCount();
            setupReviewList();
        } else {
            setNoReviewState();
        }
    }

    private void setReviewCount() {
        txtProductReviewCount.setText(MethodUtils.formatOrderOrReviewCount(product.getReviewCount()));
        txtRatingProduct.setText(MethodUtils.formatDecimalString(product.getRating()));
        ratingProduct.setRating((float) product.getRating());
        ratingProduct.setIsIndicator(true);
    }

    private void setupReviewList() {
        recViewReview.setAdapter(null);
        adapter = new RecViewReviewAdapter(getContext());
        adapter.setReviewList(reviewList);
        recViewReview.setAdapter(adapter);
        recViewReview.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        setReviewLoadedState();
    }

    private void setMainLoadingState() {
        layoutLoading.setVisibility(View.VISIBLE);
        layoutNoReview.setVisibility(View.INVISIBLE);
        layoutReview.setVisibility(View.INVISIBLE);
    }

    private void setReviewLoadedState() {
        layoutLoading.setVisibility(View.INVISIBLE);
        layoutNoReview.setVisibility(View.INVISIBLE);
        layoutReview.setVisibility(View.VISIBLE);
        recViewReview.setVisibility(View.VISIBLE);
    }

    private void setNoReviewState() {
        layoutLoading.setVisibility(View.INVISIBLE);
        layoutNoReview.setVisibility(View.VISIBLE);
        layoutReview.setVisibility(View.INVISIBLE);
    }
}