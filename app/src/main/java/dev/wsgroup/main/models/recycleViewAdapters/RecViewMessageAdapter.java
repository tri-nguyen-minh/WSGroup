package dev.wsgroup.main.models.recycleViewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.Message;
import dev.wsgroup.main.models.utils.MethodUtils;

public class RecViewMessageAdapter extends RecyclerView.Adapter<RecViewMessageAdapter.ViewHolder> {

    private Context context;
    private List<Message> messageList;
    private String userAccountId;
    private Message currentMessage;
    private View.OnClickListener listener;

    public RecViewMessageAdapter(Context context, String userAccountId) {
        this.context = context;
        this.userAccountId = userAccountId;
        listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecViewMessageAdapter.this.onClick();
            }
        };
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.recycle_view_message, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        currentMessage = messageList.get(position);
        try {
            if (position == (messageList.size() - 1)
                    || !MethodUtils.compareDatesByDay(currentMessage.getCreateDate(),
                                                messageList.get(position + 1).getCreateDate())) {
                holder.txtDate.setVisibility(View.VISIBLE);
                holder.txtDate.setText(MethodUtils.formatDate(currentMessage.getCreateDate()));
            } else {
                holder.txtDate.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            holder.txtDate.setVisibility(View.GONE);
        }
        if (currentMessage.getFromId().equals(userAccountId)) {
            holder.layoutForeignMessage.setVisibility(View.GONE);
            holder.txtOwnMessage.setText(currentMessage.getMessage());
            holder.txtOwnTime.setText(MethodUtils.convertTime(currentMessage.getCreateDate()));
        } else {
            holder.layoutOwnMessage.setVisibility(View.GONE);
            holder.txtForeignMessage.setText(currentMessage.getMessage());
            holder.txtForeignTime.setText(MethodUtils.convertTime(currentMessage.getCreateDate()));
        }
        holder.layoutParent.setOnClickListener(listener);
        holder.txtDate.setOnClickListener(listener);
        holder.txtOwnTime.setOnClickListener(listener);
        holder.txtOwnMessage.setOnClickListener(listener);
        holder.txtForeignTime.setOnClickListener(listener);
        holder.txtForeignMessage.setOnClickListener(listener);
        holder.layoutParent.setOnClickListener(listener);
        holder.layoutOwnMessage.setOnClickListener(listener);
        holder.layoutForeignMessage.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public void onClick() {}

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtDate, txtOwnTime, txtOwnMessage, txtForeignTime, txtForeignMessage;
        private LinearLayout layoutOwnMessage, layoutForeignMessage;
        private ConstraintLayout layoutParent;

        public ViewHolder(View view) {
            super(view);
            txtDate = view.findViewById(R.id.txtDate);
            txtOwnTime = view.findViewById(R.id.txtOwnTime);
            txtOwnMessage = view.findViewById(R.id.txtOwnMessage);
            txtForeignTime = view.findViewById(R.id.txtForeignTime);
            txtForeignMessage = view.findViewById(R.id.txtForeignMessage);
            layoutOwnMessage = view.findViewById(R.id.layoutOwnMessage);
            layoutForeignMessage = view.findViewById(R.id.layoutForeignMessage);
            layoutParent = view.findViewById(R.id.layoutParent);
        }
    }
}
