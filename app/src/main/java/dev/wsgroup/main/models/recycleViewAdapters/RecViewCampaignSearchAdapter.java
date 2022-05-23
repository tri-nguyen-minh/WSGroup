package dev.wsgroup.main.models.recycleViewAdapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.Campaign;
import dev.wsgroup.main.models.dtos.CampaignMilestone;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.views.activities.productviews.ProductDetailActivity;
import dev.wsgroup.main.views.dialogbox.DialogBoxLoading;

public class RecViewCampaignSearchAdapter
        extends RecyclerView.Adapter<RecViewCampaignSearchAdapter.ViewHolder> {

    private Context context;
    private Activity activity;
    private List<Campaign> campaignList;
    private Campaign campaign;
    private double productPrice;
    private int quantity;

    public RecViewCampaignSearchAdapter(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        campaignList = new ArrayList<>();
    }

    public void setCampaignList(List<Campaign> campaignList) {
        this.campaignList = campaignList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.recycle_view_search_campaign, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        campaign = campaignList.get(position);
        holder.btnSelect.setVisibility(View.VISIBLE);
        holder.btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCampaignSelected(campaignList.get(position));
            }
        });
        holder.txtCampaignNote.setText(campaign.getDescription());
        holder.txtCampaignCode.setText(campaign.getCode());
        holder.txtCampaignMaxQuantity.setText(campaign.getMaxQuantity() + "");
        holder.txtDiscountStartDate
              .setText(MethodUtils.formatDate(campaign.getStartDate(), false));
        holder.txtDiscountEndDate
              .setText(MethodUtils.formatDate(campaign.getEndDate(), false));
        if(campaign.getProduct().getImageList().size() > 0) {
            Glide.with(context).load(campaign.getProduct().getImageList().get(0))
                               .into(holder.imgRecViewProduct);
        }
        holder.txtProductName.setText(campaign.getProduct().getName());
        holder.txtSupplier.setText(campaign.getProduct().getSupplier().getId());
        if (campaign.getShareFlag()) {
            holder.txtProductPrice.setText("DOWN TO");
            CampaignMilestone milestone
                    = MethodUtils.getLastCampaignMilestone(campaign.getMilestoneList());
            holder.txtCampaignPrice
                  .setText(MethodUtils.formatPriceString(milestone.getPrice()));
            quantity = campaign.getMaxQuantity() - campaign.getQuantityCount();
            holder.lblQuantityCount.setText("Remaining:");
            holder.txtCampaignQuantity.setText(quantity + "");
            holder.layoutCampaignInfo.setVisibility(View.VISIBLE);
            holder.txtCampaignOrderCount.setText(campaign.getOrderCount() + "");
            holder.txtCampaignQuantityCount.setText(campaign.getQuantityCount() + "");
            holder.txtCampaignQuantityBar.setText(campaign.getMaxQuantity() + "");
            holder.lblProductOrderCount.setText((campaign.getOrderCount() == 1) ? "order" : "orders");
            holder.lblQuantityCountSeparator.setText("/");
            holder.progressBarQuantityCount.setMax(campaign.getMaxQuantity());
            holder.progressBarQuantityCount.setProgress(campaign.getQuantityCount());
            holder.progressBarQuantityCount
                  .getProgressDrawable()
                  .setColorFilter(context.getResources().getColor(R.color.blue_main),
                                  android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            productPrice = campaign.getProduct().getRetailPrice();
            holder.txtProductPrice.setText(MethodUtils.formatPriceString(productPrice));
            holder.txtProductPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.txtCampaignPrice
                  .setText(MethodUtils.formatPriceString(campaign.getPrice()));
            holder.lblQuantityCount.setText("Minimum:");
            holder.txtCampaignQuantity.setText(campaign.getMinQuantity() + "");
            holder.layoutCampaignInfo.setVisibility(View.GONE);
        }
        holder.cardViewParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String productId = campaignList.get(position).getProduct().getProductId();
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("PRODUCT_ID", productId);
                activity.startActivityForResult(intent, IntegerUtils.REQUEST_COMMON);
            }
        });
    }

    public void onCampaignSelected(Campaign campaign) { }

    @Override
    public int getItemCount() {
        return campaignList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardViewParent;
        private final TextView txtCampaignNote, txtProductPrice, txtCampaignPrice,
                txtDiscountStartDate, txtDiscountEndDate, lblQuantityCount, txtCampaignQuantity,
                txtCampaignMaxQuantity, txtCampaignOrderCount, txtCampaignQuantityCount,
                txtCampaignQuantityBar, lblProductOrderCount, lblQuantityCountSeparator,
                txtProductName, txtSupplier, txtCampaignCode;
        private final LinearLayout layoutCampaignInfo;
        private final ProgressBar progressBarQuantityCount;
        private final Button btnSelect;
        private final ImageView imgRecViewProduct;


        public ViewHolder(View view) {
            super(view);
            cardViewParent = view.findViewById(R.id.cardViewParent);
            txtCampaignNote = view.findViewById(R.id.txtCampaignNote);
            txtProductPrice = view.findViewById(R.id.txtProductPrice);
            txtCampaignPrice = view.findViewById(R.id.txtCampaignPrice);
            txtDiscountStartDate = view.findViewById(R.id.txtDiscountStartDate);
            txtDiscountEndDate = view.findViewById(R.id.txtDiscountEndDate);
            lblQuantityCount = view.findViewById(R.id.lblQuantityCount);
            txtCampaignQuantity = view.findViewById(R.id.txtCampaignQuantity);
            txtCampaignMaxQuantity = view.findViewById(R.id.txtCampaignMaxQuantity);
            txtCampaignOrderCount = view.findViewById(R.id.txtCampaignOrderCount);
            txtCampaignQuantityCount = view.findViewById(R.id.txtCampaignQuantityCount);
            txtCampaignQuantityBar = view.findViewById(R.id.txtCampaignQuantityBar);
            lblProductOrderCount = view.findViewById(R.id.lblProductOrderCount);
            lblQuantityCountSeparator = view.findViewById(R.id.lblQuantityCountSeparator);
            txtProductName = view.findViewById(R.id.txtProductName);
            txtSupplier = view.findViewById(R.id.txtSupplier);
            txtCampaignCode = view.findViewById(R.id.txtCampaignCode);
            layoutCampaignInfo = view.findViewById(R.id.layoutCampaignInfo);
            progressBarQuantityCount = view.findViewById(R.id.progressBarQuantityCount);
            btnSelect = view.findViewById(R.id.btnSelect);
            imgRecViewProduct = view.findViewById(R.id.imgRecViewProduct);
        }
    }
}
