package dev.wsgroup.main.models.recycleViewAdapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.Order;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.views.activities.order.OrderActivity;
import dev.wsgroup.main.views.dialogbox.DialogBoxLoading;

public class RecViewOrderListAdapter
        extends RecyclerView.Adapter<RecViewOrderListAdapter.ViewHolder> {

    private Context context;
    private Activity activity;
    private List<Order> orderList;
    private double totalPrice;

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
    }

    public RecViewOrderListAdapter(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.recycle_view_order_list, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        totalPrice = orderList.get(position).getTotalPrice();
        totalPrice -= (orderList.get(position).getDiscountPrice()
                        + orderList.get(position).getAdvanceFee());
        holder.txtOrderPrice
                .setText(MethodUtils.formatPriceString(totalPrice));
        holder.txtSupplier.setText(orderList.get(position).getSupplier().getName());
        int productCount = orderList.get(position).getOrderProductList().size();
        holder.txtProductCount.setText(productCount + "");
        holder.lblProduct.setText((productCount == 1) ? "product" : "products");
        RecViewOrderingProductPriceAdapter adapter = new RecViewOrderingProductPriceAdapter(context);
        adapter.setOrderProductList(orderList.get(position).getOrderProductList());
        holder.recViewOrderProduct.setAdapter(adapter);
        holder.recViewOrderProduct.setLayoutManager(new LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false));

        holder.layoutParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToOrderDetail(orderList.get(position).getCode());
            }
        });
    }

    private void goToOrderDetail(String orderCode) {
        Intent orderDetailIntent = new Intent(context, OrderActivity.class);
        orderDetailIntent.putExtra("ORDER_CODE", orderCode);
        orderDetailIntent.putExtra("REQUEST_CODE", IntegerUtils.REQUEST_COMMON);
        activity.startActivityForResult(orderDetailIntent, IntegerUtils.REQUEST_COMMON);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout layoutParent;
        private TextView txtOrderPrice, txtSupplier, txtProductCount, lblProduct;
        private RecyclerView recViewOrderProduct;


        public ViewHolder(View view) {
            super(view);
            layoutParent = view.findViewById(R.id.layoutParent);
            txtOrderPrice = view.findViewById(R.id.txtOrderPrice);
            txtSupplier = view.findViewById(R.id.txtSupplier);
            txtProductCount = view.findViewById(R.id.txtProductCount);
            lblProduct = view.findViewById(R.id.lblProduct);
            recViewOrderProduct = view.findViewById(R.id.recViewOrderProduct);
        }
    }
}
