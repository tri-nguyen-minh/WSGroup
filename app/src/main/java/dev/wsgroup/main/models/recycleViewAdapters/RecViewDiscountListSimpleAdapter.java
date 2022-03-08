package dev.wsgroup.main.models.recycleViewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.Discount;
import dev.wsgroup.main.models.utils.MethodUtils;

public class RecViewDiscountListSimpleAdapter
        extends RecyclerView.Adapter<RecViewDiscountListSimpleAdapter.ViewHolder> {

    private List<Discount> discountList;
    private Context context;

    public RecViewDiscountListSimpleAdapter(Context context) {
        this.context = context;
    }

    public void setDiscountList(List<Discount> discountList) {
        this.discountList = discountList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.recycle_view_discount_list_simple, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtDiscountDescription.setText(discountList.get(position).getDescription());
        holder.txtSupplierName.setText(discountList.get(position).getSupplier().getName());
        holder.txtDiscountPrice
                .setText(MethodUtils.formatPriceString(discountList.get(position).getDiscountPrice()));
    }

    @Override
    public int getItemCount() {
        return discountList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtDiscountDescription, txtSupplierName, txtDiscountPrice;


        public ViewHolder(View view) {
            super(view);
            txtDiscountDescription = view.findViewById(R.id.txtDiscountDescription);
            txtSupplierName = view.findViewById(R.id.txtSupplierName);
            txtDiscountPrice = view.findViewById(R.id.txtDiscountPrice);
        }
    }
}
