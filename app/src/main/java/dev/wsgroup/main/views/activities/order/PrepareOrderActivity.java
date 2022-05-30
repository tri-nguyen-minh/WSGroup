package dev.wsgroup.main.views.activities.order;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APICartCaller;
import dev.wsgroup.main.models.dtos.Campaign;
import dev.wsgroup.main.models.dtos.CampaignMilestone;
import dev.wsgroup.main.models.dtos.CartProduct;
import dev.wsgroup.main.models.dtos.Order;
import dev.wsgroup.main.models.dtos.OrderProduct;
import dev.wsgroup.main.models.dtos.Product;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.StringUtils;
import dev.wsgroup.main.views.activities.MainActivity;
import dev.wsgroup.main.views.activities.productviews.CampaignListActivity;
import dev.wsgroup.main.views.dialogbox.DialogBoxAlert;
import dev.wsgroup.main.views.dialogbox.DialogBoxConfirm;
import dev.wsgroup.main.views.dialogbox.DialogBoxLoading;

public class PrepareOrderActivity extends AppCompatActivity {

    private ConstraintLayout layoutParent, layoutSelectCampaign, layoutBasePrice;
    private LinearLayout layoutCampaign, layoutNextMilestone, layoutCampaignInfo;
    private ImageView imgBackFromPrepareProduct,imgPrepareProductHome, imgProduct,
            imgProductQuantityMinus, imgProductQuantityPlus;
    private EditText editProductQuantity;
    private TextView txtProductName, txtNumberInStorage, txtProductPrice, txtTotalPrice,
            txtPricingDescription, txtCampaignTag, txtCampaignNote, txtCampaignNextMilestone,
            lblCampaignNextMilestone, txtCampaignPrice, txtCampaignQuantityCount,
            txtCampaignQuantityBar, lblQuantityCountSeparator, txtProductPriceORG,
            txtOrderCount, lblOrderCount;
    private Button btnConfirmAddToCart, btnConfirmPurchase;
    private ProgressBar progressBarQuantityCount;

