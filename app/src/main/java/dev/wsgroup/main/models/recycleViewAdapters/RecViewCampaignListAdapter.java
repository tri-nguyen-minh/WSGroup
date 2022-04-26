package dev.wsgroup.main.models.recycleViewAdapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.Campaign;
import dev.wsgroup.main.models.utils.MethodUtils;

public class RecViewCampaignListAdapter
        extends RecyclerView.Adapter<RecViewCampaignListAdapter.ViewHolder> {

    private Context context;
    private List<Campaign> campaignList;
    private double productPrice;

    public RecViewCampaignListAdapter(Context context, double productPrice) {
        this.context = context;
        this.productPrice = productPrice;
    }

    public void setCampaignList(List<Campaign> campaignList) {
        this.campaignList = campaignList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.recycle_view_campaign_list, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtCampaignNote.setText(campaignList.get(position).getDescription());
        holder.txtDiscountStartDate
                .setText(MethodUtils.formatDate(campaignList.get(position).getStartDate()));
        holder.txtDiscountEndDate
                .setText(MethodUtils.formatDate(campaignList.get(position).getEndDate()));
        holder.txtCampaignMaxQuantity.setText(campaignList.get(position).getMaxQuantity() + "");
        holder.txtCampaignQuantity.setText(campaignList.get(position).getMinQuantity() + "");
        holder.txtProductPrice.setText(MethodUtils.formatPriceString(productPrice));
        holder.txtProductPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        holder.txtCampaignPrice
                .setText(MethodUtils.formatPriceString(campaignList.get(position).getPrice()));
        holder.btnSelect.setVisibility(View.VISIBLE);
        holder.btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executeOnCampaignSelected(campaignList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return campaignList.size();
    }

    public void executeOnCampaignSelected(Campaign campaign) {}

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtCampaignNote, txtProductPrice, txtCampaignPrice, txtDiscountStartDate,
                txtDiscountEndDate, txtCampaignQuantity, txtCampaignMaxQuantity;
        private Button btnSelect;


        public ViewHolder(View view) {
            super(view);
            txtCampaignNote = view.findViewById(R.id.txtCampaignNote);
            txtProductPrice = view.findViewById(R.id.txtProductPrice);
            txtCampaignPrice = view.findViewById(R.id.txtCampaignPrice);
            txtCampaignQuantity = view.findViewById(R.id.txtCampaignQuantity);
            txtCampaignMaxQuantity = view.findViewById(R.id.txtCampaignMaxQuantity);
            txtDiscountStartDate = view.findViewById(R.id.txtDiscountStartDate);
            txtDiscountEndDate = view.findViewById(R.id.txtDiscountEndDate);
            btnSelect = view.findViewById(R.id.btnSelect);
        }
    }
}
