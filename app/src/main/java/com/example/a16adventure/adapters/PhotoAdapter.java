package com.example.a16adventure.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.a16adventure.R;
import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private List<Uri> photoUris;
    private OnAddPhotoClickListener onAddPhotoClickListener;

    public interface OnAddPhotoClickListener {
        void onAddPhotoClick();
    }

    public PhotoAdapter(List<Uri> photoUris, OnAddPhotoClickListener listener) {
        this.photoUris = photoUris;
        this.onAddPhotoClickListener = listener;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        if (position == photoUris.size()) {
            // Ô cuối cùng là nút thêm ảnh
            holder.imgPhoto.setImageResource(android.R.drawable.ic_input_add);
            holder.itemView.setOnClickListener(v -> onAddPhotoClickListener.onAddPhotoClick());
        } else {
            // Hiển thị ảnh đã chọn
            holder.imgPhoto.setImageURI(photoUris.get(position));
            holder.itemView.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return photoUris.size() + 1; // +1 cho nút thêm ảnh
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPhoto;
        PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPhoto = itemView.findViewById(R.id.imgPhoto);
        }
    }
}