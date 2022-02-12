package dev.wsgroup.main.models.recycleViewAdapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APICampaignCaller;
import dev.wsgroup.main.models.dtos.Campaign;
import dev.wsgroup.main.models.dtos.Order;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.views.activities.OrderActivity;
import dev.wsgroup.main.views.dialogbox.DialogBoxLoading;

public class RecViewOrderListAdapter extends RecyclerView.Adapter<RecViewOrderListAdapter.ViewHolder> {

    private Context context;
    private Activity activity;
    private List<Order> orderList;
    private DialogBoxLoading dialogBoxLoading;

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
    }

    public RecViewOrderListAdapter(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_order_list, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.constraintLayoutDiscount.setVisibility(View.GONE);
        holder.constraintLayoutDeliverDate.setVisibility(View.GONE);
        holder.constraintLayoutReturnDate.setVisibility(View.GONE);
        holder.constraintLayoutCancelDate.setVisibility(View.GONE);
        holder.txtOrderPrice.setText(MethodUtils.formatPriceString(orderList.get(position).getTotalPrice()));
        int productCount = orderList.get(position).getOrderProductList().size();
        holder.txtProductCount.setText(productCount + "");
        holder.lblProduct.setText((productCount == 1) ? "product" : "products");
        if (!orderList.get(position).getCustomerDiscount().getId().isEmpty()) {
            holder.constraintLayoutDiscount.setVisibility(View.VISIBLE);
            holder.txtDiscountPrice.setText(MethodUtils.formatPriceString(orderList.get(position).getDiscountPrice()));

        }
        if (orderList.get(position).getPayment().getId().isEmpty()) {
            holder.txtPayment.setText("Payment on Delivery");
        } else {

        }
        holder.txtAddress.setText(orderList.get(position).getAddress().getAddressString());
        String status = orderList.get(position).getStatus();
        holder.txtOrderDate.setText(MethodUtils.formatDateWithTime(orderList.get(position).getDateCreated()));
        if (MethodUtils.checkDeliveredOrder(status)) {
            holder.constraintLayoutDeliverDate.setVisibility(View.VISIBLE);
            holder.txtDeliverDate.setText(MethodUtils.formatDateWithTime(orderList.get(position).getDateUpdated()));
        } else if (MethodUtils.checkReturnedOrder(status)){
            holder.constraintLayoutReturnDate.setVisibility(View.VISIBLE);
            holder.txtReturnDate.setText(MethodUtils.formatDateWithTime(orderList.get(position).getDateUpdated()));
        } else if (MethodUtils.checkCancelledOrder(status)){
            holder.constraintLayoutCancelDate.setVisibility(View.VISIBLE);
            holder.txtCancelDate.setText(MethodUtils.formatDateWithTime(orderList.get(position).getDateUpdated()));
        }
        holder.layoutParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBoxLoading = new DialogBoxLoading(activity);
                dialogBoxLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBoxLoading.show();
                Order order = orderList.get(position);
                if (!order.getCampaignId().isEmpty()) {
                    APICampaignCaller.getCampaignById(order.getCampaignId(), activity.getApplication(), new APIListener() {
                        @Override
                        public void onCampaignFound(Campaign campaign) {
                            super.onCampaignFound(campaign);
                            order.getOrderProductList().get(0).getProduct().setCampaign(campaign);
                            dialogBoxLoading.dismiss();
                            goToOrderDetail(order);
                        }
                    });
                } else {
                    dialogBoxLoading.dismiss();
                    goToOrderDetail(order);
                }
            }
        });
    }

    private void goToOrderDetail(Order order) {
        Intent orderDetailIntent = new Intent(context, OrderActivity.class);
        orderDetailIntent.putExtra("ORDER", order);
        orderDetailIntent.putExtra("REQUEST_CODE", IntegerUtils.REQUEST_COMMON);
        activity.startActivity(orderDetailIntent);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout layoutParent;
        private TextView txtOrderPrice, txtProductCount, lblProduct, txtDiscountCode, txtDiscountPrice,
                txtPayment, txtAddress, txtOrderDate, txtDeliverDate, txtReturnDate, txtCancelDate;
        private ConstraintLayout constraintLayoutDiscount, constraintLayoutDeliverDate,
                constraintLayoutReturnDate, constraintLayoutCancelDate;


        public ViewHolder(View view) {
            super(view);
            layoutParent = view.findViewById(R.id.layoutParent);
            txtOrderPrice = view.findViewById(R.id.txtOrderPrice);
            txtProductCount = view.findViewById(R.id.txtProductCount);
            lblProduct = view.findViewById(R.id.lblProduct);
            txtDiscountCode = view.findViewById(R.id.txtDiscountCode);
            txtDiscountPrice = view.findViewById(R.id.txtDiscountPrice);
            txtPayment = view.findViewById(R.id.txtPayment);
            txtAddress = view.findViewById(R.id.txtAddress);
            txtOrderDate = view.findViewById(R.id.txtOrderDate);
            txtDeliverDate = view.findViewById(R.id.txtDeliverDate);
            txtReturnDate = view.findViewById(R.id.txtReturnDate);
            txtCancelDate = view.findViewById(R.id.txtCancelDate);
            constraintLayoutDiscount = view.findViewById(R.id.constraintLayoutDiscount);
            constraintLayoutDeliverDate = view.findViewById(R.id.constraintLayoutDeliverDate);
            constraintLayoutReturnDate = view.findViewById(R.id.constraintLayoutReturnDate);
            constraintLayoutCancelDate = view.findViewById(R.id.constraintLayoutCancelDate);
        }
    }
}