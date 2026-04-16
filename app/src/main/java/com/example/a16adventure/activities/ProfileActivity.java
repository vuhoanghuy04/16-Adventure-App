package com.example.a16adventure.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a16adventure.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends BaseActivity {

    private TextView tvProfileName, tvProfileEmail;
    private Button btnLogout;
    private LinearLayout btnSavedMonuments;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // 1. Khởi tạo Firebase Auth và Ánh xạ View
        mAuth = FirebaseAuth.getInstance();
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        btnLogout = findViewById(R.id.btnLogout);
        btnSavedMonuments = findViewById(R.id.btnSavedMonuments);

        // 2. Cấu hình Bottom Navigation (Tab Cá nhân sáng đỏ)
        setupBottomNavigation(R.id.bottomNavigation, R.id.nav_profile);

        // 3. LẤY DỮ LIỆU NGƯỜI DÙNG TỪ FIREBASE
        loadUserData();

        // 4. XỬ LÝ NÚT ĐỊA DANH ĐÃ LƯU
        if (btnSavedMonuments != null) {
            btnSavedMonuments.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, SavedMonumentsActivity.class);
                startActivity(intent);
            });
        }

        // 5. XỬ LÝ ĐĂNG XUẤT
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                mAuth.signOut();
                Toast.makeText(this, "Đã đăng xuất thành công!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }
    }

    private void loadUserData() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();

            if (tvProfileName != null) {
                tvProfileName.setText(name != null && !name.isEmpty() ? name : "Chưa đặt tên");
            }
            if (tvProfileEmail != null) {
                tvProfileEmail.setText(email);
            }
        } else {
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        }
    }
}