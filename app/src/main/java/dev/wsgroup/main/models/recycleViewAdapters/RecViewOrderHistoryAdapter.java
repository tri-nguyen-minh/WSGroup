package dev.wsgroup.main.models.recycleViewAdapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.OrderHistory;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.StringUtils;

public class RecViewOrderHistoryAdapter
        extends RecyclerView.Adapter<RecViewOrderHistoryAdapter.ViewHolder> {

    private Context context;
    private Activity activity;
    private List<OrderHistory> orderHistoryList;
    private OrderHistory orderHistory;
    private String message;
    private String reason;
    private RecViewImageAdapter adapter;

    public RecViewOrderHistoryAdapter(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public void setOrderHistoryList(List<OrderHistory> orderHistoryList) {
        this.orderHistoryList = orderHistoryList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.recycle_view_order_history_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        orderHistory = orderHistoryList.get(position);
        holder.txtDate.setText(MethodUtils.formatDate(orderHistory.getCreateDate(), true));
        if (orderHistory.getImageList().size() > 0) {
            holder.recViewImage.setVisibility(View.VISIBLE);
            adapter = new RecViewImageAdapter(context, activity, R.layout.recycle_view_image_small);
            adapter.setImageList(orderHistory.getImageList());
            holder.recViewImage.setAdapter(adapter);
            holder.recViewImage.setLayoutManager(new LinearLayoutManager(context,
                    LinearLayoutManager.HORIZONTAL, false));
        } else {
            holder.recViewImage.setVisibility(View.GONE);
        }
        setHistoryDescription(holder, position);
    }

    private void setHistoryDescription(ViewHolder holder, int position) {
        switch (orderHistory.getStatus()) {
            case "unpaid": {
                holder.txtStatus.setText(MethodUtils.displayStatus(orderHistory.getStatus()));
                message = "Order's full online payment not completed";
                reason = null;
                break;
            }
            case "advanced": {
                holder.txtStatus.setText(MethodUtils.displayStatus(orderHistory.getStatus()));
                if (orderHistory.getDescription().contains("advanced")) {
                    message = "Order's advanced payment completed";
                } else {
                    message = "Order's full online payment completed.\n" +
                            "Check back when campaign ended.";
                }
                reason = null;
                break;
            }
            case "created": {
                holder.txtStatus.setText(MethodUtils.displayStatus(orderHistory.getStatus()));
                message = "Order successfully placed";
                reason = null;
                break;
            }
            case "cancelled": {
                if (orderHistory.getDescription().contains("Customer Service")) {
                    holder.txtStatus.setText("Cancelled");
                    message = "Customer Service cancelled the order";
                } else if (orderHistory.getDescription().contains("Supplier")) {
                    holder.txtStatus.setText("Cancelled");
                    message = "Supplier cancelled the order";
                } else if (orderHistory.getDescription().contains("Deliverer")) {
                    holder.txtStatus.setText("Delivery failed");
                    message = "Delivery did not deliver the order";
                } else {
                    holder.txtStatus.setText("Delivery failed");
                    message = "The Order has been cancelled";
                }
                reason = MethodUtils.getStatusChangedReason(orderHistory.getDescription());
                break;
            }
            case "returned": {
                holder.txtStatus.setText(MethodUtils.displayStatus(orderHistory.getStatus()));
                message = "Supplier confirmed order's return";
                reason = MethodUtils.getStatusChangedReason(orderHistory.getDescription());
                break;
            }
            case "returning": {
                holder.txtStatus.setText("Return request submitted");
                message = StringUtils.MES_SUCCESSFUL_REQUEST_SENT;
                reason = MethodUtils.getStatusChangedReason(orderHistory.getDescription());
                break;
            }
            case "requestAccepted": {
                holder.txtStatus.setText("Return request accepted");
                if (orderHistory.getDescription().contains("Supplier")) {
                    message = "Return request accepted by Supplier.";
                } else if (orderHistory.getDescription().contains("Customer Service")) {
                    message = "Return request accepted by Customer Service.";
                }
                reason = null;
                break;
            }
            case "requestRejected": {
                holder.txtStatus.setText("Return request rejected");
                if (orderHistory.getDescription().contains("Supplier")) {
                    message = "Return request rejected by Supplier.";
                } else if (orderHistory.getDescription().contains("Customer Service")) {
                    message = "Return request rejected by Customer Service.";
                }
                reason = MethodUtils.getStatusChangedReason(orderHistory.getDescription());
                break;
            }
            case "returningInProgress": {
                holder.txtStatus.setText("Returning order collected");
                message = "Delivery has collected your returned order.";
                reason = null;
                break;
            }
            case "finishReturning": {
                holder.txtStatus.setText("Returning order delivered");
                message = "Delivery has delivered your order to Supplier.";
                reason = null;
                break;
            }
            case "requestRefund": {
                holder.txtStatus.setText("Waiting for refund");
                message = "Order's payment will soon be refunded.";
                reason = null;
                break;
            }
            case "successRefund": {
                holder.txtStatus.setText("Refund completed");
                message = "Order has been refunded.";
                reason = null;
                break;
            }
            default: {
                holder.txtStatus.setText(MethodUtils.displayStatus(orderHistory.getStatus()));
                message = "Order " + orderHistory.getDescription();
                reason = null;
                break;
            }
        }
        holder.txtMessage.setText(message);
        if (reason == null || reason.isEmpty()) {
            holder.txtReason.setVisibility(View.GONE);
        } else {
            holder.txtReason.setVisibility(View.VISIBLE);
            holder.txtReason.setText(reason);
        }
    }

    @Override
    public int getItemCount() {
        return orderHistoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtStatus, txtDate, txtMessage, txtReason;
        private final RecyclerView recViewImage;

        public ViewHolder(View view) {
            super(view);
            txtStatus = view.findViewById(R.id.txtStatus);
            txtDate = view.findViewById(R.id.txtDate);
            txtMessage = view.findViewById(R.id.txtMessage);
            txtReason = view.findViewById(R.id.txtReason);
            recViewImage = view.findViewById(R.id.recViewImage);
        }
    }
}

