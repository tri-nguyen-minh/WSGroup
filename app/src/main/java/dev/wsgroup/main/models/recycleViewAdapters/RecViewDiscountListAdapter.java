package dev.wsgroup.main.models.recycleViewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.CustomerDiscount;
import dev.wsgroup.main.models.dtos.Discount;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;

public class RecViewDiscountListAdapter
        extends RecyclerView.Adapter<RecViewDiscountListAdapter.ViewHolder> {

    private List<CustomerDiscount> discountList;
    private Context context;
    private int identifier;
    private CustomerDiscount customerDiscount;
    private Discount discount;

    public RecViewDiscountListAdapter(Context context, int identifier) {
        this.context = context;
        this.identifier = identifier;
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
        customerDiscount = discountList.get(position);
        discount = customerDiscount.getDiscount();
        holder.txtDiscountEndDate.setText(MethodUtils.formatDate(discount.getEndDate()));
        if (!discount.getDescription().equals("null")) {
            holder.txtDiscountDescription.setVisibility(View.VISIBLE);
            holder.txtDiscountDescription.setText(discount.getDescription());
        } else {
            holder.txtDiscountDescription.setVisibility(View.GONE);
        }
        holder.txtDiscountPrice.setText(MethodUtils.formatPriceString(discount.getDiscountPrice()));
        holder.txtDiscountCode.setText(discount.getCode());
        holder.txtDiscountCondition.setText(MethodUtils.formatPriceString(discount.getMinPrice()));
        holder.txtSupplierName.setText(discount.getSupplier().getName());
        holder.txtSupplierAddress.setText(discount.getSupplier().getAddress());
        Glide.with(context)
             .load(discount.getSupplier().getAvatarLink())
             .into(holder.imgSupplierAvatar);
        holder.btnSelect.setVisibility(View.INVISIBLE);
        if (identifier == IntegerUtils.IDENTIFIER_DISCOUNT_SELECT) {
            holder.layoutSupplier.setVisibility(View.GONE);
            holder.btnSelect.setVisibility(View.VISIBLE);
            holder.btnSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDiscountSelected(discountList.get(position));
                }
            });
        } else {
            holder.layoutSupplier.setVisibility(View.VISIBLE);
            holder.btnSelect.setVisibility(View.GONE);
        }
    }

    public void onDiscountSelected(CustomerDiscount discount) {}

    @Override
    public int getItemCount() {
        return discountList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtDiscountEndDate, txtDiscountDescription, txtDiscountCode,
                txtDiscountPrice, txtDiscountCondition, txtSupplierName, txtSupplierAddress;
        private Button btnSelect;
        private RelativeLayout layoutParent;
        private ConstraintLayout layoutSupplier;
        private ImageView imgSupplierAvatar;

        public ViewHolder(View view) {
            super(view);
            txtDiscountEndDate = view.findViewById(R.id.txtDiscountEndDate);
            txtDiscountDescription = view.findViewById(R.id.txtDiscountDescription);
            txtDiscountCode = view.findViewById(R.id.txtDiscountCode);
            txtDiscountPrice = view.findViewById(R.id.txtDiscountPrice);
            txtDiscountCondition = view.findViewById(R.id.txtDiscountCondition);
            txtSupplierName = view.findViewById(R.id.txtSupplierName);
            txtSupplierAddress = view.findViewById(R.id.txtSupplierAddress);
            btnSelect = view.findViewById(R.id.btnSelect);
            layoutParent = view.findViewById(R.id.layoutParent);
            layoutSupplier = view.findViewById(R.id.layoutSupplier);
            imgSupplierAvatar = view.findViewById(R.id.imgSupplierAvatar);
        }
    }
}
