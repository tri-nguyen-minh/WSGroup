package dev.wsgroup.main.views.dialogbox;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APICartCaller;
import dev.wsgroup.main.models.dtos.Campaign;
import dev.wsgroup.main.models.dtos.CartProduct;
import dev.wsgroup.main.models.dtos.Order;
import dev.wsgroup.main.models.dtos.Product;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.ObjectSerializer;
import dev.wsgroup.main.models.utils.StringUtils;
import dev.wsgroup.main.views.activities.order.ConfirmOrderActivity;

public class DialogBoxOrderDetail extends Dialog{

    private Button btnConfirmAddToCart, btnConfirmPurchase;
    private ImageView imgCloseDialogBox, imgProductQuantityMinus, imgProductQuantityPlus,
            checkboxBasePrice, checkboxCampaign;
    private TextView txtNumberInStorage, txtTotalPrice, lblBasePrice, lblCampaignPrice,
            txtCampaignPrice, txtProductPrice;
    private EditText editProductQuantity;
    private ConstraintLayout layoutBasePrice;
    private LinearLayout layoutCampaign;
    private CardView cardViewParent;

    private Activity activity;
    private Context context;
    private String userId;
    private Product product;
    private Campaign campaign;
    private SharedPreferences sharedPreferences;
    private int maxQuantity;
    private String campaignId;
    private DialogBoxLoading dialogBoxLoading;
    private boolean retailPurchaseOutOfStock;

