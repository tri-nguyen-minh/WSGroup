package dev.wsgroup.main.models.recycleViewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.Campaign;
import dev.wsgroup.main.models.utils.MethodUtils;

public class RecViewCampaignListAdapter extends RecyclerView.Adapter<RecViewCampaignListAdapter.ViewHolder> {

    private Context context;
    private List<Campaign> campaignList;

    public RecViewCampaignListAdapter(Context context) {
        this.context = context;
    }

    public void setCampaignList(List<Campaign> campaignList) {
        this.campaignList = campaignList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_view_campaign_list, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtCampaignNote.setText(campaignList.get(position).getDescription());
        holder.txtCampaignPrice.setText(MethodUtils.formatPriceString(campaignList.get(position).getSavingPrice()));
        holder.txtCampaignTag.setText(campaignList.get(position).getShareFlag() ? "Sharing Campaign" : "Single Campaign");
        holder.txtCampaignQuantity.setText(campaignList.get(position).getMinQuantity() + "");
        holder.txtCampaignMaxQuantity.setText(campaignList.get(position).getMaxQuantity() + "");
        holder.txtDiscountEndDate.setText(MethodUtils.formatDate(campaignList.get(position).getEndDate()));
        holder.txtCampaignOrderCount.setText(campaignList.get(position).getOrderCount() + "");
        holder.txtCampaignQuantityCount.setText(campaignList.get(position).getQuantityCount() + "");
        holder.txtCampaignQuantityBar.setText(campaignList.get(position).getMinQuantity() + "");
        holder.lblProductOrderCount.setText((campaignList.get(position).getOrderCount() == 1) ? "order" : "orders");
        holder.lblProductOrderCountSeparator.setText("/");
        holder.progressBarQuantityCount.setMax(campaignList.get(position).getMinQuantity());
        holder.progressBarQuantityCount.setProgress(campaignList.get(position).getQuantityCount());
        holder.progressBarQuantityCount.getProgressDrawable().setColorFilter(
                context.getResources().getColor(R.color.blue_main), android.graphics.PorterDuff.Mode.SRC_IN);
        if (!campaignList.get(position).getStatus().equals("active")) {
            holder.btnSelect.setVisibility(View.GONE);
        } else {
            holder.btnSelect.setVisibility(View.VISIBLE);
            holder.btnSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    executeOnCampaignSelected(campaignList.get(position));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return campaignList.size();
    }

    public void executeOnCampaignSelected(Campaign campaign) {}

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtCampaignNote, txtCampaignPrice, txtCampaignTag, txtDiscountEndDate,
                txtCampaignQuantity, txtCampaignMaxQuantity, txtCampaignOrderCount, txtCampaignQuantityCount,
                txtCampaignQuantityBar, lblProductOrderCount, lblProductOrderCountSeparator;
        private ProgressBar progressBarQuantityCount;
        private Button btnSelect;


        public ViewHolder(View view) {
            super(view);
            txtCampaignNote = view.findViewById(R.id.txtCampaignNote);
            txtCampaignPrice = view.findViewById(R.id.txtCampaignPrice);
            txtCampaignTag = view.findViewById(R.id.txtCampaignTag);
            txtCampaignQuantity = view.findViewById(R.id.txtCampaignQuantity);
            txtCampaignMaxQuantity = view.findViewById(R.id.txtCampaignMaxQuantity);
            txtDiscountEndDate = view.findViewById(R.id.txtDiscountEndDate);
            txtCampaignOrderCount = view.findViewById(R.id.txtCampaignOrderCount);
            txtCampaignQuantityCount = view.findViewById(R.id.txtCampaignQuantityCount);
            txtCampaignQuantityBar = view.findViewById(R.id.txtCampaignQuantityBar);
            lblProductOrderCount = view.findViewById(R.id.lblProductOrderCount);
            lblProductOrderCountSeparator = view.findViewById(R.id.lblProductOrderCountSeparator);
            progressBarQuantityCount = view.findViewById(R.id.progressBarQuantityCount);
            btnSelect = view.findViewById(R.id.btnSelect);
        }
    }
}
