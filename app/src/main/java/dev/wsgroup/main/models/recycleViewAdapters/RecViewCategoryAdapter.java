package dev.wsgroup.main.models.recycleViewAdapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.Category;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.views.activities.productviews.SearchProductActivity;

public class RecViewCategoryAdapter
        extends RecyclerView.Adapter<RecViewCategoryAdapter.ViewHolder> {

    private Context context;
    private Activity activity;
    private List<Category> categoryList;

    public RecViewCategoryAdapter(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.recycle_view_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtCategory.setText(categoryList.get(position).getName());
        holder.cardViewParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String categoryId = categoryList.get(position).getCategoryId();
                Intent searchIntent = new Intent(context, SearchProductActivity.class);
                searchIntent.putExtra("IDENTIFIER", IntegerUtils.IDENTIFIER_SEARCH_CATEGORY);
                searchIntent.putExtra("SEARCH_STRING", categoryId);
                activity.startActivityForResult(searchIntent, IntegerUtils.REQUEST_COMMON);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtCategory;
        private final CardView cardViewParent;


        public ViewHolder(View view) {
            super(view);
            txtCategory = view.findViewById(R.id.txtCategory);
            cardViewParent = view.findViewById(R.id.cardViewParent);
        }
    }
}