    public DialogBoxOrderDetail(Activity activity, Context context, String userId, Product product) {
        super(activity);
        sharedPreferences = activity.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        this.activity = activity;
        this.context = context;
        this.product = product;
        this.userId = userId;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_box_order_detail);
        setCancelable(false);
        getWindow()
                .setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnConfirmAddToCart = findViewById(R.id.btnConfirmAddToCart);
        btnConfirmPurchase = findViewById(R.id.btnConfirmPurchase);
        imgCloseDialogBox = findViewById(R.id.imgCloseDialogBox);
        imgProductQuantityMinus = findViewById(R.id.imgProductQuantityMinus);
        imgProductQuantityPlus = findViewById(R.id.imgProductQuantityPlus);
        checkboxBasePrice = findViewById(R.id.checkboxBasePrice);
        checkboxCampaign = findViewById(R.id.checkboxCampaign);
        txtNumberInStorage = findViewById(R.id.txtNumberInStorage);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);
        lblBasePrice = findViewById(R.id.txtPricingDescription);
        lblCampaignPrice = findViewById(R.id.lblCampaignPrice);
        txtCampaignPrice = findViewById(R.id.txtCampaignPrice);
        txtProductPrice = findViewById(R.id.txtProductPrice);
        editProductQuantity = findViewById(R.id.editProductQuantity);
        layoutBasePrice = findViewById(R.id.layoutBasePrice);
        layoutCampaign = findViewById(R.id.layoutCampaign);
        cardViewParent = findViewById(R.id.cardViewParent);

        editProductQuantity.setText("1");
        if(product != null) {
            setupCampaign();
            maxQuantity = getMaximumQuantity();
            txtNumberInStorage.setText(maxQuantity + "");
            editProductQuantity.setText("1");
        }

        cardViewParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editProductQuantity.hasFocus()) {
                    editProductQuantity.clearFocus();
                }
            }
        });

        editProductQuantity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    MethodUtils.hideKeyboard(view, context);
                }
            }
        });

        btnConfirmAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBoxLoading = new DialogBoxLoading(activity);
                dialogBoxLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBoxLoading.show();
                CartProduct cartProduct = new CartProduct();
                cartProduct.setQuantity(Integer.parseInt(editProductQuantity.getText().toString()));
                cartProduct.setProduct(product);
                HashMap<String, List<CartProduct>> shoppingCart = null;
                List<CartProduct> cartProductList;
                try {
                    shoppingCart = (HashMap<String, List<CartProduct>>) ObjectSerializer
                            .deserialize(sharedPreferences.getString("SHOPPING_CART", ""));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                cartProductList = shoppingCart.get(product.getSupplier().getId());
                if(cartProductList == null) {
                    addCartProduct(cartProduct);
                } else if (cartProductList.size() == 0) {
                    addCartProduct(cartProduct);
                } else {
                    boolean duplicateProduct = false;
                    for (CartProduct cartProductInList: cartProductList) {
                        if(cartProductInList.getProduct().getProductId()
                                .equals(cartProduct.getProduct().getProductId())) {
                            cartProduct.getQuantity();
                            cartProduct.setId(cartProductInList.getId());
                            cartProduct.setQuantity(cartProduct.getQuantity() + cartProductInList.getQuantity());
                            duplicateProduct = true;
                        }
                    }
                    if(duplicateProduct) {
                        updateCartProduct(cartProduct);
                    } else {
                        addCartProduct(cartProduct);
                    }
                }
            }
        });

        btnConfirmPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Order order = getOrder();
                DialogBoxConfirm dialogBoxConfirm
                        = new DialogBoxConfirm(activity, StringUtils.MES_CONFIRM_IMMEDIATE_CHECKOUT) {
                    @Override
                    public void onYesClicked() {
                        dismiss();
                        Intent checkoutActivity = new Intent(context, ConfirmOrderActivity.class);
                        checkoutActivity.putExtra("ORDER", (Serializable) order);
                        checkoutActivity.putExtra("PROCESS", IntegerUtils.REQUEST_INSTANT_CHECKOUT);
                        activity.startActivityForResult(checkoutActivity, IntegerUtils.REQUEST_COMMON);
                    }
                };
                dialogBoxConfirm.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBoxConfirm.show();
            }
        });

        imgCloseDialogBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        imgProductQuantityMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantityCount = Integer.parseInt(editProductQuantity.getText().toString());
                if(quantityCount > 1) {
                    quantityCount -= 1;
                }
                editProductQuantity.setText(quantityCount + "");
                editTotalPrice();
            }
        });

        imgProductQuantityPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantityCount = Integer.parseInt(editProductQuantity.getText().toString());
                if(quantityCount < maxQuantity)
                quantityCount += 1;
                editProductQuantity.setText(quantityCount + "");
                editTotalPrice();
            }
        });
        
        editProductQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    editProductQuantity.setText("1");
                }
                else if(Integer.parseInt(s.toString()) > maxQuantity) {
                    editProductQuantity.setText(product.getQuantity() + "");
                }
                editTotalPrice();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editTotalPrice();
    }

    private void setupCampaign() {
        campaign = product.getCampaign();

        txtProductPrice.setText(MethodUtils.formatPriceString(product.getRetailPrice()));
        if (campaign == null) {
            campaignId = "";
            layoutCampaign.setVisibility(View.GONE);
            setBasePriceSelected(true);
        } else if (!campaign.getStatus().equals("active")) {
            campaignId = "";
            layoutCampaign.setVisibility(View.GONE);
            setBasePriceSelected(true);
        } else {
            retailPurchaseOutOfStock = (product.getQuantity() <= campaign.getMinQuantity());
            if (retailPurchaseOutOfStock) {
                campaignId = campaign.getId();
                setBasePriceSelected(false);
            } else {
                campaignId = "";
                setBasePriceSelected(true);
            }
            layoutCampaign.setVisibility(View.VISIBLE);
            double price = product.getRetailPrice() - campaign.getPrice();
            txtCampaignPrice.setText(MethodUtils.formatPriceString(price));
            layoutBasePrice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (retailPurchaseOutOfStock) {
                        DialogBoxAlert dialogBox =
                                new DialogBoxAlert(activity, IntegerUtils.CONFIRM_ACTION_CODE_FAILED,
                                        StringUtils.MES_ERROR_OUT_OF_STOCK, "");
                        dialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialogBox.show();
                    } else {
                        if(!campaignId.isEmpty()) {
                            campaignId = "";
                            maxQuantity = getMaximumQuantity();
                            txtNumberInStorage.setText(maxQuantity + "");
                            if(Integer.parseInt(editProductQuantity.getText().toString()) > maxQuantity) {
                                editProductQuantity.setText(maxQuantity + "");
                            }
                            setBasePriceSelected(true);
                            editTotalPrice();
                        }
                    }
                }
            });
            layoutCampaign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(campaignId.isEmpty()) {
                        campaignId = campaign.getId();
                        maxQuantity = getMaximumQuantity();
                        txtNumberInStorage.setText(maxQuantity + "");
                        if(Integer.parseInt(editProductQuantity.getText().toString()) > maxQuantity) {
                            editProductQuantity.setText(maxQuantity + "");
                        }
                        setBasePriceSelected(false);
                        editTotalPrice();
                    }
                }
            });
            layoutCampaign.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    DialogBoxCampaign dialogBoxCampaign = new DialogBoxCampaign(activity, product) {
                        @Override
                        public void executeOnCampaignSelectedOnDialog(Campaign campaign) {
                            if (campaignId.isEmpty()) {
                                campaignId = campaign.getId();
                                maxQuantity = getMaximumQuantity();
                                txtNumberInStorage.setText(maxQuantity + "");
                                if(Integer.parseInt(editProductQuantity.getText().toString()) > maxQuantity) {
                                    editProductQuantity.setText(maxQuantity + "");
                                }
                                setBasePriceSelected(false);
                                editTotalPrice();
                            }
                        }
                    };
                    dialogBoxCampaign.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialogBoxCampaign.show();
                    return true;
                }
            });
        }
    }
    
    private void setBasePriceSelected(boolean selected) {
        if(selected) {
            checkboxBasePrice.setImageResource(R.drawable.ic_checkbox_checked);
            checkboxBasePrice.setColorFilter(context.getResources().getColor(R.color.blue_main));
            lblBasePrice.setTextSize(19);
            lblBasePrice.setTextColor(context.getResources().getColor(R.color.blue_dark));
            txtProductPrice.setTextSize(19);
            txtProductPrice.setTextColor(context.getResources().getColor(R.color.blue_dark));
            checkboxCampaign.setImageResource(R.drawable.ic_checkbox_unchecked);
            checkboxCampaign.setColorFilter(context.getResources().getColor(R.color.gray));
            lblCampaignPrice.setTextSize(15);
            lblCampaignPrice.setTextColor(context.getResources().getColor(R.color.gray));
            txtCampaignPrice.setTextSize(15);
            txtCampaignPrice.setTextColor(context.getResources().getColor(R.color.gray));
        } else {
            checkboxCampaign.setImageResource(R.drawable.ic_checkbox_checked);
            checkboxCampaign.setColorFilter(context.getResources().getColor(R.color.blue_main));
            lblCampaignPrice.setTextSize(19);
            lblCampaignPrice.setTextColor(context.getResources().getColor(R.color.blue_dark));
            txtCampaignPrice.setTextSize(19);
            txtCampaignPrice.setTextColor(context.getResources().getColor(R.color.blue_dark));
            checkboxBasePrice.setImageResource(R.drawable.ic_checkbox_unchecked);
            checkboxBasePrice.setColorFilter(context.getResources().getColor(R.color.gray));
            lblBasePrice.setTextSize(15);
            lblBasePrice.setTextColor(context.getResources().getColor(R.color.gray));
            txtProductPrice.setTextSize(15);
            txtProductPrice.setTextColor(context.getResources().getColor(R.color.gray));
        }
    }

    private void editTotalPrice() {
        double totalPrice = Integer.parseInt(editProductQuantity.getText().toString());
        if(campaignId.isEmpty()) {
            totalPrice *= product.getRetailPrice();
        } else {
            double price = product.getRetailPrice() - campaign.getPrice();
            totalPrice *= price;
        }
        txtTotalPrice.setText(MethodUtils.formatPriceString(totalPrice));
    }

    private int getMaximumQuantity() {
        int quantity = product.getQuantity();
        if (campaignId.isEmpty()) {
            if (product.getCampaign() != null) {
                if (product.getCampaign().getStatus().equals("active")) {
                    quantity -= product.getCampaign().getMinQuantity();
                }
            }
        }
        return quantity;
    }

    private double getTotalPrice() {
        double totalPrice = Double.parseDouble(editProductQuantity.getText().toString());
        totalPrice *= product.getRetailPrice();

//        double totalPrice = Double.parseDouble(editProductQuantity.getText().toString());
//        if(campaignId.isEmpty()) {
//            totalPrice *= product.getRetailPrice();
//        } else {
//            double price = product.getRetailPrice() - campaign.getSavingPrice();
//            totalPrice *= price;
//        }
        return totalPrice;
    }

    private void updateCartProduct(CartProduct cartProduct) {
        APICartCaller.updateCartItem(sharedPreferences.getString("TOKEN", ""), cartProduct,
                activity.getApplication(), new APIListener() {
                    @Override
                    public void onUpdateSuccessful() {
                        if (dialogBoxLoading.isShowing()) {
                            dialogBoxLoading.dismiss();
                        }
                        onCartProductAdded(cartProduct);
                        dismiss();
                    }

                    @Override
                    public void onFailedAPICall(int code) {
                    }
                });
    }

    private void addCartProduct(CartProduct cartProduct) {
        APICartCaller.addCartItem(sharedPreferences.getString("TOKEN", ""),
                cartProduct, activity.getApplication(), new APIListener() {
            @Override
            public void onAddCartItemSuccessful(CartProduct cartProduct) {
                super.onAddCartItemSuccessful(cartProduct);
                if (dialogBoxLoading.isShowing()) {
                    dialogBoxLoading.dismiss();
                }
                onCartProductAdded(cartProduct);
                dismiss();
            }

            @Override
            public void onFailedAPICall(int code) {
                if (code == IntegerUtils.ERROR_NO_USER) {
                    MethodUtils.displayErrorAccountMessage(context, activity);
                } else {

                }
            }
        });
    }

    public void onCartProductAdded(CartProduct cartProduct) {}

    private Order getOrder() {
        Order order = new Order();
//        List<OrderProduct> list = new ArrayList<>();
//        OrderProduct orderProduct = new OrderProduct();
//        CartProduct cartProduct = new CartProduct();
//        cartProduct.setQuantity(Integer.parseInt(editProductQuantity.getText().toString()));
//        cartProduct.setProduct(product);
////        cartProduct.setCampaignId(campaignId);
//        cartProduct.setProductType(product.getTypeOfProduct());
//        orderProduct.setProductId(product.getProductId());
//        orderProduct.setQuantity(cartProduct.getQuantity());
//        orderProduct.setPrice(product.getRetailPrice());
////        if (campaignId.isEmpty()) {
////            orderProduct.setPrice(product.getRetailPrice());
////        } else {
////            orderProduct.setPrice(campaign.getSavingPrice());
////        }
//        orderProduct.setTotalPrice(getTotalPrice());
//        orderProduct.setCampaignId(campaignId);
//        orderProduct.setProductType(cartProduct.getProductType());
//        orderProduct.setProduct(product);
//        orderProduct.setCartProduct(cartProduct);
//        orderProduct.setNote("");
//        orderProduct.setTypeList(cartProduct.getTypeList());
//        list.add(orderProduct);
//        order.setOrderProductList(list);
//        order.setSupplier(product.getSupplier());
//        order.setCampaignId(campaignId);
//        order.setInCart(false);
        return order;
    }

}
