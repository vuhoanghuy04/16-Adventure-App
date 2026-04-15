package com.example.a16adventure.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.example.a16adventure.R;

public class AiActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai);

        setupBottomNavigation(R.id.bottomNavigation, R.id.nav_ai);

        View cardRecognize = findViewById(R.id.cardRecognize);
        View btnAskAi = findViewById(R.id.btnAskAi);

        // Mở chức năng Nhận diện địa danh
        if (cardRecognize != null) {
            cardRecognize.setOnClickListener(v -> {
                Intent intent = new Intent(AiActivity.this, RecognitionActivity.class);
                startActivity(intent);
            });
        }

        // Mở chức năng Chatbot
        if (btnAskAi != null) {
            btnAskAi.setOnClickListener(v -> {
                Intent intent = new Intent(AiActivity.this, ChatActivity.class);
                startActivity(intent);
            });
        }
    }
}
