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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APICategoryCaller;
import dev.wsgroup.main.models.apis.callers.APIProductCaller;
import dev.wsgroup.main.models.dtos.Category;
import dev.wsgroup.main.models.dtos.Product;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewProductSearchAdapter;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.views.activities.MainActivity;

public class SearchProductActivity extends AppCompatActivity {

    private EditText editSearchProduct;
    private TextView txtProductSearchCount, lblProductSearchCount, txtProductCategoryCount,
            lblProductCategoryCount, txtCategory, lblRetryGetProduct;
    private ImageView imgBackFromSearch, imgSearchHome;
    private RelativeLayout layoutLoading, layoutNoProductFound;
    private ConstraintLayout layoutParent;
    private LinearLayout layoutList, layoutSearch, layoutCategory, layoutFailedGettingProduct;
    private RecyclerView recViewSearchProductList;

    private String searchString;
    private boolean orderCountCheck, ratingCheck;
    private RecViewProductSearchAdapter adapter;
    private int identifier;
    private APIListener listener;
    private List<Product> productList;
    private Category category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_product);
        this.getSupportActionBar().hide();

        editSearchProduct = findViewById(R.id.editSearchProduct);
        txtProductSearchCount = findViewById(R.id.txtProductSearchCount);
        lblProductSearchCount = findViewById(R.id.lblProductSearchCount);
        txtProductCategoryCount = findViewById(R.id.txtProductCategoryCount);
        lblProductCategoryCount = findViewById(R.id.lblProductCategoryCount);
        txtCategory = findViewById(R.id.txtCategory);
        lblRetryGetProduct = findViewById(R.id.lblRetryGetProduct);
        imgBackFromSearch = findViewById(R.id.imgBackFromSearch);
        imgSearchHome = findViewById(R.id.imgSearchHome);
        layoutLoading = findViewById(R.id.layoutLoading);
        layoutNoProductFound = findViewById(R.id.layoutNoProductFound);
        layoutParent = findViewById(R.id.layoutParent);
        layoutList = findViewById(R.id.layoutList);
        layoutSearch = findViewById(R.id.layoutSearch);
        layoutCategory = findViewById(R.id.layoutCategory);
        layoutFailedGettingProduct = findViewById(R.id.layoutFailedGettingProduct);
        recViewSearchProductList = findViewById(R.id.recViewSearchProductList);

        setNoProductState();
        identifier = getIntent().getIntExtra("IDENTIFIER", IntegerUtils.IDENTIFIER_SEARCH_BAR);
        switch (identifier) {
            case IntegerUtils.IDENTIFIER_SEARCH_BAR: {
                System.out.println("in search");
                editSearchProduct.setVisibility(View.VISIBLE);
                editSearchProduct.requestFocus();
                layoutSearch.setVisibility(View.VISIBLE);
                layoutCategory.setVisibility(View.GONE);
                break;
            }
            case IntegerUtils.IDENTIFIER_SEARCH_SUPPLIER: {
                System.out.println("in supplier");
                searchString = getIntent().getStringExtra("SEARCH_STRING");
                editSearchProduct.setText(searchString);
                layoutSearch.setVisibility(View.VISIBLE);
                layoutCategory.setVisibility(View.GONE);
                runSearch();
                break;

            }
            case IntegerUtils.IDENTIFIER_SEARCH_CATEGORY: {
                System.out.println("in category");
                searchString = getIntent().getStringExtra("SEARCH_STRING");
                editSearchProduct.setVisibility(View.GONE);
                layoutSearch.setVisibility(View.GONE);
                layoutCategory.setVisibility(View.VISIBLE);
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
        lblRetryGetProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runSearchCategory();
            }
        });
    }

    private void runSearchCategory() {
        orderCountCheck = false; ratingCheck = false;
        setLoadingState();
        APICategoryCaller.getCategoryById(searchString, getApplication(), new APIListener() {
            @Override
            public void onCategoryFound(Category foundCategory) {
                if (foundCategory != null) {
                    category = foundCategory;
                    List<Category> list = new ArrayList<>();
                    list.add(category);
                    APIProductCaller.getProductByCategoryList(list, null,
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
                            setFailedState();
                        }
                    });
                } else {
                    setFailedState();
                }
            }
            @Override
            public void onFailedAPICall(int code) {
                setFailedState();
            }
        });
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
            if (identifier == IntegerUtils.IDENTIFIER_SEARCH_CATEGORY) {
                txtCategory.setText(category.getName());
                txtProductCategoryCount.setText(productList.size() + "");
                lblProductCategoryCount.setText(productList.size() > 1 ?
                                                "Products Found" : "Product Found");
            } else {
                txtProductSearchCount.setText(productList.size() + "");
                lblProductSearchCount.setText(productList.size() > 1 ?
                                                "Products" : "Product");
            }
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
        layoutFailedGettingProduct.setVisibility(View.INVISIBLE);
    }

    private void setNoProductState() {
        layoutLoading.setVisibility(View.INVISIBLE);
        layoutNoProductFound.setVisibility(View.VISIBLE);
        layoutList.setVisibility(View.INVISIBLE);
        layoutFailedGettingProduct.setVisibility(View.INVISIBLE);
    }

    private void setProductFoundState() {
        layoutLoading.setVisibility(View.INVISIBLE);
        layoutNoProductFound.setVisibility(View.INVISIBLE);
        layoutList.setVisibility(View.VISIBLE);
        layoutFailedGettingProduct.setVisibility(View.INVISIBLE);
    }

    private void setFailedState() {
        layoutLoading.setVisibility(View.INVISIBLE);
        layoutNoProductFound.setVisibility(View.INVISIBLE);
        layoutList.setVisibility(View.INVISIBLE);
        layoutFailedGettingProduct.setVisibility(View.VISIBLE);
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager
                = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}