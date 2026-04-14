package com.example.a16adventure.activities;

import android.os.Bundle;
import com.example.a16adventure.R;

public class ExploreActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        // Kích hoạt thanh điều hướng, cho tab "Khám phá" sáng màu
        setupBottomNavigation(R.id.bottomNavigation, R.id.nav_explore);

        // Nút 1: Di tích lịch sử (Load MainActivity - list monument)
        findViewById(R.id.btnKnowledgeLibrary).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(ExploreActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // Các Ảnh nền giờ đã được ép thẳng vào XML (không cần chờ mạng tải nữa)
        
        // Hiệu ứng Fade & Phóng to nẩy cho các Card
        android.view.View c1 = findViewById(R.id.btnKnowledgeLibrary);
        android.view.View c2 = findViewById(R.id.btnMediaLibrary);
        android.view.View c3 = findViewById(R.id.btnGalleryLibrary);
        
        android.view.View[] cards = {c1, c2, c3};
        for (int i = 0; i < cards.length; i++) {
            cards[i].setAlpha(0f);
            cards[i].setScaleX(0.8f);
            cards[i].setScaleY(0.8f);
            cards[i].animate()
                .alpha(1f).scaleX(1f).scaleY(1f)
                .setDuration(500)
                .setStartDelay(i * 150L)
                .setInterpolator(new android.view.animation.OvershootInterpolator())
                .start();
        }

        // Nút 2: Lễ hội & Sự kiện (Load EventListActivity)
        findViewById(R.id.btnMediaLibrary).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(ExploreActivity.this, EventListActivity.class);
            startActivity(intent);
        });

        // Nút 3: Thư viện Đa phương tiện (Load MediaLibraryActivity)
        findViewById(R.id.btnGalleryLibrary).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(ExploreActivity.this, MediaLibraryActivity.class);
            startActivity(intent);
        });
    }
}