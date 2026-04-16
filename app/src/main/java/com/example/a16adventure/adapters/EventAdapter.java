package com.example.a16adventure.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.a16adventure.R;
import com.example.a16adventure.activities.EventDetailActivity;
import com.example.a16adventure.models.Event;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private Context context;
    private List<Event> eventList;

    public EventAdapter(Context context, List<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);

        holder.tvEventName.setText(event.getName());
        holder.tvEventDate.setText(event.getDate());
        holder.tvEventLocation.setText(event.getLocation());
        holder.tvEventDesc.setText(event.getShortDescription());

        int resId = context.getResources().getIdentifier(event.getImageUrl(), "drawable", context.getPackageName());
        if (resId != 0) {
            Glide.with(context)
                    .load(resId)
                    .centerCrop()
                    .placeholder(R.drawable.bg_weather_default)
                    .into(holder.imgEvent);
        } else {
            Glide.with(context)
                    .load(event.getImageUrl())
                    .centerCrop()
                    .placeholder(R.drawable.bg_weather_default)
                    .into(holder.imgEvent);
        }

        // Hiệu ứng màu xen kẽ như Figma (Đỏ nhạt, Xám nhạt...)
        if (position % 2 == 0) {
            holder.layoutRoot.setBackgroundColor(Color.parseColor("#EAEAEA"));
            holder.tvEventName.setTextColor(Color.parseColor("#333333")); // Chữ đen
            holder.tvEventDate.setTextColor(Color.parseColor("#FF5252")); // Ngày màu đỏ
            holder.tvEventLocation.setTextColor(Color.parseColor("#666666")); // Địa điểm gốc
            holder.tvEventDesc.setTextColor(Color.parseColor("#555555"));
            if (holder.iconDate != null) holder.iconDate.setColorFilter(Color.parseColor("#D32F2F"));
            if (holder.iconLocation != null) holder.iconLocation.setColorFilter(Color.parseColor("#555555"));
        } else {
            holder.layoutRoot.setBackgroundColor(Color.parseColor("#FF5252"));
            holder.tvEventName.setTextColor(Color.WHITE);
            holder.tvEventDate.setTextColor(Color.WHITE);
            holder.tvEventLocation.setTextColor(Color.WHITE);
            holder.tvEventDesc.setTextColor(Color.WHITE);
            if (holder.iconDate != null) holder.iconDate.setColorFilter(Color.WHITE);
            if (holder.iconLocation != null) holder.iconLocation.setColorFilter(Color.WHITE);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventDetailActivity.class);
            intent.putExtra("EVENT_OBJECT", event);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void updateList(List<Event> newList) {
        eventList = newList;
        notifyDataSetChanged();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView imgEvent, iconDate, iconLocation;
        TextView tvEventName, tvEventDate, tvEventLocation, tvEventDesc;
        LinearLayout layoutRoot;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            imgEvent = itemView.findViewById(R.id.imgEvent);
            iconDate = itemView.findViewById(R.id.iconDate);
            iconLocation = itemView.findViewById(R.id.iconLocation);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvEventDate = itemView.findViewById(R.id.tvEventDate);
            tvEventLocation = itemView.findViewById(R.id.tvEventLocation);
            tvEventDesc = itemView.findViewById(R.id.tvEventDesc);
            View parent = itemView.findViewById(R.id.imgEvent).getParent() instanceof LinearLayout
                    ? (View) itemView.findViewById(R.id.imgEvent).getParent() : null;
            layoutRoot = parent instanceof LinearLayout ? (LinearLayout) parent : null;
        }
    }
}
