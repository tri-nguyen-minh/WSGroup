package dev.wsgroup.main.models.recycleViewAdapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.Order;
import dev.wsgroup.main.models.dtos.OrderProduct;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;

public class RecViewOrderProductListAdapter extends RecyclerView.Adapter<RecViewOrderProductListAdapter.ViewHolder> {

    private Order order;
    private List<OrderProduct> orderProductList;
    private Context context;
    private Activity activity;

    public RecViewOrderProductListAdapter(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public void setOrder(Order order) {
        this.order = order;
        orderProductList = order.getOrderProductList();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_ordering_product_list, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if(orderProductList.get(position).getProduct().getImageList() != null) {
            Glide.with(context).load(orderProductList.get(position).getProduct().getImageList().get(0)).into(holder.imgRecViewProduct);
        }
        holder.txtRecViewProductOrderName.setText(orderProductList.get(position).getProduct().getName());
        if (order.getCampaignId().isEmpty()) {
            holder.layoutBasePrice.setVisibility(View.VISIBLE);
        }
        if (order.getCampaignId().isEmpty()) {
            holder.lblPrice.setText("Base Price");
            holder.txtProductPrice.setText(MethodUtils.convertPriceString(orderProductList.get(position).getProduct().getRetailPrice()));
        } else {
            holder.lblPrice.setText("Campaign Price");
            holder.txtProductPrice.setText(MethodUtils.convertPriceString(orderProductList.get(position).getProduct().getCampaign().getPrice()));
        }
        holder.txtOrderQuantity.setText(orderProductList.get(position).getQuantity() + "");
        holder.txtTotalPrice.setText(MethodUtils.convertPriceString(orderProductList.get(position).getPrice()));

        RecViewProductTypeAdapter adapter =
                new RecViewProductTypeAdapter(context, activity, IntegerUtils.IDENTIFIER_PRODUCT_TYPE_SELECTED);
        adapter.setTypeList(orderProductList.get(position).getTypeList());
        holder.recViewProductTypeTag.setAdapter(adapter);
        holder.recViewProductTypeTag.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));

    }

    @Override
    public int getItemCount() {
        return orderProductList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgRecViewProduct;
        private TextView txtRecViewProductOrderName, lblPrice, txtProductPrice,
                txtOrderQuantity, txtTotalPrice;
        private ConstraintLayout layoutBasePrice;
        private RecyclerView recViewProductTypeTag;

        public ViewHolder(View view) {
            super(view);
            imgRecViewProduct = view.findViewById(R.id.imgRecViewProduct);
            txtRecViewProductOrderName = view.findViewById(R.id.txtRecViewProductOrderName);
            lblPrice = view.findViewById(R.id.lblPrice);
            txtProductPrice = view.findViewById(R.id.txtProductPrice);
            txtOrderQuantity = view.findViewById(R.id.txtOrderQuantity);
            txtTotalPrice = view.findViewById(R.id.txtTotalPrice);
            layoutBasePrice = view.findViewById(R.id.layoutBasePrice);
            recViewProductTypeTag = view.findViewById(R.id.recViewProductTypeTag);
        }
    }
}
