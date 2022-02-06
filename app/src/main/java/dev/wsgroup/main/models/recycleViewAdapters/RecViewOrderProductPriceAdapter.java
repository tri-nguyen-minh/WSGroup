package dev.wsgroup.main.models.recycleViewAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.OrderProduct;
import dev.wsgroup.main.models.utils.MethodUtils;

public class RecViewOrderProductPriceAdapter extends RecyclerView.Adapter<RecViewOrderProductPriceAdapter.ViewHolder> {

    private List<OrderProduct> orderProductList;

    public RecViewOrderProductPriceAdapter() {
    }

    public void setOrderProductList(List<OrderProduct> orderProductList) {
        this.orderProductList = orderProductList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_ordering_product_price, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtProductName.setText(orderProductList.get(position).getProduct().getName());
        holder.txtProductPrice.setText(MethodUtils.convertPriceString(orderProductList.get(position).getPrice()));
    }

    @Override
    public int getItemCount() {
        return orderProductList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtProductName, txtProductPrice;

        public ViewHolder(View view) {
            super(view);
            txtProductName = view.findViewById(R.id.txtProductName);
            txtProductPrice = view.findViewById(R.id.txtProductPrice);
        }
    }
}
