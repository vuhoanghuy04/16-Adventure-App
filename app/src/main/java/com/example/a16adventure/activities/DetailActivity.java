package com.example.a16adventure.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.a16adventure.R;

public class DetailActivity extends BaseActivity {

    private ImageView imgDetail;
    private TextView tvDetailName, tvDetailDistrict, tvDetailDescription;
    private Button btnViewOnMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // 1. Ánh xạ View
        imgDetail = findViewById(R.id.imgDetail);
        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailDistrict = findViewById(R.id.tvDetailDistrict);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);
        btnViewOnMap = findViewById(R.id.btnViewOnMap);

        // 2. Nhận dữ liệu từ Intent gửi tới
        Intent intent = getIntent();
        if (intent != null) {
            String name = intent.getStringExtra("name");
            String district = intent.getStringExtra("district");
            String description = intent.getStringExtra("description");
            String imageUrl = intent.getStringExtra("image");

            // Lấy toạ độ để dùng cho Google Maps
            double lat = intent.getDoubleExtra("lat", 0);
            double lng = intent.getDoubleExtra("lng", 0);

            // 3. Hiển thị dữ liệu lên màn hình
            tvDetailName.setText(name);
            tvDetailDistrict.setText("📍 " + district);
            tvDetailDescription.setText(description);

            Glide.with(this)
                    .load(imageUrl)
                    .into(imgDetail);

            // 4. Xử lý nút bấm Xem trên bản đồ (Chuyển hướng sang Google Maps ngoài)
            btnViewOnMap.setOnClickListener(v -> {
                // Tạo chuỗi URI theo chuẩn của hệ điều hành để gọi Bản đồ
                // Cú pháp: geo:lat,lng?q=lat,lng(Tên nhãn)
                String uriString = "geo:" + lat + "," + lng + "?q=" + lat + "," + lng + "(" + name + ")";
                android.net.Uri gmmIntentUri = android.net.Uri.parse(uriString);

                // Tạo Intent yêu cầu hệ thống mở URI này
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

                // Thử mở ứng dụng. Nếu máy (đặc biệt là máy ảo) không có app Bản đồ thì báo lỗi nhẹ nhàng
                try {
                    startActivity(mapIntent);
                } catch (android.content.ActivityNotFoundException e) {
                    Toast.makeText(DetailActivity.this, "Máy của bạn chưa cài đặt ứng dụng Bản đồ (Google Maps)!", Toast.LENGTH_SHORT).show();
                }
            });


        }
    }
}