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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APICartCaller;
import dev.wsgroup.main.models.dtos.Campaign;
import dev.wsgroup.main.models.dtos.CampaignMilestone;
import dev.wsgroup.main.models.dtos.CartProduct;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.StringUtils;
import dev.wsgroup.main.views.dialogbox.DialogBoxConfirm;

public class RecViewCartProductListAdapter
        extends RecyclerView.Adapter<RecViewCartProductListAdapter.ViewHolder> {

    private List<CartProduct> shoppingCart;
    private CartProduct cartProduct;
    private Context context;
    private Activity activity;
    private SharedPreferences sharedPreferences;
    private int identifier, quantity, minQuantity, maxQuantity, milestoneQuantity;
    private double price, totalPrice;
    private Campaign campaign;
    private CampaignMilestone milestone;

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
        View view = LayoutInflater.from(context)
                .inflate(R.layout.recycle_view_cart_product_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        cartProduct = shoppingCart.get(position);
        holder.txtRecViewCartProductName.setText(cartProduct.getProduct().getName());
        if(cartProduct.getProduct().getImageList().size() > 0) {
            Glide.with(context)
                 .load(cartProduct.getProduct().getImageList().get(0))
                 .into(holder.imgRecViewProduct);
        }
        if (identifier == IntegerUtils.IDENTIFIER_RETAIL_CART) {
            price = cartProduct.getProduct().getRetailPrice();
            holder.layoutBasePrice.setVisibility(View.VISIBLE);
            holder.layoutCampaign.setVisibility(View.GONE);
            holder.checkboxCartProduct.setVisibility(View.VISIBLE);
            setCheckboxSelected(holder, cartProduct.getSelectedFlag());
            holder.txtPricingDescription.setText("Retail Price");
            holder.btnCheckout.setVisibility(View.GONE);
            holder.lblCampaignEnded.setVisibility(View.GONE);
            holder.checkboxCartProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cartProduct = shoppingCart.get(position);
                    onCheckboxProductChanged(cartProduct.getId(), !cartProduct.getSelectedFlag());
                }
            });
        } else {
            campaign = cartProduct.getCampaign();
            if (campaign != null) {
                price = cartProduct.getProduct().getRetailPrice();
                holder.txtProductPriceORG.setText(MethodUtils.formatPriceString(price));
                holder.txtProductPriceORG.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                holder.layoutBasePrice.setVisibility(View.GONE);
                holder.layoutCampaign.setVisibility(View.VISIBLE);
                holder.txtCampaignCode.setText(campaign.getCode());
                holder.txtCampaignEndDate.setText(MethodUtils.formatDate(campaign.getEndDate(), false));
                holder.txtCampaignNote.setText(campaign.getDescription());
                holder.lblQuantityCountSeparator.setText("/");
                holder.checkboxCartProduct.setVisibility(View.GONE);
                if (!campaign.getShareFlag()) {
                    holder.txtCampaignTag.setText("Single Campaign");
                    holder.layoutNextMilestone.setVisibility(View.GONE);
                    holder.layoutPreviousMilestone.setVisibility(View.GONE);
                    holder.layoutCampaignInfo.setVisibility(View.GONE);
                    price = campaign.getPrice();
                    holder.txtCampaignPrice.setText(MethodUtils.formatPriceString(campaign.getPrice()));
                    holder.txtCampaignQuantityCount.setText(campaign.getQuantityCount() + "");
                    holder.txtCampaignQuantityBar.setText(campaign.getMinQuantity() + "");
                }
                if (campaign.getStatus().equals("active")) {
                    holder.lblCampaignEnded.setVisibility(View.GONE);
                    holder.btnCheckout.setVisibility(View.VISIBLE);
                    holder.btnCheckout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onCheckout(position);
                        }
                    });
                } else {
                    holder.btnCheckout.setVisibility(View.GONE);
                    holder.lblCampaignEnded.setVisibility(View.VISIBLE);
                }
            }
        }
        holder.txtProductPrice.setText(MethodUtils.formatPriceString(price));
        quantity = cartProduct.getQuantity();
        minQuantity = getMinQuantity();
        maxQuantity = getMaxQuantity();
        if (quantity < minQuantity) {
            quantity = minQuantity;
        } else if (quantity > maxQuantity) {
            quantity = maxQuantity;
        }
        holder.editProductQuantity.setText(quantity + "");
        holder.txtNumberInStorage.setText(maxQuantity + "");
        updateSharingCampaignLayout(holder);
        getPurchasePrice(position);
        setupQuantityButtons(holder);
        setTotalPrice(holder, position);
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
                int quantityCount
                        = Integer.parseInt(holder.editProductQuantity.getText().toString()) - 1;
                holder.editProductQuantity.setText(quantityCount + "");
            }
        });

        holder.imgProductQuantityPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantityCount
                        = Integer.parseInt(holder.editProductQuantity.getText().toString()) + 1;
                holder.editProductQuantity.setText(quantityCount + "");
            }
        });

        holder.editProductQuantity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    MethodUtils.hideKeyboard(view, context);
                }
            }
        });

        holder.editProductQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    cartProduct = shoppingCart.get(position);
                    minQuantity = getMinQuantity();
                    maxQuantity = getMaxQuantity();
                    quantity = Integer.parseInt(holder.editProductQuantity.getText().toString());
                    if (quantity < minQuantity) {
                        holder.editProductQuantity.setText(minQuantity + "");
                    } else if (quantity > maxQuantity) {
                        holder.editProductQuantity.setText(maxQuantity + "");
                    } else {
                        cartProduct.setQuantity(quantity);
                        setupQuantityButtons(holder);
                        updateSharingCampaignLayout(holder);
                        setTotalPrice(holder, position);
                        updateCartItem(holder);
                    }
                } catch (Exception e) {
                    holder.editProductQuantity.setText(minQuantity + "");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        holder.btnDeleteCartProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogBoxConfirm dialogBoxConfirm
                        = new DialogBoxConfirm(activity, StringUtils.MES_CONFIRM_DELETE_CART) {
                    @Override
                    public void onYesClicked() {
                        onRemoveProduct(shoppingCart.get(position).getId());
                    }
                };
                dialogBoxConfirm.getWindow()
                                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBoxConfirm.show();
            }
        });
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

    private int getMinQuantity() {
        int quantity = 1;
        if (cartProduct.getCampaignFlag()) {
            campaign = cartProduct.getCampaign();
            quantity = campaign.getMinQuantity();
        }
        return quantity;
    }

    private int getMaxQuantity() {
        int quantity = cartProduct.getProduct().getQuantity();
        if (!cartProduct.getCampaignFlag()) {
            for (Campaign ongoingCampaign : cartProduct.getProduct().getCampaignList()) {
                quantity -= ongoingCampaign.getMaxQuantity();
            }
        } else {
            campaign = cartProduct.getCampaign();
            quantity = campaign.getMaxQuantity() - campaign.getQuantityCount();
        }
        return quantity;
    }

    private void updateSharingCampaignLayout(ViewHolder holder) {
        if (identifier == IntegerUtils.IDENTIFIER_CAMPAIGN_CART) {
            campaign = cartProduct.getCampaign();
            if (campaign != null && campaign.getShareFlag()) {
                holder.txtCampaignTag.setText("Sharing Campaign");
                milestoneQuantity = Integer.parseInt(holder.editProductQuantity.getText().toString())
                        + campaign.getQuantityCount();
                milestone = MethodUtils.getReachedCampaignMilestone(campaign.getMilestoneList(),
                        milestoneQuantity);
                holder.layoutNextMilestone.setVisibility(View.VISIBLE);
                holder.layoutPreviousMilestone.setVisibility(View.VISIBLE);
                holder.layoutCampaignInfo.setVisibility(View.VISIBLE);
                if (milestone == null) {
                    price = campaign.getPrice();
                    holder.lblCampaignNextMilestone.setText("Next milestone:");
                    milestoneQuantity = campaign.getMilestoneList().get(0).getQuantity();
                    holder.txtCampaignNextMilestone.setText(milestoneQuantity + "");
                    holder.layoutPreviousMilestone.setVisibility(View.GONE);
                } else {
                    price = milestone.getPrice();
                    if (milestone.equals(campaign.getMilestoneList()
                                              .get(campaign.getMilestoneList().size() - 1))) {
                        holder.lblCampaignNextMilestone.setText("Final milestone reached!");
                        milestoneQuantity = campaign.getMaxQuantity();
                        holder.txtCampaignNextMilestone.setText("");
                    } else {
                        holder.lblCampaignNextMilestone.setText("Next milestone:");
                        milestoneQuantity = campaign.getMilestoneList()
                                                    .get(campaign.getMilestoneList().indexOf(milestone) + 1)
                                                    .getQuantity();
                        holder.txtCampaignPreviousMilestone.setText(milestone.getQuantity() + "");
                        holder.txtCampaignNextMilestone.setText(milestoneQuantity + "");
                    }
                }
                holder.txtCampaignPrice.setText(MethodUtils.formatPriceString(price));
                quantity = cartProduct.getQuantity() + campaign.getQuantityCount();
                if (quantity > campaign.getMaxQuantity()) {
                    quantity = campaign.getMaxQuantity();
                }
                holder.txtCampaignQuantityCount.setText(quantity + "");
                holder.txtCampaignQuantityBar.setText(milestoneQuantity + "");
                holder.progressBarQuantityCount.setMax(milestoneQuantity);
                holder.progressBarQuantityCount.setProgress(quantity);
            }
        }
    }

    private void getPurchasePrice(int position) {
        price = shoppingCart.get(position).getProduct().getRetailPrice();
        if (identifier == IntegerUtils.IDENTIFIER_CAMPAIGN_CART) {
            campaign = shoppingCart.get(position).getCampaign();
            price = shoppingCart.get(position).getCampaign().getPrice();
            if (campaign.getShareFlag()) {
                milestoneQuantity = shoppingCart.get(position).getQuantity() + campaign.getQuantityCount();
                milestone = MethodUtils.getReachedCampaignMilestone(campaign.getMilestoneList(), milestoneQuantity);
                if (milestone != null) {
                    price = milestone.getPrice();
                }
            }
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

    private void setClickableQuantityButton(ImageView view, boolean clickable) {
        view.setEnabled(clickable);
        if (clickable) {
            view.setColorFilter(context.getResources().getColor(R.color.gray_dark));
        } else {
            view.setColorFilter(context.getResources().getColor(R.color.gray_light));
        }
    }

    private void setTotalPrice(ViewHolder holder, int position) {
        quantity = Integer.parseInt(holder.editProductQuantity.getText().toString());
        totalPrice = calculateTotalPrice(position);
        holder.txtTotalPrice.setText(MethodUtils.formatPriceString(totalPrice));
    }

    private double calculateTotalPrice(int position) {
        getPurchasePrice(position);
        return quantity * price;
    }

    private void updateCartItem(ViewHolder holder) {
        holder.btnCheckout.setEnabled(false);
        holder.btnCheckout.getBackground().setTint(context.getResources().getColor(R.color.gray_light));
        if (!cartProduct.getSelectedFlag()) {
            holder.checkboxCartProduct.setEnabled(false);
            holder.checkboxCartProduct.setColorFilter(context.getResources().getColor(R.color.gray_light));
        } else {
            onPriceChanging();
        }
        String token = sharedPreferences.getString("TOKEN", "");
        APICartCaller.updateCartItem(token, cartProduct, activity.getApplication(), new APIListener() {
            @Override
            public void onUpdateSuccessful() {
                holder.btnCheckout.setEnabled(true);
                holder.btnCheckout.getBackground()
                        .setTint(context.getResources().getColor(R.color.blue_main));
                if (!cartProduct.getSelectedFlag()) {
                    holder.checkboxCartProduct.setEnabled(true);
                    holder.checkboxCartProduct
                            .setColorFilter(context.getResources().getColor(R.color.gray));
                } else {
                    onPriceChanged();
                }
            }

            @Override
            public void onFailedAPICall(int code) {
                if (code == IntegerUtils.ERROR_NO_USER) {
                    MethodUtils.displayErrorAccountMessage(context, activity);
                } else {
                    MethodUtils.displayErrorAPIMessage(activity);
                    holder.btnCheckout.setEnabled(true);
                    holder.btnCheckout.getBackground()
                            .setTint(context.getResources().getColor(R.color.blue_main));
                }
            }
        });
    }

    public void onCheckboxProductChanged(String cartProductId, boolean status) {}

    public void onCheckout(int position) {}

    public void onRemoveProduct(String cartProductId) {}

    public void onPriceChanging() {}

    public void onPriceChanged() {}

    @Override
    public int getItemCount() {
        return shoppingCart.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView checkboxCartProduct, imgRecViewProduct,
                imgProductQuantityMinus, imgProductQuantityPlus;
        private final TextView txtRecViewCartProductName, txtNumberInStorage,
                txtPricingDescription, txtProductPrice, txtTotalPrice, txtCampaignTag,
                txtCampaignNote, txtProductPriceORG, txtCampaignPrice, lblCampaignNextMilestone,
                txtCampaignNextMilestone, txtCampaignPreviousMilestone, txtCampaignQuantityCount,
                txtCampaignQuantityBar, lblQuantityCountSeparator, txtCampaignCode,
                txtCampaignEndDate, lblCampaignEnded;
        private final Button btnDeleteCartProducts, btnCheckout;
        private final EditText editProductQuantity;
        private final RelativeLayout layoutParent;
        private final ConstraintLayout layoutBasePrice;
        private final LinearLayout layoutCampaign, layoutNextMilestone,
                layoutPreviousMilestone, layoutCampaignInfo;
        private final ProgressBar progressBarQuantityCount;

        public ViewHolder(View view) {
            super(view);
            checkboxCartProduct = view.findViewById(R.id.checkboxCartProduct);
            imgRecViewProduct = view.findViewById(R.id.imgRecViewProduct);
            imgProductQuantityMinus = view.findViewById(R.id.imgProductQuantityMinus);
            imgProductQuantityPlus = view.findViewById(R.id.imgProductQuantityPlus);
            txtRecViewCartProductName = view.findViewById(R.id.txtRecViewCartProductName);
            txtNumberInStorage = view.findViewById(R.id.txtNumberInStorage);
            txtPricingDescription = view.findViewById(R.id.txtPricingDescription);
            txtProductPrice = view.findViewById(R.id.txtProductPrice);
            txtTotalPrice = view.findViewById(R.id.txtTotalPrice);
            txtCampaignTag = view.findViewById(R.id.txtCampaignTag);
            txtCampaignNote = view.findViewById(R.id.txtCampaignNote);
            txtProductPriceORG = view.findViewById(R.id.txtProductPriceORG);
            txtCampaignPrice = view.findViewById(R.id.txtCampaignPrice);
            lblCampaignNextMilestone = view.findViewById(R.id.lblCampaignNextMilestone);
            txtCampaignNextMilestone = view.findViewById(R.id.txtCampaignNextMilestone);
            txtCampaignPreviousMilestone = view.findViewById(R.id.txtCampaignPreviousMilestone);
            txtCampaignQuantityCount = view.findViewById(R.id.txtCampaignQuantityCount);
            txtCampaignQuantityBar = view.findViewById(R.id.txtCampaignQuantityBar);
            lblQuantityCountSeparator = view.findViewById(R.id.lblQuantityCountSeparator);
            txtCampaignCode = view.findViewById(R.id.txtCampaignCode);
            txtCampaignEndDate = view.findViewById(R.id.txtCampaignEndDate);
            lblCampaignEnded = view.findViewById(R.id.lblCampaignEnded);
            btnDeleteCartProducts= view.findViewById(R.id.btnDeleteCartProducts);
            btnCheckout = view.findViewById(R.id.btnCheckout);
            editProductQuantity = view.findViewById(R.id.editProductQuantity);
            layoutParent = view.findViewById(R.id.layoutParent);
            layoutBasePrice = view.findViewById(R.id.layoutBasePrice);
            layoutCampaign = view.findViewById(R.id.layoutCampaign);
            layoutNextMilestone = view.findViewById(R.id.layoutNextMilestone);
            layoutPreviousMilestone = view.findViewById(R.id.layoutPreviousMilestone);
            layoutCampaignInfo = view.findViewById(R.id.layoutCampaignInfo);
            progressBarQuantityCount = view.findViewById(R.id.progressBarQuantityCount);
        }
    }
}
