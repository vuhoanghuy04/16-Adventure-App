package com.example.a16adventure.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.example.a16adventure.R;
import com.example.a16adventure.domain.model.UserSession;
import com.example.a16adventure.presentation.auth.AuthViewModel;

public class ProfileActivity extends BaseActivity {

    private TextView tvProfileName, tvProfileEmail;
    private Button btnLogout;
    private LinearLayout btnSavedMonuments;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // 1. Khởi tạo AuthViewModel và Ánh xạ View
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        btnLogout = findViewById(R.id.btnLogout);
        btnSavedMonuments = findViewById(R.id.btnSavedMonuments);

        // 2. Cấu hình Bottom Navigation (Tab Cá nhân sáng đỏ)
        setupBottomNavigation(R.id.bottomNavigation, R.id.nav_profile);

        // 3. LẤY DỮ LIỆU NGƯỜI DÙNG TỪ FIREBASE
        observeViewModel();
        authViewModel.loadCurrentUser();

        // 4. XỬ LÝ NÚT ĐỊA DANH ĐÃ LƯU
        if (btnSavedMonuments != null) {
            btnSavedMonuments.setOnClickListener(v -> {
                // Tạm thời hiện Toast, sau này bạn có thể chuyển sang trang SavedActivity
                Toast.makeText(this, "Đang mở danh sách đã lưu...", Toast.LENGTH_SHORT).show();
            });
            View btnSaved = findViewById(R.id.btnSavedMonuments); // Thay ID đúng của bạn
            if (btnSaved != null) {
                btnSaved.setOnClickListener(v -> {
                    Intent intent = new Intent(ProfileActivity.this, SavedMonumentsActivity.class);
                    startActivity(intent);
                });
            }
        }

        // 5. XỬ LÝ ĐĂNG XUẤT
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                authViewModel.signOut();

                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }
    }

    private void observeViewModel() {
        authViewModel.getCurrentUserLiveData().observe(this, this::renderUser);
        authViewModel.getSuccessMessage().observe(this, message -> {
            if (message != null && !message.trim().isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void renderUser(UserSession user) {
        if (user == null) {
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
            return;
        }

        if (tvProfileName != null) {
            String name = user.getDisplayName();
            tvProfileName.setText(name != null && !name.isEmpty() ? name : "Chưa đặt tên");
        }
        if (tvProfileEmail != null) {
            tvProfileEmail.setText(user.getEmail());
        }
    }
}
