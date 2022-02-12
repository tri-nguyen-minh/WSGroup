package dev.wsgroup.main.models.recycleViewAdapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.Campaign;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;

public class RecViewCampaignListAdapter extends RecyclerView.Adapter<RecViewCampaignListAdapter.ViewHolder> {

    private int code;
    private Context context;
    private Activity activity;
    private Campaign selectedCampaign;
    private List<Campaign> campaignList;

    public RecViewCampaignListAdapter(Context context, Activity activity, int code, Campaign campaign) {
        this.context = context;
        this.activity = activity;
        this.code = code;
        selectedCampaign = campaign;
    }

    public void setCampaignList(List<Campaign> campaignList) {
        this.campaignList = campaignList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_campaign_list, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.lblCampaignQuantitySeparator.setText("/");
        if(code == IntegerUtils.CAMPAIGN_LIST_VIEW) {
            holder.layoutRecViewCampaign.setBackground(context.getResources().getDrawable(R.color.blue_main));
            holder.constraintLayoutCampaign.setBackground(context.getResources().getDrawable(R.color.white));
            holder.layoutRecViewCampaign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    executeOnCampaignSelected(campaignList.get(position));
                }
            });
            holder.constraintLayoutCampaign.setVisibility(View.VISIBLE);
            holder.txtCampaignPrice.setText(MethodUtils.formatPriceString(campaignList.get(position).getPrice()));
            holder.txtCampaignQuantity.setText(campaignList.get(position).getQuantity() + "");
            holder.txtDiscountEndDate.setText(MethodUtils.formatDate(campaignList.get(position).getEndDate()));
            holder.txtCampaignOrderCount.setText(campaignList.get(position).getOrderCount() + "");
            holder.txtCampaignQuantityCount.setText(campaignList.get(position).getQuantityCount() + "");
            holder.lblProductOrderCount.setText((campaignList.get(position).getOrderCount() == 1) ? "order" : "orders");
            holder.progressBarQuantityCount.setMax(campaignList.get(position).getQuantity());
            holder.progressBarQuantityCount.setProgress(campaignList.get(position).getQuantityCount());
            holder.progressBarQuantityCount.getProgressDrawable().setColorFilter(
                    context.getResources().getColor(R.color.blue_main), android.graphics.PorterDuff.Mode.SRC_IN);
            holder.txtCampaignQuantityBar.setText(campaignList.get(position).getQuantity() + "");
        } else {
            if(campaignList.get(position).getId() == null) {

            } else {

            }
        }

    }

    @Override
    public int getItemCount() {
        return campaignList.size();
    }

    public void executeOnCampaignSelected(Campaign campaign) {}

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView lblCampaignQuantitySeparator, txtCampaignPrice, txtCampaignQuantity,
                txtDiscountEndDate, txtCampaignOrderCount, txtCampaignQuantityCount,
                txtCampaignQuantityBar, lblProductOrderCount;
        private ProgressBar progressBarQuantityCount;
        private ConstraintLayout constraintLayoutCampaign;

        private RelativeLayout layoutRecViewCampaign;


        public ViewHolder(View view) {
            super(view);
            lblCampaignQuantitySeparator = view.findViewById(R.id.lblCampaignQuantitySeparator);
            txtCampaignPrice = view.findViewById(R.id.txtCampaignPrice);
            txtCampaignQuantity = view.findViewById(R.id.txtCampaignQuantity);
            txtDiscountEndDate = view.findViewById(R.id.txtDiscountEndDate);
            txtCampaignOrderCount = view.findViewById(R.id.txtCampaignOrderCount);
            txtCampaignQuantityCount = view.findViewById(R.id.txtCampaignQuantityCount);
            txtCampaignQuantityBar = view.findViewById(R.id.txtCampaignQuantityBar);
            lblProductOrderCount = view.findViewById(R.id.lblProductOrderCount);
            progressBarQuantityCount = view.findViewById(R.id.progressBarQuantityCount);
            constraintLayoutCampaign = view.findViewById(R.id.constraintLayoutCampaign);
            layoutRecViewCampaign = view.findViewById(R.id.layoutRecViewCampaign);
        }
    }
}
