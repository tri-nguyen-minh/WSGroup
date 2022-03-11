package dev.wsgroup.main.views.activities.productviews;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
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
import dev.wsgroup.main.models.utils.ObjectSerializer;
import dev.wsgroup.main.views.activities.CartActivity;
import dev.wsgroup.main.views.activities.MainActivity;
import dev.wsgroup.main.views.activities.account.SignInActivity;
import dev.wsgroup.main.views.fragments.tabs.product.DetailTab;
import dev.wsgroup.main.views.fragments.tabs.product.ReviewTab;

public class ProductDetailActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabLayout.Tab tabCommon;
    private ImageView imgBackFromProductDetail, imgProductDetailHome;
    private TextView  txtProductDetailCartCount;
    private CardView cardViewProductDetailCartCount;
    private ConstraintLayout constraintLayoutShoppingCart, layoutLoading, layoutMainLayout,
            constraintLayoutMessage;

    private SharedPreferences sharedPreferences;
    private String userId, productId, token;
    private int cartCount;
    private Product product;
    private Supplier supplier;
    private List<CartProduct> retailList, campaignList;
    private boolean campaignListStatus, reviewListStatus, orderCountStatus,
            reviewCountStatus, loyaltyCheckStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        this.getSupportActionBar().hide();
        {
            tabLayout = findViewById(R.id.productTabLayout);
            viewPager = findViewById(R.id.productViewPager);
            imgBackFromProductDetail = findViewById(R.id.imgBackFromProductDetail);
            imgProductDetailHome = findViewById(R.id.imgProductDetailHome);
            txtProductDetailCartCount = findViewById(R.id.txtProductDetailCartCount);
            cardViewProductDetailCartCount = findViewById(R.id.cardViewProductDetailCartCount);
            constraintLayoutShoppingCart = findViewById(R.id.constraintLayoutShoppingCart);
            layoutLoading = findViewById(R.id.layoutLoading);
            layoutMainLayout = findViewById(R.id.layoutMainLayout);
        }

        sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("USER_ID", "");
        token = sharedPreferences.getString("TOKEN", "");
        productId = getIntent().getStringExtra("PRODUCT_ID");

        loadProduct();
        editShoppingCart();

        imgBackFromProductDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
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
    }

    private void loadProduct() {
        layoutLoading.setVisibility(View.VISIBLE);
        layoutMainLayout.setVisibility(View.INVISIBLE);
        campaignListStatus = false;
        reviewListStatus = false;
        orderCountStatus = false;
        reviewCountStatus = false;
        loyaltyCheckStatus = false;
        APIProductCaller.getProductById(productId,
                getApplication(), new APIListener() {
            @Override
            public void onProductFound(Product foundProduct) {
                product = foundProduct;
                APICampaignCaller.getCampaignListByProductId(productId, "active", null,
                        getApplication(), new APIListener() {
                    @Override
                    public void onCampaignListFound(List<Campaign> campaignList) {
                        product.setCampaign(null);
                        product.setCampaignList(campaignList);
                        campaignListStatus = true;
                        finishSetup(product);
                    }
                    @Override
                    public void onNoJSONFound() {
                        product.setCampaign(null);
                        product.setCampaignList(new ArrayList<Campaign>());
                        campaignListStatus = true;
                        finishSetup(product);
                    }
                });
                APIReviewCaller.getReviewListByProductId(productId, null,
                        getApplication(), new APIListener() {
                    @Override
                    public void onReviewListFound(List<Review> reviewList) {
                        product.setReviewList(reviewList);
                        reviewListStatus = true;
                        finishSetup(product);
                    }

                    @Override
                    public void onFailedAPICall(int code) {
                        product.setReviewList(new ArrayList<>());
                        campaignListStatus = true;
                        finishSetup(product);
                    }
                });
                APIReviewCaller.getReviewCountByProductId(productId,
                        getApplication(), new APIListener() {
                    @Override
                    public void onReviewCountFound(int count, double rating) {
                        product.setReviewCount(count);
                        product.setRating(rating);
                        reviewCountStatus = true;
                        finishSetup(product);
                    }

                    @Override
                    public void onFailedAPICall(int code) {
                        product.setReviewCount(0);
                        product.setRating(0);
                        reviewCountStatus = true;
                        finishSetup(product);
                    }
                });
                if (!token.isEmpty()) {
                    APISupplierCaller.getCustomerLoyaltyStatus(token, product.getSupplier().getId(),
                            getApplication(), new APIListener() {
                        @Override
                        public void onLoyaltyStatusFound(LoyaltyStatus status) {
                            supplier = product.getSupplier();
                            supplier.setLoyaltyStatus(status);
                            product.setSupplier(supplier);
                            loyaltyCheckStatus = true;
                            finishSetup(product);
                        }

                        @Override
                        public void onFailedAPICall(int code) {
                            loyaltyCheckStatus = true;
                            finishSetup(product);
                        }
                    });
                } else {
                    loyaltyCheckStatus = true;
                    finishSetup(product);
                }
                List<String> list = new ArrayList<>();
                list.add(productId);
                APIProductCaller.getOrderCountByProductIdList(list,
                        getApplication(), new APIListener() {
                    @Override
                    public void onProductOrderCountFound(Map<String, Integer> countList) {
                        if (countList.size() > 0) {
                            product.setOrderCount(countList.get(productId));
                        }
                        orderCountStatus = true;
                        finishSetup(product);
                    }

                    @Override
                    public void onFailedAPICall(int code) {
                        product.setOrderCount(0);
                        orderCountStatus = true;
                        finishSetup(product);
                    }
                });
            }
        });
    }

    private void finishSetup(Product product) {
        if (campaignListStatus && reviewListStatus && orderCountStatus
                && reviewCountStatus && loyaltyCheckStatus) {
            getIntent().putExtra("PRODUCT", product);
            setupTabLayout();
        }
    }

    private void startDialogBoxProductDetail() {
        if (userId.isEmpty()) {
            Intent SignInIntent = new Intent(getApplicationContext(), SignInActivity.class);
            startActivityForResult(SignInIntent, IntegerUtils.REQUEST_LOGIN);
        } else {
            Intent campaignSelectIntent
                    = new Intent(getApplicationContext(), PrepareProductActivity.class);
            campaignSelectIntent.putExtra("PRODUCT", product);
            startActivityForResult(campaignSelectIntent, IntegerUtils.REQUEST_MAKE_PURCHASE);
        }
    }

    private void setupTabLayout() {
        layoutLoading.setVisibility(View.INVISIBLE);
        layoutMainLayout.setVisibility(View.VISIBLE);
        tabLayout.removeAllTabs();

        tabCommon = tabLayout.newTab();
        tabCommon.setText("About");
        tabLayout.addTab(tabCommon);

        tabCommon = tabLayout.newTab();
        tabCommon.setText("Reviews");
        tabLayout.addTab(tabCommon);

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabTextColors(getResources().getColor(R.color.black),
                                    getResources().getColor(R.color.black));

        NavigationAdapter adapter = new NavigationAdapter(getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
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
    }

    private void getShoppingCart() {
        try {
            retailList = (List<CartProduct>) ObjectSerializer
                    .deserialize(sharedPreferences.getString("RETAIL_CART", ""));
            campaignList = (List<CartProduct>) ObjectSerializer
                    .deserialize(sharedPreferences.getString("CAMPAIGN_CART", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void editShoppingCart() {
        getShoppingCart();
        if(retailList == null && campaignList == null) {
            cardViewProductDetailCartCount.setVisibility(View.INVISIBLE);
        } else {
            if (retailList.size() > 0 || campaignList.size() > 0) {
                cardViewProductDetailCartCount.setVisibility(View.VISIBLE);
                cartCount = retailList.size() + campaignList.size();
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
                putSessionCart(new ArrayList<>(), new ArrayList<>());
                editShoppingCart();
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("USER_ID", "");
        loadProduct();
        setUpShoppingCart();
        if (resultCode == RESULT_OK) {
            if (requestCode == IntegerUtils.REQUEST_LOGIN_FOR_CART) {
                constraintLayoutShoppingCart.performClick();
            } else if (requestCode == IntegerUtils.REQUEST_LOGIN) {
                startDialogBoxProductDetail();
            } else if (data != null) {
                if (data.getIntExtra("REQUEST_CODE", IntegerUtils.REQUEST_COMMON)
                        == IntegerUtils.REQUEST_SELECT_CAMPAIGN) {
                    Campaign campaign = (Campaign) data.getSerializableExtra("CAMPAIGN_SELECTED");
                    product.setCampaign(campaign);
                    startDialogBoxProductDetail();
                }
            }
        }
    }
}