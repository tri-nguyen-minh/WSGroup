package dev.wsgroup.main.models.recycleViewAdapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.Discount;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.views.dialogbox.DialogBoxDiscountSingle;

public class RecViewDiscountSimpleAdapter extends RecyclerView.Adapter<RecViewDiscountSimpleAdapter.ViewHolder> {

    private List<Discount> discountList;
    private Context context;
    private Activity activity;
    private String userId;

    public RecViewDiscountSimpleAdapter(Context context, Activity activity, String userId) {
        this.context = context;
        this.activity = activity;
        this.userId = userId;
    }

    public void setDiscountList(List<Discount> discountList) {
        this.discountList = discountList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.recycle_view_discount_simple_card, parent, false);
        RecViewDiscountSimpleAdapter.ViewHolder holder = new RecViewDiscountSimpleAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecViewDiscountSimpleAdapter.ViewHolder holder, int position) {
        holder.txtRecViewDiscountPrice.setText(MethodUtils.formatPriceString(discountList.get(position).getDiscountPrice()));
        holder.lblDiscountOFF.setText("OFF");
        holder.txtDiscountEndDate.setText(MethodUtils.formatDate(discountList.get(position).getEndDate()));
        holder.cardRecViewDiscountSimple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogBoxDiscountSingle dialogBox = new DialogBoxDiscountSingle(activity, discountList.get(position), userId);
                dialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBox.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return discountList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtRecViewDiscountPrice, lblDiscountOFF, txtDiscountEndDate;
        private CardView cardRecViewDiscountSimple;


        public ViewHolder(View view) {
            super(view);
            txtRecViewDiscountPrice = view.findViewById(R.id.txtRecViewDiscountPrice);
            lblDiscountOFF = view.findViewById(R.id.lblDiscountOFF);
            txtDiscountEndDate = view.findViewById(R.id.txtDiscountEndDate);
            cardRecViewDiscountSimple = view.findViewById(R.id.cardRecViewDiscountSimple);
        }
    }
}
