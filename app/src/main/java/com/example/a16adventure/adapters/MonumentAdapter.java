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
import com.example.a16adventure.activities.DetailActivity;
import com.example.a16adventure.models.Monument;
import java.util.List;

public class MonumentAdapter extends RecyclerView.Adapter<MonumentAdapter.MonumentViewHolder> {

    private Context context;
    private List<Monument> monumentList;

    public MonumentAdapter(Context context, List<Monument> monumentList) {
        this.context = context;
        this.monumentList = monumentList;
    }

    @NonNull
    @Override
    public MonumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Nạp giao diện thẻ di tích bạn đã thiết kế
        View view = LayoutInflater.from(context).inflate(R.layout.item_monument, parent, false);
        return new MonumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MonumentViewHolder holder, int position) {
        Monument monument = monumentList.get(position);

        // Gắn dữ liệu vào các thành phần trên thẻ
        holder.tvMonumentName.setText(monument.getName());
        holder.tvDistrict.setText("📍 " + monument.getDistrict());

        // Sử dụng Glide để tải ảnh từ URL vào ImageView
        Glide.with(context)
                .load(monument.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background) // Ảnh hiển thị tạm lúc đang tải
                .into(holder.imgMonument);

        // Xử lý sự kiện khi bấm nút Information (i)
        holder.btnInfo.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            // Gói dữ liệu để gửi sang DetailActivity
            intent.putExtra("name", monument.getName());
            intent.putExtra("district", monument.getDistrict());
            intent.putExtra("description", monument.getDescription());
            intent.putExtra("image", monument.getImageUrl());
            intent.putExtra("lat", monument.getLatitude());
            intent.putExtra("lng", monument.getLongitude());

            // Mở màn hình mới
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return monumentList != null ? monumentList.size() : 0;
    }

    // Lớp nội (Inner class) để giữ các View trong thẻ
    public static class MonumentViewHolder extends RecyclerView.ViewHolder {
        ImageView imgMonument;
        TextView tvMonumentName, tvDistrict;
        View btnInfo;

        public MonumentViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMonument = itemView.findViewById(R.id.imgMonument);
            tvMonumentName = itemView.findViewById(R.id.tvMonumentName);
            tvDistrict = itemView.findViewById(R.id.tvDistrict);
            btnInfo = itemView.findViewById(R.id.btnInfo);
        }
    }
}
