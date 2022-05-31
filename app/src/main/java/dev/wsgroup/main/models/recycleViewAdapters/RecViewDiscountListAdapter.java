package dev.wsgroup.main.models.recycleViewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.CustomerDiscount;
import dev.wsgroup.main.models.dtos.Discount;
import dev.wsgroup.main.models.dtos.Supplier;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;

public class RecViewDiscountListAdapter
        extends RecyclerView.Adapter<RecViewDiscountListAdapter.ViewHolder> {

    private List<CustomerDiscount> discountList;
    private Context context;
    private int identifier;
    private CustomerDiscount customerDiscount;
    private Discount discount;
    private Supplier supplier;

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
        supplier = discount.getSupplier();
        holder.txtDiscountEndDate.setText(MethodUtils.formatDate(discount.getEndDate(), false));
        if (!discount.getDescription().equals("null")) {
            holder.txtDiscountDescription.setVisibility(View.VISIBLE);
            holder.txtDiscountDescription.setText(discount.getDescription());
        } else {
            holder.txtDiscountDescription.setVisibility(View.GONE);
        }
        holder.txtDiscountPrice.setText(MethodUtils.formatPriceString(discount.getDiscountPrice()));
        holder.txtDiscountCode.setText(discount.getCode());
        holder.txtDiscountCondition.setText(MethodUtils.formatPriceString(discount.getMinPrice()));
        holder.txtSupplierName.setText(supplier.getName());
        holder.txtSupplierAddress.setText(supplier.getAddress().getFullAddressString());
        if (!supplier.getAvatarLink().equals("null")) {
            Glide.with(context)
                 .load(supplier.getAvatarLink())
                 .into(holder.imgSupplierAvatar);
        } else {
            Glide.with(context)
                 .load(R.drawable.ic_profile_circle)
                 .into(holder.imgSupplierAvatar);
            holder.imgSupplierAvatar.setScaleX((float) 1.1);
            holder.imgSupplierAvatar.setScaleY((float) 1.1);
        }
        holder.btnSelect.setVisibility(View.INVISIBLE);
        if (identifier == IntegerUtils.IDENTIFIER_EXECUTABLE) {
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtDiscountEndDate, txtDiscountDescription, txtDiscountCode,
                txtDiscountPrice, txtDiscountCondition, txtSupplierName, txtSupplierAddress;
        private final Button btnSelect;
        private final ConstraintLayout layoutSupplier;
        private final ImageView imgSupplierAvatar;

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
            layoutSupplier = view.findViewById(R.id.layoutSupplier);
            imgSupplierAvatar = view.findViewById(R.id.imgSupplierAvatar);
        }
    }
}
