package dev.wsgroup.main.models.recycleViewAdapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APIProductCaller;
import dev.wsgroup.main.models.dtos.Product;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.StringUtils;
import dev.wsgroup.main.views.activities.productviews.ProductDetailActivity;
import dev.wsgroup.main.views.dialogbox.DialogBoxAlert;
import dev.wsgroup.main.views.dialogbox.DialogBoxLoading;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class RecViewProductSearchAdapter
        extends RecyclerView.Adapter<RecViewProductSearchAdapter.ViewHolder> {

    private List<Product> productsList;
    private Product product;
    private Context context;
    private Activity activity;

    public RecViewProductSearchAdapter(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public void setProductsList(List<Product> productsList) {
        this.productsList = productsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.recycle_view_search_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        product = productsList.get(position);
        holder.txtCampaign.setVisibility(View.GONE);
        if (product.getStatus().equals("incampaign")) {
            holder.txtCampaign.bringToFront();
            holder.txtCampaign.setVisibility(View.VISIBLE);
            holder.txtCampaign.setText("Ongoing\nCampaign");
        }
        holder.txtSupplier.setText(product.getSupplier().getName());
        holder.txtProductName.setText(product.getName());
        holder.txtProductOrderCount.setText(MethodUtils.formatOrderOrReviewCount(product.getOrderCount()));
        holder.lblProductOrderCount.setText((product.getOrderCount() > 0) ? "orders" : "order");
        if(product.getImageList().size() > 0) {
            Glide.with(context).load(product.getImageList().get(0)).into(holder.imgProduct);
        }
        holder.txtRetailPrice.setText(MethodUtils.formatPriceString(product.getRetailPrice()));
        holder.ratingProduct.setIsIndicator(true);
        holder.ratingProduct.setRating( (float) product.getRating());
        holder.txtRatingProduct.setText(product.getRating() + "");
        holder.layoutParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productId = productsList.get(position).getProductId();
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("PRODUCT_ID", productId);
                activity.startActivityForResult(intent, IntegerUtils.REQUEST_COMMON);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final FrameLayout layoutParent;
        private final ImageView imgProduct;
        private final TextView txtCampaign, txtSupplier, txtProductName, txtProductOrderCount,
                txtRetailPrice, lblProductOrderCount, txtRatingProduct;
        private final MaterialRatingBar ratingProduct;

        public ViewHolder(View view) {
            super(view);
            layoutParent = view.findViewById(R.id.layoutParent);
            imgProduct = view.findViewById(R.id.imgRecViewProduct);
            txtCampaign = view.findViewById(R.id.txtCampaign);
            txtSupplier = view.findViewById(R.id.txtSupplier);
            txtProductName = view.findViewById(R.id.txtProductName);
            txtProductOrderCount = view.findViewById(R.id.txtRecViewProductOrderCount);
            txtRetailPrice = view.findViewById(R.id.txtRecViewRetailPrice);
            lblProductOrderCount = view.findViewById(R.id.lblProductOrderCount);
            txtRatingProduct = view.findViewById(R.id.txtRatingProduct);
            ratingProduct = view.findViewById(R.id.ratingProduct);
        }
    }
}
