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
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtRecViewCartProductName.setText(shoppingCart.get(position).getProduct().getName());
        if(shoppingCart.get(position).getProduct().getImageList().size() > 0) {
            Glide.with(context)
                 .load(shoppingCart.get(position).getProduct().getImageList().get(0))
                 .into(holder.imgRecViewProduct);
        }
        if (identifier == IntegerUtils.IDENTIFIER_RETAIL_CART) {
            holder.layoutBasePrice.setVisibility(View.VISIBLE);
            holder.layoutCampaign.setVisibility(View.GONE);
            holder.checkboxCartProduct.setVisibility(View.VISIBLE);
            setCheckboxSelected(holder, shoppingCart.get(position).getSelectedFlag());
            holder.txtPricingDescription.setText("Retail Price");
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
                    onCheckboxProductChanged(shoppingCart.get(position).getId(), currentStatus);
                }
            });
        } else {
            campaign = shoppingCart.get(position).getCampaign();
            price = shoppingCart.get(position).getProduct().getRetailPrice();
            holder.txtProductPriceORG.setText(MethodUtils.formatPriceString(price));
            holder.txtProductPriceORG.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.layoutBasePrice.setVisibility(View.GONE);
            holder.layoutCampaign.setVisibility(View.VISIBLE);
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
            holder.btnCheckout.setVisibility(View.VISIBLE);
            holder.btnCheckout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onCheckout(position);
                }
            });
        }
        holder.txtProductPrice.setText(MethodUtils.formatPriceString(price));
        quantity = shoppingCart.get(position).getQuantity();
        minQuantity = getMinQuantity(position);
        maxQuantity = getMaxQuantity(position);
        if (quantity < minQuantity) {
            quantity = minQuantity;
        } else if (quantity > maxQuantity) {
            quantity = maxQuantity;
        }
        holder.editProductQuantity.setText(quantity + "");
        holder.txtNumberInStorage.setText(maxQuantity + "");
        updateSharingCampaignLayout(holder, position);
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
                    minQuantity = getMinQuantity(position);
                    maxQuantity = getMaxQuantity(position);
                    quantity = Integer.parseInt(holder.editProductQuantity.getText().toString());
                    if (quantity < minQuantity) {
                        holder.editProductQuantity.setText(minQuantity + "");
                    } else if (quantity > maxQuantity) {
                        holder.editProductQuantity.setText(maxQuantity + "");
                    } else {
                        CartProduct cartProduct = shoppingCart.get(position);
                        cartProduct.setQuantity(quantity);
                        setupQuantityButtons(holder);
                        updateSharingCampaignLayout(holder, position);
                        setTotalPrice(holder, position);
                        updateCartItem(cartProduct, holder);
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

    private void updateSharingCampaignLayout(ViewHolder holder, int position) {
        if (identifier == IntegerUtils.IDENTIFIER_CAMPAIGN_CART) {
            campaign = shoppingCart.get(position).getCampaign();
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
                quantity = shoppingCart.get(position).getQuantity() + campaign.getQuantityCount();
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

    private double calculateTotalPrice(int position) {
        getPurchasePrice(position);
        return quantity * price;
    }

    private void setTotalPrice(ViewHolder holder, int position) {
        quantity = Integer.parseInt(holder.editProductQuantity.getText().toString());
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
        int quantity = 1;
        if (shoppingCart.get(position).getCampaignFlag()) {
            Campaign campaign = shoppingCart.get(position).getCampaign();
            quantity = campaign.getMinQuantity();
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

    public void onRemoveProduct(String cartProductId) {}

    public void onCheckboxProductChanged(String cartProductId, boolean status) {}

    public void onPriceChanging() {}

    public void onPriceChanged() {}

    public void onCheckout(int position) {
    }

    @Override
    public int getItemCount() {
        return shoppingCart.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView checkboxCartProduct, imgRecViewProduct,
                imgProductQuantityMinus, imgProductQuantityPlus;
        private TextView txtRecViewCartProductName, txtNumberInStorage, txtPricingDescription,
                txtProductPrice, txtTotalPrice, txtCampaignTag, txtCampaignNote,
                txtProductPriceORG, txtCampaignPrice, lblCampaignNextMilestone,
                txtCampaignNextMilestone, txtCampaignPreviousMilestone,
                txtCampaignQuantityCount, txtCampaignQuantityBar, lblQuantityCountSeparator;
        private Button btnDeleteCartProducts, btnCheckout;
        private EditText editProductQuantity;
        private RelativeLayout layoutParent;
        private ConstraintLayout layoutBasePrice;
        private LinearLayout layoutCampaign, layoutNextMilestone,
                layoutPreviousMilestone, layoutCampaignInfo;
        private ProgressBar progressBarQuantityCount;

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
