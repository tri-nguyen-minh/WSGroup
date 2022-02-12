package dev.wsgroup.main.views.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APICampaignCaller;
import dev.wsgroup.main.models.apis.callers.APICartCaller;
import dev.wsgroup.main.models.dtos.Campaign;
import dev.wsgroup.main.models.dtos.CartProduct;
import dev.wsgroup.main.models.dtos.Supplier;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewCartSupplierListAdapter;
import dev.wsgroup.main.models.utils.ObjectSerializer;

public class CartActivity extends AppCompatActivity {

    private ImageView imgBackFromCart, imgCartHome;
    private RecyclerView recViewCartSuppliers;
    private LinearLayout layoutNoShoppingCart;
    private ConstraintLayout layoutCartDetails;

    private String userId;
    private Activity activity;
    private SharedPreferences sharedPreferences;
    private RecViewCartSupplierListAdapter adapter;
    private List<CartProduct> productList;
    private List<Supplier> supplierList;
    private HashMap<String, List<CartProduct>> shoppingCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        this.getSupportActionBar().hide();

        imgBackFromCart = findViewById(R.id.imgBackFromCart);
        imgCartHome = findViewById(R.id.imgCartHome);
        recViewCartSuppliers = findViewById(R.id.recViewCartSuppliers);
        layoutNoShoppingCart = findViewById(R.id.layoutNoShoppingCart);
        layoutCartDetails = findViewById(R.id.layoutCartDetails);

        sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("USER_ID", "");
        activity = this;

        setUpShoppingCart(sharedPreferences.getString("TOKEN", ""));
        try {
            supplierList = (ArrayList<Supplier>) ObjectSerializer
                    .deserialize(sharedPreferences.getString("SUPPLIER_LIST", ""));
            shoppingCart = (HashMap<String, List<CartProduct>>) ObjectSerializer
                    .deserialize(sharedPreferences.getString("SHOPPING_CART", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (supplierList != null) {
            if(supplierList.size() != 0) {
                setupRecViewCartSupplierList();
            } else {
                setupEmptyShoppingCart();
            }
        } else {
            setupEmptyShoppingCart();
        }

//        btnCheckout.setEnabled(false);
//        btnCheckout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DialogBoxConfirm dialogBoxConfirm = new DialogBoxConfirm(activity, StringUtils.MES_CONFIRM_CHECKOUT) {
//                    @Override
//                    public void onYesClicked() {
//                        super.onYesClicked();
//                        List<Supplier> orderSupplierList = new ArrayList<>();
//                        HashMap<String, List<OrderProduct>> checkoutList = new HashMap<>();
//                        List<OrderProduct> orderProductList = new ArrayList<>();
//                        for (Supplier supplier : supplierList) {
//                            orderProductList = getSelectedCartProduct(supplier.getId());
//                            if(productList.size() > 0) {
//                                orderSupplierList.add(supplier);
//                                checkoutList.put(supplier.getId(), orderProductList);
//                            }
//                        }
//                        Intent checkoutActivity = new Intent(getApplicationContext(), ConfirmActivity.class);
//                        checkoutActivity.putExtra("SUPPLIER_LIST", (Serializable) orderSupplierList);
//                        checkoutActivity.putExtra("CHECKOUT_LIST", (Serializable) checkoutList);
//                        startActivityForResult(checkoutActivity, IntegerUtils.REQUEST_COMMON);
//                    }
//                };
//                dialogBoxConfirm.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                dialogBoxConfirm.show();
//            }
//        });

        imgBackFromCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });

        imgCartHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }

    private void setupEmptyShoppingCart() {
        layoutNoShoppingCart.setVisibility(View.VISIBLE);
        layoutCartDetails.setVisibility(View.GONE);
    }

    private void setupRecViewCartSupplierList() {
        layoutNoShoppingCart.setVisibility(View.GONE);
        layoutCartDetails.setVisibility(View.VISIBLE);
        adapter =
                new RecViewCartSupplierListAdapter(getApplicationContext(), CartActivity.this) {
                    @Override
                    public void onRemoveSupplier(int position) {
                        super.onRemoveSupplier(position);
                        String supplierId = supplierList.get(position).getId();
                        supplierList.remove(position);
                        shoppingCart.remove(supplierId);
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
                        if(supplierList.size() == 0) {
                            setupEmptyShoppingCart();
                        } else {
                            setupRecViewCartSupplierList();
                        }
                    }
                };
        adapter.setCartList(supplierList, shoppingCart);
        recViewCartSuppliers.setAdapter(adapter);
        recViewCartSuppliers.setLayoutManager(new LinearLayoutManager(CartActivity.this, RecyclerView.VERTICAL,false));
    }

    private void setUpShoppingCart(String token) {
        APICartCaller.getCartList(token, getApplication(), new APIListener() {
            @Override
            public void onCartListFound(HashMap<String, List<CartProduct>> shoppingCart, List<Supplier> supplierList) {
                super.onCartListFound(shoppingCart, supplierList);
                List<String> productIdList = new ArrayList<>();
                for (Supplier supplier : supplierList) {
                    productList = shoppingCart.get(supplier.getId());
                    for (CartProduct product : productList) {
                        productIdList.add(product.getProduct().getProductId());
                    }
                }
                APICampaignCaller.getCampaignListByProductId(productIdList,null, getApplication(), new APIListener() {
                    @Override
                    public void onCampaignListFound(List<Campaign> campaignList) {
                        super.onCampaignListFound(campaignList);
                        for (Campaign campaign : campaignList) {
                            for (Supplier supplier : supplierList) {
                                productList = shoppingCart.get(supplier.getId());
                                for (CartProduct product : productList) {
                                    if(product.getProduct().getProductId().equals(campaign.getProductId())) {
                                        product.getProduct().setCampaign(campaign);
                                    }
                                }
                            }
                        }
                        putShoppingCartToSession(shoppingCart, supplierList);
                    }

                    @Override
                    public void onNoCampaignFound() {
                        super.onNoCampaignFound();
                        putShoppingCartToSession(shoppingCart, supplierList);
                    }
                });
            }
            @Override
            public void onFailedAPICall(int code) {
                super.onFailedAPICall(code);
                shoppingCart = new HashMap<>();
                supplierList = new ArrayList<>();
                putShoppingCartToSession(shoppingCart, supplierList);
            }
        });
    }

    private void putShoppingCartToSession(HashMap<String, List<CartProduct>> shoppingCart, List<Supplier> supplierList) {
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            supplierList = (ArrayList<Supplier>) ObjectSerializer
                    .deserialize(sharedPreferences.getString("SUPPLIER_LIST", ""));
            shoppingCart = (HashMap<String, List<CartProduct>>) ObjectSerializer
                    .deserialize(sharedPreferences.getString("SHOPPING_CART", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (supplierList != null) {
            if(supplierList.size() != 0) {
                setupRecViewCartSupplierList();
            } else {
                setupEmptyShoppingCart();
            }
        } else {
            setupEmptyShoppingCart();
        }
    }
}