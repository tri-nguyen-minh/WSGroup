package dev.wsgroup.main.models.recycleViewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.Address;
import dev.wsgroup.main.models.utils.IntegerUtils;

public class RecViewAddressListAdapter
        extends RecyclerView.Adapter<RecViewAddressListAdapter.ViewHolder> {

    private Context context;
    private int request;
    private List<Address> addressList;

    public RecViewAddressListAdapter(Context context, int request) {
        this.context = context;
        this.request = request;
    }

    public void setAddressList(List<Address> addressList) {
        this.addressList = addressList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.recycle_view_address_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtAddressStreet.setText(addressList.get(position).getStreet());
        holder.txtAddressDistrict.setText(addressList.get(position).getDistrictString());
        holder.txtAddressProvince.setText(addressList.get(position).getProvince());
        if (request == IntegerUtils.REQUEST_COMMON) {
            holder.checkboxAddress.setVisibility(View.GONE);
            holder.layoutParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onAddressSelected(addressList.get(position));
                }
            });
        } else {
            if (!addressList.get(position).getSelectedFlag()) {
                holder.checkboxAddress.setImageResource(R.drawable.ic_checkbox_unchecked);
                holder.checkboxAddress.setColorFilter(context.getResources().getColor(R.color.gray));
            } else {
                holder.checkboxAddress.setImageResource(R.drawable.ic_checkbox_checked);
                holder.checkboxAddress.setColorFilter(context.getResources().getColor(R.color.blue_main));
            }
            holder.layoutParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCheckboxSelected(holder.checkboxAddress, position);
                }
            });
            holder.layoutParent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onAddressSelected(addressList.get(position));
                    return true;
                }
            });
        }
    }

    public void onAddressSelected(Address address) {}

    public void onCheckboxSelected(ImageView view, int position) {}

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ConstraintLayout layoutParent;
        private final TextView txtAddressStreet, txtAddressDistrict, txtAddressProvince;
        private final ImageView checkboxAddress;

        public ViewHolder(View view) {
            super(view);
            layoutParent = view.findViewById(R.id.layoutParent);
            txtAddressStreet = view.findViewById(R.id.txtAddressStreet);
            txtAddressDistrict = view.findViewById(R.id.txtAddressDistrict);
            txtAddressProvince = view.findViewById(R.id.txtAddressProvince);
            checkboxAddress = view.findViewById(R.id.checkboxAddress);
        }
    }
}
