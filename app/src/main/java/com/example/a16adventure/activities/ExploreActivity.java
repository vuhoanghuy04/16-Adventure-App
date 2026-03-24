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
    }
}