package dev.wsgroup.main.models.recycleViewAdapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.Notification;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.views.activities.account.AccountInformationActivity;

public class RecViewNotificationAdapter
        extends RecyclerView.Adapter<RecViewNotificationAdapter.ViewHolder> {

    private Context context;
    private List<Notification> notificationList;
    private String message;
    private Notification notification;

    public RecViewNotificationAdapter(Context context) {
        this.context = context;
        notificationList = new ArrayList<>();
    }

    public void setNotificationList(List<Notification> notificationList) {
        this.notificationList = notificationList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.recycle_view_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecViewNotificationAdapter.ViewHolder holder, int position) {
        if (notificationList.size() > 0) {
            notification = notificationList.get(position);
            holder.txtNotificationDate
                  .setText(MethodUtils.formatDate(notification.getCreatedDate(), true));
            checkNotificationStatus(holder, notification);
            if (notification.getMessage().toLowerCase().contains("campaign")) {
                holder.layoutIdentifier.setVisibility(View.VISIBLE);
                holder.lblOrderCode.setText("Campaign:");
                message = "Campaign has ended.";
                holder.txtOrderCode.setText(notification.getLink());
            }
            if (notification.getMessage().toLowerCase().contains("discount")) {
                holder.layoutIdentifier.setVisibility(View.VISIBLE);
                message = notification.getMessage();
                message = message.substring(message.indexOf(":") + 1);
                holder.lblOrderCode.setText("New Discount Code:");
                message = "You received a new discount code.";
                holder.layoutParent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onDiscountNotificationClicked(notificationList.get(position));
                    }
                });
                holder.txtOrderCode.setText(message);
            }
            if (notification.getMessage().toLowerCase().contains("ewallet required")) {
                holder.layoutIdentifier.setVisibility(View.GONE);
                message = "You have not set your E-Wallet Account." +
                        "\nYour E-Wallet Account is required to process order's refund.";
                holder.layoutParent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onEWalletNotificationClicked(notificationList.get(position));
                    }
                });
                holder.txtOrderCode.setText(notification.getLink());
            }
            if (notification.getMessage().toLowerCase().contains("order")) {
                holder.layoutIdentifier.setVisibility(View.VISIBLE);
                holder.lblOrderCode.setText("Order:");
                setupNotificationStatus(notification);
                holder.layoutParent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onOrderNotificationClicked(notificationList.get(position));
                    }
                });
                holder.txtOrderCode.setText(notification.getLink());
            }
            holder.txtMessage.setText(message);
        }
    }

    private void checkNotificationStatus(ViewHolder holder, Notification notification) {
        if (notification.getNotificationRead()) {
            holder.txtNotificationDate
                  .setTextColor(context.getResources().getColor(R.color.gray_darker));
            holder.txtOrderCode
                  .setTextColor(context.getResources().getColor(R.color.gray_darker));
            holder.txtMessage
                  .setTextColor(context.getResources().getColor(R.color.gray_darker));
            holder.lblOrderCode
                  .setTextColor(context.getResources().getColor(R.color.gray_darker));
            holder.imgStatusCheck.setVisibility(View.GONE);
        } else {
            holder.txtNotificationDate
                  .setTextColor(context.getResources().getColor(R.color.black));
            holder.txtOrderCode
                  .setTextColor(context.getResources().getColor(R.color.black));
            holder.txtMessage
                  .setTextColor(context.getResources().getColor(R.color.black));
            holder.lblOrderCode
                  .setTextColor(context.getResources().getColor(R.color.black));
            holder.imgStatusCheck.setVisibility(View.VISIBLE);
        }
    }
    private void setupNotificationStatus(Notification notification) {
        if (notification.getMessage().toLowerCase().contains("unpaid")) {
            message = "Order required full payment before processing.";
        } else if (notification.getMessage().toLowerCase().contains("created")) {
            message = "Order has been successfully ordered.";
        } else if (notification.getMessage().toLowerCase().contains("advanced")) {
            message = "Order is waiting on campaign.";
        } else if (notification.getMessage().toLowerCase().contains("processing")) {
            message = "Order is being processed.";
        } else if (notification.getMessage().toLowerCase().contains("delivering")) {
            message = "Order is being delivered.";
        } else if (notification.getMessage().toLowerCase().contains("delivered")) {
            message = "Order has been successfully delivered.";
        } else if (notification.getMessage().toLowerCase().contains("returned")) {
            message = "Order has been successfully returned.";
        } else if (notification.getMessage().toLowerCase().contains("cancelled")) {
            message = "Order has been cancelled.";
        } else if (notification.getMessage().toLowerCase().contains("requestrejected")) {
            message = "Return request has been rejected.";
        } else if (notification.getMessage().toLowerCase().contains("requestaccepted")) {
            message = "Return request has been accepted.";
        } else if (notification.getMessage().toLowerCase().contains("returninginprogress")) {
            message = "Order has been collected by Delivery.";
        } else if (notification.getMessage().toLowerCase().contains("finishreturning")) {
            message = "Order has been delivered to Supplier.";
        } else if (notification.getMessage().toLowerCase().contains("milestone")) {
            message = "The joined campaign\nhas reached a new milestone.\nOrder's price has been updated.";
        }
    }

    public void onOrderNotificationClicked(Notification notification) { }

    public void onDiscountNotificationClicked(Notification notification) { }

    public void onEWalletNotificationClicked(Notification notification) {
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ConstraintLayout layoutParent;
        private final TextView txtNotificationDate, txtOrderCode, txtMessage, lblOrderCode;
        private final ImageView imgStatusCheck;
        private final LinearLayout layoutIdentifier;

        public ViewHolder(View view) {
            super(view);
            layoutParent = view.findViewById(R.id.layoutParent);
            txtNotificationDate = view.findViewById(R.id.txtNotificationDate);
            txtOrderCode = view.findViewById(R.id.txtOrderCode);
            txtMessage = view.findViewById(R.id.txtMessage);
            lblOrderCode = view.findViewById(R.id.lblOrderCode);
            imgStatusCheck = view.findViewById(R.id.imgStatusCheck);
            layoutIdentifier = view.findViewById(R.id.layoutIdentifier);
        }
    }
}
