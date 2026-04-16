package com.example.a16adventure.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.a16adventure.R;
import com.example.a16adventure.models.Monument;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class MapMonumentAdapter extends RecyclerView.Adapter<MapMonumentAdapter.MonumentViewHolder> {

    private Context context;
    private List<Monument> monumentList;
    private OnMonumentClickListener listener;

    public interface OnMonumentClickListener {
        void onMonumentClick(Monument monument);
        void onDirectionsClick(Monument monument);
    }

    public MapMonumentAdapter(Context context, List<Monument> monumentList, OnMonumentClickListener listener) {
        this.context = context;
        this.monumentList = monumentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MonumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_monument_map, parent, false);
        return new MonumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MonumentViewHolder holder, int position) {
        Monument monument = monumentList.get(position);

        holder.tvMonumentName.setText(monument.getName());
        holder.tvDistrict.setText("📍 " + monument.getDistrict());

        Glide.with(context).load(monument.getImageUrl())
                .placeholder(android.R.color.darker_gray).into(holder.imgMonument);

        // Click vào cả thẻ để di chuyển map
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMonumentClick(monument);
            }
        });

        // Click vào nút chỉ đường
        holder.btnDirections.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDirectionsClick(monument);
            }
        });
    }

    @Override
    public int getItemCount() {
        return monumentList.size();
    }

    public static class MonumentViewHolder extends RecyclerView.ViewHolder {
        FrameLayout cardContainer;
        ImageView imgMonument;
        TextView tvMonumentName, tvDistrict;
        MaterialCardView btnDirections;

        public MonumentViewHolder(@NonNull View itemView) {
            super(itemView);
            cardContainer = itemView.findViewById(R.id.cardContainer);
            imgMonument = itemView.findViewById(R.id.imgMonument);
            tvMonumentName = itemView.findViewById(R.id.tvMonumentName);
            tvDistrict = itemView.findViewById(R.id.tvDistrict);
            btnDirections = itemView.findViewById(R.id.btnDirections);
        }
    }
}