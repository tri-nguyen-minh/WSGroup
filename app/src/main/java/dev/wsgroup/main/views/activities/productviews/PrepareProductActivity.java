package dev.wsgroup.main.views.activities.productviews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APICartCaller;
import dev.wsgroup.main.models.dtos.Campaign;
import dev.wsgroup.main.models.dtos.CartProduct;
import dev.wsgroup.main.models.dtos.Order;
import dev.wsgroup.main.models.dtos.OrderProduct;
import dev.wsgroup.main.models.dtos.Product;
import dev.wsgroup.main.models.dtos.Supplier;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.ObjectSerializer;
import dev.wsgroup.main.models.utils.StringUtils;
import dev.wsgroup.main.views.activities.MainActivity;
import dev.wsgroup.main.views.activities.ordering.ConfirmActivity;
import dev.wsgroup.main.views.dialogbox.DialogBoxAlert;
import dev.wsgroup.main.views.dialogbox.DialogBoxConfirm;
import dev.wsgroup.main.views.dialogbox.DialogBoxLoading;

public class PrepareProductActivity extends AppCompatActivity {

    private ConstraintLayout layoutParent, layoutSelectCampaign;
    private ImageView imgBackFromPrepareProduct, imgPrepareProductMessage,
                imgPrepareProductHome, imgProduct, imgProductQuantityMinus, imgProductQuantityPlus;
    private EditText editProductQuantity;
    private TextView txtProductName, txtNumberInStorage, txtProductPriceORG,
                    txtProductPrice, txtTotalPrice, txtPricingDescription, txtCampaignTag;
    private Button btnConfirmAddToCart, btnConfirmPurchase;

