package com.example.a16adventure.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.a16adventure.R;

import java.util.List;

/**
 * GalleryPagerAdapter - Adapter cho ViewPager2 hiển thị nhiều ảnh gallery của một di tích
 */
public class GalleryPagerAdapter extends RecyclerView.Adapter<GalleryPagerAdapter.GalleryViewHolder> {

    private final Context context;
    private final List<String> imageUrls;
    private OnImageClickListener listener;

    public interface OnImageClickListener {
        void onImageClick(int position);
    }

    public GalleryPagerAdapter(Context context, List<String> imageUrls, OnImageClickListener listener) {
        this.context = context;
        this.imageUrls = imageUrls;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gallery_page, parent, false);
        return new GalleryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {
        String url = imageUrls.get(position);

        // Check nếu là drawable local
        int resId = context.getResources().getIdentifier(url, "drawable", context.getPackageName());
        if (resId != 0) {
            Glide.with(context)
                    .load(resId)
                    .transition(DrawableTransitionOptions.withCrossFade(300))
                    .centerCrop()
                    .placeholder(R.drawable.bg_weather_default)
                    .into(holder.imgGallery);
        } else {
            Glide.with(context)
                    .load(url)
                    .transition(DrawableTransitionOptions.withCrossFade(300))
                    .centerCrop()
                    .placeholder(R.drawable.bg_weather_default)
                    .error(R.drawable.bg_weather_default)
                    .into(holder.imgGallery);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onImageClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public static class GalleryViewHolder extends RecyclerView.ViewHolder {
        ImageView imgGallery;

        public GalleryViewHolder(@NonNull View itemView) {
            super(itemView);
            imgGallery = itemView.findViewById(R.id.imgGallery);
        }
    }
}
