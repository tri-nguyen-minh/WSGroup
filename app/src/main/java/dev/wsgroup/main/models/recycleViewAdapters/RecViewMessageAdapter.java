package dev.wsgroup.main.models.recycleViewAdapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.Message;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.views.dialogbox.DialogBoxImage;

public class RecViewMessageAdapter extends RecyclerView.Adapter<RecViewMessageAdapter.ViewHolder> {

    private Context context;
    private Activity activity;
    private List<Message> messageList;
    private String userAccountId;
    private Message currentMessage;
    private View.OnClickListener listener, imageListener;
    private DialogBoxImage dialogBoxImage;

    public RecViewMessageAdapter(Context context, Activity activity, String userAccountId) {
        this.context = context;
        this.activity = activity;
        this.userAccountId = userAccountId;
        messageList = new ArrayList<>();
        listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecViewMessageAdapter.this.onClick();
            }
        };
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
        notifyDataSetChanged();
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
        if (currentMessage.getFromId().equals(userAccountId)) {
            holder.layoutForeignMessage.setVisibility(View.GONE);
            holder.layoutOwnMessage.setVisibility(View.VISIBLE);
            if (currentMessage.getMessage() == null) {
                holder.txtOwnMessage.setVisibility(View.GONE);
                holder.imgOwnFile.setVisibility(View.VISIBLE);
                Glide.with(context).load(currentMessage.getLink()).into(holder.imgOwnFile);
                holder.imgOwnFile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentMessage = messageList.get(position);
                        dialogBoxImage = new DialogBoxImage(activity, context,
                                                                currentMessage.getLink());
                        dialogBoxImage.getWindow()
                                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialogBoxImage.show();
                    }
                });
            } else {
                holder.txtOwnMessage.setVisibility(View.VISIBLE);
                holder.imgOwnFile.setVisibility(View.GONE);
                holder.txtOwnMessage.setText(currentMessage.getMessage());
            }
        } else {
            holder.layoutOwnMessage.setVisibility(View.GONE);
            holder.layoutForeignMessage.setVisibility(View.VISIBLE);
            if (currentMessage.getMessage() == null) {
                holder.txtForeignMessage.setVisibility(View.GONE);
                holder.imgForeignFile.setVisibility(View.VISIBLE);
                Glide.with(context).load(currentMessage.getLink()).into(holder.imgForeignFile);
                holder.imgForeignFile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentMessage = messageList.get(position);
                        dialogBoxImage = new DialogBoxImage(activity, context,
                                                                currentMessage.getLink());
                        dialogBoxImage.getWindow()
                                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialogBoxImage.show();
                    }
                });
            } else {
                holder.txtForeignMessage.setVisibility(View.VISIBLE);
                holder.imgForeignFile.setVisibility(View.GONE);
                holder.txtForeignMessage.setText(currentMessage.getMessage());
            }
        }
        holder.layoutParent.setOnClickListener(listener);
        holder.txtOwnMessage.setOnClickListener(listener);
        holder.txtForeignMessage.setOnClickListener(listener);
        holder.layoutParent.setOnClickListener(listener);
        holder.layoutOwnMessage.setOnClickListener(listener);
        holder.layoutForeignMessage.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public void updateMessage(Message message) {
        messageList.add(0, message);
        notifyDataSetChanged();
    }

    public void onClick() {}

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtOwnMessage, txtForeignMessage;
        private LinearLayout layoutOwnMessage, layoutForeignMessage;
        private ConstraintLayout layoutParent;
        private ImageView imgForeignFile, imgOwnFile;

        public ViewHolder(View view) {
            super(view);
            txtOwnMessage = view.findViewById(R.id.txtOwnMessage);
            txtForeignMessage = view.findViewById(R.id.txtForeignMessage);
            layoutOwnMessage = view.findViewById(R.id.layoutOwnMessage);
            layoutForeignMessage = view.findViewById(R.id.layoutForeignMessage);
            layoutParent = view.findViewById(R.id.layoutParent);
            imgForeignFile = view.findViewById(R.id.imgForeignFile);
            imgOwnFile = view.findViewById(R.id.imgOwnFile);
        }
    }
}
