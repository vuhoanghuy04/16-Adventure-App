package com.example.a16adventure.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;

import com.example.a16adventure.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        // Áp dụng theme trước khi super.onCreate
        applyTheme();
        super.onCreate(savedInstanceState);
    }

    private void applyTheme() {
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        int themeMode = prefs.getInt("ThemeMode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(themeMode);
    }

    protected void setupBottomNavigation(int bottomNavId, int selectedItemId) {
        BottomNavigationView bottomNavigation = findViewById(bottomNavId);
        if (bottomNavigation == null) return;

        // 1. Cấu hình màu sắc
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        int themeMode = prefs.getInt("ThemeMode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        
        boolean isDark;
        if (themeMode == AppCompatDelegate.MODE_NIGHT_YES) {
            isDark = true;
        } else if (themeMode == AppCompatDelegate.MODE_NIGHT_NO) {
            isDark = false;
        } else {
            // Nếu là Follow System, kiểm tra cấu hình hiện tại của máy
            int currentNightMode = getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
            isDark = currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES;
        }

        int[][] states = new int[][] {
                new int[] { android.R.attr.state_checked},
                new int[] {-android.R.attr.state_checked}
        };
        int[] colors = new int[] {
                Color.parseColor("#FF4B4B"),
                isDark ? Color.parseColor("#B0B0B0") : Color.parseColor("#808080")
        };
        ColorStateList colorStateList = new ColorStateList(states, colors);
        bottomNavigation.setItemIconTintList(colorStateList);
        bottomNavigation.setItemTextColor(colorStateList);

        // Đặt màu nền cho BottomNavigationView dựa trên chế độ tối
        bottomNavigation.setBackgroundColor(isDark ? Color.parseColor("#1E1E1E") : Color.WHITE);

        // 2. Set tab đang được chọn
        if (selectedItemId != 0) {
            bottomNavigation.setSelectedItemId(selectedItemId);
        } else {
            bottomNavigation.getMenu().setGroupCheckable(0, false, true);
        }

        // 3. Xử lý chuyển trang (Đã bỏ FLAG_ACTIVITY_REORDER_TO_FRONT)
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            // Nếu bấm vào chính tab đang mở thì bỏ qua
            if (itemId == selectedItemId) return true;

            Intent intent = null;

            if (itemId == R.id.nav_home) {
                intent = new Intent(this, MainActivity.class);
                // Xóa toàn bộ stack phía trên MainActivity để tránh đầy RAM
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            } else if (itemId == R.id.nav_explore) {
                // Đã sửa lại thành ExploreActivity để ra Menu Khám phá 2 nút
                intent = new Intent(this, ExploreActivity.class);
            } else if (itemId == R.id.nav_map) {
                intent = new Intent(this, MapActivity.class);
            } else if (itemId == R.id.nav_ai) {
                intent = new Intent(this, AiActivity.class);
            } else if (itemId == R.id.nav_profile) {
                intent = new Intent(this, ProfileActivity.class);
            }

            if (intent != null) {
                startActivity(intent);
                overridePendingTransition(0, 0); // TẮT HIỆU ỨNG CHUYỂN CẢNH MƯỢT MÀ

                // Đóng trang hiện tại để giải phóng RAM (trừ MainActivity)
                if (!this.getClass().getSimpleName().equals("MainActivity")) {
                    finish();
                }
            }
            return false;
        });
    }
}