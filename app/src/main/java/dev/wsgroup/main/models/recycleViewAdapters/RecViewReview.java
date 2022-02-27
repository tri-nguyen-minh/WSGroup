package dev.wsgroup.main.models.recycleViewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.Review;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class RecViewReview extends RecyclerView.Adapter<RecViewReview.ViewHolder> {

    private Context context;
    private List<Review> reviewList;

    public RecViewReview(Context context) {
        this.context = context;
    }

    public void setReviewList(List<Review> reviewList) {
        this.reviewList = reviewList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_view_review, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

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