    private SharedPreferences sharedPreferences;
    private String token;
    private Product product;
    private Campaign campaign;
    private CampaignMilestone currentCampaignMilestone;
    private int milestoneQuantity;
    private List<CampaignMilestone> milestoneList;
    private int quantity, minQuantity, maxQuantity;
    private double price, totalPrice;
    private DialogBoxLoading dialogBoxLoading;
    private DialogBoxConfirm dialogBoxConfirm;
    private List<CartProduct> cartList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepare_order);
        this.getSupportActionBar().hide();

        layoutParent = findViewById(R.id.layoutParent);
        layoutSelectCampaign = findViewById(R.id.layoutSelectCampaign);
        layoutBasePrice = findViewById(R.id.layoutBasePrice);
        layoutCampaign = findViewById(R.id.layoutCampaign);
        layoutNextMilestone = findViewById(R.id.layoutNextMilestone);
        layoutCampaignInfo = findViewById(R.id.layoutCampaignInfo);
        imgBackFromPrepareProduct = findViewById(R.id.imgBackFromPrepareProduct);
        imgPrepareProductHome = findViewById(R.id.imgPrepareProductHome);
        imgProduct = findViewById(R.id.imgProduct);
        imgProductQuantityMinus = findViewById(R.id.imgProductQuantityMinus);
        imgProductQuantityPlus = findViewById(R.id.imgProductQuantityPlus);
        editProductQuantity = findViewById(R.id.editProductQuantity);
        txtProductName = findViewById(R.id.txtProductName);
        txtNumberInStorage = findViewById(R.id.txtNumberInStorage);
        txtProductPrice = findViewById(R.id.txtProductPrice);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);
        txtPricingDescription = findViewById(R.id.txtPricingDescription);
        txtCampaignTag = findViewById(R.id.txtCampaignTag);
        txtCampaignNote = findViewById(R.id.txtCampaignNote);
        txtCampaignNextMilestone = findViewById(R.id.txtCampaignNextMilestone);
        lblCampaignNextMilestone = findViewById(R.id.lblCampaignNextMilestone);
        txtCampaignPrice = findViewById(R.id.txtCampaignPrice);
        txtCampaignQuantityCount = findViewById(R.id.txtCampaignQuantityCount);
        txtCampaignQuantityBar = findViewById(R.id.txtCampaignQuantityBar);
        lblQuantityCountSeparator = findViewById(R.id.lblQuantityCountSeparator);
        txtProductPriceORG = findViewById(R.id.txtProductPriceORG);
        txtOrderCount = findViewById(R.id.txtOrderCount);
        lblOrderCount = findViewById(R.id.lblOrderCount);
        btnConfirmAddToCart = findViewById(R.id.btnConfirmAddToCart);
        btnConfirmPurchase = findViewById(R.id.btnConfirmPurchase);
        progressBarQuantityCount = findViewById(R.id.progressBarQuantityCount);

        quantity = 0; minQuantity = 0; maxQuantity = 0;
        product = (Product) getIntent().getSerializableExtra("PRODUCT");
        if (product != null) {
            txtProductName.setText(product.getName());
            if (product.getImageList().size() > 0) {
                Glide.with(getApplicationContext())
                     .load(product.getImageList().get(0))
                     .into(imgProduct);
            }
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
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    MethodUtils.hideKeyboard(view, getApplicationContext());
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
                        checkSharedCampaignStatus();
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
                Intent campaignSelectIntent
                        = new Intent(getApplicationContext(), CampaignListActivity.class);
                campaignSelectIntent.putExtra("PRODUCT", product);
                startActivityForResult(campaignSelectIntent, IntegerUtils.REQUEST_SELECT_CAMPAIGN);
            }
        });

        btnConfirmAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
                token = sharedPreferences.getString("TOKEN", "");
                dialogBoxLoading = new DialogBoxLoading(PrepareOrderActivity.this);
                dialogBoxLoading.getWindow()
                                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBoxLoading.show();
                APICartCaller.getCartList(token, getApplication(), new APIListener() {
                    @Override
                    public void onCartListFound(ArrayList<CartProduct> retailCartProductList,
                                                ArrayList<CartProduct> campaignCartProductList) {
                        cartList = new ArrayList<>();
                        if (campaign == null) {
                            for (CartProduct cartProduct : retailCartProductList) {
                                cartList.add(cartProduct);
                            }
                        } else  {
                            for (CartProduct cartProduct : campaignCartProductList) {
                                cartList.add(cartProduct);
                            }
                        }
                        CartProduct cartProduct = getCartProduct();
                        String cartId = checkDuplicateCartProduct();
                        if (cartId == null) {
                            addCartProduct(cartProduct);
                        } else {
                            cartProduct.setId(cartId);
                            if (dialogBoxLoading.isShowing()) {
                                dialogBoxLoading.dismiss();
                            }

                            dialogBoxConfirm= new DialogBoxConfirm(PrepareOrderActivity.this,
                                    StringUtils.MES_CONFIRM_DUPLICATE_CART_PRODUCT) {
                                @Override
                                public void onYesClicked() {
                                    dialogBoxLoading.show();
                                    updateCartProduct(cartProduct);
                                }
                            };
                            dialogBoxConfirm.getWindow()
                                    .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialogBoxConfirm.show();
                        }
                    }

                    @Override
                    public void onFailedAPICall(int code) {
                        MethodUtils.displayErrorAPIMessage(PrepareOrderActivity.this);
                    }
                });
            }
        });

        btnConfirmPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBoxConfirm = new DialogBoxConfirm(PrepareOrderActivity.this,
                        StringUtils.MES_CONFIRM_IMMEDIATE_CHECKOUT) {
                    @Override
                    public void onYesClicked() {
                        dismiss();
                        startInstantOrder();
                    }
                };
                dialogBoxConfirm.getWindow()
                                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBoxConfirm.show();
            }
        });
    }

    private void setupPurchasePriceAndQuantity() {
        campaign = product.getCampaign();
        if (campaign != null) {
            txtProductPriceORG.setText(MethodUtils.formatPriceString(product.getRetailPrice()));
            txtProductPriceORG.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            layoutBasePrice.setVisibility(View.GONE);
            layoutCampaign.setVisibility(View.VISIBLE);
            txtCampaignNote.setText(campaign.getDescription());
            lblQuantityCountSeparator.setText("/");
            if (!campaign.getShareFlag()) {
                txtCampaignTag.setText("Single Campaign");
                layoutCampaignInfo.setVisibility(View.GONE);
                price = campaign.getPrice();
                txtCampaignPrice.setText(MethodUtils.formatPriceString(campaign.getPrice()));
                txtCampaignQuantityCount.setText(campaign.getQuantityCount() + "");
                txtCampaignQuantityBar.setText(campaign.getMinQuantity() + "");
            } else {
                layoutCampaignInfo.setVisibility(View.VISIBLE);
                txtOrderCount.setText(campaign.getOrderCount() + "");
                lblOrderCount.setText((campaign.getOrderCount() > 1) ? "Orders" : "Order");
                txtCampaignQuantityBar.setText(campaign.getMaxQuantity() + "");
                progressBarQuantityCount.setMax(campaign.getMaxQuantity());
            }
        } else {
            layoutBasePrice.setVisibility(View.VISIBLE);
            layoutCampaign.setVisibility(View.GONE);
            txtPricingDescription.setText("Retail Price");
            price = product.getRetailPrice();
            txtProductPrice.setText(MethodUtils.formatPriceString(price));
        }
        getRangeQuantity();
        getQuantity();
        editProductQuantity.setText(quantity + "");
        txtNumberInStorage.setText(maxQuantity + "");
        checkSharedCampaignStatus();
        setupQuantityButtons();
        calculateTotalPrice();
    }

    private void checkSharedCampaignStatus() {
        if (campaign != null && campaign.getShareFlag()) {
            txtCampaignTag.setText("Sharing Campaign");
            milestoneList = campaign.getMilestoneList();
            quantity += campaign.getQuantityCount();
            currentCampaignMilestone = MethodUtils.getReachedCampaignMilestone(milestoneList, quantity);
            layoutNextMilestone.setVisibility(View.VISIBLE);
            layoutCampaignInfo.setVisibility(View.VISIBLE);
            if (currentCampaignMilestone == null) {
                price = campaign.getPrice();
                lblCampaignNextMilestone.setText("Next milestone:");
                milestoneQuantity = milestoneList.get(0).getQuantity();
                txtCampaignNextMilestone.setText(milestoneQuantity + "");
            } else {
                price = currentCampaignMilestone.getPrice();
                if (currentCampaignMilestone.equals(milestoneList.get(milestoneList.size() - 1))) {
                    lblCampaignNextMilestone.setText("Final milestone reached!");
                    milestoneQuantity = campaign.getMaxQuantity();
                    txtCampaignNextMilestone.setText("");
                } else {
                    lblCampaignNextMilestone.setText("Next milestone:");
                    milestoneQuantity = milestoneList.get(milestoneList.indexOf(currentCampaignMilestone) + 1)
                                                     .getQuantity();
                    txtCampaignNextMilestone.setText(milestoneQuantity + "");
                }
            }
            txtCampaignPrice.setText(MethodUtils.formatPriceString(price));
            txtCampaignQuantityCount.setText(quantity + "");
            progressBarQuantityCount.setProgress(quantity);
        }
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
            minQuantity = campaign.getMinQuantity();
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

    private void calculateTotalPrice() {
        quantity = Integer.parseInt(editProductQuantity.getText().toString());
        totalPrice = quantity * price;
        txtTotalPrice.setText(MethodUtils.formatPriceString(totalPrice));
    }

    private CartProduct getCartProduct() {
        CartProduct cartProduct = new CartProduct();
        cartProduct.setProduct(product);
        cartProduct.setQuantity(Integer.parseInt(editProductQuantity.getText().toString()));
        cartProduct.setCampaign(campaign);
        cartProduct.setCampaignFlag(campaign != null);
        return cartProduct;
    }

    private void startInstantOrder() {
        CartProduct cartProduct = getCartProduct();
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
        order.setTotalPrice(totalPrice);
        order.setCampaign(cartProduct.getCampaign());
        order.setSupplier(product.getSupplier());
        order.setInCart(false);
        order.setOrderProductList(selectedProductList);
        order.setLoyaltyDiscountPercent(0);
        ordersList.add(order);
        int request = cartProduct.getCampaignFlag()
                ? IntegerUtils.REQUEST_ORDER_CAMPAIGN : IntegerUtils.REQUEST_ORDER_RETAIL;
        Intent confirmOrderIntent = new Intent(getApplicationContext(), ConfirmOrderActivity.class);
        confirmOrderIntent.putExtra("ORDER_LIST", ordersList);
        confirmOrderIntent.putExtra("REQUEST_CODE", request);
        startActivityForResult(confirmOrderIntent, IntegerUtils.REQUEST_COMMON);
    }

    private void addCartProduct(CartProduct cartProduct) {
        APICartCaller.addCartItem(token, cartProduct,
                getApplication(), new APIListener() {
            @Override
            public void onAddCartItemSuccessful(CartProduct cartProduct) {
                cartList.add(cartProduct);
                displaySuccessMessage();
            }

            @Override
            public void onFailedAPICall(int code) {
                if (dialogBoxLoading.isShowing()) {
                    dialogBoxLoading.dismiss();
                }
                if (code == IntegerUtils.ERROR_NO_USER) {
                    MethodUtils.displayErrorAccountMessage(getApplicationContext(),
                            PrepareOrderActivity.this);
                } else {
                    MethodUtils.displayErrorAPIMessage(PrepareOrderActivity.this);
                }
            }
        });
    }

    private void updateCartProduct(CartProduct cartProduct) {
        APICartCaller.updateCartItem(token, cartProduct,
                getApplication(), new APIListener() {
            @Override
            public void onUpdateSuccessful() {
                displaySuccessMessage();
            }

            @Override
            public void onFailedAPICall(int code) {
                if (dialogBoxLoading.isShowing()) {
                    dialogBoxLoading.dismiss();
                }
                if (code == IntegerUtils.ERROR_NO_USER) {
                    MethodUtils.displayErrorAccountMessage(getApplicationContext(),
                            PrepareOrderActivity.this);
                } else {
                    MethodUtils.displayErrorAPIMessage(PrepareOrderActivity.this);
                }
            }
        });
    }

    private String checkDuplicateCartProduct() {
        for (CartProduct cartProduct : cartList) {
            if (cartProduct.getProduct().getProductId().equals(product.getProductId())) {
                if (campaign == null) {
                    return cartProduct.getId();
                } else if (cartProduct.getCampaign().getId().equals(campaign.getId())) {
                    return cartProduct.getId();
                }
            }
        }
        return null;
    }

    private void setClickableQuantityButton(ImageView view, boolean clickable) {
        view.setEnabled(clickable);
        if (clickable) {
            view.setColorFilter(getApplicationContext().getResources()
                    .getColor(R.color.gray_dark));
        } else {
            view.setColorFilter(getApplicationContext().getResources()
                    .getColor(R.color.gray_light));
        }
    }

    private void setupQuantityButtons() {
        if (quantity == maxQuantity) {
            setClickableQuantityButton(imgProductQuantityMinus, true);
            setClickableQuantityButton(imgProductQuantityPlus, false);
        }
        if (quantity == minQuantity) {
            setClickableQuantityButton(imgProductQuantityMinus, false);
            setClickableQuantityButton(imgProductQuantityPlus, true);
        }
        if (quantity < maxQuantity && quantity > minQuantity){
            setClickableQuantityButton(imgProductQuantityMinus, true);
            setClickableQuantityButton(imgProductQuantityPlus, true);
        }
    }

    private void displaySuccessMessage() {
        if (dialogBoxLoading.isShowing()) {
            dialogBoxLoading.dismiss();
        }
        DialogBoxAlert dialogBox = new DialogBoxAlert(PrepareOrderActivity.this,
                IntegerUtils.CONFIRM_ACTION_CODE_SUCCESS,
                StringUtils.MES_SUCCESSFUL_ADD_CART,"") {
            @Override
            public void onClickAction() {
                setResult(RESULT_OK);
                finish();
            }
        };
        dialogBox.show();
    }

    @Override
    public void onBackPressed() {
        imgBackFromPrepareProduct.performClick();
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