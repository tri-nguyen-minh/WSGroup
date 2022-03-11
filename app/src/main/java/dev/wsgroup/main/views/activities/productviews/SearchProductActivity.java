package dev.wsgroup.main.views.activities.productviews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APIProductCaller;
import dev.wsgroup.main.models.dtos.Product;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewProductSearchAdapter;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.views.activities.MainActivity;

public class SearchProductActivity extends AppCompatActivity {

    private EditText editSearchProduct;
    private TextView txtProductListCount, lblProductListCount;
    private ImageView imgBackFromSearch, imgSearchHome;
    private RelativeLayout layoutLoading, layoutNoProductFound;
    private ConstraintLayout layoutParent;
    private LinearLayout layoutList;
    private RecyclerView recViewSearchProductList;

    private String searchString;
    private boolean orderCountCheck, ratingCheck;
    private RecViewProductSearchAdapter adapter;
    private int identifier;
    private APIListener listener;
    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_product);
        this.getSupportActionBar().hide();

        editSearchProduct = findViewById(R.id.editSearchProduct);
        txtProductListCount = findViewById(R.id.txtProductListCount);
        lblProductListCount = findViewById(R.id.lblProductListCount);
        imgBackFromSearch = findViewById(R.id.imgBackFromSearch);
        imgSearchHome = findViewById(R.id.imgSearchHome);
        layoutLoading = findViewById(R.id.layoutLoading);
        layoutNoProductFound = findViewById(R.id.layoutNoProductFound);
        layoutParent = findViewById(R.id.layoutParent);
        layoutList = findViewById(R.id.layoutList);
        recViewSearchProductList = findViewById(R.id.recViewSearchProductList);

        setNoProductState();
        identifier = getIntent().getIntExtra("IDENTIFIER", IntegerUtils.IDENTIFIER_SEARCH_BAR);
        switch (identifier) {
            case IntegerUtils.IDENTIFIER_SEARCH_BAR: {
                editSearchProduct.setVisibility(View.VISIBLE);
                editSearchProduct.requestFocus();
                break;
            }
            case IntegerUtils.IDENTIFIER_SEARCH_SUPPLIER: {
                searchString = getIntent().getStringExtra("SEARCH_STRING");
                editSearchProduct.setText(searchString);
                runSearch();
                break;

            }
            case IntegerUtils.IDENTIFIER_SEARCH_CATEGORY: {
                searchString = getIntent().getStringExtra("SEARCH_STRING");
                runSearchCategory();
                break;
            }
        }
        listener = new APIListener() {
            @Override
            public void onProductOrderCountFound(Map<String, Integer> countList) {
                for (Product product : productList) {
                    if (countList.get(product.getProductId()) != null) {
                        product.setOrderCount(countList.get(product.getProductId()));
                    }else {
                        product.setOrderCount(0);
                    }
                }
                orderCountCheck = true;
                setupListView(productList);
            }

            @Override
            public void onRatingListCount(Map<String, Double> ratingList) {
                for (Product product : productList) {
                    if (ratingList.get(product.getProductId()) != null) {
                        product.setRating(ratingList.get(product.getProductId()));
                    } else {
                        product.setRating(0);
                    }
                }
                ratingCheck = true;
                setupListView(productList);
            }

            @Override
            public void onFailedAPICall(int code) {setNoProductState();}
        };

        imgBackFromSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Activity.RESULT_OK);
                finish();
            }
        });

        imgSearchHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        layoutParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editSearchProduct.hasFocus()) {
                    editSearchProduct.clearFocus();
                }
            }
        });
        editSearchProduct.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(view);
                    searchString = editSearchProduct.getText().toString();
                    if (searchString.isEmpty()) {
                        setNoProductState();
                    } else {
                        runSearch();
                    }
                }
            }
        });
    }

    private void runSearchCategory() {
        orderCountCheck = false; ratingCheck = false;
        setLoadingState();

//        search by category

//        APIProductCaller.searchProductByNameOrSupplier(searchString, null,
//                getApplication(), new APIListener() {
//            @Override
//            public void onProductListFound(List<Product> list) {
//                if (list.size() == 0) {
//                    setNoProductState();
//                } else {
//                    productList = list;
//                    APIProductCaller.getOrderCountByProductList(productList,
//                            getApplication(), listener);
//                    APIProductCaller.getRatingByProductIdList(productList,
//                            getApplication(), listener);
//                }
//            }
//            @Override
//            public void onFailedAPICall(int code) {
//                setNoProductState();
//            }
//        });
    }

    private void runSearch() {
        orderCountCheck = false; ratingCheck = false;
        setLoadingState();
        APIProductCaller.searchProductByNameOrSupplier(searchString, null,
                getApplication(), new APIListener() {
            @Override
            public void onProductListFound(List<Product> list) {
                if (list.size() == 0) {
                    setNoProductState();
                } else {
                    productList = list;
                    APIProductCaller.getOrderCountByProductList(productList,
                            getApplication(), listener);
                    APIProductCaller.getRatingByProductIdList(productList,
                            getApplication(), listener);
                }
            }
            @Override
            public void onFailedAPICall(int code) {
                setNoProductState();
            }
        });
    }

    private void setupListView(List<Product> productList) {
        if (orderCountCheck && ratingCheck) {
            txtProductListCount.setText(productList.size() + "");
            lblProductListCount.setText(productList.size() > 1 ? "Products Found" : "Product Found");
            adapter = new RecViewProductSearchAdapter(getApplicationContext(),
                    SearchProductActivity.this);
            adapter.setProductsList(productList);
            recViewSearchProductList.setAdapter(adapter);
            recViewSearchProductList.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                    RecyclerView.VERTICAL, false));
            setProductFoundState();
        }
    }

    private void setLoadingState() {
        layoutLoading.setVisibility(View.VISIBLE);
        layoutNoProductFound.setVisibility(View.INVISIBLE);
        layoutList.setVisibility(View.INVISIBLE);
    }

    private void setNoProductState() {
        layoutLoading.setVisibility(View.INVISIBLE);
        layoutNoProductFound.setVisibility(View.VISIBLE);
        layoutList.setVisibility(View.INVISIBLE);
    }

    private void setProductFoundState() {
        layoutLoading.setVisibility(View.INVISIBLE);
        layoutNoProductFound.setVisibility(View.INVISIBLE);
        layoutList.setVisibility(View.VISIBLE);
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager
                = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}