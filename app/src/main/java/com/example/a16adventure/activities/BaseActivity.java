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
        bottomNavigation.setItemTextColor(colorStateList);

        // 2. Set tab đang được chọn
        if (selectedItemId != 0) {
            bottomNavigation.setSelectedItemId(selectedItemId);
        } else {
            bottomNavigation.getMenu().setGroupCheckable(0, false, true);
        }

        // 3. Xử lý chuyển trang tối ưu RAM
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == selectedItemId) return true;

            Class<?> targetActivity = null;

            if (itemId == R.id.nav_home) targetActivity = MainActivity.class;
            else if (itemId == R.id.nav_explore) targetActivity = ExploreActivity.class;
            else if (itemId == R.id.nav_map) targetActivity = MapActivity.class;
            else if (itemId == R.id.nav_ai) targetActivity = AiActivity.class;
            else if (itemId == R.id.nav_profile) {
                if (com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser() != null) {
                    targetActivity = ProfileActivity.class;
                } else {
                    targetActivity = LoginActivity.class;
                }
            }

            if (targetActivity != null) {
                Intent intent = new Intent(this, targetActivity);
                // FLAG_ACTIVITY_REORDER_TO_FRONT: Tái sử dụng Activity cũ nếu có, 
                // giúp giảm thiểu việc khởi tạo lại và tiết kiệm RAM.
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }
}
