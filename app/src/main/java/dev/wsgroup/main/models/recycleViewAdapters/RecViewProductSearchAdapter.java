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
    private Context context;
    private Activity activity;
    private DialogBoxLoading dialogBoxLoading;

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
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtCampaign.setVisibility(View.GONE);
        if (productsList.get(position).getStatus().equals("incampaign")) {
            holder.txtCampaign.setVisibility(View.VISIBLE);
            holder.txtCampaign.setText("Ongoing\nCampaign");
        }
        holder.txtProductName.setText(productsList.get(position).getName());

        holder.txtProductOrderCount
                .setText(MethodUtils.formatOrderOrReviewCount(productsList.get(position).getOrderCount()));
        holder.lblProductOrderCount
                .setText((productsList.get(position).getOrderCount() > 0) ? "orders" : "order");

        String productId = productsList.get(position).getProductId();
        if(productsList.get(position).getImageList().size() > 0) {
            Glide.with(context)
                    .load(productsList.get(position).getImageList().get(0))
                    .into(holder.imgProduct);
        }
        holder.txtCampaign.bringToFront();
        holder.txtRetailPrice
                .setText(MethodUtils.formatPriceString(productsList.get(position).getRetailPrice()));
        holder.ratingProduct.setIsIndicator(true);
        holder.ratingProduct.setRating( (float) productsList.get(position).getRating());
        holder.txtRatingProduct.setText(productsList.get(position).getRating() + "");
        holder.layoutParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBoxLoading = new DialogBoxLoading(activity);
                dialogBoxLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBoxLoading.show();
                APIProductCaller.getProductById(productId, activity.getApplication(), new APIListener() {
                    @Override
                    public void onProductFound(Product product) {
                        super.onProductFound(product);
                        if (dialogBoxLoading.isShowing()) {
                            dialogBoxLoading.dismiss();
                        }
                        Intent intent = new Intent(activity.getApplicationContext(),
                                ProductDetailActivity.class);
                        intent.putExtra("PRODUCT_ID", product.getProductId());
                        activity.startActivityForResult(intent, IntegerUtils.REQUEST_COMMON);
                    }

                    @Override
                    public void onFailedAPICall(int errorCode) {
                        super.onFailedAPICall(errorCode);
                        if (dialogBoxLoading.isShowing()) {
                            dialogBoxLoading.dismiss();
                        }
                        MethodUtils.displayErrorAPIMessage(activity);
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private FrameLayout layoutParent;
        private ImageView imgProduct;
        private TextView txtCampaign, txtProductName, txtProductOrderCount, txtRetailPrice,
                lblProductOrderCount, txtRatingProduct;
        private MaterialRatingBar ratingProduct;

        public ViewHolder(View view) {
            super(view);
            layoutParent = view.findViewById(R.id.layoutParent);
            imgProduct = view.findViewById(R.id.imgRecViewProduct);
            txtCampaign = view.findViewById(R.id.txtCampaign);
            txtProductName = view.findViewById(R.id.txtProductName);
            txtProductOrderCount = view.findViewById(R.id.txtRecViewProductOrderCount);
            txtRetailPrice = view.findViewById(R.id.txtRecViewRetailPrice);
            lblProductOrderCount = view.findViewById(R.id.lblProductOrderCount);
            txtRatingProduct = view.findViewById(R.id.txtRatingProduct);
            ratingProduct = view.findViewById(R.id.ratingProduct);
        }
    }
}