    private Product product;
    private Campaign campaign;
    private SharedPreferences sharedPreferences;
    private String cartTag, supplierListTag;
    private int quantity, minQuantity, maxQuantity;
    private double price, totalPrice;
    private DialogBoxLoading dialogBoxLoading;
    private List<Supplier> supplierList;
    private HashMap<String, List<CartProduct>> cart;
    private List<CartProduct> cartProductList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepare_product);
        this.getSupportActionBar().hide();

        layoutParent = findViewById(R.id.layoutParent);
        layoutSelectCampaign = findViewById(R.id.layoutSelectCampaign);
        imgBackFromPrepareProduct = findViewById(R.id.imgBackFromPrepareProduct);
        imgPrepareProductMessage = findViewById(R.id.imgPrepareProductMessage);
        imgPrepareProductHome = findViewById(R.id.imgPrepareProductHome);
        imgProduct = findViewById(R.id.imgProduct);
        imgProductQuantityMinus = findViewById(R.id.imgProductQuantityMinus);
        imgProductQuantityPlus = findViewById(R.id.imgProductQuantityPlus);
        editProductQuantity = findViewById(R.id.editProductQuantity);
        txtProductName = findViewById(R.id.txtProductName);
        txtNumberInStorage = findViewById(R.id.txtNumberInStorage);
        txtProductPriceORG = findViewById(R.id.txtProductPriceORG);
        txtProductPrice = findViewById(R.id.txtProductPrice);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);
        txtPricingDescription = findViewById(R.id.txtPricingDescription);
        txtCampaignTag = findViewById(R.id.txtCampaignTag);
        btnConfirmAddToCart = findViewById(R.id.btnConfirmAddToCart);
        btnConfirmPurchase = findViewById(R.id.btnConfirmPurchase);

        sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        product = (Product) getIntent().getSerializableExtra("PRODUCT");
        if (product != null) {
            txtProductName.setText(product.getName());
            if (product.getImageList().size() > 0) {
                Glide.with(getApplicationContext())
                     .load(product.getImageList().get(0))
                     .into(imgProduct);
            }
            txtProductPriceORG.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            setupPurchasePriceAndQuantity();

        }

        imgBackFromPrepareProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        imgPrepareProductHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        layoutParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editProductQuantity.hasFocus()) {
                    editProductQuantity.clearFocus();
                }
            }
        });

        imgProductQuantityMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantityCount = Integer.parseInt(editProductQuantity.getText().toString()) - 1;
                editProductQuantity.setText(quantityCount + "");
            }
        });

        imgProductQuantityPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantityCount = Integer.parseInt(editProductQuantity.getText().toString()) + 1;
                editProductQuantity.setText(quantityCount + "");
            }
        });

        editProductQuantity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        editProductQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    quantity = Integer.parseInt(editProductQuantity.getText().toString());
                    if (quantity < minQuantity) {
                        editProductQuantity.setText(minQuantity + "");
                    } else if (quantity > maxQuantity) {
                        editProductQuantity.setText(maxQuantity + "");
                    } else {
                        setupQuantityButtons();
                        calculateTotalPrice();
                    }
                } catch (Exception e) {
                    editProductQuantity.setText(minQuantity + "");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        layoutSelectCampaign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent campaignSelectIntent = new Intent(getApplicationContext(), CampaignListActivity.class);
                campaignSelectIntent.putExtra("PRODUCT", product);
                startActivityForResult(campaignSelectIntent, IntegerUtils.REQUEST_SELECT_CAMPAIGN);
            }
        });

        btnConfirmAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBoxLoading = new DialogBoxLoading(PrepareProductActivity.this);
                dialogBoxLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBoxLoading.show();
                cartTag = (campaign != null) ? "CAMPAIGN_CART" : "RETAIL_CART";
                supplierListTag = (campaign != null) ? "SUPPLIER_CAMPAIGN_LIST" : "SUPPLIER_RETAIL_LIST";
                try {
                    cart = (HashMap<String, List<CartProduct>>) ObjectSerializer
                            .deserialize(sharedPreferences.getString(cartTag, ""));
                    supplierList = (ArrayList<Supplier>) ObjectSerializer
                            .deserialize(sharedPreferences.getString(supplierListTag, ""));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                CartProduct cartProduct = getCartProduct();
                if (supplierList == null) {
                    addCartProduct(cartProduct);
                } else if (supplierList.size() == 0) {
                    addCartProduct(cartProduct);
                } else {
                    boolean duplicateProduct = false;
                    cartProductList = cart.get(product.getSupplier().getId());
                    if (cartProductList != null) {
                        for (CartProduct cProduct : cartProductList) {
                            if (cProduct.getProduct().getProductId().equals(product.getProductId())) {
                                cartProduct.setId(cProduct.getId());
                                duplicateProduct = true;
                            }
                        }
                        if (duplicateProduct) {
                            if (dialogBoxLoading.isShowing()) {
                                dialogBoxLoading.dismiss();
                            }
                            DialogBoxConfirm confirmLogoutBox = new DialogBoxConfirm(PrepareProductActivity.this,
                                    StringUtils.MES_CONFIRM_DUPLICATE_CART_PRODUCT) {
                                @Override
                                public void onYesClicked() {
                                    dialogBoxLoading.show();
                                    updateCartProduct(cartProduct);
                                }
                            };
                            confirmLogoutBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            confirmLogoutBox.show();
                        } else {
                            addCartProduct(cartProduct);
                        }
                    } else {
                        addCartProduct(cartProduct);
                    }
                }
            }
        });

        btnConfirmPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogBoxConfirm dialogBoxConfirm = new DialogBoxConfirm(PrepareProductActivity.this,
                        StringUtils.MES_CONFIRM_IMMEDIATE_CHECKOUT) {
                    @Override
                    public void onYesClicked() {
                        super.onYesClicked();
                        dismiss();
                        startInstantOrder();
                    }
                };
                dialogBoxConfirm.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBoxConfirm.show();
            }
        });
    }

    private void startInstantOrder() {
        CartProduct cartProduct = getCartProduct();
        int request = cartProduct.getCampaignFlag() ? IntegerUtils.REQUEST_ORDER_CAMPAIGN : IntegerUtils.REQUEST_ORDER_RETAIL;
        ArrayList<Order> ordersList = new ArrayList<>();
        List<OrderProduct> selectedProductList = new ArrayList<>();
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setProduct(cartProduct.getProduct());
        orderProduct.setQuantity(cartProduct.getQuantity());
        orderProduct.setPrice(cartProduct.getProduct().getRetailPrice());
        totalPrice = cartProduct.getQuantity() * cartProduct.getProduct().getRetailPrice();
        orderProduct.setTotalPrice(totalPrice);
        orderProduct.setCartProduct(cartProduct);
        orderProduct.setCampaign(cartProduct.getCampaign());
        selectedProductList.add(orderProduct);
        Order order = new Order();
        order.setCampaign(cartProduct.getCampaign());
        order.setSupplier(product.getSupplier());
        order.setInCart(false);
        order.setOrderProductList(selectedProductList);
        ordersList.add(order);
        Intent confirmOrderIntent = new Intent(getApplicationContext(), ConfirmActivity.class);
        confirmOrderIntent.putExtra("ORDER_LIST", ordersList);
        confirmOrderIntent.putExtra("REQUEST_CODE", request);
        startActivityForResult(confirmOrderIntent, IntegerUtils.REQUEST_COMMON);
    }

    private CartProduct getCartProduct() {
        CartProduct cartProduct = new CartProduct();
        cartProduct.setProduct(product);
        cartProduct.setQuantity(Integer.parseInt(editProductQuantity.getText().toString()));
        cartProduct.setProductType(product.getTypeOfProduct());
        if (campaign != null) {
            cartProduct.setCampaign(campaign);
            cartProduct.setCampaignFlag(true);
        } else {
            cartProduct.setCampaignFlag(false);
        }
        return cartProduct;
    }

    private void addCartProduct(CartProduct cartProduct) {
        APICartCaller.addCartItem(sharedPreferences.getString("TOKEN", ""),
                cartProduct, getApplication(), new APIListener() {
                    @Override
                    public void onAddCartItemSuccessful(CartProduct cartProduct) {
                        super.onAddCartItemSuccessful(cartProduct);
                        cartProductList = cart.get(product.getSupplier().getId());
                        if (cartProductList == null) {
                            supplierList.add(product.getSupplier());
                            cartProductList = new ArrayList<>();
                        }
                        cartProductList.add(cartProduct);
                        cart.put(product.getSupplier().getId(), cartProductList);
                        try {
                            sharedPreferences.edit()
                                    .putString(cartTag, ObjectSerializer.serialize((Serializable) cart))
                                    .putString(supplierListTag, ObjectSerializer.serialize((Serializable) supplierList))
                                    .commit();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        addCartSuccessful();
                    }

                    @Override
                    public void onFailedAPICall(int code) {
                        addCartFailed();
                    }
                });
    }

    private void updateCartProduct(CartProduct cartProduct) {
        APICartCaller.updateCartItem(sharedPreferences.getString("TOKEN", ""), cartProduct,
                getApplication(), new APIListener() {
                    @Override
                    public void onUpdateCartItemSuccessful() {
                        addCartSuccessful();
                    }

                    @Override
                    public void onFailedAPICall(int code) {
                        addCartFailed();
                    }
                });
    }

    private void addCartSuccessful() {
        if (dialogBoxLoading.isShowing()) {
            dialogBoxLoading.dismiss();
        }
        DialogBoxAlert dialogBox =
                new DialogBoxAlert(PrepareProductActivity.this,
                        IntegerUtils.CONFIRM_ACTION_CODE_SUCCESS,
                        StringUtils.MES_SUCCESSFUL_ADD_CART,"") {
                    @Override
                    public void onClickAction() {
                        super.onClickAction();
                        setResult(RESULT_OK);
                        finish();
                    }
                };
        dialogBox.show();
    }

    private void addCartFailed() {
        if (dialogBoxLoading.isShowing()) {
            dialogBoxLoading.dismiss();
        }
        DialogBoxAlert dialogBox =
                new DialogBoxAlert(PrepareProductActivity.this,
                        IntegerUtils.CONFIRM_ACTION_CODE_FAILED,
                        StringUtils.MES_ERROR_FAILED_API_CALL,"");
        dialogBox.show();
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void setClickableQuantityButton(ImageView view, boolean clickable) {
        view.setEnabled(clickable);
        if (clickable) {
            view.setColorFilter(getApplicationContext().getResources().getColor(R.color.gray_dark));
        } else {
            view.setColorFilter(getApplicationContext().getResources().getColor(R.color.gray_light));
        }
    }

    private void setupPurchasePriceAndQuantity() {
        campaign = product.getCampaign();
        setPurchasePrice();
        getRangeQuantity();
        getQuantity();
        editProductQuantity.setText(quantity + "");
        txtNumberInStorage.setText(maxQuantity + "");
        setupQuantityButtons();
    }

    private void getRangeQuantity() {
        maxQuantity = product.getQuantity();
        minQuantity = 1;
        if (campaign == null) {
            for (Campaign ongoingCampaign : product.getCampaignList()) {
                maxQuantity -= ongoingCampaign.getMaxQuantity();
            }
        } else {
            maxQuantity = campaign.getMaxQuantity() - campaign.getQuantityCount();
            if (!campaign.getShareFlag()) {
                minQuantity = campaign.getMinQuantity();
            }
        }
    }

    private void getQuantity() {
        try {
            quantity = Integer.parseInt(editProductQuantity.getText().toString());
            if (quantity < minQuantity) {
                quantity = minQuantity;
            } else if (quantity > maxQuantity) {
                quantity = maxQuantity;
            }
        } catch (Exception e) {
            quantity = minQuantity;
        }
    }

    private void setPurchasePrice() {
        price = product.getRetailPrice();
        if (campaign != null) {
            txtPricingDescription.setText(campaign.getDescription());
            txtCampaignTag.setText(campaign.getShareFlag() ? "Sharing Campaign" : "Single Campaign");
            txtProductPriceORG.setVisibility(View.VISIBLE);
            txtProductPriceORG.setText(MethodUtils.formatPriceString(price));
            price -= campaign.getSavingPrice();
        } else {
            txtPricingDescription.setText("Retail Price");
            txtCampaignTag.setVisibility(View.GONE);
            txtProductPriceORG.setVisibility(View.GONE);
        }
        txtProductPrice.setText(MethodUtils.formatPriceString(price));
    }

    private void calculateTotalPrice() {
        totalPrice = quantity * price;
        txtTotalPrice.setText(MethodUtils.formatPriceString(totalPrice));
    }

    private void setupQuantityButtons() {
        if (quantity == maxQuantity) {
            setClickableQuantityButton(imgProductQuantityMinus, true);
            setClickableQuantityButton(imgProductQuantityPlus, false);
        } else if (quantity == minQuantity) {
            setClickableQuantityButton(imgProductQuantityMinus, false);
            setClickableQuantityButton(imgProductQuantityPlus, true);
        } else {
            setClickableQuantityButton(imgProductQuantityMinus, true);
            setClickableQuantityButton(imgProductQuantityPlus, true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            product.setCampaign((Campaign) data.getSerializableExtra("CAMPAIGN_SELECTED"));
            setupPurchasePriceAndQuantity();
        }
    }
}