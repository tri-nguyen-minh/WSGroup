package dev.wsgroup.main.models.recycleViewAdapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import dev.wsgroup.main.views.dialogbox.DialogBoxConfirm;

public class RecViewCartProductListAdapter extends RecyclerView.Adapter<RecViewCartProductListAdapter.ViewHolder> {

    private List<CartProduct> shoppingCart;
    private Context context;
    private Activity activity;
    private SharedPreferences sharedPreferences;
    private int identifier, quantity, minQuantity, maxQuantity;
    private double price, totalPrice;

    public RecViewCartProductListAdapter(Context context, Activity activity, int identifier) {
        sharedPreferences = activity.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        this.context = context;
        this.activity = activity;
        this.identifier = identifier;
    }

    public void setShoppingCart(List<CartProduct> shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_view_cart_product_list, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtRecViewCartProductName.setText(shoppingCart.get(position).getProduct().getName());
        if(shoppingCart.get(position).getProduct().getImageList().size() > 0) {
            Glide.with(context).load(shoppingCart.get(position).getProduct().getImageList().get(0)).into(holder.imgRecViewProduct);
        }

        RecViewProductTypeAdapter adapter = new RecViewProductTypeAdapter(context, activity, IntegerUtils.IDENTIFIER_PRODUCT_TYPE_SELECTED);
        adapter.setTypeList(shoppingCart.get(position).getTypeList());
        holder.recViewProductTypeTag.setAdapter(adapter);
        holder.recViewProductTypeTag.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));

        if (identifier == IntegerUtils.IDENTIFIER_RETAIL_CART) {
            holder.checkboxCartProduct.setVisibility(View.VISIBLE);
            setCheckboxSelected(holder, shoppingCart.get(position).getSelectedFlag());
            holder.txtPricingDescription.setText("Retail Price");
            holder.txtCampaignTag.setVisibility(View.GONE);
            holder.txtProductPriceORG.setVisibility(View.GONE);
            holder.btnCheckout.setVisibility(View.GONE);
            holder.checkboxCartProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean currentStatus = shoppingCart.get(position).getSelectedFlag();
                    if (currentStatus) {
                        currentStatus = false;
                    } else {
                        currentStatus = true;
                    }
                    onCheckboxProductChanged(position, currentStatus);
                }
            });
        } else {
            holder.checkboxCartProduct.setVisibility(View.GONE);
            holder.txtPricingDescription.setText(shoppingCart.get(position).getCampaign().getDescription());
            boolean sharing = shoppingCart.get(position).getCampaign().getShareFlag();
            holder.txtCampaignTag.setText(sharing ? "Sharing Campaign" : "Single Campaign");
            holder.txtProductPriceORG.setVisibility(View.VISIBLE);
            holder.txtProductPriceORG.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            price = shoppingCart.get(0).getProduct().getRetailPrice();
            holder.txtProductPriceORG.setText(MethodUtils.formatPriceString(price));
            holder.btnCheckout.setVisibility(View.VISIBLE);
            holder.btnCheckout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onCheckout(position);
                }
            });
        }
        getPurchasePrice(position);
        holder.txtProductPrice.setText(MethodUtils.formatPriceString(price));
        quantity = shoppingCart.get(position).getQuantity();
        minQuantity = getMinQuantity(position);
        maxQuantity = getMaxQuantity(position);
        if (quantity < minQuantity) {
            quantity = minQuantity;
        } else if (quantity > maxQuantity) {
            quantity = maxQuantity;
        }
        setupQuantityButtons(holder);
        setTotalPrice(holder, position);
        holder.editProductQuantity.setText(quantity + "");
        holder.txtNumberInStorage.setText(maxQuantity + "");

        holder.layoutParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.editProductQuantity.hasFocus()) {
                    holder.editProductQuantity.clearFocus();
                }
            }
        });

        holder.imgProductQuantityMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantityCount = Integer.parseInt(holder.editProductQuantity.getText().toString()) - 1;
                holder.editProductQuantity.setText(quantityCount + "");
            }
        });

        holder.imgProductQuantityPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantityCount = Integer.parseInt(holder.editProductQuantity.getText().toString()) + 1;
                holder.editProductQuantity.setText(quantityCount + "");
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

        holder.editProductQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    minQuantity = getMinQuantity(position);
                    maxQuantity = getMaxQuantity(position);
                    quantity = Integer.parseInt(holder.editProductQuantity.getText().toString());
                    if (quantity < minQuantity) {
                        holder.editProductQuantity.setText(minQuantity + "");
                    } else if (quantity > maxQuantity) {
                        holder.editProductQuantity.setText(maxQuantity + "");
                    } else {
                        setupQuantityButtons(holder);
                        setTotalPrice(holder, position);
                        CartProduct cartProduct = shoppingCart.get(position);
                        cartProduct.setQuantity(quantity);
                        updateCartItem(cartProduct, holder);
                    }
                } catch (Exception e) {
                    holder.editProductQuantity.setText(minQuantity + "");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        holder.btnDeleteCartProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
    }

    private void getPurchasePrice(int position) {
        price = shoppingCart.get(0).getProduct().getRetailPrice();
        if (identifier == IntegerUtils.IDENTIFIER_CAMPAIGN_CART) {
            price -= shoppingCart.get(position).getCampaign().getSavingPrice();
        }
    }

    private double calculateTotalPrice(int position) {
        getPurchasePrice(position);
        return quantity * price;
    }

    private void setTotalPrice(ViewHolder holder, int position) {
        totalPrice = calculateTotalPrice(position);
        holder.txtTotalPrice.setText(MethodUtils.formatPriceString(totalPrice));
    }

    private int getMaxQuantity(int position) {
        int quantity = shoppingCart.get(position).getProduct().getQuantity();
        if (!shoppingCart.get(position).getCampaignFlag()) {
            for (Campaign ongoingCampaign : shoppingCart.get(position).getProduct().getCampaignList()) {
                quantity -= ongoingCampaign.getMaxQuantity();
            }
        } else {
            Campaign campaign = shoppingCart.get(position).getCampaign();
            quantity = campaign.getMaxQuantity() - campaign.getQuantityCount();
        }
        return quantity;
    }

    private int getMinQuantity(int position) {
        int quantity = 0;
        if (shoppingCart.get(position).getCampaignFlag()) {
            Campaign campaign = shoppingCart.get(position).getCampaign();
            if (!campaign.getShareFlag()) {
                quantity = campaign.getMinQuantity();
            }
        }
        return quantity;
    }

    private void setClickableQuantityButton(ImageView view, boolean clickable) {
        view.setEnabled(clickable);
        if (clickable) {
            view.setColorFilter(context.getResources().getColor(R.color.gray_dark));
        } else {
            view.setColorFilter(context.getResources().getColor(R.color.gray_light));
        }
    }

    private void setupQuantityButtons(ViewHolder holder) {
        if (quantity == maxQuantity) {
            setClickableQuantityButton(holder.imgProductQuantityMinus, true);
            setClickableQuantityButton(holder.imgProductQuantityPlus, false);
        } else if (quantity == minQuantity) {
            setClickableQuantityButton(holder.imgProductQuantityMinus, false);
            setClickableQuantityButton(holder.imgProductQuantityPlus, true);
        } else {
            setClickableQuantityButton(holder.imgProductQuantityMinus, true);
            setClickableQuantityButton(holder.imgProductQuantityPlus, true);
        }
    }

    private void setCheckboxSelected(ViewHolder holder, boolean selected) {
        holder.checkboxCartProduct.setVisibility(View.VISIBLE);
        if (selected) {
            holder.checkboxCartProduct.setImageResource(R.drawable.ic_checkbox_checked);
            holder.checkboxCartProduct.setColorFilter(context.getResources().getColor(R.color.blue_main));
        } else {
            holder.checkboxCartProduct.setImageResource(R.drawable.ic_checkbox_unchecked);
            holder.checkboxCartProduct.setColorFilter(context.getResources().getColor(R.color.gray));
        }
    }

    private void updateCartItem(CartProduct cartProduct, ViewHolder holder) {
        holder.btnCheckout.setEnabled(false);
        holder.btnCheckout.getBackground().setTint(context.getResources().getColor(R.color.gray_light));
        if (!cartProduct.getSelectedFlag()) {
            holder.checkboxCartProduct.setEnabled(false);
            holder.checkboxCartProduct.setColorFilter(context.getResources().getColor(R.color.gray_light));
        } else {
            onPriceChanging();
        }
        APICartCaller.updateCartItem(sharedPreferences.getString("TOKEN", ""), cartProduct,
                activity.getApplication(), new APIListener() {
                    @Override
                    public void onUpdateCartItemSuccessful() {
                        super.onUpdateCartItemSuccessful();
                        holder.btnCheckout.setEnabled(true);
                        holder.btnCheckout.getBackground().setTint(context.getResources().getColor(R.color.blue_main));
                        if (!cartProduct.getSelectedFlag()) {
                            holder.checkboxCartProduct.setEnabled(true);
                            holder.checkboxCartProduct.setColorFilter(context.getResources().getColor(R.color.gray));
                        } else {
                            onPriceChanged();
                        }
                    }

                    @Override
                    public void onFailedAPICall(int code) {
                        super.onFailedAPICall(code);
                        holder.btnCheckout.setEnabled(true);
                        holder.btnCheckout.getBackground().setTint(context.getResources().getColor(R.color.blue_main));
                    }
                });
    }

    public void onRemoveProduct(int position) {}

    public void onCheckboxProductChanged(int position, boolean newStatus) {}

    public void onPriceChanging() {}

    public void onPriceChanged() {}

    public void onCheckout(int position) {
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public int getItemCount() {
        return shoppingCart.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView checkboxCartProduct, imgRecViewProduct,
                imgProductQuantityMinus, imgProductQuantityPlus;
        private TextView txtRecViewCartProductName, txtNumberInStorage, txtPricingDescription,
                txtCampaignTag, txtProductPriceORG, txtProductPrice, txtTotalPrice;
        private Button btnDeleteCartProducts, btnCheckout;
        private EditText editProductQuantity;
        private RecyclerView recViewProductTypeTag;
        private RelativeLayout layoutParent;

        public ViewHolder(View view) {
            super(view);
            checkboxCartProduct = view.findViewById(R.id.checkboxCartProduct);
            imgRecViewProduct = view.findViewById(R.id.imgRecViewProduct);
            imgProductQuantityMinus = view.findViewById(R.id.imgProductQuantityMinus);
            imgProductQuantityPlus = view.findViewById(R.id.imgProductQuantityPlus);
            txtRecViewCartProductName = view.findViewById(R.id.txtRecViewCartProductName);
            txtNumberInStorage = view.findViewById(R.id.txtNumberInStorage);
            txtPricingDescription = view.findViewById(R.id.txtPricingDescription);
            txtCampaignTag = view.findViewById(R.id.txtCampaignTag);
            txtProductPriceORG = view.findViewById(R.id.txtProductPriceORG);
            txtProductPrice = view.findViewById(R.id.txtProductPrice);
            txtTotalPrice = view.findViewById(R.id.txtTotalPrice);
            btnDeleteCartProducts= view.findViewById(R.id.btnDeleteCartProducts);
            btnCheckout = view.findViewById(R.id.btnCheckout);
            editProductQuantity = view.findViewById(R.id.editProductQuantity);
            recViewProductTypeTag = view.findViewById(R.id.recViewProductTypeTag);
            layoutParent = view.findViewById(R.id.layoutParent);
        }
    }
}
