package dev.wsgroup.main.models.recycleViewAdapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.Discount;
import dev.wsgroup.main.models.utils.MethodUtils;

public class RecViewDiscountAdapter extends RecyclerView.Adapter<RecViewDiscountAdapter.ViewHolder> {

    private List<Discount> discountList;
    private Context context;
    private Activity activity;

    public RecViewDiscountAdapter(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public void setDiscountList(List<Discount> discountList) {
        this.discountList = discountList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_discount_card, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(RecViewDiscountAdapter.ViewHolder holder, int position) {
        holder.txtDiscountDescription.setText(discountList.get(position).getDescription());
        holder.txtDiscountEndDate.setText(MethodUtils.formatDate(discountList.get(position).getEndDate()));
        holder.txtDiscountPrice.setText(MethodUtils.convertPriceString(discountList.get(position).getDiscountPrice()));
        if(discountList.get(position).getStatus()) {
            holder.btnSave.setEnabled(true);
            holder.btnSave.setText("SAVE");
        } else {
            holder.btnSave.setEnabled(false);
            holder.btnSave.setText("SAVED");
        }
        holder.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.btnSave.setEnabled(false);
                holder.btnSave.setText("SAVED");
            }
        });

    }

    @Override
    public int getItemCount() {
        return discountList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtDiscountDescription, txtDiscountEndDate, txtDiscountPrice;
        private Button btnSave;


        public ViewHolder(View view) {
            super(view);
            txtDiscountDescription = view.findViewById(R.id.txtDiscountDescription);
            txtDiscountEndDate = view.findViewById(R.id.txtDiscountEndDate);
            txtDiscountPrice = view.findViewById(R.id.txtDiscountPrice);
//            btnSave = view.findViewById(R.id.btnSave);
        }
    }
}
