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
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.views.dialogbox.DialogBoxOrderNote;

public class RecViewOrderProductListAdapter extends RecyclerView.Adapter<RecViewOrderProductListAdapter.ViewHolder> {

    private Order order;
    private List<OrderProduct> orderProductList;
    private Context context;
    private Activity activity;
    private int requestCode;

    public RecViewOrderProductListAdapter(Context context, Activity activity, int requestCode) {
        this.context = context;
        this.activity = activity;
        this.requestCode = requestCode;
    }

    public void setOrder(Order order) {
        this.order = order;
        orderProductList = order.getOrderProductList();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_view_order_product_list, parent, false);
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

        holder.btnNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogBoxOrderNote dialogBox = new DialogBoxOrderNote(activity, context,
                        orderProductList.get(position), requestCode) {
                    @Override
                    public void onConfirmNote(String note) {
                        super.onConfirmNote(note);
                        orderProductList.get(position).setNote(note);
                    }
                };
                dialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBox.show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return orderProductList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgRecViewProduct;
        private TextView txtRecViewProductOrderName, txtOrderQuantity, txtProductPrice, txtTotalPrice;
        private Button btnNote;

        public ViewHolder(View view) {
            super(view);
            imgRecViewProduct = view.findViewById(R.id.imgRecViewProduct);
            txtRecViewProductOrderName = view.findViewById(R.id.txtRecViewProductOrderName);
            txtOrderQuantity = view.findViewById(R.id.txtOrderQuantity);
            txtProductPrice = view.findViewById(R.id.txtProductPrice);
            txtTotalPrice = view.findViewById(R.id.txtTotalPrice);
            btnNote = view.findViewById(R.id.btnNote);
        }
    }
}
