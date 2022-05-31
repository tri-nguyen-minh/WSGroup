package dev.wsgroup.main.models.recycleViewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.Review;
import dev.wsgroup.main.models.dtos.User;
import dev.wsgroup.main.models.utils.MethodUtils;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class RecViewReviewAdapter
        extends RecyclerView.Adapter<RecViewReviewAdapter.ViewHolder> {

    private Context context;
    private List<Review> reviewList;
    private Review review;
    private User user;

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
        review = reviewList.get(position);
        if (!review.isRemoved()) {
            user = review.getUser();
            if (!user.getAvatarLink().equals("null")) {
                Glide.with(context).load(user.getAvatarLink()).into(holder.imgAccountAvatar);
            } else {
                Glide.with(context)
                     .load(R.drawable.ic_profile_circle)
                     .into(holder.imgAccountAvatar);
                holder.imgAccountAvatar.setScaleX((float) 1.1);
                holder.imgAccountAvatar.setScaleY((float) 1.1);
            }
            holder.txtDate.setText(MethodUtils.formatDate(review.getDate(), true));
            holder.txtCustomerName.setText(user.getDisplayName());
            holder.ratingProduct.setRating((float) review.getRating());
            holder.txtReview.setText(review.getReview());
        }
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgAccountAvatar;
        private final TextView txtCustomerName, txtDate, txtReview;
        private final MaterialRatingBar ratingProduct;

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
