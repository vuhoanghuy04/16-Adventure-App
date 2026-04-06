package com.example.a16adventure.activities;

import android.content.Intent;
import android.os.Bundle;
import com.example.a16adventure.R;
import com.google.android.material.card.MaterialCardView;

public class AiActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai);

        setupBottomNavigation(R.id.bottomNavigation, R.id.nav_ai);

        MaterialCardView cardRecognize = findViewById(R.id.cardRecognize);
        MaterialCardView btnAskAi = findViewById(R.id.btnAskAi);

        // Mở chức năng Nhận diện địa danh
        cardRecognize.setOnClickListener(v -> {
            Intent intent = new Intent(AiActivity.this, RecognitionActivity.class);
            startActivity(intent);
        });

        // Mở chức năng Chatbot
        btnAskAi.setOnClickListener(v -> {
            Intent intent = new Intent(AiActivity.this, ChatActivity.class);
            startActivity(intent);
        });
    }
}