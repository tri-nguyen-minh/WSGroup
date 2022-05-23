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
    private Order order;
    private double totalPrice;
    private RecViewOrderingProductPriceAdapter adapter;

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
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        order = orderList.get(position);
        totalPrice = order.getTotalPrice();
        totalPrice -= order.getDiscountPrice();
        totalPrice -= order.getAdvanceFee();
        holder.txtOrderPrice
              .setText(MethodUtils.formatPriceString(totalPrice));
        holder.txtSupplier.setText(order.getSupplier().getName());
        int productCount = order.getOrderProductList().size();
        holder.txtProductCount.setText(productCount + "");
        holder.lblProduct.setText((productCount == 1) ? "product" : "products");
        adapter = new RecViewOrderingProductPriceAdapter(context);
        adapter.setOrderProductList(order.getOrderProductList());
        holder.recViewOrderProduct.setAdapter(adapter);
        holder.recViewOrderProduct.setLayoutManager(new LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false));
        holder.layoutParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                order = orderList.get(position);
                goToOrderDetail(order.getCode());
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

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final RelativeLayout layoutParent;
        private final TextView txtOrderPrice, txtSupplier, txtProductCount, lblProduct;
        private final RecyclerView recViewOrderProduct;


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
