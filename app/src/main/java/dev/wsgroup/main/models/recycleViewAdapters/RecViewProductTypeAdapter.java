package dev.wsgroup.main.models.recycleViewAdapters;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.utils.IntegerUtils;

public class RecViewProductTypeAdapter  extends RecyclerView.Adapter<RecViewProductTypeAdapter.ViewHolder> {

    private int code;
    private Context context;
    private Activity activity;
    private List<String> typeList;
    private String type;

    public RecViewProductTypeAdapter(Context context, Activity activity, int code) {
        this.context = context;
        this.activity = activity;
        this.code = code;
    }

    public void setTypeList(List<String> typeList) {
        this.typeList = typeList;
        type = typeList.get(0);
    }

    public String getType() {
        return type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_view_product_type, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (code == IntegerUtils.IDENTIFIER_PRODUCT_TYPE_COMMON) {

        } else {
            holder.txtRecViewTypeOfProduct.setText(typeList.get(position));
            holder.cardRecViewTypeOfProduct.getBackground().setTint(context.getResources().getColor(R.color.gray_dark));
            holder.layoutRecViewTypeOfProduct.getBackground().setTint(context.getResources().getColor(R.color.gray_lighter));
            holder.txtRecViewTypeOfProduct.setTextColor(context.getResources().getColor(R.color.black));

//            holder.cardRecViewTypeOfProduct.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    type = typeList.get(position);
//                    notifyDataSetChanged();
//                }
//            });
//            if (type.equals(typeList.get(position))) {
//                holder.cardRecViewTypeOfProduct.getBackground().setTint(context.getResources().getColor(R.color.blue_dark));
//                holder.layoutRecViewTypeOfProduct.getBackground().setTint(context.getResources().getColor(R.color.blue_main));
//                holder.txtRecViewTypeOfProduct.setTextColor(context.getResources().getColor(R.color.white));
//            } else {
//                holder.cardRecViewTypeOfProduct.getBackground().setTint(context.getResources().getColor(R.color.gray_dark));
//                holder.layoutRecViewTypeOfProduct.getBackground().setTint(context.getResources().getColor(R.color.gray_lighter));
//                holder.txtRecViewTypeOfProduct.setTextColor(context.getResources().getColor(R.color.black));
//            }
        }
    }

    @Override
    public int getItemCount() {
        return typeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtRecViewTypeOfProduct;
        private CardView cardRecViewTypeOfProduct;
        private LinearLayout layoutRecViewTypeOfProduct;


        public ViewHolder(View view) {
            super(view);
            txtRecViewTypeOfProduct = view.findViewById(R.id.txtRecViewTypeOfProduct);
            cardRecViewTypeOfProduct = view.findViewById(R.id.cardRecViewTypeOfProduct);
            layoutRecViewTypeOfProduct = view.findViewById(R.id.layoutRecViewTypeOfProduct);
        }
    }
}
