package dev.wsgroup.main.views.activities.productviews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APICampaignCaller;
import dev.wsgroup.main.models.apis.callers.APIProductCaller;
import dev.wsgroup.main.models.dtos.Campaign;
import dev.wsgroup.main.models.dtos.CartProduct;
import dev.wsgroup.main.models.dtos.Product;
import dev.wsgroup.main.models.dtos.Supplier;
import dev.wsgroup.main.models.navigationAdapters.NavigationAdapter;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.ObjectSerializer;
import dev.wsgroup.main.views.activities.CartActivity;
import dev.wsgroup.main.views.activities.MainActivity;
import dev.wsgroup.main.views.activities.account.SignInActivity;
import dev.wsgroup.main.views.dialogbox.DialogBoxOrderDetail;
import dev.wsgroup.main.views.fragments.tabs.product.DetailTab;
import dev.wsgroup.main.views.fragments.tabs.product.ReviewTab;

public class ProductDetailActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabLayout.Tab tabCommon;
    private ImageView imgProductDetailMessage, imgBackFromProductDetail, imgProductDetailHome;
    private TextView  txtProductDetailCartCount;
    private CardView cardViewProductDetailCartCount;
    private ConstraintLayout constraintLayoutShoppingCart, layoutLoading,layoutMainLayout;
    private Button btnPurchaseProduct;

    private SharedPreferences sharedPreferences;
    private String userId, productId;
    private int cartCount;
    private Product product;
    private List<Supplier> supplierRetailList, supplierCampaignList;
    private HashMap<String, List<CartProduct>> retailCart, campaignCart;
    private List<CartProduct> cartProductList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        this.getSupportActionBar().hide();
        {
            tabLayout = findViewById(R.id.productTabLayout);
            viewPager = findViewById(R.id.productViewPager);
            imgProductDetailMessage = findViewById(R.id.imgProductDetailMessage);
            imgBackFromProductDetail = findViewById(R.id.imgBackFromProductDetail);
            imgProductDetailHome = findViewById(R.id.imgProductDetailHome);
            btnPurchaseProduct = findViewById(R.id.btnPurchaseProduct);
            txtProductDetailCartCount = findViewById(R.id.txtProductDetailCartCount);
            cardViewProductDetailCartCount = findViewById(R.id.cardViewProductDetailCartCount);
            constraintLayoutShoppingCart = findViewById(R.id.constraintLayoutShoppingCart);
            layoutLoading = findViewById(R.id.layoutLoading);
            layoutMainLayout = findViewById(R.id.layoutMainLayout);
        }

        sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("USER_ID", "");
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
                if(userId.isEmpty()) {
                    Intent signInIntent = new Intent(getApplicationContext(), SignInActivity.class);
                    startActivityForResult(signInIntent, IntegerUtils.REQUEST_LOGIN_FOR_CART);
                } else {
                    Intent cartIntent = new Intent(getApplicationContext(), CartActivity.class);
                    startActivityForResult(cartIntent, IntegerUtils.REQUEST_COMMON);
                }
            }
        });

        btnPurchaseProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                product.setCampaign(null);
                startDialogBoxProductDetail();
            }
        });
    }

    private void loadProduct() {
        layoutLoading.setVisibility(View.VISIBLE);
        layoutMainLayout.setVisibility(View.INVISIBLE);
        APIProductCaller.getProductById(productId, getApplication(), new APIListener() {
            @Override
            public void onProductFound(Product foundProduct) {
                product = foundProduct;
                APICampaignCaller.getCampaignListByProductId(productId, "active", null,
                                                    getApplication(), new APIListener() {
                    @Override
                    public void onCampaignListFound(List<Campaign> campaignList) {
                        super.onCampaignListFound(campaignList);
                        product.setCampaign(null);
                        product.setCampaignList(campaignList);
                        getIntent().putExtra("PRODUCT", foundProduct);
                        setupTabLayout();
                    }
                    @Override
                    public void onNoJSONFound() {
                        super.onNoJSONFound();
                        product.setCampaign(null);
                        product.setCampaignList(new ArrayList<Campaign>());
                        getIntent().putExtra("PRODUCT", foundProduct);
                        setupTabLayout();
                    }
                });
            }
        });
    }

    private void startDialogBoxProductDetail() {
        if(userId.isEmpty()) {
            Intent SignInIntent = new Intent(getApplicationContext(), SignInActivity.class);
            startActivityForResult(SignInIntent, IntegerUtils.REQUEST_LOGIN);
        } else {
            Intent campaignSelectIntent = new Intent(getApplicationContext(), PrepareProductActivity.class);
            campaignSelectIntent.putExtra("PRODUCT", product);
            startActivityForResult(campaignSelectIntent, IntegerUtils.REQUEST_MAKE_PURCHASE);
//            DialogBoxOrderDetail dialogBox = new DialogBoxOrderDetail(ProductDetailActivity.this, getApplicationContext(), userId, product) {
//                @Override
//                public void onCartProductAdded(CartProduct cartProduct) {
//                    super.onCartProductAdded(cartProduct);
//                    cartProduct.setSelectableFlag(true);
//                    Supplier supplier = cartProduct.getProduct().getSupplier();
//                    cartProductList = shoppingCart.get(supplier.getId());
//                    if(cartProductList == null) {
//                        supplierList.add(supplier);
//                        cartProductList = new ArrayList<>();
//                        cartProductList.add(cartProduct);
//                    } else {
//                        int cartProductPosition = checkDuplicateCartProduct(cartProduct.getId());
//                        if(cartProductPosition >= 0) {
//                            cartProductList.get(cartProductPosition).setQuantity(cartProduct.getQuantity());
//                        } else {
//                            cartProductList.add(cartProduct);
//                        }
//                    }
//                    shoppingCart.put(supplier.getId(), cartProductList);
//                    try {
//                        sharedPreferences.edit()
//                                .putString("SHOPPING_CART", ObjectSerializer.serialize((Serializable) shoppingCart))
//                                .commit();
//                        sharedPreferences.edit()
//                                .putString("SUPPLIER_LIST", ObjectSerializer.serialize((Serializable) supplierList))
//                                .commit();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    editShoppingCart();
//                }
//            };
//            dialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            dialogBox.show();
        }
    }

    private int checkDuplicateCartProduct(String id) {
        for (int i = 0; i < cartProductList.size(); i++) {
            if(cartProductList.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
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
        tabLayout.setTabTextColors(getResources().getColor(R.color.black), getResources().getColor(R.color.black));

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
            retailCart = (HashMap<String, List<CartProduct>>) ObjectSerializer
                    .deserialize(sharedPreferences.getString("RETAIL_CART", ""));
            campaignCart = (HashMap<String, List<CartProduct>>) ObjectSerializer
                    .deserialize(sharedPreferences.getString("CAMPAIGN_CART", ""));
            supplierRetailList = (ArrayList<Supplier>) ObjectSerializer
                    .deserialize(sharedPreferences.getString("SUPPLIER_RETAIL_LIST", ""));
            supplierCampaignList = (ArrayList<Supplier>) ObjectSerializer
                    .deserialize(sharedPreferences.getString("SUPPLIER_CAMPAIGN_LIST", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void editShoppingCart() {
        getShoppingCart();
        if(supplierRetailList == null && supplierCampaignList == null) {
            cardViewProductDetailCartCount.setVisibility(View.INVISIBLE);
        } else {
            if (supplierRetailList.size() > 0 || supplierCampaignList.size() > 0) {
                cartCount = 0;
                cardViewProductDetailCartCount.setVisibility(View.VISIBLE);
                if (supplierRetailList.size() > 0) {
                    cartCount += getRetailCartCount();
                }
                if (supplierCampaignList.size() > 0) {
                    cartCount += getCampaignCartCount();
                }
                txtProductDetailCartCount.setText(cartCount + "");
            } else {
                cardViewProductDetailCartCount.setVisibility(View.INVISIBLE);
            }
        }
    }

    private int getRetailCartCount() {
        int count = 0;
        for (Supplier supplier : supplierRetailList) {
            cartProductList = retailCart.get(supplier.getId());
            count += cartProductList.size();
        }
        return count;
    }

    private int getCampaignCartCount() {
        int count = 0;
        for (Supplier supplier : supplierCampaignList) {
            cartProductList = campaignCart.get(supplier.getId());
            count += cartProductList.size();
        }
        return count;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("USER_ID", "");
        loadProduct();
        editShoppingCart();
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