package com.example.a16adventure.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.a16adventure.R;
import com.google.android.material.card.MaterialCardView;

public class AiActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai);

        // Kích hoạt thanh điều hướng, cho tab "Trợ lý AI" sáng màu
        setupBottomNavigation(R.id.bottomNavigation, R.id.nav_ai);

        // Ánh xạ các view từ XML
        MaterialCardView cardRecognize = findViewById(R.id.cardRecognize);
        MaterialCardView btnAskAi = findViewById(R.id.btnAskAi);

        // Xử lý khi bấm vào Card Nhận diện hoặc hình cô gái (mở Chatbot)
        // Dựa trên Figma, ta có thể cho phép bấm vào card 2 để mở chat
        findViewById(R.id.btnAskAi).setOnClickListener(v -> openChat());

        // Xử lý logic cho nút "Nhận diện địa danh"
        cardRecognize.setOnClickListener(v -> {
            Toast.makeText(this, "Tính năng Nhận diện địa danh qua Camera đang được phát triển!", Toast.LENGTH_LONG).show();
            // Sau này sẽ Intent tới RecognitionActivity
        });

        // Xử lý logic cho nút "ĐẶT CÂU HỎI"
        btnAskAi.setOnClickListener(v -> openChat());
    }

    private void openChat() {
        Intent intent = new Intent(AiActivity.this, ChatActivity.class);
        startActivity(intent);
    }
}