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

        // --- NỐI DÂY SANG TRANG KIẾN THỨC CỦA ĐỨC ---
        // Đã cập nhật đúng ID: btnKnowledgeLibrary từ file XML của leader
        View cardKnowledge = findViewById(R.id.btnKnowledgeLibrary);

        if (cardKnowledge != null) {
            cardKnowledge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Chuyển sang trang 12 bài viết của Đức
                    Intent intent = new Intent(ExploreActivity.this, KnowledgeActivity.class);
                    startActivity(intent);
                }
            });
        }
    }
}