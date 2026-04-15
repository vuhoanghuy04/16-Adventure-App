package com.example.a16adventure.activities;

import android.content.Intent;
import android.os.Bundle;
import com.example.a16adventure.R;

public class AiActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai);

        // Khởi tạo thanh điều hướng và báo cho nó biết tab AI đang được chọn
        setupBottomNavigation(R.id.bottomNavigation, R.id.nav_ai);

        // Bắt sự kiện chuyển sang trang Chat AI
        findViewById(R.id.btnChatAi).setOnClickListener(v -> {
            Intent intent = new Intent(AiActivity.this, ChatActivity.class);
            startActivity(intent);
        });

        // Bắt sự kiện chuyển sang trang Nhận diện ảnh
        findViewById(R.id.btnRecognition).setOnClickListener(v -> {
            Intent intent = new Intent(AiActivity.this, RecognitionActivity.class);
            startActivity(intent);
        });
    }
}