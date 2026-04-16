package com.example.a16adventure.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.a16adventure.R;

public class ExploreActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        // Bật sáng icon Khám phá ở thanh điều hướng dưới cùng
        setupBottomNavigation(R.id.bottomNavigation, R.id.nav_explore);

        // --- NỐI DÂY SANG CÁC PHÂN KHU KHÁM PHÁ ---
        View btnKnowledge = findViewById(R.id.btnKnowledgeLibrary);
        View btnMedia = findViewById(R.id.btnMediaLibrary);
        View btnFestival = findViewById(R.id.btnFestivalEvents);

        // 1. Thư viện Kiến thức (Link như cũ)
        if (btnKnowledge != null) {
            btnKnowledge.setOnClickListener(v -> {
                Intent intent = new Intent(ExploreActivity.this, KnowledgeActivity.class);
                startActivity(intent);
            });
        }

        // 2. Thư viện Đa phương tiện
        if (btnMedia != null) {
            btnMedia.setOnClickListener(v -> {
                Intent intent = new Intent(ExploreActivity.this, MediaLibraryActivity.class);
                startActivity(intent);
            });
        }

        // 3. Lễ hội và Sự kiện
        if (btnFestival != null) {
            btnFestival.setOnClickListener(v -> {
                Intent intent = new Intent(ExploreActivity.this, EventListActivity.class);
                startActivity(intent);
            });
        }
    }
}