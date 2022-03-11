package dev.wsgroup.main.models.recycleViewAdapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
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

public class RecViewOrderListAdapter
        extends RecyclerView.Adapter<RecViewOrderListAdapter.ViewHolder> {

    private Context context;
    private Activity activity;
    private List<Order> orderList;
    private DialogBoxLoading dialogBoxLoading;
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

//        still need to work on discount

        holder.constraintLayoutDiscount.setVisibility(View.GONE);
        holder.constraintLayoutDeliverDate.setVisibility(View.GONE);
        holder.constraintLayoutReturnDate.setVisibility(View.GONE);
        holder.constraintLayoutCancelDate.setVisibility(View.GONE);
        totalPrice = orderList.get(position).getTotalPrice();
        totalPrice -= (orderList.get(position).getDiscountPrice()
                        + orderList.get(position).getAdvanceFee());
        holder.txtOrderPrice
                .setText(MethodUtils.formatPriceString(totalPrice));
        holder.txtSupplier.setText(orderList.get(position).getSupplier().getName());
        int productCount = orderList.get(position).getOrderProductList().size();
        holder.txtProductCount.setText(productCount + "");
        holder.lblProduct.setText((productCount == 1) ? "product" : "products");
        if (orderList.get(position).getCustomerDiscount() != null) {
            holder.constraintLayoutDiscount.setVisibility(View.VISIBLE);
            holder.txtDiscountPrice
                    .setText(MethodUtils.formatPriceString(orderList.get(position).getDiscountPrice()));
        }
        if (orderList.get(position).getPaymentMethod().equals("cod")) {
            holder.txtPayment.setText("Payment on Delivery");
        } else {
            holder.txtPayment.setText("Payment via VNPay");
        }
        holder.txtAddress.setText(orderList.get(position).getAddress().getAddressString());
        String status = orderList.get(position).getStatus();
        holder.txtOrderDate
                .setText(MethodUtils.formatDateWithTime(orderList.get(position).getDateCreated()));
        if (MethodUtils.checkDeliveredOrder(status)) {
            holder.constraintLayoutDeliverDate.setVisibility(View.VISIBLE);
            holder.txtDeliverDate
                    .setText(MethodUtils.formatDateWithTime(orderList.get(position).getDateUpdated()));
        } else if (MethodUtils.checkReturnedOrder(status)){
            holder.constraintLayoutReturnDate.setVisibility(View.VISIBLE);
            holder.txtReturnDate
                    .setText(MethodUtils.formatDateWithTime(orderList.get(position).getDateUpdated()));
        } else if (MethodUtils.checkCancelledOrder(status)){
            holder.constraintLayoutCancelDate.setVisibility(View.VISIBLE);
            holder.txtCancelDate
                    .setText(MethodUtils.formatDateWithTime(orderList.get(position).getDateUpdated()));
        }
        RecViewOrderingProductPriceAdapter adapter = new RecViewOrderingProductPriceAdapter(context);
        adapter.setOrderProductList(orderList.get(position).getOrderProductList());
        holder.recViewOrderProduct.setAdapter(adapter);
        holder.recViewOrderProduct.setLayoutManager(new LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false));

        holder.layoutParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToOrderDetail(orderList.get(position).getId());
//                dialogBoxLoading = new DialogBoxLoading(activity);
//                dialogBoxLoading.getWindow()
//                                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                dialogBoxLoading.show();
//                Order order = orderList.get(position);
//                if (order.getCampaign() != null) {
//                    APICampaignCaller.getCampaignById(order.getCampaign().getId(),
//                            activity.getApplication(), new APIListener() {
//                        @Override
//                        public void onCampaignFound(Campaign campaign) {
//                            super.onCampaignFound(campaign);
//                            order.getOrderProductList().get(0).getProduct().setCampaign(campaign);
//                            dialogBoxLoading.dismiss();
//                            goToOrderDetail(order);
//                        }
//                    });
//                } else {
//                    dialogBoxLoading.dismiss();
//                    goToOrderDetail(order);
//                }
            }
        });
    }

    private void goToOrderDetail(String orderId) {
        Intent orderDetailIntent = new Intent(context, OrderActivity.class);
        orderDetailIntent.putExtra("ORDER_ID", orderId);
        orderDetailIntent.putExtra("REQUEST_CODE", IntegerUtils.REQUEST_COMMON);
        activity.startActivity(orderDetailIntent);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout layoutParent;
        private TextView txtOrderPrice, txtSupplier, txtProductCount, lblProduct,
                txtDiscountCode, txtDiscountPrice, txtPayment, txtAddress,
                txtOrderDate, txtDeliverDate, txtReturnDate, txtCancelDate;
        private ConstraintLayout constraintLayoutDiscount, constraintLayoutDeliverDate,
                constraintLayoutReturnDate, constraintLayoutCancelDate;
        private RecyclerView recViewOrderProduct;


        public ViewHolder(View view) {
            super(view);
            layoutParent = view.findViewById(R.id.layoutParent);
            txtOrderPrice = view.findViewById(R.id.txtOrderPrice);
            txtSupplier = view.findViewById(R.id.txtSupplier);
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
            recViewOrderProduct = view.findViewById(R.id.recViewOrderProduct);
        }
    }
}
