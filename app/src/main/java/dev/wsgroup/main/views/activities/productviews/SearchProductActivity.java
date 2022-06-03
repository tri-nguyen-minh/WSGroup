package dev.wsgroup.main.views.activities.productviews;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APICampaignCaller;
import dev.wsgroup.main.models.apis.callers.APICategoryCaller;
import dev.wsgroup.main.models.apis.callers.APIProductCaller;
import dev.wsgroup.main.models.dtos.Campaign;
import dev.wsgroup.main.models.dtos.Category;
import dev.wsgroup.main.models.dtos.Product;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewCampaignSearchAdapter;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewProductSearchAdapter;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.views.activities.MainActivity;
import dev.wsgroup.main.views.activities.account.SignInActivity;
import dev.wsgroup.main.views.activities.order.PrepareOrderActivity;
import dev.wsgroup.main.views.dialogbox.DialogBoxLoading;

public class SearchProductActivity extends AppCompatActivity {

    private EditText editSearchProduct;
    private TextView txtProductSearchCount, lblProductSearchCount, txtProductCategoryCount,
            lblProductCategoryCount, txtCategory, lblRetryGetProduct;
    private ImageView imgBackFromSearch, imgSearchHome, imgSearchInfo;
    private RelativeLayout layoutLoading, layoutNoProductFound;
    private ConstraintLayout layoutParent, layoutSearchBar;
    private LinearLayout layoutList, layoutSearch, layoutCategory, layoutFailedGettingProduct,
            layoutSharingCampaign, layoutSingleCampaign, layoutListProduct;
    private RecyclerView recViewSearchProductList, recViewSharingCampaign, recViewSingleCampaign;

    private String searchString;
    private boolean orderCountCheck, ratingCheck, campaignCheck,
            activeCampaignStatus, readyCampaignStatus;
    private RecViewProductSearchAdapter productAdapter;
    private RecViewCampaignSearchAdapter campaignAdapter;
    private int identifier;
    private APIListener listener;
    private List<Product> productList;
    private List<Campaign> sharingCampaignList, singleCampaignList, tempList;
    private Category category;
    private SharedPreferences sharedPreferences;
    private DialogBoxLoading dialogBoxLoading;

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
        imgSearchInfo = findViewById(R.id.imgSearchInfo);
        layoutLoading = findViewById(R.id.layoutLoading);
        layoutNoProductFound = findViewById(R.id.layoutNoProductFound);
        layoutParent = findViewById(R.id.layoutParent);
        layoutSearchBar = findViewById(R.id.layoutSearchBar);
        layoutList = findViewById(R.id.layoutList);
        layoutSearch = findViewById(R.id.layoutSearch);
        layoutCategory = findViewById(R.id.layoutCategory);
        layoutFailedGettingProduct = findViewById(R.id.layoutFailedGettingProduct);
        layoutSharingCampaign = findViewById(R.id.layoutSharingCampaign);
        layoutSingleCampaign = findViewById(R.id.layoutSingleCampaign);
        layoutListProduct = findViewById(R.id.layoutListProduct);
        recViewSearchProductList = findViewById(R.id.recViewSearchProductList);
        recViewSharingCampaign = findViewById(R.id.recViewSharingCampaign);
        recViewSingleCampaign = findViewById(R.id.recViewSingleCampaign);

