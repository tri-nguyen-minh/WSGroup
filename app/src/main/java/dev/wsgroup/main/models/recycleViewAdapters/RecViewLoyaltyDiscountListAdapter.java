package dev.wsgroup.main.models.recycleViewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.LoyaltyStatus;
import dev.wsgroup.main.models.utils.MethodUtils;

public class RecViewLoyaltyDiscountListAdapter
        extends RecyclerView.Adapter<RecViewLoyaltyDiscountListAdapter.ViewHolder> {

    private Context context;
    private List<LoyaltyStatus> loyaltyStatusList;
    private LoyaltyStatus loyaltyStatus;

    public RecViewLoyaltyDiscountListAdapter(Context context) {
        this.context = context;
    }

    public void setLoyaltyStatusList(List<LoyaltyStatus> loyaltyStatusList) {
        this.loyaltyStatusList = loyaltyStatusList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.recycle_view_loyalty_discount_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecViewLoyaltyDiscountListAdapter.ViewHolder holder, int position) {
        loyaltyStatus = loyaltyStatusList.get(position);
        holder.txtSupplierName.setText(loyaltyStatus.getSupplier().getName());
        holder.txtDiscountPrice
              .setText(MethodUtils.formatPriceString(loyaltyStatus.getDiscountPrice()));
    }

    @Override
    public int getItemCount() {
        return loyaltyStatusList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtSupplierName, txtDiscountPrice;

        public ViewHolder(View view) {
            super(view);
            txtSupplierName = view.findViewById(R.id.txtSupplierName);
            txtDiscountPrice = view.findViewById(R.id.txtDiscountPrice);
        }
    }
}
