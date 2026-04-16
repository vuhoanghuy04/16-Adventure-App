package com.example.a16adventure.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.a16adventure.R;
import com.example.a16adventure.models.Monument;
import java.util.List;

// Đảm bảo phải có đoạn extends RecyclerView.Adapter này thì mới hết lỗi
public class SavedMonumentAdapter extends RecyclerView.Adapter<SavedMonumentAdapter.ViewHolder> {

    private List<Monument> savedList;
    private Context context;
    private OnRemoveClickListener listener;

    // Interface để bắt sự kiện khi người dùng ấn nút X
    public interface OnRemoveClickListener {
        void onRemove(int position);
    }

    public SavedMonumentAdapter(Context context, List<Monument> savedList, OnRemoveClickListener listener) {
        this.context = context;
        this.savedList = savedList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_saved_monument, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Monument m = savedList.get(position);
        holder.tvName.setText(m.getName());
        holder.tvDistrict.setText(m.getDistrict());

        // Load ảnh bằng Glide
        Glide.with(context).load(m.getImageUrl()).into(holder.img);

        // Bắt sự kiện ấn nút X (gửi ngược vị trí về cho Activity xử lý)
        holder.btnRemove.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemove(position); // Không dùng lambda trực tiếp trong setOnClickListener nếu bị lỗi JDK cũ
            }
        });
    }

    @Override
    public int getItemCount() {
        return savedList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img, btnRemove;
        TextView tvName, tvDistrict;

        public ViewHolder(@NonNull View v) {
            super(v);
            img = v.findViewById(R.id.imgSaved);
            btnRemove = v.findViewById(R.id.btnRemoveSaved);
            tvName = v.findViewById(R.id.tvSavedName);
            tvDistrict = v.findViewById(R.id.tvSavedDistrict);
        }
    }
}