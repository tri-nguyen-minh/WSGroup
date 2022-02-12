package dev.wsgroup.main.models.recycleViewAdapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APICartCaller;
import dev.wsgroup.main.models.dtos.Campaign;
import dev.wsgroup.main.models.dtos.CartProduct;
import dev.wsgroup.main.models.dtos.Product;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.StringUtils;
import dev.wsgroup.main.views.dialogbox.DialogBoxAlert;
import dev.wsgroup.main.views.dialogbox.DialogBoxCampaign;
import dev.wsgroup.main.views.dialogbox.DialogBoxConfirm;

public class RecViewCartProductListAdapter extends RecyclerView.Adapter<RecViewCartProductListAdapter.ViewHolder> {

    private List<CartProduct> shoppingCart;
    private Context context;
    private Activity activity;
    private SharedPreferences sharedPreferences;
    private int quantity;

    public RecViewCartProductListAdapter(Context context, Activity activity) {
        sharedPreferences = activity.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        this.context = context;
        this.activity = activity;
    }

    public void setShoppingCart(List<CartProduct> shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_cart_product_list, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.layoutParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.editProductQuantity.clearFocus();
            }
        });
        holder.editProductQuantity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        holder.txtRecViewCartProductName.setText(shoppingCart.get(position).getProduct().getName());
        quantity = getMaximumQuantity(position);
        holder.txtNumberInStorage.setText(quantity + "");
        if(shoppingCart.get(position).getQuantity() > quantity) {
            holder.editProductQuantity.setText(quantity + "");
        } else {
            holder.editProductQuantity.setText(shoppingCart.get(position).getQuantity() + "");
        }

        RecViewProductTypeAdapter adapter = new RecViewProductTypeAdapter(context, activity, IntegerUtils.IDENTIFIER_PRODUCT_TYPE_SELECTED);
        adapter.setTypeList(shoppingCart.get(position).getTypeList());
        holder.recViewProductTypeTag.setAdapter(adapter);
        holder.recViewProductTypeTag.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));

        if(shoppingCart.get(position).getProduct().getImageList() != null) {
            Glide.with(context).load(shoppingCart.get(position).getProduct().getImageList().get(0)).into(holder.imgRecViewProduct);
        }

        if(shoppingCart.get(position).getProduct().getImageList() != null) {
            Glide.with(context)
                    .load(shoppingCart.get(position).getProduct().getImageList().get(0)).into(holder.imgRecViewProduct);
        }

        if (shoppingCart.get(position).getSelected()) {
            setCheckboxSelectable(holder.checkboxCartProduct, true);
        } else {
            setCheckboxSelectable(holder.checkboxCartProduct, false);
        }

        holder.imgProductQuantityMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantityCount = Integer.parseInt(holder.editProductQuantity.getText().toString());
                if(quantityCount > 0) {
                    quantityCount -= 1;
                }
                shoppingCart.get(position).setQuantity(quantityCount);
                holder.editProductQuantity.setText(quantityCount + "");
            }
        });

        holder.imgProductQuantityPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantityCount = Integer.parseInt(holder.editProductQuantity.getText().toString());
                if(quantityCount < getMaximumQuantity(position)) {
                    quantityCount += 1;
                }
                shoppingCart.get(position).setQuantity(quantityCount);
                holder.editProductQuantity.setText(quantityCount + "");
            }
        });

        holder.editProductQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int quantity = 0;
                if (s.length() == 0) {
                    quantity = 1;
                    holder.editProductQuantity.setText("1");
                }
                else if (s.toString().equals("0")) {
                    quantity = 1;
                    holder.editProductQuantity.setText("1");
                }
                else if (Integer.parseInt(s.toString()) > getMaximumQuantity(position)) {
                    quantity = getMaximumQuantity(position);
                    holder.editProductQuantity.setText(quantity + "");
                } else {
                    quantity = Integer.parseInt(holder.editProductQuantity.getText().toString());
                }
                shoppingCart.get(position).setQuantity(quantity);
                updateCartItem(shoppingCart.get(position));
                editTotalPrice(holder, shoppingCart.get(position));
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (shoppingCart.get(position).getSelectableFlag()) {
            holder.checkboxCartProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCheckboxProductChange(holder.checkboxCartProduct, position);
                    setupSelectableProduct();
                }
            });
        } else {
            holder.checkboxCartProduct.setColorFilter(context.getResources().getColor(R.color.gray_light));
            holder.checkboxCartProduct.setClickable(false);
        }

        holder.btnDeleteCartProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogBoxConfirm dialogBoxConfirm = new DialogBoxConfirm(activity, StringUtils.MES_CONFIRM_DELETE_CART) {
                    @Override
                    public void onYesClicked() {
                        onRemoveProduct(position);
                    }
                };
                dialogBoxConfirm.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBoxConfirm.show();
            }
        });

        setupCampaign(position, holder);
    }

    private void setupCampaign(int position, ViewHolder holder) {
        CartProduct cartProduct = shoppingCart.get(position);
        Product product = cartProduct.getProduct();
        Campaign campaign = product.getCampaign();
        editTotalPrice(holder, cartProduct);

        holder.txtProductPrice.setText(MethodUtils.formatPriceString(product.getRetailPrice()));
        if (campaign == null) {
            holder.layoutCampaign.setVisibility(View.GONE);
            setBasePriceSelected(holder, true);
        } else if (!campaign.getStatus().equals("active")) {
            holder.layoutCampaign.setVisibility(View.GONE);
            setBasePriceSelected(holder, true);
        } else {
            holder.layoutCampaign.setVisibility(View.VISIBLE);
            if (cartProduct.getCampaignId().isEmpty()) {
                setBasePriceSelected(holder, true);
            } else {
                setBasePriceSelected(holder, false);
            }
            holder.txtCampaignPrice.setText(MethodUtils.formatPriceString(campaign.getPrice()));
            holder.layoutBasePrice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!cartProduct.getCampaignId().isEmpty()) {
                        cartProduct.setCampaignId("");
                        quantity = getMaximumQuantity(position);
                        holder.txtNumberInStorage.setText(quantity + "");
                        if(cartProduct.getQuantity() > quantity) {
                            holder.editProductQuantity.setText(quantity + "");
                        }
                        setBasePriceSelected(holder, true);
                        editTotalPrice(holder, cartProduct);
                        onPriceMethodChange("", position);
                        setupSelectableProduct();
                    }
                }
            });
            holder.layoutCampaign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(cartProduct.getCampaignId().isEmpty()) {
                        CartProduct cartProduct = shoppingCart.get(position);
                        String campaignId = cartProduct.getProduct().getCampaign().getId();
                        if (!campaignId.isEmpty() && cartProduct.getSelected()
                                && (checkNormalProductSelected(shoppingCart) > 1)) {
                            DialogBoxAlert dialogBoxAlert = new DialogBoxAlert(activity, IntegerUtils.CONFIRM_ACTION_CODE_ALERT,
                                    StringUtils.MES_ALERT_CAMPAIGN_CART_ORDER, "");
                            dialogBoxAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialogBoxAlert.show();
                        } else {
                            cartProduct.setCampaignId(campaignId);
                            quantity = getMaximumQuantity(position);
                            holder.txtNumberInStorage.setText(quantity + "");
                            if(cartProduct.getQuantity() > quantity) {
                                holder.editProductQuantity.setText(quantity + "");
                            }
                            setBasePriceSelected(holder, false);
                            editTotalPrice(holder, cartProduct);
                            onPriceMethodChange(campaignId, position);
                            setupSelectableProduct();
                        }
                    }
                }
            });
            holder.layoutCampaign.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    DialogBoxCampaign dialogBoxCampaign = new DialogBoxCampaign(activity, context, cartProduct.getProduct()) {
                        @Override
                        public void executeOnCampaignSelectedOnDialog(Campaign campaign) {
                            super.executeOnCampaignSelectedOnDialog(campaign);
                            if(cartProduct.getCampaignId().isEmpty()) {
                                CartProduct cartProduct = shoppingCart.get(position);
                                String campaignId = cartProduct.getProduct().getCampaign().getId();
                                if (!campaignId.isEmpty() && cartProduct.getSelected()
                                        && (checkNormalProductSelected(shoppingCart) > 1)) {
                                    DialogBoxAlert dialogBoxAlert = new DialogBoxAlert(activity, IntegerUtils.CONFIRM_ACTION_CODE_ALERT,
                                            StringUtils.MES_ALERT_CAMPAIGN_CART_ORDER, "");
                                    dialogBoxAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    dialogBoxAlert.show();
                                } else {
                                    cartProduct.setCampaignId(campaignId);
                                    quantity = getMaximumQuantity(position);
                                    holder.txtNumberInStorage.setText(quantity + "");
                                    if(cartProduct.getQuantity() > quantity) {
                                        holder.editProductQuantity.setText(quantity + "");
                                    }
                                    setBasePriceSelected(holder, false);
                                    editTotalPrice(holder, cartProduct);
                                    onPriceMethodChange(campaignId, position);
                                    setupSelectableProduct();
                                }
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

    private void updateCartItem(CartProduct cartProduct) {
        APICartCaller.updateCartItem(sharedPreferences.getString("TOKEN", ""), cartProduct,
                activity.getApplication(), new APIListener() {
                    @Override
                    public void onUpdateCartItemSuccessful() {
                        super.onUpdateCartItemSuccessful();
                    }

                    @Override
                    public void onFailedAPICall(int code) {
                        super.onFailedAPICall(code);
                    }
                });
    }

    public void onRemoveProduct(int position) {}

    public void onCheckboxProductChange(ImageView view, int position) {}

    public void onPriceMethodChange(String campaignId, int position) {}

    private void setCheckboxSelectable(ImageView view, boolean selected) {
        view.setVisibility(View.VISIBLE);
        if (selected) {
            view.setImageResource(R.drawable.ic_checkbox_checked);
            view.setColorFilter(context.getResources().getColor(R.color.blue_main));
        } else {
            view.setImageResource(R.drawable.ic_checkbox_unchecked);
            view.setColorFilter(context.getResources().getColor(R.color.gray));
        }
    }

    private void editTotalPrice(ViewHolder holder, CartProduct cartProduct) {
        double totalPrice = cartProduct.getQuantity();
        if(cartProduct.getCampaignId().isEmpty()) {
            totalPrice *= cartProduct.getProduct().getRetailPrice();
        } else {
            totalPrice *= cartProduct.getProduct().getCampaign().getPrice();
        }
        holder.txtTotalPrice.setText(MethodUtils.formatPriceString(totalPrice));
    }

    private void setBasePriceSelected(ViewHolder viewHolder, boolean selected) {
        if(selected) {
            viewHolder.checkboxBasePrice.setImageResource(R.drawable.ic_checkbox_checked);
            viewHolder.checkboxBasePrice.setColorFilter(context.getResources().getColor(R.color.blue_main));
            viewHolder.lblBasePrice.setTextSize(19);
            viewHolder.lblBasePrice.setTextColor(context.getResources().getColor(R.color.blue_dark));
            viewHolder.txtProductPrice.setTextSize(19);
            viewHolder.txtProductPrice.setTextColor(context.getResources().getColor(R.color.blue_dark));
            viewHolder.checkboxCampaign.setImageResource(R.drawable.ic_checkbox_unchecked);
            viewHolder.checkboxCampaign.setColorFilter(context.getResources().getColor(R.color.gray));
            viewHolder.lblCampaignPrice.setTextSize(15);
            viewHolder.lblCampaignPrice.setTextColor(context.getResources().getColor(R.color.gray));
            viewHolder.txtCampaignPrice.setTextSize(15);
            viewHolder.txtCampaignPrice.setTextColor(context.getResources().getColor(R.color.gray));
        } else {
            viewHolder.checkboxCampaign.setImageResource(R.drawable.ic_checkbox_checked);
            viewHolder.checkboxCampaign.setColorFilter(context.getResources().getColor(R.color.blue_main));
            viewHolder.lblCampaignPrice.setTextSize(19);
            viewHolder.lblCampaignPrice.setTextColor(context.getResources().getColor(R.color.blue_dark));
            viewHolder.txtCampaignPrice.setTextSize(19);
            viewHolder.txtCampaignPrice.setTextColor(context.getResources().getColor(R.color.blue_dark));
            viewHolder.checkboxBasePrice.setImageResource(R.drawable.ic_checkbox_unchecked);
            viewHolder.checkboxBasePrice.setColorFilter(context.getResources().getColor(R.color.gray));
            viewHolder.lblBasePrice.setTextSize(15);
            viewHolder.lblBasePrice.setTextColor(context.getResources().getColor(R.color.gray));
            viewHolder.txtProductPrice.setTextSize(15);
            viewHolder.txtProductPrice.setTextColor(context.getResources().getColor(R.color.gray));
        }
    }

    private int getMaximumQuantity(int position) {
        int quantity = shoppingCart.get(position).getProduct().getQuantity();
        if (shoppingCart.get(position).getCampaignId().isEmpty()) {
            if (shoppingCart.get(position).getProduct().getCampaign() != null) {
                if (shoppingCart.get(position).getProduct().getCampaign().getStatus().equals("active")) {
                    quantity -= shoppingCart.get(position).getProduct().getCampaign().getQuantity();
                }
            }
        }
        return quantity;
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    private int checkNormalProductSelected(List<CartProduct> cartProductList) {
        int count = 0;
        for (CartProduct cartProduct : cartProductList) {
            if (cartProduct.getCampaignId().isEmpty() && cartProduct.getSelected()) {
                count++;
            }
        }
        return count;
    }

    private int checkCampaignProductSelected(List<CartProduct> cartProductList) {
        int count = 0;
        for (CartProduct cartProduct : cartProductList) {
            if (!cartProduct.getCampaignId().isEmpty() && cartProduct.getSelected()) {
                count++;
            }
        }
        return count;
    }

    private void setupSelectableProduct() {
        if (checkNormalProductSelected(shoppingCart) > 0) {
            for (CartProduct cartProduct : shoppingCart) {
                if (!cartProduct.getCampaignId().isEmpty()) {
                    cartProduct.setSelectableFlag(false);
                } else {
                    cartProduct.setSelectableFlag(true);
                }
            }
        } else if (checkCampaignProductSelected(shoppingCart) > 0) {
            for (CartProduct cartProduct : shoppingCart) {
                if (!cartProduct.getSelected()) {
                    cartProduct.setSelectableFlag(false);
                }
            }
        } else {
            for (CartProduct cartProduct : shoppingCart) {
                cartProduct.setSelectableFlag(true);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return shoppingCart.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView checkboxCartProduct, imgRecViewProduct,imgProductQuantityMinus,
                imgProductQuantityPlus, checkboxBasePrice, checkboxCampaign;
        private TextView txtRecViewCartProductName, txtNumberInStorage, txtTotalPrice,
                btnDeleteCartProducts, lblBasePrice, lblCampaignPrice,
                txtCampaignPrice, txtProductPrice;
        private EditText editProductQuantity;
        private RecyclerView recViewProductTypeTag;
        private ConstraintLayout layoutBasePrice;
        private LinearLayout layoutCampaign;
        private RelativeLayout layoutParent;

        public ViewHolder(View view) {
            super(view);
            checkboxCartProduct = view.findViewById(R.id.checkboxCartProduct);
            imgRecViewProduct = view.findViewById(R.id.imgRecViewProduct);
            imgProductQuantityMinus = view.findViewById(R.id.imgProductQuantityMinus);
            imgProductQuantityPlus = view.findViewById(R.id.imgProductQuantityPlus);
            checkboxBasePrice = view.findViewById(R.id.checkboxBasePrice);
            checkboxCampaign = view.findViewById(R.id.checkboxCampaign);
            txtRecViewCartProductName = view.findViewById(R.id.txtRecViewCartProductName);
            txtNumberInStorage = view.findViewById(R.id.txtNumberInStorage);
            txtTotalPrice = view.findViewById(R.id.txtTotalPrice);
            btnDeleteCartProducts = view.findViewById(R.id.btnDeleteCartProducts);
            lblBasePrice = view.findViewById(R.id.lblBasePrice);
            lblCampaignPrice= view.findViewById(R.id.lblCampaignPrice);
            txtCampaignPrice = view.findViewById(R.id.txtCampaignPrice);
            txtProductPrice = view.findViewById(R.id.txtProductPrice);
            editProductQuantity = view.findViewById(R.id.editProductQuantity);
            recViewProductTypeTag = view.findViewById(R.id.recViewProductTypeTag);
            layoutBasePrice = view.findViewById(R.id.layoutBasePrice);
            layoutCampaign = view.findViewById(R.id.layoutCampaign);
            layoutParent = view.findViewById(R.id.layoutParent);
        }
    }
}
