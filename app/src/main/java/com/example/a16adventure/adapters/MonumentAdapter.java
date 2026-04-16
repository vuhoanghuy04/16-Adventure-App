package com.example.a16adventure.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.a16adventure.R;
import com.example.a16adventure.activities.DetailActivity;
import com.example.a16adventure.models.Monument;
import com.google.android.material.card.MaterialCardView;

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
        View view = LayoutInflater.from(context).inflate(R.layout.item_monument, parent, false);

        // --- TỰ ĐỘNG CÂN CHỈNH KÍCH THƯỚC THEO THIẾT BỊ (RESPONSIVE) ---
        // Lấy chiều rộng màn hình thực tế của thiết bị
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        // Thiết lập chiều rộng mỗi thẻ chiếm 82% màn hình để tạo hiệu ứng "nhìn thấy một phần thẻ sau"
        int itemWidth = (int) (screenWidth * 0.82);

        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params != null) {
            params.width = itemWidth;
            view.setLayoutParams(params);
        }

        return new MonumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MonumentViewHolder holder, int position) {
        // Lấy đúng đối tượng Monument tại vị trí hiện tại
        Monument monument = monumentList.get(position);

        holder.tvMonumentName.setText(monument.getName());
        holder.tvDistrict.setText("📍 " + monument.getDistrict());

        // Glide tải ảnh
        Glide.with(context).load(monument.getImageUrl())
                .placeholder(android.R.color.darker_gray).into(holder.imgMonument);

        // 1. Chuyển trang khi bấm vào Ảnh/Nền thẻ
        holder.cardContainer.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(context, DetailActivity.class);
            intent.putExtra("EXTRA_NAME", monument.getName());
            intent.putExtra("EXTRA_DISTRICT", monument.getDistrict());
            intent.putExtra("EXTRA_DESC", monument.getDescription());
            intent.putExtra("EXTRA_IMAGE", monument.getImageUrl());
            intent.putExtra("EXTRA_LAT", monument.getLatitude());
            intent.putExtra("EXTRA_LNG", monument.getLongitude());
            context.startActivity(intent);
        });

        // 2. KHỞI TẠO TRẠNG THÁI MÀU SẮC (Khi cuộn danh sách)
        updateLikeUI(holder, monument.isLiked());
        updateSaveUI(holder, monument.isSaved());

        // 3. XỬ LÝ NÚT THÍCH (Trái tim Xanh lá) - TÍCH HỢP THUẬT TOÁN GỢI Ý
        holder.btnLike.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION) return;

            Monument currentMonument = monumentList.get(currentPosition);
            currentMonument.setLiked(!currentMonument.isLiked()); // Đảo ngược trạng thái
            updateLikeUI(holder, currentMonument.isLiked());

            // NẾU NGƯỜI DÙNG ẤN THÍCH -> KÍCH HOẠT GỢI Ý
            if (currentMonument.isLiked()) {
                String targetDistrict = currentMonument.getDistrict();
                Toast.makeText(context, "Sẽ ưu tiên gợi ý thêm địa danh tại " + targetDistrict, Toast.LENGTH_SHORT).show();

                // Vị trí để chèn các thẻ tương tự (Ngay sau thẻ hiện tại)
                int insertPos = currentPosition + 1;

                // Quét qua toàn bộ các thẻ đang nằm xếp hàng ở phía sau
                for (int i = insertPos; i < monumentList.size(); i++) {
                    Monument candidate = monumentList.get(i);

                    // Nếu phát hiện thẻ có cùng Quận/Huyện
                    if (candidate.getDistrict().equals(targetDistrict)) {
                        // Nhấc thẻ đó ra khỏi vị trí hiện tại...
                        monumentList.remove(i);
                        // ...và chèn nó lên phía trên (ngay sau nhóm thẻ tương tự)
                        monumentList.add(insertPos, candidate);

                        // Kích hoạt hiệu ứng mượt mà của Android báo rằng thẻ đã được dời lên
                        notifyItemMoved(i, insertPos);

                        // Nhích vị trí chèn lên 1 nấc để đón thẻ tương tự tiếp theo
                        insertPos++;
                    }
                }
            }
        });

        // 4. XỬ LÝ NÚT LƯU (Hình ngôi sao)
        holder.btnSave.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION) return;

            Monument currentMonument = monumentList.get(currentPosition);

            // --- KIỂM TRA ĐĂNG NHẬP ---
            com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                android.widget.Toast.makeText(context, "Bạn cần đăng nhập để lưu địa danh!", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }

            // --- LOGIC LƯU LOCAL VÀ ĐỔI UI ---
            String uid = user.getUid();
            String monumentId = currentMonument.getId();

            boolean newSavedState = !currentMonument.isSaved();
            currentMonument.setSaved(newSavedState);
            updateSaveUI(holder, newSavedState);

            // --- ĐỒNG BỘ LÊN FIREBASE REALTIME DATABASE ---
            com.google.firebase.database.DatabaseReference savedRef = com.google.firebase.database.FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(uid)
                    .child("saved_ids")
                    .child(monumentId);

            if (newSavedState) {
                savedRef.setValue(true);
            } else {
                savedRef.removeValue();
            }
        });

        // 5. XỬ LÝ NÚT KHÔNG THÍCH (Chữ X Đỏ) -> XÓA THẺ VÀ ĐẨY CÁC THẺ CÙNG KHU VỰC XUỐNG CUỐI
        holder.btnDislike.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION) return;

            Monument currentMonument = monumentList.get(currentPosition);
            String dislikedDistrict = currentMonument.getDistrict();

            // Hiệu ứng chớp đỏ ngay lập tức trước khi bay màu
            holder.btnDislike.setCardBackgroundColor(Color.parseColor("#F44336"));
            holder.iconDislike.setColorFilter(Color.WHITE);

            // Delay 0.2s để người dùng kịp nhìn thấy hiệu ứng
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {

                // --- THUẬT TOÁN "GIẢM GỢI Ý" ---
                // Quét ngược từ thẻ cuối cùng của danh sách lên đến thẻ hiện tại
                boolean hasMoved = false;
                for (int i = monumentList.size() - 1; i > currentPosition; i--) {
                    Monument candidate = monumentList.get(i);

                    // Nếu phát hiện thẻ có cùng Quận/Huyện mà người dùng vừa ấn Không Thích
                    if (candidate.getDistrict().equals(dislikedDistrict)) {
                        // 1. Nhấc thẻ đó ra khỏi vị trí hiện tại
                        monumentList.remove(i);
                        // 2. Ném thẻ đó xuống tận cùng của danh sách
                        monumentList.add(candidate);
                        // 3. Kích hoạt hiệu ứng thẻ trượt lùi về sau
                        notifyItemMoved(i, monumentList.size() - 1);
                        hasMoved = true;
                    }
                }

                // --- SAU ĐÓ XÓA THẺ HIỆN TẠI (Thẻ bị ấn X) ---
                monumentList.remove(currentPosition);
                notifyItemRemoved(currentPosition);
                // Cập nhật lại số thứ tự cho các thẻ phía sau
                notifyItemRangeChanged(currentPosition, monumentList.size() - currentPosition);

                // Hiện thông báo cho người dùng biết thuật toán đã hoạt động
                if (hasMoved) {
                    Toast.makeText(context, "Sẽ ít gợi ý các địa danh tại " + dislikedDistrict + " hơn", Toast.LENGTH_SHORT).show();
                }

                // Trả lại màu trắng/đỏ mặc định cho nút X
                holder.btnDislike.setCardBackgroundColor(Color.WHITE);
                holder.iconDislike.setColorFilter(Color.parseColor("#F44336"));

            }, 200);
        });
    }

    // --- HÀM PHỤ TRỢ: ĐỔI MÀU NÚT ---
    private void updateLikeUI(MonumentViewHolder holder, boolean isLiked) {
        if (isLiked) {
            holder.btnLike.setCardBackgroundColor(Color.parseColor("#4CAF50")); // Nền Xanh
            holder.iconLike.setColorFilter(Color.WHITE); // Tim Trắng
        } else {
            holder.btnLike.setCardBackgroundColor(Color.WHITE); // Nền Trắng
            holder.iconLike.setColorFilter(Color.parseColor("#4CAF50")); // Tim Xanh
        }
    }

    private void updateSaveUI(MonumentViewHolder holder, boolean isSaved) {
        if (isSaved) {
            holder.btnSave.setCardBackgroundColor(Color.parseColor("#2196F3")); // Nền Xanh lam
            holder.iconSave.setColorFilter(Color.WHITE); // Sao Trắng
            holder.iconSave.setImageResource(android.R.drawable.star_on); // Icon sao đặc
        } else {
            holder.btnSave.setCardBackgroundColor(Color.WHITE); // Nền Trắng
            holder.iconSave.setColorFilter(Color.parseColor("#2196F3")); // Sao Xanh lam
            holder.iconSave.setImageResource(android.R.drawable.star_off); // Icon sao rỗng
        }
    }

    @Override
    public int getItemCount() {
        return monumentList.size();
    }

    public static class MonumentViewHolder extends RecyclerView.ViewHolder {
        FrameLayout cardContainer;
        ImageView imgMonument;
        TextView tvMonumentName, tvDistrict;
        MaterialCardView btnDislike, btnSave, btnLike;
        ImageView iconDislike, iconSave, iconLike;

        public MonumentViewHolder(@NonNull View itemView) {
            super(itemView);
            cardContainer = itemView.findViewById(R.id.cardContainer);
            imgMonument = itemView.findViewById(R.id.imgMonument);
            tvMonumentName = itemView.findViewById(R.id.tvMonumentName);
            tvDistrict = itemView.findViewById(R.id.tvDistrict);

            btnDislike = itemView.findViewById(R.id.btnDislike);
            btnSave = itemView.findViewById(R.id.btnSave);
            btnLike = itemView.findViewById(R.id.btnLike);

            iconDislike = itemView.findViewById(R.id.iconDislike);
            iconSave = itemView.findViewById(R.id.iconSave);
            iconLike = itemView.findViewById(R.id.iconLike);
        }
    }
}