        setNoProductState();
        identifier = getIntent().getIntExtra("IDENTIFIER", IntegerUtils.IDENTIFIER_SEARCH_BAR);
        switch (identifier) {
            case IntegerUtils.IDENTIFIER_SEARCH_BAR: {
                layoutSearchBar.setVisibility(View.VISIBLE);
                editSearchProduct.requestFocus();
                layoutSearch.setVisibility(View.VISIBLE);
                layoutCategory.setVisibility(View.GONE);
                break;
            }
            case IntegerUtils.IDENTIFIER_SEARCH_SUPPLIER: {
                searchString = getIntent().getStringExtra("SEARCH_STRING");
                layoutSearchBar.setVisibility(View.VISIBLE);
                editSearchProduct.setText(searchString);
                layoutSearch.setVisibility(View.VISIBLE);
                layoutCategory.setVisibility(View.GONE);
                runSearch();
                break;

            }
            case IntegerUtils.IDENTIFIER_SEARCH_CATEGORY: {
                searchString = getIntent().getStringExtra("SEARCH_STRING");
                layoutSearchBar.setVisibility(View.GONE);
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
            public void onFailedAPICall(int code) {setFailedState();}
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

        layoutNoProductFound.setOnClickListener(new View.OnClickListener() {
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
                    MethodUtils.hideKeyboard(view, getApplicationContext());
                    searchString = editSearchProduct.getText().toString();
                    if (searchString.isEmpty()) {
                        setNoProductState();
                    } else {
                        runSearch();
                    }
                }
            }
        });

