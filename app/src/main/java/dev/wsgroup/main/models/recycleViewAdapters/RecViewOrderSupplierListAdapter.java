package dev.wsgroup.main.models.recycleViewAdapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.CustomerDiscount;
import dev.wsgroup.main.models.dtos.Order;
import dev.wsgroup.main.models.dtos.OrderProduct;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;

public class RecViewOrderSupplierListAdapter extends RecyclerView.Adapter<RecViewOrderSupplierListAdapter.ViewHolder> {

    private Context context;
    private Activity activity;
    private int requestCode;
    private List<Order> orderList;
    private List<OrderProduct> orderProductList;
    private double price;

    public RecViewOrderSupplierListAdapter(Context context, Activity activity, int requestCode) {
        this.context = context;
        this.activity = activity;
        this.requestCode = requestCode;
    }

    public void setList(List<Order> orderList) {
        this.orderList = orderList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_view_order_supplier_list, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        orderProductList = orderList.get(position).getOrderProductList();
        holder.txtRecViewOrderSupplierName.setText(orderList.get(position).getSupplier().getName());
        holder.txtOrderPriceDiscount.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        holder.lblTotalPrice.setText("Total Order Price");
        holder.lblNoDiscount.setText("No Discount");
        setupDiscountLayout(holder);
        price = getTotalPrice();
        holder.txtTotalPrice.setText(MethodUtils.formatPriceString(price));
        RecViewOrderProductListAdapter adapter = new RecViewOrderProductListAdapter(context, activity,
                                                        IntegerUtils.REQUEST_COMMON);
        adapter.setOrder(orderList.get(position));
        holder.recViewOrderProductList.setAdapter(adapter);
        holder.recViewOrderProductList.setLayoutManager(new LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false));
        holder.layoutParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.editDiscount.hasFocus()) {
                    holder.editDiscount.clearFocus();
                }
            }
        });
    }

    private void setupDiscountLayout(ViewHolder holder) {
        if (requestCode == IntegerUtils.REQUEST_ORDER_RETAIL) {
            holder.layoutDiscount.setVisibility(View.VISIBLE);
            holder.lblTotalPrice.setVisibility(View.GONE);
            holder.txtOrderPriceDiscount.setVisibility(View.GONE);
            holder.cardViewMoreDiscount.setVisibility(View.VISIBLE);
            holder.progressBarLoading.setVisibility(View.INVISIBLE);
            setupNoDiscount(holder);
            holder.cardViewMoreDiscount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            holder.editDiscount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    String code = holder.editDiscount.getText().toString();
                    if (code.isEmpty()) {
                        setupNoDiscount(holder);
                        applyCustomerDiscount(null);
                    } else {
                        loadDiscount(holder);

//                        find discount

                    }
                }
            });
        } else {
            holder.layoutDiscount.setVisibility(View.GONE);
            holder.lblTotalPrice.setVisibility(View.VISIBLE);
            OrderProduct orderProduct = orderProductList.get(0);
            price = orderProduct.getCampaign().getSavingPrice();
            price *= orderProduct.getQuantity();
            holder.txtOrderPriceDiscount.setVisibility(View.VISIBLE);
            holder.txtOrderPriceDiscount.setText(MethodUtils.formatPriceString(price));
        }
    }

    private double getTotalPrice() {
        double totalPrice = 0;
        if (requestCode == IntegerUtils.REQUEST_ORDER_RETAIL) {
            for (OrderProduct orderProduct : orderProductList) {
                totalPrice += (orderProduct.getProduct().getRetailPrice() * orderProduct.getQuantity());
            }
        } else {
            OrderProduct orderProduct = orderProductList.get(0);
            totalPrice = orderProduct.getProduct().getRetailPrice();
            totalPrice -= orderProduct.getCampaign().getSavingPrice();
            totalPrice *= orderProduct.getQuantity();
        }
        return totalPrice;
    }

    private void loadDiscount(ViewHolder holder) {
        holder.progressBarLoading.setVisibility(View.VISIBLE);
        holder.cardViewMoreDiscount.setVisibility(View.INVISIBLE);
        holder.layoutDiscountPrice.setVisibility(View.INVISIBLE);
        holder.lblNoDiscount.setVisibility(View.INVISIBLE);
        holder.txtOrderPriceDiscount.setVisibility(View.INVISIBLE);
    }

    private void setupDiscount(ViewHolder holder) {
        holder.progressBarLoading.setVisibility(View.INVISIBLE);
        holder.cardViewMoreDiscount.setVisibility(View.VISIBLE);
        holder.layoutDiscountPrice.setVisibility(View.VISIBLE);
        holder.lblNoDiscount.setVisibility(View.INVISIBLE);
        holder.txtOrderPriceDiscount.setVisibility(View.VISIBLE);
    }

    private void setupNoDiscount(ViewHolder holder) {
        holder.progressBarLoading.setVisibility(View.INVISIBLE);
        holder.cardViewMoreDiscount.setVisibility(View.VISIBLE);
        holder.layoutDiscountPrice.setVisibility(View.INVISIBLE);
        holder.lblNoDiscount.setVisibility(View.VISIBLE);
        holder.txtOrderPriceDiscount.setVisibility(View.INVISIBLE);
    }

    public void applyCustomerDiscount(CustomerDiscount customerDiscount) {}

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtRecViewOrderSupplierName, txtTotalPrice, txtOrderPriceDiscount,
                        txtDiscountPrice, lblNoDiscount, lblTotalPrice;
        private RecyclerView recViewOrderProductList;
        private LinearLayout layoutDiscount, layoutDiscountPrice, layoutParent;
        private EditText editDiscount;
        private ProgressBar progressBarLoading;
        private CardView cardViewMoreDiscount;

        public ViewHolder(View view) {
            super(view);
            txtRecViewOrderSupplierName = view.findViewById(R.id.txtRecViewOrderSupplierName);
            txtTotalPrice = view.findViewById(R.id.txtTotalPrice);
            txtOrderPriceDiscount = view.findViewById(R.id.txtOrderPriceDiscount);
            txtDiscountPrice = view.findViewById(R.id.txtDiscountPrice);
            lblNoDiscount = view.findViewById(R.id.lblNoDiscount);
            lblTotalPrice = view.findViewById(R.id.lblTotalPrice);
            recViewOrderProductList = view.findViewById(R.id.recViewOrderProductList);
            layoutDiscount = view.findViewById(R.id.layoutDiscount);
            layoutDiscountPrice = view.findViewById(R.id.layoutDiscountPrice);
            layoutParent = view.findViewById(R.id.layoutParent);
            editDiscount = view.findViewById(R.id.editDiscount);
            progressBarLoading = view.findViewById(R.id.progressBarLoading);
            cardViewMoreDiscount = view.findViewById(R.id.cardViewMoreDiscount);
        }
    }
}
