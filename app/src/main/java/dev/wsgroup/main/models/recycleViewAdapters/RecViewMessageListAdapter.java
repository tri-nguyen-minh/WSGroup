package dev.wsgroup.main.models.recycleViewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.Conversation;
import dev.wsgroup.main.models.utils.MethodUtils;

public class RecViewMessageListAdapter
        extends RecyclerView.Adapter<RecViewMessageListAdapter.ViewHolder> {

    private Context context;
    private List<Conversation> conversationList;
    private Conversation conversation;
    private String senderName, lastMessage;

    public RecViewMessageListAdapter(Context context) {
        this.context = context;
    }

    public void setConversationList(List<Conversation> conversationList) {
        this.conversationList = conversationList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.recycle_view_message_list, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtSupplierName.setText(conversationList.get(position).getSupplier().getName());
        Glide.with(context)
             .load(conversationList.get(position).getSupplier().getAvatarLink())
             .into(holder.imgAvatar);
        if (conversationList.get(position).getLastMessage() != null) {
            holder.txtMessageDate
                  .setText(MethodUtils.formatDateWithTime(conversationList.get(position)
                                                                          .getLastMessage()
                                                                          .getCreateDate()));
            if (conversationList.get(position).getUserMessageStatus()) {
                senderName = "You";
            } else {
                senderName = conversationList.get(position).getSupplier().getName();
            }
            holder.lblSender.setText(senderName);
            lastMessage = conversationList.get(position).getLastMessage().getMessage();
            if (lastMessage.equals("null")) {
                lastMessage = "sent an image";
            }
            holder.txtMessage.setText(lastMessage);
            if (!conversationList.get(position).getReadStatus()) {
                holder.txtSupplierName.setTextColor(context.getResources().getColor(R.color.black));
                holder.txtMessageDate.setTextColor(context.getResources().getColor(R.color.black));
                holder.lblSender.setTextColor(context.getResources().getColor(R.color.black));
                holder.lblMessageSeparator.setTextColor(context.getResources().getColor(R.color.black));
                holder.txtMessage.setTextColor(context.getResources().getColor(R.color.black));
                holder.imgStatusCheck.setVisibility(View.VISIBLE);
            } else {
                holder.imgStatusCheck.setVisibility(View.GONE);
            }
        } else {
            holder.layoutMessageDate.setVisibility(View.INVISIBLE);
            holder.lblMessageSeparator.setVisibility(View.GONE);
            holder.lblSender.setText("Message");
            holder.txtMessage.setText(conversationList.get(position).getSupplier().getName());
            holder.imgStatusCheck.setVisibility(View.INVISIBLE);
        }
        holder.layoutParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                conversation = conversationList.get(position);
                onConversationSelected(conversation.getMainUser().getAccountId(),
                        conversation.getSupplier().getAccountId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }

    public void onConversationSelected(String userId, String supplierId) {
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ConstraintLayout layoutParent, layoutMessageDate;
        private TextView txtMessageDate, txtSupplierName,
                lblSender, lblMessageSeparator, txtMessage;
        private ImageView imgStatusCheck, imgAvatar;

        public ViewHolder(View view) {
            super(view);
            layoutParent = view.findViewById(R.id.layoutParent);
            layoutMessageDate = view.findViewById(R.id.layoutMessageDate);
            txtMessageDate = view.findViewById(R.id.txtMessageDate);
            txtSupplierName = view.findViewById(R.id.txtSupplierName);
            lblSender = view.findViewById(R.id.lblSender);
            lblMessageSeparator = view.findViewById(R.id.lblMessageSeparator);
            txtMessage = view.findViewById(R.id.txtMessage);
            imgStatusCheck = view.findViewById(R.id.imgStatusCheck);
            imgAvatar = view.findViewById(R.id.imgAvatar);
        }
    }
}
