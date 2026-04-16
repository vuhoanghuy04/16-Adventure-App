package com.example.a16adventure.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.a16adventure.R;
import com.example.a16adventure.activities.LandmarkGalleryActivity;
import com.example.a16adventure.activities.PhotoViewerActivity;
import com.example.a16adventure.activities.VideoPlayerActivity;
import com.example.a16adventure.models.LandmarkGallery;
import com.example.a16adventure.models.LandmarkGalleryDataManager;
import com.example.a16adventure.models.MediaItem;

import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaViewHolder> {

    private Context context;
    private List<MediaItem> mediaList;

    public MediaAdapter(Context context, List<MediaItem> mediaList) {
        this.context = context;
        this.mediaList = mediaList;
    }

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_media, parent, false);
        return new MediaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder holder, int position) {
        MediaItem item = mediaList.get(position);

        holder.tvMediaTitle.setText(item.getTitle());

        // Load ảnh thumbnail
        int resId = context.getResources().getIdentifier(item.getThumbnailUrl(), "drawable", context.getPackageName());
        if (resId != 0) {
            Glide.with(context)
                    .load(resId)
                    .centerCrop()
                    .placeholder(R.drawable.bg_weather_default)
                    .into(holder.imgThumbnail);
        } else {
            Glide.with(context)
                    .load(item.getThumbnailUrl())
                    .centerCrop()
                    .placeholder(R.drawable.bg_weather_default)
                    .into(holder.imgThumbnail);
        }

        // Hiện icon play nếu là video
        if ("video".equalsIgnoreCase(item.getType())) {
            holder.imgPlayIcon.setVisibility(View.VISIBLE);
        } else {
            holder.imgPlayIcon.setVisibility(View.GONE);
        }

        // Hiện badge Gallery nếu là Di tích có nhiều ảnh
        boolean hasGallery = "Di tích".equalsIgnoreCase(item.getCategory())
                && LandmarkGalleryDataManager.hasGallery(item.getId());
        holder.tvGalleryBadge.setVisibility(hasGallery ? View.VISIBLE : View.GONE);

        // Xử lý sự kiện click
        holder.itemView.setOnClickListener(v -> {
            if ("video".equalsIgnoreCase(item.getType())) {
                // Item video -> mở VideoPlayerActivity
                Intent intent = new Intent(context, VideoPlayerActivity.class);
                intent.putExtra("VIDEO_URL", item.getUrl());
                context.startActivity(intent);

            } else if ("Di tích".equalsIgnoreCase(item.getCategory())
                    && LandmarkGalleryDataManager.hasGallery(item.getId())) {
                // Item Di tích có gallery -> mở LandmarkGalleryActivity
                LandmarkGallery gallery = LandmarkGalleryDataManager.getGallery(item.getId());
                Intent intent = new Intent(context, LandmarkGalleryActivity.class);
                intent.putExtra(LandmarkGalleryActivity.EXTRA_GALLERY, gallery);
                context.startActivity(intent);

            } else {
                // Item ảnh thường -> mở PhotoViewerActivity
                Intent intent = new Intent(context, PhotoViewerActivity.class);
                intent.putExtra("IMAGE_URL", item.getUrl());
                context.startActivity(intent);
            }
        });

        // Hiệu ứng Fade + Slide Up
        holder.itemView.setAlpha(0f);
        holder.itemView.setTranslationY(80f);
        holder.itemView.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(450)
                .setInterpolator(new android.view.animation.DecelerateInterpolator())
                .setStartDelay(position * 30L)
                .start();
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    public void updateList(List<MediaItem> newList) {
        mediaList = newList;
        notifyDataSetChanged();
    }

    public static class MediaViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumbnail, imgPlayIcon;
        TextView tvMediaTitle, tvGalleryBadge;

        public MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumbnail = itemView.findViewById(R.id.imgThumbnail);
            imgPlayIcon = itemView.findViewById(R.id.imgPlayIcon);
            tvMediaTitle = itemView.findViewById(R.id.tvMediaTitle);
            tvGalleryBadge = itemView.findViewById(R.id.tvGalleryBadge);
        }
    }
}
