package dev.wsgroup.main.models.recycleViewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.CustomerDiscount;
import dev.wsgroup.main.models.utils.MethodUtils;

public class RecViewDiscountListAdapter
        extends RecyclerView.Adapter<RecViewDiscountListAdapter.ViewHolder> {

    private List<CustomerDiscount> discountList;
    private Context context;

    public RecViewDiscountListAdapter(Context context) {
        this.context = context;
    }

    public void setDiscountList(List<CustomerDiscount> discountList) {
        this.discountList = discountList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.recycle_view_discount_list, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(RecViewDiscountListAdapter.ViewHolder holder, int position) {
        holder.txtDiscountUse.setText(discountList.get(position).getDiscount().getQuantity() +  "");
        holder.txtDiscountEndDate
              .setText(MethodUtils.formatDate(discountList.get(position).getDiscount().getEndDate()));
        holder.txtDiscountDescription
              .setText(discountList.get(position).getDiscount().getDescription());
        holder.txtDiscountPrice.setText(MethodUtils.formatPriceString(discountList.get(position)
                                                                                  .getDiscount()
                                                                                  .getDiscountPrice()));
        holder.txtDiscountCode.setText(discountList.get(position).getDiscount().getCode());
        holder.btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDiscountSelected(discountList.get(position));
            }
        });
    }

    public void onDiscountSelected(CustomerDiscount discount) {}

    @Override
    public int getItemCount() {
        return discountList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtDiscountUse, txtDiscountEndDate,
                txtDiscountDescription, txtDiscountCode, txtDiscountPrice;
        private Button btnSelect;


        public ViewHolder(View view) {
            super(view);
            txtDiscountUse = view.findViewById(R.id.txtDiscountUse);
            txtDiscountEndDate = view.findViewById(R.id.txtDiscountEndDate);
            txtDiscountDescription = view.findViewById(R.id.txtDiscountDescription);
            txtDiscountPrice = view.findViewById(R.id.txtDiscountPrice);
            txtDiscountCode = view.findViewById(R.id.txtDiscountCode);
            btnSelect = view.findViewById(R.id.btnSelect);
        }
    }
}
