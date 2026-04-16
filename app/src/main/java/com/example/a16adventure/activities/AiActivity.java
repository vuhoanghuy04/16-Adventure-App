package com.example.a16adventure.activities;

import android.os.Bundle;
import com.example.a16adventure.R;

public class AiActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai);

        // Kích hoạt thanh điều hướng, cho tab "Trợ lý AI" sáng màu
        setupBottomNavigation(R.id.bottomNavigation, R.id.nav_ai);

    }
}