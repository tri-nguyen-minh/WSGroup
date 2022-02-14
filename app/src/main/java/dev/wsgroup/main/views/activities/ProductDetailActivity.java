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
    private ConstraintLayout constraintLayoutShoppingCart;
    private Button btnPurchaseProduct;

    private SharedPreferences sharedPreferences;
    private String userId;
    private Product product;
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
            imgProductDetailMessage = findViewById(R.id.imgProductDetailMessage);
            imgBackFromProductDetail = findViewById(R.id.imgBackFromProductDetail);
            imgProductDetailHome = findViewById(R.id.imgProductDetailHome);
            btnPurchaseProduct = findViewById(R.id.btnPurchaseProduct);
            txtProductDetailCartCount = findViewById(R.id.txtProductDetailCartCount);
            cardViewProductDetailCartCount = findViewById(R.id.cardViewProductDetailCartCount);
            constraintLayoutShoppingCart = findViewById(R.id.constraintLayoutShoppingCart);
        }

        sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("USER_ID", "");
        product = (Product)getIntent().getSerializableExtra("PRODUCT");

//        setProduct();
//
//        getDiscountList();
//        RecViewDiscountSimpleAdapter adapter = new RecViewDiscountSimpleAdapter(getApplicationContext(), activity, userId);
//        adapter.setDiscountList(discountList);
//        recViewDiscountSimple.setAdapter(adapter);
//        recViewDiscountSimple.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));

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
                startDialogBoxProductDetail();
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
                    cartProduct.setSelectableFlag(true);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
            userId = sharedPreferences.getString("USER_ID", "");
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

    private void getShoppingCart() {
        try {
            sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
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