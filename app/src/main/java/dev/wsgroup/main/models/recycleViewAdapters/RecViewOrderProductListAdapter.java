package dev.wsgroup.main.models.recycleViewAdapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import dev.wsgroup.main.models.dtos.Review;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.views.dialogbox.DialogBoxOrderNote;
import dev.wsgroup.main.views.dialogbox.DialogBoxReview;

public class RecViewOrderProductListAdapter extends RecyclerView.Adapter<RecViewOrderProductListAdapter.ViewHolder> {

    private Order order;
    private List<OrderProduct> orderProductList;
    private Context context;
    private Activity activity;
    private int requestCode, requestState;

    public RecViewOrderProductListAdapter(Context context, Activity activity, int requestState, int requestCode) {
        this.context = context;
        this.activity = activity;
        this.requestState = requestState;
        this.requestCode = requestCode;
    }

    public void setOrder(Order order) {
        this.order = order;
        orderProductList = order.getOrderProductList();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.recycle_view_order_product_list, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(orderProductList.get(position).getProduct().getImageList().size() > 0) {
            Glide.with(context).load(orderProductList.get(position).getProduct().getImageList().get(0)).into(holder.imgRecViewProduct);
        }
        holder.txtRecViewProductOrderName.setText(orderProductList.get(position).getProduct().getName());
        holder.txtProductPrice.setText(MethodUtils.formatPriceString(orderProductList.get(position).getPrice()));
        holder.txtOrderQuantity.setText(orderProductList.get(position).getQuantity() + "");
        holder.txtTotalPrice.setText(MethodUtils.formatPriceString(orderProductList.get(position).getTotalPrice()));
        holder.btnNote.setText("Note");
        holder.btnNote.setEnabled(true);
        holder.btnNote.getBackground().setTint(context.getResources().getColor(R.color.blue_main));
        holder.btnNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogBoxOrderNote dialogBox = new DialogBoxOrderNote(activity, context,
                        orderProductList.get(position), requestState) {
                    @Override
                    public void onConfirmNote(String note) {
                        orderProductList.get(position).setNote(note);
                    }
                };
                dialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBox.show();
            }
        });
        if (order.getStatus() != null) {
            if (order.getStatus().equals("completed")) {
                holder.layoutReview.setVisibility(View.VISIBLE);
                holder.txtReviewStatus.setText("Write Review");
                if (orderProductList.get(position).getReview() != null) {
                    holder.txtReviewStatus.setText("View Review");
                }
                holder.layoutReview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogBoxReview dialogBox = new DialogBoxReview(activity, context,
                                orderProductList.get(position)) {
                            @Override
                            public void onConfirmReview(Review review) {
                                addingReview(review);
                            }
                        };
                        dialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialogBox.show();
                    }
                });
            } else {
                holder.layoutReview.setVisibility(View.GONE);
            }
        } else {
            holder.layoutReview.setVisibility(View.GONE);
        }
    }

    public void addingReview(Review review) {}

    @Override
    public int getItemCount() {
        return orderProductList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgRecViewProduct;
        private TextView txtRecViewProductOrderName, txtOrderQuantity, txtProductPrice,
                txtTotalPrice, txtReviewStatus;
        private Button btnNote, btnReview;
        private ConstraintLayout layoutReview;

        public ViewHolder(View view) {
            super(view);
            imgRecViewProduct = view.findViewById(R.id.imgRecViewProduct);
            txtRecViewProductOrderName = view.findViewById(R.id.txtRecViewProductOrderName);
            txtOrderQuantity = view.findViewById(R.id.txtOrderQuantity);
            txtProductPrice = view.findViewById(R.id.txtProductPrice);
            txtTotalPrice = view.findViewById(R.id.txtTotalPrice);
            txtReviewStatus = view.findViewById(R.id.txtReviewStatus);
            btnNote = view.findViewById(R.id.btnNote);
            layoutReview = view.findViewById(R.id.layoutReview);
        }
    }
}
