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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APICampaignCaller;
import dev.wsgroup.main.models.apis.callers.APIProductCaller;
import dev.wsgroup.main.models.apis.callers.APIReviewCaller;
import dev.wsgroup.main.models.apis.callers.APISupplierCaller;
import dev.wsgroup.main.models.dtos.Campaign;
import dev.wsgroup.main.models.dtos.LoyaltyStatus;
import dev.wsgroup.main.models.dtos.Product;
import dev.wsgroup.main.models.dtos.Review;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.views.activities.productviews.PrepareProductActivity;
import dev.wsgroup.main.views.activities.productviews.ProductDetailActivity;
import dev.wsgroup.main.views.dialogbox.DialogBoxLoading;

public class RecViewCampaignSearchAdapter
        extends RecyclerView.Adapter<RecViewCampaignSearchAdapter.ViewHolder> {

    private Context context;
    private Activity activity;
    private List<Campaign> campaignList;
    private double productPrice;
    private int quantity;
    private String headerString;
    private DialogBoxLoading dialogBoxLoading;

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
        holder.txtHeader.setText("");
        try {
            if (MethodUtils.checkCampaignEndDate(campaignList.get(position))) {
                holder.txtHeader.setText("Less than 3 days remaining");
            } else if (MethodUtils.checkCampaignQuantity(campaignList.get(position)) > 0) {
                headerString = MethodUtils.checkCampaignQuantity(campaignList.get(position)) + " ";
                headerString +=  "more to complete";
                holder.txtHeader.setText(headerString);
            }
        } catch (Exception e) {
            holder.txtHeader.setText("");
            e.printStackTrace();
        }
        if (!campaignList.get(position).getStatus().equals("active")) {
            holder.btnSelect.setVisibility(View.GONE);
            if (holder.txtHeader.getText().toString().isEmpty()) {
                holder.txtHeader.setText("UPCOMING CAMPAIGN");
            }
        } else {
            holder.btnSelect.setVisibility(View.VISIBLE);
            holder.btnSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    executeOnCampaignSelected(campaignList.get(position));
                }
            });
            if (holder.txtHeader.getText().toString().isEmpty()) {
                holder.txtHeader.setText("ONGOING CAMPAIGN");
            }
        }
        holder.txtCampaignNote.setText(campaignList.get(position).getDescription());
        holder.txtCampaignMaxQuantity.setText(campaignList.get(position).getMaxQuantity() + "");
        holder.txtDiscountStartDate
                .setText(MethodUtils.formatDate(campaignList.get(position).getStartDate()));
        holder.txtDiscountEndDate
                .setText(MethodUtils.formatDate(campaignList.get(position).getEndDate()));
        if(campaignList.get(position).getProduct().getImageList().size() > 0) {
            Glide.with(context)
                    .load(campaignList.get(position).getProduct().getImageList().get(0))
                    .into(holder.imgRecViewProduct);
        }
        holder.txtProductName.setText(campaignList.get(position).getProduct().getName());
        holder.txtSupplier.setText(campaignList.get(position).getProduct().getSupplier().getId());
        if (campaignList.get(position).getShareFlag()) {
            holder.txtProductPrice.setText("DOWN TO");
            holder.txtCampaignPrice
                    .setText(MethodUtils.formatPriceString(campaignList.get(position).getPrice()));
            quantity = campaignList.get(position).getMaxQuantity()
                    - campaignList.get(position).getQuantityCount();
            holder.lblQuantityCount.setText("Remaining:");
            holder.txtCampaignQuantity.setText(quantity + "");
            holder.layoutCampaignInfo.setVisibility(View.VISIBLE);
            holder.txtCampaignOrderCount.setText(campaignList.get(position).getOrderCount() + "");
            holder.txtCampaignQuantityCount.setText(campaignList.get(position).getQuantityCount() + "");
            holder.txtCampaignQuantityBar.setText(campaignList.get(position).getMaxQuantity() + "");
            holder.lblProductOrderCount
                    .setText((campaignList.get(position).getOrderCount() == 1) ? "order" : "orders");
            holder.lblQuantityCountSeparator.setText("/");
            holder.progressBarQuantityCount.setMax(campaignList.get(position).getMaxQuantity());
            holder.progressBarQuantityCount.setProgress(campaignList.get(position).getQuantityCount());
            holder.progressBarQuantityCount.getProgressDrawable().setColorFilter(
                    context.getResources().getColor(R.color.blue_main),
                    android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            productPrice = campaignList.get(position).getProduct().getRetailPrice();
            holder.txtProductPrice.setText(MethodUtils.formatPriceString(productPrice));
            holder.txtProductPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.txtCampaignPrice
                    .setText(MethodUtils.formatPriceString(campaignList.get(position).getPrice()));
            holder.lblQuantityCount.setText("Minimum:");
            holder.txtCampaignQuantity.setText(campaignList.get(position).getMinQuantity() + "");
            holder.layoutCampaignInfo.setVisibility(View.GONE);
        }
        holder.cardViewParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBoxLoading = new DialogBoxLoading(activity);
                dialogBoxLoading.getWindow()
                                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBoxLoading.show();
                String productId = campaignList.get(position).getProduct().getProductId();
                Intent intent = new Intent(activity.getApplicationContext(),
                        ProductDetailActivity.class);
                intent.putExtra("PRODUCT_ID", productId);
                if (dialogBoxLoading.isShowing()) {
                    dialogBoxLoading.dismiss();
                }
                activity.startActivityForResult(intent, IntegerUtils.REQUEST_COMMON);
            }
        });
    }

    public void executeOnCampaignSelected(Campaign campaign) { }

    @Override
    public int getItemCount() {
        return campaignList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardViewParent;
        private TextView txtCampaignNote, txtProductPrice, txtCampaignPrice, txtCampaignTag,
                txtDiscountStartDate, txtDiscountEndDate, lblQuantityCount, txtCampaignQuantity,
                txtCampaignMaxQuantity, txtCampaignOrderCount, txtCampaignQuantityCount,
                txtCampaignQuantityBar, lblProductOrderCount, lblQuantityCountSeparator,
                txtHeader, txtProductName, txtSupplier;
        private ConstraintLayout layoutHeader;
        private LinearLayout layoutCampaignInfo;
        private ProgressBar progressBarQuantityCount;
        private Button btnSelect;
        private ImageView imgRecViewProduct;


        public ViewHolder(View view) {
            super(view);
            cardViewParent = view.findViewById(R.id.cardViewParent);
            txtCampaignNote = view.findViewById(R.id.txtCampaignNote);
            txtProductPrice = view.findViewById(R.id.txtProductPrice);
            txtCampaignPrice = view.findViewById(R.id.txtCampaignPrice);
            txtCampaignTag = view.findViewById(R.id.txtCampaignTag);
            lblQuantityCount = view.findViewById(R.id.lblQuantityCount);
            txtCampaignQuantity = view.findViewById(R.id.txtCampaignQuantity);
            txtCampaignMaxQuantity = view.findViewById(R.id.txtCampaignMaxQuantity);
            txtDiscountStartDate = view.findViewById(R.id.txtDiscountStartDate);
            txtDiscountEndDate = view.findViewById(R.id.txtDiscountEndDate);
            txtCampaignOrderCount = view.findViewById(R.id.txtCampaignOrderCount);
            txtCampaignQuantityCount = view.findViewById(R.id.txtCampaignQuantityCount);
            txtCampaignQuantityBar = view.findViewById(R.id.txtCampaignQuantityBar);
            lblProductOrderCount = view.findViewById(R.id.lblProductOrderCount);
            lblQuantityCountSeparator = view.findViewById(R.id.lblQuantityCountSeparator);
            txtHeader = view.findViewById(R.id.txtHeader);
            txtProductName = view.findViewById(R.id.txtProductName);
            txtSupplier = view.findViewById(R.id.txtSupplier);
            layoutHeader = view.findViewById(R.id.layoutHeader);
            layoutCampaignInfo = view.findViewById(R.id.layoutCampaignInfo);
            progressBarQuantityCount = view.findViewById(R.id.progressBarQuantityCount);
            btnSelect = view.findViewById(R.id.btnSelect);
            imgRecViewProduct = view.findViewById(R.id.imgRecViewProduct);
        }
    }
}
