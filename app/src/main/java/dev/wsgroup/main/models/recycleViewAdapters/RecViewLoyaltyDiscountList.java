package dev.wsgroup.main.models.recycleViewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.LoyaltyStatus;

public class RecViewLoyaltyDiscountList extends RecyclerView.Adapter<RecViewLoyaltyDiscountList.ViewHolder> {

    private Context context;
    private List<LoyaltyStatus> loyaltyStatusList;

    public RecViewLoyaltyDiscountList(Context context) {
        this.context = context;
    }

    public void setLoyaltyStatusList(List<LoyaltyStatus> loyaltyStatusList) {
        this.loyaltyStatusList = loyaltyStatusList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.recycle_view_loyalty_discount_list, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecViewLoyaltyDiscountList.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return loyaltyStatusList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtSupplierName, txtDiscountPrice;

        public ViewHolder(View view) {
            super(view);
            txtSupplierName = view.findViewById(R.id.txtSupplierName);
            txtDiscountPrice = view.findViewById(R.id.txtDiscountPrice);
        }
    }
}