        editSearchProduct.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    searchString = editSearchProduct.getText().toString();
                    if (searchString.isEmpty()) {
                        setNoProductState();
                    } else {
                        runSearch();
                    }
                    return true;
                }
                return false;
            }
        });

        lblRetryGetProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runSearchCategory();
            }
        });

        imgSearchInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editSearchProduct.hasFocus()) {
                    MethodUtils.hideKeyboard(view, getApplicationContext());
                }
                searchString = editSearchProduct.getText().toString();
                if (searchString.isEmpty()) {
                    setNoProductState();
                } else {
                    runSearch();
                }
            }
        });
    }

    private void runSearch() {
        orderCountCheck = false; ratingCheck = false; campaignCheck = false;
        setLoadingState();
        APIProductCaller.searchProductByNameOrSupplier(searchString, getApplication(),
                new APIListener() {
                    @Override
                    public void onProductListFound(List<Product> list) {
                        if (list.size() == 0) {
                            orderCountCheck = true; ratingCheck = true;
                            checkAllDisplayedList();
                        } else {
                            productList = list;
                            APIProductCaller.getOrderCountByProductList(productList,
                                    getApplication(), listener);
                            APIProductCaller.getRatingByProductIdList(productList,
                                    getApplication(), listener);
                            setupListView(productList);
                        }
                    }
                    @Override
                    public void onFailedAPICall(int code) {
                        setNoProductState();
                    }
                });
        APICampaignCaller.searchCampaign(searchString, getApplication(), new APIListener() {
            @Override
            public void onCampaignListFound(List<Campaign> campaignList) {
                if (campaignList.size() > 0) {
                    sharingCampaignList = new ArrayList<>();
                    singleCampaignList = new ArrayList<>();
                    for (Campaign campaign : campaignList) {
                        if (campaign.getShareFlag()) {
                            sharingCampaignList.add(campaign);
                        } else {
                            singleCampaignList.add(campaign);
                        }
                    }
                    if (sharingCampaignList.size() > 0) {
                        MethodUtils.sortSharingCampaign(sharingCampaignList);
                        campaignAdapter = new RecViewCampaignSearchAdapter(getApplicationContext(),
                                SearchProductActivity.this) {
                            @Override
                            public void onCampaignSelected(Campaign campaign) {
                                checkLoginStatus(campaign);
                            }
                        };
                        campaignAdapter.setCampaignList(sharingCampaignList);
                        recViewSharingCampaign.setAdapter(campaignAdapter);
                        layoutSharingCampaign.setVisibility(View.VISIBLE);
                    }
                    if (singleCampaignList.size() > 0) {
                        MethodUtils.sortSingleCampaign(singleCampaignList);
                        campaignAdapter = new RecViewCampaignSearchAdapter(getApplicationContext(),
                                SearchProductActivity.this) {
                            @Override
                            public void onCampaignSelected(Campaign campaign) {
                                checkLoginStatus(campaign);
                            }
                        };
                        campaignAdapter.setCampaignList(singleCampaignList);
                        recViewSingleCampaign.setAdapter(campaignAdapter);
                        layoutSingleCampaign.setVisibility(View.VISIBLE);
                    }
                }
                recViewSharingCampaign.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                        LinearLayoutManager.HORIZONTAL,false));
                recViewSingleCampaign.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                        LinearLayoutManager.HORIZONTAL,false));
                campaignCheck = true;
                checkAllDisplayedList();
            }

            @Override
            public void onFailedAPICall(int code) {
                campaignCheck = true;
                checkAllDisplayedList();
            }
        });
    }


    private void runSearchCategory() {
        orderCountCheck = false; ratingCheck = false;
        setLoadingState();
        APICategoryCaller.getCategoryById(searchString, getApplication(), new APIListener() {
            @Override
            public void onCategoryListFound(List<Category> categoryList) {
                if (categoryList.size() > 0) {
                    category = categoryList.get(0);
                    List<Category> list = new ArrayList<>();
                    list.add(category);
                    APIProductCaller.getProductByCategoryList(list, getApplication(),
                            new APIListener() {
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

                                orderCountCheck = true; ratingCheck = true;
                                setupListView(productList);
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

    private void checkLoginStatus(Campaign campaign) {
        sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("USER_ID", "");
        if (userId.isEmpty()) {
            Intent signInIntent = new Intent(getApplicationContext(), SignInActivity.class);
            startActivityForResult(signInIntent, IntegerUtils.REQUEST_SELECT_CAMPAIGN);
        } else {
            onCampaignSelected(campaign);
        }
    }

    private void onCampaignSelected(Campaign campaign) {
        dialogBoxLoading = new DialogBoxLoading(SearchProductActivity.this);
        dialogBoxLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBoxLoading.show();
        activeCampaignStatus = false; readyCampaignStatus = false;
        String productId = campaign.getProduct().getProductId();
        APIProductCaller.getProductById(productId, getApplication(), new APIListener() {
            @Override
            public void onProductListFound(List<Product> productList) {
                if (productList.size() > 0) {
                    Product product = productList.get(0);
                    product.setCampaign(campaign);;
                    getExtraCampaignData(product);
                } else {
                    if (dialogBoxLoading.isShowing()) {
                        dialogBoxLoading.dismiss();
                    }
                    MethodUtils.displayErrorAPIMessage(SearchProductActivity.this);
                }
            }

            @Override
            public void onFailedAPICall(int code) {
                if (dialogBoxLoading.isShowing()) {
                    dialogBoxLoading.dismiss();
                }
                MethodUtils.displayErrorAPIMessage(SearchProductActivity.this);
            }
        });
    }

    private void getExtraCampaignData(Product product) {
        APICampaignCaller.getCampaignListByProductId(product.getProductId(),"active",
                getApplication(), new APIListener() {
            @Override
            public void onCampaignListFound(List<Campaign> campaignList) {
                if (product.getCampaignList() == null
                        || product.getCampaignList().size() == 0) {
                    product.setCampaignList(campaignList);
                } else if (campaignList.size() > 0) {
                    tempList = product.getCampaignList();
                    for (Campaign campaign : campaignList) {
                        tempList.add(campaign);
                    }
                    product.setCampaignList(tempList);
                }
                activeCampaignStatus = true;
                finishSetup(product);
            }

            @Override
            public void onNoJSONFound() {
                if (product.getCampaignList() == null
                        || product.getCampaignList().size() == 0) {
                    product.setCampaignList(new ArrayList<>());
                }
                activeCampaignStatus = true;
                finishSetup(product);
            }

            @Override
            public void onFailedAPICall(int code) {
                if (dialogBoxLoading.isShowing()) {
                    dialogBoxLoading.dismiss();
                }
                MethodUtils.displayErrorAPIMessage(SearchProductActivity.this);
            }
        });
        APICampaignCaller.getCampaignListByProductId(product.getProductId(),"ready",
                getApplication(), new APIListener() {
            @Override
            public void onCampaignListFound(List<Campaign> campaignList) {
                if (product.getCampaignList() == null
                        || product.getCampaignList().size() == 0) {
                    product.setCampaignList(campaignList);
                } else if (campaignList.size() > 0) {
                    tempList = product.getCampaignList();
                    for (Campaign campaign : campaignList) {
                        tempList.add(campaign);
                    }
                    product.setCampaignList(tempList);
                }
                readyCampaignStatus = true;
                finishSetup(product);
            }

            @Override
            public void onNoJSONFound() {
                if (product.getCampaignList() == null
                        || product.getCampaignList().size() == 0) {
                    product.setCampaignList(new ArrayList<>());
                }
                readyCampaignStatus = true;
                finishSetup(product);
            }

            @Override
            public void onFailedAPICall(int code) {
                if (dialogBoxLoading.isShowing()) {
                    dialogBoxLoading.dismiss();
                }
                MethodUtils.displayErrorAPIMessage(SearchProductActivity.this);
            }
        });
    }

    private void finishSetup(Product product) {
        if (activeCampaignStatus && readyCampaignStatus) {
            Intent campaignSelectIntent
                    = new Intent(getApplicationContext(), PrepareOrderActivity.class);
            campaignSelectIntent.putExtra("PRODUCT", product);
            if (dialogBoxLoading.isShowing()) {
                dialogBoxLoading.dismiss();
            }
            startActivityForResult(campaignSelectIntent, IntegerUtils.REQUEST_MAKE_PURCHASE);
        }
    }

    private void setupListView(List<Product> productList) {
        if (orderCountCheck && ratingCheck) {
            if (identifier == IntegerUtils.IDENTIFIER_SEARCH_CATEGORY) {
                txtCategory.setText(category.getName());
                txtProductCategoryCount.setText(productList.size() + "");
                lblProductCategoryCount.setText(productList.size() > 1 ?
                        "Products" : "Product");
            } else {
                txtProductSearchCount.setText(productList.size() + "");
                lblProductSearchCount.setText(productList.size() > 1 ?
                        "Products" : "Product");
            }
            productAdapter = new RecViewProductSearchAdapter(getApplicationContext(),
                    SearchProductActivity.this);
            productAdapter.setProductsList(productList);
            recViewSearchProductList.setAdapter(productAdapter);
            recViewSearchProductList.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                    RecyclerView.VERTICAL, false));
            layoutListProduct.setVisibility(View.VISIBLE);
            checkAllDisplayedList();
        }
    }

    private void checkAllDisplayedList() {
        if (identifier == IntegerUtils.IDENTIFIER_SEARCH_CATEGORY) {
            if (orderCountCheck && ratingCheck) {
                if (layoutListProduct.getVisibility() == View.VISIBLE) {
                    setLoadedState();
                } else {
                    setNoProductState();
                }
            }
        } else {
            if (orderCountCheck && ratingCheck && campaignCheck) {
                if (layoutListProduct.getVisibility() == View.VISIBLE
                        || layoutSharingCampaign.getVisibility() == View.VISIBLE
                        || layoutSingleCampaign.getVisibility() == View.VISIBLE) {
                    setLoadedState();
                } else {
                    setNoProductState();
                }
            }
        }
    }

    private void setLoadingState() {
        layoutLoading.setVisibility(View.VISIBLE);
        layoutNoProductFound.setVisibility(View.INVISIBLE);
        layoutList.setVisibility(View.INVISIBLE);
        layoutFailedGettingProduct.setVisibility(View.INVISIBLE);
        layoutSharingCampaign.setVisibility(View.GONE);
        layoutSingleCampaign.setVisibility(View.GONE);
        layoutListProduct.setVisibility(View.GONE);
    }

    private void setNoProductState() {
        layoutLoading.setVisibility(View.INVISIBLE);
        layoutNoProductFound.setVisibility(View.VISIBLE);
        layoutList.setVisibility(View.INVISIBLE);
        layoutFailedGettingProduct.setVisibility(View.INVISIBLE);
    }

    private void setLoadedState() {
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

    @Override
    public void onBackPressed() {
        imgBackFromSearch.performClick();
    }
}