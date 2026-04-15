package com.example.a16adventure.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.a16adventure.R;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private final List<Uri> photoUris;
    private final Runnable onAddPhotoClick;

    public PhotoAdapter(List<Uri> photoUris, Runnable onAddPhotoClick) {
        this.photoUris = photoUris;
        this.onAddPhotoClick = onAddPhotoClick;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        if (position < photoUris.size()) {
            Glide.with(holder.itemView.getContext())
                    .load(photoUris.get(position))
                    .centerCrop()
                    .into(holder.imgPhoto);
            holder.imgRemove.setVisibility(View.VISIBLE);
            holder.imgRemove.setOnClickListener(v -> {
                photoUris.remove(position);
                notifyDataSetChanged();
            });
        } else {
            holder.imgPhoto.setImageResource(android.R.drawable.ic_input_add);
            holder.imgPhoto.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            holder.imgRemove.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(v -> onAddPhotoClick.run());
        }
    }

    @Override
    public int getItemCount() {
        return photoUris.size() + 1;
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPhoto;
        ImageView imgRemove;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPhoto = itemView.findViewById(R.id.imgPhoto);
            imgRemove = itemView.findViewById(R.id.imgRemove);
        }
    }
}
