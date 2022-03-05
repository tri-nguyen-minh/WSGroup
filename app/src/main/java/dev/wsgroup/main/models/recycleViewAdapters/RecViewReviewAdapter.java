package dev.wsgroup.main.models.recycleViewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.Review;
import dev.wsgroup.main.models.utils.MethodUtils;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class RecViewReviewAdapter extends RecyclerView.Adapter<RecViewReviewAdapter.ViewHolder> {

    private Context context;
    private List<Review> reviewList;

    public RecViewReviewAdapter(Context context) {
        this.context = context;
    }

    public void setReviewList(List<Review> reviewList) {
        this.reviewList = reviewList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.recycle_view_review, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtCustomerName.setText(reviewList.get(position).getUser().getDisplayName());
        holder.txtDate.setText(MethodUtils.formatDateWithTime(reviewList.get(position).getCreateDate()));
        holder.ratingProduct.setRating((float) reviewList.get(position).getRating());
        holder.txtReview.setText(reviewList.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgAccountAvatar;
        private TextView txtCustomerName, txtDate, txtReview;
        private MaterialRatingBar ratingProduct;
//        private RatingBar ratingProduct;


        public ViewHolder(View view) {
            super(view);
            imgAccountAvatar = view.findViewById(R.id.imgAccountAvatar);
            txtCustomerName = view.findViewById(R.id.txtCustomerName);
            txtDate = view.findViewById(R.id.txtDate);
            txtReview = view.findViewById(R.id.txtReview);
            ratingProduct = view.findViewById(R.id.ratingProduct);
        }
    }
}
