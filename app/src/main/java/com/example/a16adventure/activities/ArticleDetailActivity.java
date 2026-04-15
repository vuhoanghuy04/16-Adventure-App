package com.example.a16adventure.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.a16adventure.R;

public class ArticleDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        // 1. Ánh xạ các thành phần giao diện
        TextView tvDetailTitle = findViewById(R.id.tvDetailTitle);
        TextView tvDetailCategory = findViewById(R.id.tvDetailCategory);
        TextView tvDetailTime = findViewById(R.id.tvDetailTime);
        TextView tvDetailContent = findViewById(R.id.tvDetailContent);
        ImageView imgArticleCover = findViewById(R.id.imgArticleCover);
        ImageView btnBack = findViewById(R.id.btnBack);

        // 2. Nhận gói hàng dữ liệu từ Adapter gửi sang
        String title = getIntent().getStringExtra("TITLE");
        String category = getIntent().getStringExtra("CATEGORY");
        String timeViews = getIntent().getStringExtra("TIME_VIEWS");
        String content = getIntent().getStringExtra("CONTENT");
        String imageUrl = getIntent().getStringExtra("IMAGE_URL");

        // 3. Hiển thị chữ lên màn hình
        if (title != null) tvDetailTitle.setText(title);
        if (category != null) tvDetailCategory.setText("• " + category);
        if (timeViews != null) tvDetailTime.setText(timeViews);
        if (content != null) tvDetailContent.setText(content);

        // 4. Ma thuật Tải ảnh từ Internet bằng Glide
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    // placeholder là ảnh sẽ hiện lên trong 1-2 giây chờ tải mạng
                    .placeholder(R.drawable.bg_search_bar)
                    .into(imgArticleCover);
        }

        // 5. Nút Quay lại
        btnBack.setOnClickListener(v -> onBackPressed());
    }
}