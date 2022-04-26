package dev.wsgroup.main.views.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Map;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APICategoryCaller;
import dev.wsgroup.main.models.apis.callers.APIProductCaller;
import dev.wsgroup.main.models.apis.callers.APISupplierCaller;
import dev.wsgroup.main.models.dtos.Category;
import dev.wsgroup.main.models.dtos.Product;
import dev.wsgroup.main.models.dtos.Supplier;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewCategoryAdapter;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewProductListAdapter;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.views.activities.account.SignInActivity;
import dev.wsgroup.main.views.activities.message.MessageActivity;

public class SupplierActivity extends AppCompatActivity {

    private ImageView imgBackFromSupplier, imgSupplierHome, imgSupplierChat,
            imgSupplierBackground, imgSupplierAvatar;
    private NestedScrollView scrollViewMain;
    private RelativeLayout layoutLoading, layoutLoadingCategory, layoutLoadingProduct;
    private RecyclerView recViewCategory, recViewProduct;
    private TextView txtSupplierName, txtSupplierAddress, txtSupplierMail,
            lblRetryGetSupplier, lblSupplierChat;
    private LinearLayout layoutCategory, layoutProduct, layoutFailedGettingSupplier;


    private SharedPreferences sharedPreferences;
    private String supplierId;
    private Supplier supplier;
    private List<Category> categoryList;
    private List<Product> productList;
    private boolean orderCountCheck, ratingCheck;
    private RecViewProductListAdapter productAdapter;
    private RecViewCategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier);
        this.getSupportActionBar().hide();

        imgBackFromSupplier = findViewById(R.id.imgBackFromSupplier);
        imgSupplierHome = findViewById(R.id.imgSupplierHome);
        imgSupplierChat = findViewById(R.id.imgSupplierChat);
        imgSupplierBackground = findViewById(R.id.imgSupplierBackground);
        imgSupplierAvatar = findViewById(R.id.imgSupplierAvatar);
        scrollViewMain = findViewById(R.id.scrollViewMain);
        layoutCategory = findViewById(R.id.layoutCategory);
        layoutProduct = findViewById(R.id.layoutProduct);
        layoutLoading = findViewById(R.id.layoutLoading);
        layoutLoadingCategory = findViewById(R.id.layoutLoadingCategory);
        layoutLoadingProduct = findViewById(R.id.layoutLoadingProduct);
        recViewCategory = findViewById(R.id.recViewCategory);
        recViewProduct = findViewById(R.id.recViewProduct);
        txtSupplierName = findViewById(R.id.txtSupplierName);
        txtSupplierAddress = findViewById(R.id.txtSupplierAddress);
        txtSupplierMail = findViewById(R.id.txtSupplierMail);
        lblRetryGetSupplier = findViewById(R.id.lblRetryGetSupplier);
        lblSupplierChat = findViewById(R.id.lblSupplierChat);
        layoutFailedGettingSupplier = findViewById(R.id.layoutFailedGettingSupplier);

        supplierId = getIntent().getStringExtra("SUPPLIER_ID");
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
                String accountId = sharedPreferences.getString("ACCOUNT_ID", "");
                if (accountId.isEmpty()) {
                    Intent signInIntent = new Intent(getApplicationContext(), SignInActivity.class);
                    startActivityForResult(signInIntent, IntegerUtils.REQUEST_COMMON);
                } else {
                    Intent messageIntent = new Intent(getApplicationContext(), MessageActivity.class);
                    messageIntent.putExtra("USER_ID", accountId);
                    messageIntent.putExtra("SUPPLIER_ID", supplier.getAccountId());
                    startActivityForResult(messageIntent, IntegerUtils.REQUEST_COMMON);
                }
            }
        };

        getSupplier();

        imgBackFromSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        imgSupplierHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
        lblRetryGetSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupplier();
            }
        });
        lblSupplierChat.setOnClickListener(listener);
        imgSupplierChat.setOnClickListener(listener);
    }

    private void getSupplier() {
        setLoadingState();
        APISupplierCaller.getSupplierById(supplierId, getApplication(), new APIListener() {
            @Override
            public void onSupplierFound(Supplier supplierFound) {
                supplier = supplierFound;
                if (supplier != null) {
                    setupSupplier();
                    APIListener listener = new APIListener() {
                        @Override
                        public void onCategoryListFound(List<Category> list) {
                            categoryList = list;
                            setupCategoryList();
                        }
                        @Override
                        public void onProductListFound(List<Product> list) {

                            orderCountCheck = false; ratingCheck = false;
                            APIListener mostPopularListener = new APIListener() {
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
                                    setupProductList();
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
                                    setupProductList();
                                }

                                @Override
                                public void onFailedAPICall(int code) {
                                    layoutLoadingProduct.setVisibility(View.GONE);
                                    layoutProduct.setVisibility(View.GONE);
                                }
                            };
                            if (list.size() > 0) {
                                productList = list;

                                orderCountCheck = true; ratingCheck = true;
                                setupProductList();

//                                APIProductCaller.getOrderCountByProductList(productList,
//                                        getApplication(), mostPopularListener);
//                                APIProductCaller.getRatingByProductIdList(productList,
//                                        getApplication(), mostPopularListener);
                            } else {
                                layoutLoadingProduct.setVisibility(View.GONE);
                                layoutProduct.setVisibility(View.GONE);
                            }
                        }
                    };
                    APICategoryCaller.getCategoryListBySupplierId(supplierId,
                            getApplication(), listener);
                    APIProductCaller.getProductListBySupplierId(supplierId, null,
                            getApplication(), listener);
                } else {
                    setFailedSupplierState();
                }
            }
            @Override
            public void onFailedAPICall(int code) {
                setFailedSupplierState();
            }
        });
    }

    private void setupSupplier() {
        txtSupplierName.setText(supplier.getName());
        txtSupplierAddress.setText(supplier.getAddress());
        txtSupplierMail.setText(supplier.getMail());
        if (!supplier.getAvatarLink().equals("null")) {
            Glide.with(getApplicationContext()).load(supplier.getAvatarLink())
                    .override(5, 5)
                    .into(imgSupplierBackground);
            Glide.with(getApplicationContext()).load(supplier.getAvatarLink())
                    .into(imgSupplierAvatar);
        } else {
            Glide.with(getApplicationContext()).load(R.drawable.ic_profile)
                    .override(5, 5)
                    .into(imgSupplierBackground);
            Glide.with(getApplicationContext()).load(R.drawable.ic_profile)
                    .into(imgSupplierAvatar);
        }
        setSupplierLoadedState();
    }

    private void setLoadingState() {
        layoutLoading.setVisibility(View.VISIBLE);
        scrollViewMain.setVisibility(View.GONE);
        layoutFailedGettingSupplier.setVisibility(View.GONE);
    }

    private void setSupplierLoadedState() {
        layoutCategory.setVisibility(View.GONE);
        layoutProduct.setVisibility(View.GONE);
        layoutLoadingProduct.setVisibility(View.VISIBLE);
        layoutLoadingCategory.setVisibility(View.VISIBLE);
        layoutLoading.setVisibility(View.GONE);
        scrollViewMain.setVisibility(View.VISIBLE);
        layoutFailedGettingSupplier.setVisibility(View.GONE);
    }

    private void setFailedSupplierState() {
        layoutLoading.setVisibility(View.GONE);
        scrollViewMain.setVisibility(View.GONE);
        layoutFailedGettingSupplier.setVisibility(View.VISIBLE);
    }

    private void setupCategoryList() {
        layoutLoadingCategory.setVisibility(View.GONE);
        if (categoryList.size() > 0) {
            layoutCategory.setVisibility(View.VISIBLE);
            categoryAdapter = new RecViewCategoryAdapter(getApplicationContext(),
                    SupplierActivity.this);
            categoryAdapter.setCategoryList(categoryList);
            recViewCategory.setAdapter(categoryAdapter);
            recViewCategory.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                    LinearLayoutManager.HORIZONTAL, false));
        } else {
            layoutCategory.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        imgBackFromSupplier.performClick();
    }

    private void setupProductList() {
        if (orderCountCheck && ratingCheck) {
            layoutLoadingProduct.setVisibility(View.GONE);
            layoutProduct.setVisibility(View.VISIBLE);
            productAdapter = new RecViewProductListAdapter(getApplicationContext(),
                                                        SupplierActivity.this);
            productAdapter.setProductsList(productList);
            recViewProduct.setAdapter(productAdapter);
            recViewProduct.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                    LinearLayoutManager.HORIZONTAL, false));
        }
    }
}