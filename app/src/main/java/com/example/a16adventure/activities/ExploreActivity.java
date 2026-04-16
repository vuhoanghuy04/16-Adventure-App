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

        // 2. Thư viện Đa phương tiện (Sẽ làm sau)
        if (btnMedia != null) {
            btnMedia.setOnClickListener(v -> {
                android.widget.Toast.makeText(this, "Tính năng Thư viện Đa phương tiện đang phát triển", android.widget.Toast.LENGTH_SHORT).show();
            });
        }

        // 3. Lễ hội và Sự kiện (Sẽ làm sau)
        if (btnFestival != null) {
            btnFestival.setOnClickListener(v -> {
                android.widget.Toast.makeText(this, "Tính năng Lễ hội và Sự kiện đang phát triển", android.widget.Toast.LENGTH_SHORT).show();
            });
        }
    }
}