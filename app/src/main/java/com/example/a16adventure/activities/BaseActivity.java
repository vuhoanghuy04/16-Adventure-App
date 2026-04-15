package com.example.a16adventure.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;

import com.example.a16adventure.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BaseActivity extends AppCompatActivity {

    protected void setupBottomNavigation(int bottomNavId, int selectedItemId) {
        BottomNavigationView bottomNavigation = findViewById(bottomNavId);
        if (bottomNavigation == null) return;

        // 1. Cấu hình màu sắc đỏ/xám
        int[][] states = new int[][] {
                new int[] { android.R.attr.state_checked},
                new int[] {-android.R.attr.state_checked}
        };
        int[] colors = new int[] {
                Color.parseColor("#FF4B4B"),
                Color.parseColor("#808080")
        };
        ColorStateList colorStateList = new ColorStateList(states, colors);
        bottomNavigation.setItemIconTintList(colorStateList);
        bottomNavigation.setItemTextColor(colorStateList); // Thêm dòng này để đổi cả màu chữ

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
                // Kiểm tra đăng nhập
                if (com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser() != null) {
                    intent = new Intent(this, ProfileActivity.class);
                } else {
                    intent = new Intent(this, LoginActivity.class); // Thay đổi tên class Login của bạn nếu cần
                }
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