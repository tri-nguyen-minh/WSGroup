package dev.wsgroup.main.models.recycleViewAdapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.views.dialogbox.DialogBoxImage;

public class RecViewImageAdapter
        extends RecyclerView.Adapter<RecViewImageAdapter.ViewHolder> {

    private Context context;
    private Activity activity;
    private int layoutId;
    private List<String> imageList;
    private DialogBoxImage dialogBoxImage;

    public RecViewImageAdapter(Context context, Activity activity, int layoutId) {
        this.context = context;
        this.activity = activity;
        this.layoutId = layoutId;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(layoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (imageList.get(position) == null) {
            holder.imgProof.setImageResource(R.drawable.ic_camera);
            holder.imgProof.setColorFilter(context.getResources().getColor(R.color.gray));
            holder.imgProof.setScaleX((float) 0.7);
            holder.imgProof.setScaleY((float) 0.7);
            holder.cardViewParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectImage();
                }
            });
        } else {
            Glide.with(context).load(imageList.get(position)).into(holder.imgProof);
            holder.imgProof.setColorFilter(null);
            holder.imgProof.setScaleX(1);
            holder.imgProof.setScaleY(1);
            holder.cardViewParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogBoxImage = new DialogBoxImage(activity, context, imageList.get(position));
                    dialogBoxImage.getWindow()
                                  .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialogBoxImage.show();
                }
            });
        }
        if (layoutId == R.layout.recycle_view_image_large) {
            if (imageList.get(position) == null) {
                holder.imgRemove.setVisibility(View.GONE);
            } else {
                holder.imgRemove.setVisibility(View.VISIBLE);
                holder.imgRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onRemoveImage(position);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public void updateList(List<String> imageList) {
        setImageList(imageList);
        notifyDataSetChanged();
    }

    public void onRemoveImage(int position) {
        imageList.remove(position);
        notifyDataSetChanged();
    }

    public void selectImage() {}

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgProof, imgRemove;
        private final CardView cardViewParent;


        public ViewHolder(View view) {
            super(view);
            imgProof = view.findViewById(R.id.imgProof);
            imgRemove = view.findViewById(R.id.imgRemove);
            cardViewParent = view.findViewById(R.id.cardViewParent);
        }
    }
}
