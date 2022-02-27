package dev.wsgroup.main.models.recycleViewAdapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.Address;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.views.dialogbox.DialogBoxAddress;

public class RecViewAddressListAdapter extends RecyclerView.Adapter<RecViewAddressListAdapter.ViewHolder> {

    private Context context;
    private Activity activity;
    private int request;
    private List<Address> addressList;

    public RecViewAddressListAdapter(Context context, Activity activity, int request) {
        this.context = context;
        this.activity = activity;
        this.request = request;
    }

    public void setAddressList(List<Address> addressList) {
        this.addressList = addressList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_view_address_list, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtAddressStreet.setText(addressList.get(position).getStreet());
        holder.txtAddressProvince.setText(addressList.get(position).getProvince());
        if (request == IntegerUtils.REQUEST_COMMON) {
            holder.checkboxAddress.setVisibility(View.GONE);
            holder.layoutParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogBoxAddress dialogBoxAddress = new DialogBoxAddress(activity, context,
                            addressList.get(position), false) {
                        @Override
                        public void onAddressUpdate(Address address) {
                            super.onAddressUpdate(address);
                            onAddressChange(address, position, IntegerUtils.ADDRESS_ACTION_UPDATE);
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onAddressDelete() {
                            super.onAddressDelete();
                            onAddressChange(addressList.get(position), position, IntegerUtils.ADDRESS_ACTION_DELETE);
                            notifyDataSetChanged();
                        }
                    };
                    dialogBoxAddress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialogBoxAddress.show();
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
                    DialogBoxAddress dialogBoxAddress = new DialogBoxAddress(activity, context,
                            addressList.get(position), false) {
                        @Override
                        public void onAddressUpdate(Address address) {
                            super.onAddressUpdate(address);
                            onAddressChange(address, position, IntegerUtils.ADDRESS_ACTION_UPDATE);
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onAddressDelete() {
                            super.onAddressDelete();
                            onAddressChange(addressList.get(position), position, IntegerUtils.ADDRESS_ACTION_DELETE);
                            notifyDataSetChanged();
                        }
                    };
                    dialogBoxAddress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialogBoxAddress.show();
                    return true;
                }
            });
        }
    }

    public void onAddressChange(Address address, int position, int action) {}

    public void onCheckboxSelected(ImageView view, int position) {}

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout layoutParent;
        private TextView txtAddressStreet, txtAddressProvince;
        private ImageView checkboxAddress;

        public ViewHolder(View view) {
            super(view);
            layoutParent = view.findViewById(R.id.layoutParent);
            txtAddressStreet = view.findViewById(R.id.txtAddressStreet);
            txtAddressProvince = view.findViewById(R.id.txtAddressProvince);
            checkboxAddress = view.findViewById(R.id.checkboxAddress);
        }
    }
}
