package dev.wsgroup.main.views.activities.productviews;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APICampaignCaller;
import dev.wsgroup.main.models.apis.callers.APICartCaller;
import dev.wsgroup.main.models.apis.callers.APIProductCaller;
import dev.wsgroup.main.models.apis.callers.APIReviewCaller;
import dev.wsgroup.main.models.apis.callers.APISupplierCaller;
import dev.wsgroup.main.models.dtos.Campaign;
import dev.wsgroup.main.models.dtos.CartProduct;
import dev.wsgroup.main.models.dtos.LoyaltyStatus;
import dev.wsgroup.main.models.dtos.Product;
import dev.wsgroup.main.models.dtos.Review;
import dev.wsgroup.main.models.dtos.Supplier;
import dev.wsgroup.main.models.navigationAdapters.NavigationAdapter;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.ObjectSerializer;
import dev.wsgroup.main.views.activities.CartActivity;
import dev.wsgroup.main.views.activities.MainActivity;
import dev.wsgroup.main.views.activities.account.SignInActivity;
import dev.wsgroup.main.views.activities.order.PrepareOrderActivity;
import dev.wsgroup.main.views.fragments.tabs.product.DetailTab;
import dev.wsgroup.main.views.fragments.tabs.product.ReviewTab;

public class ProductDetailActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageView imgBackFromProductDetail, imgProductDetailHome;
    private TextView  txtProductDetailCartCount, lblRetry;
    private CardView cardViewProductDetailCartCount;
    private ConstraintLayout constraintLayoutShoppingCart,
            layoutLoading, layoutMainLayout;
    private LinearLayout layoutFailed;

    private SharedPreferences sharedPreferences;
    private String userId, productId, token;
    private int cartCount;
    private Product product;
    private Supplier supplier;
    private List<CartProduct> retailCartList, campaignCartList;
    private List<Campaign> currentCampaignList;
    private boolean activeCampaignCheck, readyCampaignCheck,
            reviewListCheck, orderCountCheck, loyaltyStatusCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        this.getSupportActionBar().hide();

        tabLayout = findViewById(R.id.productTabLayout);
        viewPager = findViewById(R.id.productViewPager);
        imgBackFromProductDetail = findViewById(R.id.imgBackFromProductDetail);
        imgProductDetailHome = findViewById(R.id.imgProductDetailHome);
        txtProductDetailCartCount = findViewById(R.id.txtProductDetailCartCount);
        lblRetry = findViewById(R.id.lblRetry);
        cardViewProductDetailCartCount = findViewById(R.id.cardViewProductDetailCartCount);
        constraintLayoutShoppingCart = findViewById(R.id.constraintLayoutShoppingCart);
        layoutLoading = findViewById(R.id.layoutLoading);
        layoutMainLayout = findViewById(R.id.layoutMainLayout);
        layoutFailed = findViewById(R.id.layoutFailed);

        loadData();

        imgBackFromProductDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK, getIntent());
                finish();
            }
        });

        imgProductDetailHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        constraintLayoutShoppingCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userId.isEmpty()) {
                    Intent signInIntent = new Intent(getApplicationContext(), SignInActivity.class);
                    startActivityForResult(signInIntent, IntegerUtils.REQUEST_LOGIN_FOR_CART);
                } else {
                    Intent cartIntent = new Intent(getApplicationContext(), CartActivity.class);
                    startActivityForResult(cartIntent, IntegerUtils.REQUEST_COMMON);
                }
            }
        });

        lblRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadData();
            }
        });
    }

    private void loadData() {
        setLoadingState();
        sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("USER_ID", "");
        token = sharedPreferences.getString("TOKEN", "");
        productId = getIntent().getStringExtra("PRODUCT_ID");
        activeCampaignCheck = false; readyCampaignCheck = false;
        reviewListCheck = true; orderCountCheck = false; loyaltyStatusCheck = false;
        APIProductCaller.getProductById(productId, getApplication(), new APIListener() {
            @Override
            public void onProductListFound(List<Product> productList) {
                if (productList.size() > 0) {
                    product = productList.get(0);
                    product.setCampaign(null);
                    findProductData();
                } else {
                    setFailedState();
                }
            }

            @Override
            public void onFailedAPICall(int code) {
                setFailedState();
            }
        });
        if (!userId.isEmpty()) {
            setUpShoppingCart();
        }
    }

    private void findProductData() {
        APICampaignCaller.getCampaignListByProductId(productId, "active",
                getApplication(), new APIListener() {
            @Override
            public void onCampaignListFound(List<Campaign> campaignList) {
                if (product.getCampaignList() == null || product.getCampaignList().size() == 0) {
                    product.setCampaignList(campaignList);
                } else if (campaignList.size() > 0){
                    currentCampaignList = product.getCampaignList();
                    for (Campaign campaign : campaignList) {
                        currentCampaignList.add(campaign);
                    }
                    product.setCampaignList(currentCampaignList);
                }
                activeCampaignCheck = true;
                finishSetup(product);
            }
            @Override
            public void onNoJSONFound() {
                if (product.getCampaignList() == null
                        || product.getCampaignList().size() == 0) {
                    product.setCampaignList(new ArrayList<>());
                }
                activeCampaignCheck = true;
                finishSetup(product);
            }
            @Override
            public void onFailedAPICall(int code) {
                setFailedState();
            }
        });
        APICampaignCaller.getCampaignListByProductId(productId, "ready",
                getApplication(), new APIListener() {
            @Override
            public void onCampaignListFound(List<Campaign> campaignList) {
                if (product.getCampaignList() == null || product.getCampaignList().size() == 0) {
                    product.setCampaignList(campaignList);
                } else if (campaignList.size() > 0){
                    currentCampaignList = product.getCampaignList();
                    for (Campaign campaign : campaignList) {
                        currentCampaignList.add(campaign);
                    }
                    product.setCampaignList(currentCampaignList);
                }
                readyCampaignCheck = true;
                finishSetup(product);
            }
            @Override
            public void onNoJSONFound() {
                if (product.getCampaignList() == null
                        || product.getCampaignList().size() == 0) {
                    product.setCampaignList(new ArrayList<>());
                }
                readyCampaignCheck = true;
                finishSetup(product);
            }
            @Override
            public void onFailedAPICall(int code) {
                setFailedState();
            }
        });
        APIReviewCaller.getReviewListByProductId(productId, getApplication(), new APIListener() {
            @Override
            public void onReviewListFound(List<Review> reviewList) {
                product.setReviewList(reviewList);
                product.setReviewCount(reviewList.size());
                double rating = 0;
                for (Review review : reviewList) {
                    rating += review.getRating();
                }
                product.setRating(rating);
                reviewListCheck = true;
                finishSetup(product);
            }

            @Override
            public void onFailedAPICall(int code) {
                setFailedState();
            }
        });
        if (!token.isEmpty()) {
            APISupplierCaller.getCustomerLoyaltyStatus(token, product.getSupplier().getId(),
                    getApplication(), new APIListener() {
                @Override
                public void onLoyaltyStatusListFound(List<LoyaltyStatus> list) {
                    supplier = product.getSupplier();
                    if (!list.isEmpty()) {
                        supplier.setLoyaltyStatus(list.get(0));
                    }
                    product.setSupplier(supplier);
                    loyaltyStatusCheck = true;
                    finishSetup(product);
                }

                @Override
                public void onFailedAPICall(int code) {
                    setFailedState();
                }
            });
        } else {
            loyaltyStatusCheck = true;
            finishSetup(product);
        }
        List<String> list = new ArrayList<>();
        list.add(productId);
        APIProductCaller.getOrderCountByProductIdList(list, getApplication(), new APIListener() {
            @Override
            public void onProductOrderCountFound(Map<String, Integer> countList) {
                if (countList.size() > 0) {
                    product.setOrderCount(countList.get(productId));
                }
                orderCountCheck = true;
                finishSetup(product);
            }

            @Override
            public void onFailedAPICall(int code) {
                setFailedState();
            }
        });
    }

    private void finishSetup(Product product) {
        if (activeCampaignCheck && readyCampaignCheck && reviewListCheck
                && orderCountCheck && loyaltyStatusCheck) {
            getIntent().putExtra("PRODUCT", product);
            setupTabLayout();
        }
    }

    private void prepareOrder() {
        if (userId.isEmpty()) {
            Intent SignInIntent = new Intent(getApplicationContext(), SignInActivity.class);
            startActivityForResult(SignInIntent, IntegerUtils.REQUEST_LOGIN);
        } else {
            Intent campaignSelectIntent
                    = new Intent(getApplicationContext(), PrepareOrderActivity.class);
            campaignSelectIntent.putExtra("PRODUCT", product);
            startActivityForResult(campaignSelectIntent, IntegerUtils.REQUEST_MAKE_PURCHASE);
        }
    }

    private void setupTabLayout() {
        tabLayout.removeAllTabs();

        TabLayout.Tab tabCommon = tabLayout.newTab();
        tabCommon.setText("About");
        tabLayout.addTab(tabCommon);

        tabCommon = tabLayout.newTab();
        tabCommon.setText("Reviews");
        tabLayout.addTab(tabCommon);

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabTextColors(getResources().getColor(R.color.black),
                                    getResources().getColor(R.color.black));

        NavigationAdapter adapter = new NavigationAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return new DetailTab();
                    case 1:
                        return new ReviewTab();
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return tabLayout.getTabCount();
            }
        };
        viewPager.setAdapter(adapter);
        tabLayout.clearOnTabSelectedListeners();
        viewPager.clearOnPageChangeListeners();

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        setLoadedState();
    }

    private void getShoppingCart() {
        try {
            retailCartList = (List<CartProduct>) ObjectSerializer
                    .deserialize(sharedPreferences.getString("RETAIL_CART", ""));
            campaignCartList = (List<CartProduct>) ObjectSerializer
                    .deserialize(sharedPreferences.getString("CAMPAIGN_CART", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void editShoppingCart() {
        getShoppingCart();
        if(retailCartList == null && campaignCartList == null) {
            cardViewProductDetailCartCount.setVisibility(View.INVISIBLE);
        } else {
            if (retailCartList.size() > 0 || campaignCartList.size() > 0) {
                cardViewProductDetailCartCount.setVisibility(View.VISIBLE);
                cartCount = retailCartList.size() + campaignCartList.size();
                txtProductDetailCartCount.setText(cartCount + "");
            } else {
                cardViewProductDetailCartCount.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void setUpShoppingCart() {
        token = sharedPreferences.getString("TOKEN", "");
        APICartCaller.getCartList(token, getApplication(), new APIListener() {
            @Override
            public void onCartListFound(List<CartProduct> retailList,
                                        List<CartProduct> campaignList) {
                putSessionCart(retailList, campaignList);
                editShoppingCart();
            }

            @Override
            public void onFailedAPICall(int code) {
                if (code == IntegerUtils.ERROR_NO_USER) {
                    MethodUtils.displayErrorAccountMessage(getApplicationContext(),
                            ProductDetailActivity.this);
                } else {
                    putSessionCart(new ArrayList<>(), new ArrayList<>());
                    editShoppingCart();
                }
            }
        });
    }

    private void putSessionCart(List<CartProduct> retailList,
                                List<CartProduct> campaignList) {
        try {
            sharedPreferences.edit()
                    .putString("RETAIL_CART",
                            ObjectSerializer.serialize((Serializable) retailList))
                    .putString("CAMPAIGN_CART",
                            ObjectSerializer.serialize((Serializable) campaignList))
                    .apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setLoadingState() {
        layoutLoading.setVisibility(View.VISIBLE);
        layoutMainLayout.setVisibility(View.INVISIBLE);
        layoutFailed.setVisibility(View.INVISIBLE);
    }

    private void setLoadedState() {
        layoutLoading.setVisibility(View.INVISIBLE);
        layoutMainLayout.setVisibility(View.VISIBLE);
        layoutFailed.setVisibility(View.INVISIBLE);
    }

    private void setFailedState() {
        layoutLoading.setVisibility(View.INVISIBLE);
        layoutMainLayout.setVisibility(View.INVISIBLE);
        layoutFailed.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        imgBackFromProductDetail.performClick();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadData();
        if (resultCode == RESULT_OK) {
            if (requestCode == IntegerUtils.REQUEST_LOGIN_FOR_CART) {
                constraintLayoutShoppingCart.performClick();
            } else if (requestCode == IntegerUtils.REQUEST_LOGIN) {
                prepareOrder();
            } else if (data != null) {
                if (data.getIntExtra("REQUEST_CODE", IntegerUtils.REQUEST_COMMON)
                        == IntegerUtils.REQUEST_SELECT_CAMPAIGN) {
                    Campaign campaign = (Campaign) data.getSerializableExtra("CAMPAIGN_SELECTED");
                    product.setCampaign(campaign);
                    prepareOrder();
                }
            }
        }
    }
}