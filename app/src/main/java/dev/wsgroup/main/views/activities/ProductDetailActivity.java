package dev.wsgroup.main.views.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.Campaign;
import dev.wsgroup.main.models.dtos.CartProduct;
import dev.wsgroup.main.models.dtos.Discount;
import dev.wsgroup.main.models.dtos.Product;
import dev.wsgroup.main.models.dtos.Supplier;
import dev.wsgroup.main.models.navigationAdapters.NavigationAdapter;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewDiscountSimpleAdapter;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.ObjectSerializer;
import dev.wsgroup.main.views.activities.account.SignInActivity;
import dev.wsgroup.main.views.boxes.DialogBoxOrderDetail;
import dev.wsgroup.main.views.fragments.tabs.product.DetailTab;
import dev.wsgroup.main.views.fragments.tabs.product.ReviewTab;

public class ProductDetailActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabLayout.Tab tabCommon;

    private ViewFlipper viewFlipperProduct;
    private ImageView imgProductDetailMessage, imgBackFromProductDetail, imgProductDetailHome, imgCommonFlipper;
    private Button btnPurchaseProduct;
    private TextView txtProductName, txtProductOrderCount, txtProductReviewCount,
            txtRatingProduct, txtRetailPriceORG, txtRetailPrice, txtProductDetailCartCount;
    private RatingBar ratingProduct;
    private CardView cardViewProductDetailCartCount;
    private ConstraintLayout constraintLayoutShoppingCart, constraintLayoutCampaign;
    private RecyclerView recViewDiscountSimple;
    private TextView txtCampaignDescription, txtCampaignPrice, txtCampaignQuantity, txtDiscountEndDate,
            txtCampaignOrderCount, txtCampaignQuantityCount, txtCampaignQuantityBar,
            lblCampaignOrderCount, lblCampaignQuantitySeparator;
    private ProgressBar progressBarQuantityCount;

    private SharedPreferences sharedPreferences;
    private String userId;
    private Product product;
    private Campaign campaign;
    private Activity activity;
    private List<Discount> discountList;
    private List<CartProduct> cartProductList;
    private HashMap<String, List<CartProduct>> shoppingCart;
    private List<Supplier> supplierList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        this.getSupportActionBar().hide();
        {
            tabLayout = findViewById(R.id.productTabLayout);
            viewPager = findViewById(R.id.productViewPager);
            viewFlipperProduct = findViewById(R.id.viewFlipperProduct);
            imgProductDetailMessage = findViewById(R.id.imgProductDetailMessage);
            imgBackFromProductDetail = findViewById(R.id.imgBackFromProductDetail);
            imgProductDetailHome = findViewById(R.id.imgProductDetailHome);
            btnPurchaseProduct = findViewById(R.id.btnPurchaseProduct);
            txtProductName = findViewById(R.id.txtProductName);
            txtProductOrderCount = findViewById(R.id.txtProductOrderCount);
            txtProductReviewCount = findViewById(R.id.txtProductReviewCount);
            txtRatingProduct = findViewById(R.id.txtRatingProduct);
            txtRetailPriceORG = findViewById(R.id.txtProductRetailPriceORG);
            txtRetailPrice = findViewById(R.id.txtProductRetailPrice);
            txtProductDetailCartCount = findViewById(R.id.txtProductDetailCartCount);
            ratingProduct = findViewById(R.id.ratingProduct);
            cardViewProductDetailCartCount = findViewById(R.id.cardViewProductDetailCartCount);
            constraintLayoutShoppingCart = findViewById(R.id.constraintLayoutShoppingCart);
            constraintLayoutCampaign = findViewById(R.id.constraintLayoutCampaign);
            recViewDiscountSimple = findViewById(R.id.recViewDiscountSimple);
            txtCampaignDescription = findViewById(R.id.txtCampaignDescription);
            txtCampaignPrice = findViewById(R.id.txtCampaignPrice);
            txtCampaignQuantity = findViewById(R.id.txtCampaignQuantity);
            txtDiscountEndDate = findViewById(R.id.txtDiscountEndDate);
            txtCampaignOrderCount = findViewById(R.id.txtCampaignOrderCount);
            txtCampaignQuantityCount = findViewById(R.id.txtCampaignQuantityCount);
            txtCampaignQuantityBar = findViewById(R.id.txtCampaignQuantityBar);
            lblCampaignOrderCount = findViewById(R.id.lblCampaignOrderCount);
            lblCampaignQuantitySeparator = findViewById(R.id.lblCampaignQuantitySeparator);
            progressBarQuantityCount = findViewById(R.id.progressBarQuantityCount);
        }

        activity = this;

        sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("USER_ID", "");

        product = (Product)getIntent().getSerializableExtra("PRODUCT");
        campaign = product.getCampaign();
        setProduct();

        getDiscountList();
        RecViewDiscountSimpleAdapter adapter = new RecViewDiscountSimpleAdapter(getApplicationContext(), activity, userId);
        adapter.setDiscountList(discountList);
        recViewDiscountSimple.setAdapter(adapter);
        recViewDiscountSimple.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));

        setupTabLayout();
        editShoppingCart();

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

        btnPurchaseProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDialogBoxProductDetail();
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
    }

    private void startDialogBoxProductDetail() {
        if(userId.isEmpty()) {
            Intent SignInIntent = new Intent(getApplicationContext(), SignInActivity.class);
            startActivityForResult(SignInIntent, IntegerUtils.REQUEST_LOGIN);
        } else {
            DialogBoxOrderDetail dialogBox = new DialogBoxOrderDetail(ProductDetailActivity.this, getApplicationContext(), userId, product) {
                @Override
                public void onCartProductAdded(CartProduct cartProduct) {
                    super.onCartProductAdded(cartProduct);
                    Supplier supplier = cartProduct.getProduct().getSupplier();
                    cartProductList = shoppingCart.get(supplier.getId());
                    if(cartProductList == null) {
                        supplierList.add(supplier);
                        cartProductList = new ArrayList<>();
                        cartProductList.add(cartProduct);
                    } else {
                        int cartProductPosition = checkDuplicateCartProduct(cartProduct.getId());
                        if(cartProductPosition >= 0) {
                            cartProductList.get(cartProductPosition).setQuantity(cartProduct.getQuantity());
                        } else {
                            cartProductList.add(cartProduct);
                        }
                    }
                    shoppingCart.put(supplier.getId(), cartProductList);
                    try {
                        sharedPreferences.edit()
                                .putString("SHOPPING_CART", ObjectSerializer.serialize((Serializable) shoppingCart))
                                .commit();
                        sharedPreferences.edit()
                                .putString("SUPPLIER_LIST", ObjectSerializer.serialize((Serializable) supplierList))
                                .commit();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    editShoppingCart();
                }
            };
            dialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogBox.show();
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

    private String getOrderCountByProductId(String productId) {
        int count = 12000;
//        API get total order count by productId
        return MethodUtils.convertOrderOrReviewCount(count);
    }

    private String getReviewCountByProductId(String productId) {
        int count = 12000;
//        API get total review count by productId
        return MethodUtils.convertOrderOrReviewCount(count);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
            userId = sharedPreferences.getString("USER_ID", "");
            try {
                supplierList = (ArrayList<Supplier>) ObjectSerializer
                        .deserialize(sharedPreferences.getString("SUPPLIER_LIST", ""));
                shoppingCart = (HashMap<String, List<CartProduct>>) ObjectSerializer
                        .deserialize(sharedPreferences.getString("SHOPPING_CART", ""));
            } catch (Exception e) {
                e.printStackTrace();
            }
            editShoppingCart();
            if(requestCode == IntegerUtils.REQUEST_LOGIN_FOR_CART) {
                Intent cartIntent = new Intent(getApplicationContext(), CartActivity.class);
                startActivityForResult(cartIntent, IntegerUtils.REQUEST_COMMON);
            }
        }
    }

    private void setupTabLayout() {
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
    }


    private void setProduct() {
//        work with discount
        txtRetailPriceORG.setVisibility(View.GONE);

        txtProductName.setText(product.getName());
        txtProductOrderCount.setText(getOrderCountByProductId(product.getProductId()));
        txtProductReviewCount.setText(getReviewCountByProductId(product.getProductId()));

        txtRatingProduct.setText(product.getRating() + "");
        txtRetailPrice.setText(MethodUtils.convertPriceString(product.getRetailPrice()));
        ratingProduct.setRating((float)product.getRating());

        if(product.getImageList() != null) {
            for (String imageLink : product.getImageList()) {
                imgCommonFlipper = new ImageView(getApplicationContext());
                Glide.with(getApplicationContext()).load(imageLink).into(imgCommonFlipper);
                viewFlipperProduct.addView(imgCommonFlipper);
            }
            imgCommonFlipper = new ImageView(getApplicationContext());
            imgCommonFlipper.setImageResource(R.drawable.img_unavailable);
            viewFlipperProduct.addView(imgCommonFlipper);
            viewFlipperProduct.setAutoStart(true);
            viewFlipperProduct.setFlipInterval(4000);
            viewFlipperProduct.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_slide_in_left));
            viewFlipperProduct.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_slide_out_left));
            viewFlipperProduct.startFlipping();
        }
        if(campaign != null) {
            if(!campaign.getStatus().equals("active")) {
                txtCampaignDescription.setText("Upcoming Campaign");
            } else {
                txtCampaignDescription.setText("Ongoing Campaign");
            }
            constraintLayoutCampaign.setVisibility(View.VISIBLE);
            txtCampaignPrice.setText(MethodUtils.convertPriceString(campaign.getPrice()));
            txtDiscountEndDate.setText(MethodUtils.formatDate(campaign.getEndDate()));
            txtCampaignQuantity.setText(campaign.getQuantity() + "");
            txtCampaignQuantityBar.setText(campaign.getQuantity() + "");
            txtCampaignOrderCount.setText(campaign.getOrderCount() + "");
            txtCampaignQuantityCount.setText(campaign.getQuantityCount() + "");
            progressBarQuantityCount.setMax(campaign.getQuantity());
            progressBarQuantityCount.setProgress(campaign.getQuantityCount());
            if(campaign.getOrderCount() > 1) {
                lblCampaignOrderCount.setText("waiting orders");
            } else {
                lblCampaignOrderCount.setText("waiting order");
            }
            lblCampaignQuantitySeparator.setText("/");
        } else {
            constraintLayoutCampaign.setVisibility(View.GONE);
        }
    }

    private void getDiscountList() {

//        call API discount list

        discountList = new ArrayList<>();
        Discount discount = new Discount();
        discount.setId("1");
        discount.setSupplierId("testing");
        discount.setCode("111111");
        discount.setDescription("Special discount for orders of more than 50 items");
        discount.setDiscountPrice(25000);
        discount.setEndDate("2022-01-24T00:00:00.000Z");
        discount.setMinPrice(300000);
        discount.setMinQuantity(10);
        discount.setStatus(true);
        discountList.add(discount);
        discount = new Discount();
        discount.setId("2");
        discount.setSupplierId("testing");
        discount.setCode("111213");
        discount.setDescription("20K off for orders above 500.000");
        discount.setDiscountPrice(20000);
        discount.setEndDate("2022-03-01T00:00:00.000Z");
        discount.setMinPrice(500000);
        discount.setStatusByString("ready");
        discountList.add(discount);
        discount = new Discount();
        discount.setId("3");
        discount.setSupplierId("testing");
        discount.setCode("533241");
        discount.setDescription("Spring discount for all orders");
        discount.setDiscountPrice(5000);
        discount.setEndDate("2022-02-24T00:00:00.000Z");
        discount.setMinQuantity(5);
        discount.setStatusByString("unready");
        discountList.add(discount);
        discount = new Discount();
        discount.setId("4");
        discount.setSupplierId("testing");
        discount.setCode("098123");
        discount.setDescription("another discount for all orders");
        discount.setDiscountPrice(25000);
        discount.setEndDate("2022-01-12T00:00:00.000Z");
        discount.setStatus(false);
        discountList.add(discount);
    }

    private void getCampaignList() {
//        call API get campaign
//
//        campaignList = new ArrayList<>();
//        Campaign campaign = new Campaign();
//        campaign.setId("1");
//        campaign.setSupplierId(product.getSupplierId());
//        campaign.setProductId(product.getProductId());
//        campaign.setCode("code1");
//        campaign.setStartDate("2022-08-01");
//        campaign.setEndDate("2022-12-15");
//        campaign.setQuantity(200);
//        campaign.setPrice(20000);
//        campaign.setOrderCount(12);
//        campaign.setQuantityCount(156);
//        campaign.setStatusByString("active");
//        campaignList.add(campaign);
//        campaign = new Campaign();
//        campaign.setId("2");
//        campaign.setSupplierId(product.getSupplierId());
//        campaign.setProductId(product.getProductId());
//        campaign.setCode("code2");
//        campaign.setStartDate("2022-04-01");
//        campaign.setEndDate("2021-03-01");
//        campaign.setQuantity(100);
//        campaign.setPrice(12000);
//        campaign.setOrderCount(5);
//        campaign.setQuantityCount(56);
//        campaign.setStatus(true);
//        campaignList.add(campaign);
    }


    private void getShoppingCart() {
        try {
            supplierList = (ArrayList<Supplier>) ObjectSerializer
                    .deserialize(sharedPreferences.getString("SUPPLIER_LIST", ""));
            shoppingCart = (HashMap<String, List<CartProduct>>) ObjectSerializer
                    .deserialize(sharedPreferences.getString("SHOPPING_CART", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void editShoppingCart() {
        getShoppingCart();
        int cartProductCount = 0;
        if(supplierList == null) {
            cardViewProductDetailCartCount.setVisibility(View.INVISIBLE);
        } else {
            for (Supplier supplier : supplierList) {
                cartProductList = shoppingCart.get(supplier.getId());
                for (CartProduct product : cartProductList) {
                    cartProductCount++;
                }
            }
            if (cartProductCount > 0) {
                cardViewProductDetailCartCount.setVisibility(View.VISIBLE);
                txtProductDetailCartCount.setText(cartProductCount + "");
            } else {
                cardViewProductDetailCartCount.setVisibility(View.INVISIBLE);
            }
        }
    }

}