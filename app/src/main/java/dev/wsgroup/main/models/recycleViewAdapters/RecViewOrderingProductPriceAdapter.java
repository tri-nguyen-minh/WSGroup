package dev.wsgroup.main.models.recycleViewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.OrderProduct;
import dev.wsgroup.main.models.dtos.Product;
import dev.wsgroup.main.models.utils.MethodUtils;

public class RecViewOrderingProductPriceAdapter
        extends RecyclerView.Adapter<RecViewOrderingProductPriceAdapter.ViewHolder> {

    private Context context;
    private List<OrderProduct> orderProductList;
    private OrderProduct orderProduct;
    private Product product;

    public RecViewOrderingProductPriceAdapter(Context context) {
        this.context = context;
    }

    public void setOrderProductList(List<OrderProduct> orderProductList) {
        this.orderProductList = orderProductList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.recycle_view_ordering_product_price, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        orderProduct = orderProductList.get(position);
        product = orderProduct.getProduct();
        if (!product.getImageList().isEmpty()) {
            Glide.with(context)
                 .load(product.getImageList().get(0))
                 .into(holder.imgRecViewProduct);
        }
        holder.txtProductName.setText(product.getName());
        double price = orderProduct.getQuantity();
        price *= orderProduct.getPrice();
        holder.txtProductPrice.setText(MethodUtils.formatPriceString(price));
        holder.txtQuantityCount.setText(orderProduct.getQuantity() + "");
    }

    @Override
    public int getItemCount() {
        return orderProductList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgRecViewProduct;
        private TextView txtProductName, txtProductPrice, txtQuantityCount;

        public ViewHolder(View view) {
            super(view);
            imgRecViewProduct = view.findViewById(R.id.imgRecViewProduct);
            txtProductName = view.findViewById(R.id.txtProductName);
            txtProductPrice = view.findViewById(R.id.txtProductPrice);
            txtQuantityCount = view.findViewById(R.id.txtQuantityCount);
        }
    }
}
