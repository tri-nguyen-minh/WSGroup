package dev.wsgroup.main.models.recycleViewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.Campaign;
import dev.wsgroup.main.models.dtos.CampaignMilestone;
import dev.wsgroup.main.models.utils.MethodUtils;

public class RecViewCampaignMilestoneListAdapter
        extends RecyclerView.Adapter<RecViewCampaignMilestoneListAdapter.ViewHolder> {

    private double retailPrice, discountPrice;
    private Campaign campaign;
    private List<CampaignMilestone> milestoneList;
    private Context context;

    public RecViewCampaignMilestoneListAdapter(Context context, double retailPrice) {
        this.context = context;
        this.retailPrice = retailPrice;
        campaign = new Campaign();
        milestoneList = new ArrayList<>();
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
        milestoneList = campaign.getMilestoneList();
        if (milestoneList == null) {
            milestoneList = new ArrayList<>();
        }
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.recycle_view_campaign_milestone_list, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtCampaignNextMilestone.setText(milestoneList.get(position).getQuantity() + "");
        holder.txtCampaignPrice
              .setText(MethodUtils.formatPriceString(milestoneList.get(position).getPrice()));
        discountPrice = retailPrice - milestoneList.get(position).getPrice();
        holder.txtDiscountPrice.setText(MethodUtils.formatPriceString(discountPrice));
        if (campaign.getQuantityCount() >= milestoneList.get(position).getQuantity()) {
            holder.layoutParent.setBackgroundColor(context.getResources().getColor(R.color.gray_lighter));
            holder.imgCheck.setImageResource(R.drawable.ic_checkbox_checked);
            holder.imgCheck.setColorFilter(context.getResources().getColor(R.color.blue_main));
        } else {
            holder.layoutParent.setBackgroundColor(context.getResources().getColor(R.color.white));
            holder.imgCheck.setImageResource(R.drawable.ic_checkbox_unchecked);
            holder.imgCheck.setColorFilter(context.getResources().getColor(R.color.gray));
        }
    }

    @Override
    public int getItemCount() {
        return milestoneList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtCampaignNextMilestone, txtDiscountPrice, txtCampaignPrice;
        private ImageView imgCheck;
        private FrameLayout layoutParent;

        public ViewHolder(View view) {
            super(view);
            txtCampaignNextMilestone = view.findViewById(R.id.txtCampaignNextMilestone);
            txtDiscountPrice = view.findViewById(R.id.txtDiscountPrice);
            txtCampaignPrice = view.findViewById(R.id.txtCampaignPrice);
            imgCheck = view.findViewById(R.id.imgCheck);
            layoutParent = view.findViewById(R.id.layoutParent);
        }
    }
}
