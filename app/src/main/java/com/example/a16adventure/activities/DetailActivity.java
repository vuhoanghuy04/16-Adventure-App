package com.example.a16adventure.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.a16adventure.R;

public class DetailActivity extends AppCompatActivity {

    private TextView tvDetailTitle, tvDetailCategory, tvDetailTime;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail); // Gắn giao diện bạn vừa tạo ở trên

        // 1. Ánh xạ các thành phần
        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailCategory = findViewById(R.id.tvDetailCategory);
        tvDetailTime = findViewById(R.id.tvDetailTime);
        btnBack = findViewById(R.id.btnBack);

        // 2. Nhận dữ liệu từ chuyến xe Intent
        String title = getIntent().getStringExtra("TITLE");
        String category = getIntent().getStringExtra("CATEGORY");
        String timeViews = getIntent().getStringExtra("TIME_VIEWS");

        // 3. Đổ dữ liệu lên màn hình
        if (title != null) tvDetailTitle.setText(title);
        if (category != null) tvDetailCategory.setText("• " + category);
        if (timeViews != null) tvDetailTime.setText(timeViews);

        // 4. Cài đặt tính năng cho nút Back (Quay lại)
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Bấm vào là lùi lại trang Khám phá
            }
        });
    }
}