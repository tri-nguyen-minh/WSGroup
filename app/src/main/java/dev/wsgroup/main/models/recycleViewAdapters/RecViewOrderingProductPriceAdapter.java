package dev.wsgroup.main.models.recycleViewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.OrderProduct;
import dev.wsgroup.main.models.utils.MethodUtils;

public class RecViewOrderingProductPriceAdapter extends RecyclerView.Adapter<RecViewOrderingProductPriceAdapter.ViewHolder> {

    private Context context;
    private List<OrderProduct> orderProductList;

    public RecViewOrderingProductPriceAdapter(Context context) {
        this.context = context;
    }

    public void setOrderProductList(List<OrderProduct> orderProductList) {
        this.orderProductList = orderProductList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_view_ordering_product_price, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtProductName.setText(orderProductList.get(position).getProduct().getName());
        double price = orderProductList.get(position).getQuantity();
        price *= orderProductList.get(position).getPrice();
        holder.txtProductPrice.setText(MethodUtils.formatPriceString(price));
        holder.txtQuantityCount.setText(orderProductList.get(position).getQuantity() + "");
    }

    @Override
    public int getItemCount() {
        return orderProductList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtProductName, txtProductPrice, txtQuantityCount;

        public ViewHolder(View view) {
            super(view);
            txtProductName = view.findViewById(R.id.txtProductName);
            txtProductPrice = view.findViewById(R.id.txtProductPrice);
            txtQuantityCount = view.findViewById(R.id.txtQuantityCount);
        }
    }
}
