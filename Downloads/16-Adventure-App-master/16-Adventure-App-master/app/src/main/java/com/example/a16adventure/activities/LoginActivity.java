package com.example.a16adventure.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast; // Thêm import này

import com.example.a16adventure.R;
import com.google.firebase.auth.FirebaseAuth; // Import rút gọn cho sạch code

public class LoginActivity extends BaseActivity {

    private FirebaseAuth mAuth;
    private EditText edtLoginEmail, edtLoginPassword;
    private Button btnLogin;
    private TextView tvGoToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 1. Khởi tạo Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // 2. Ánh xạ View
        edtLoginEmail = findViewById(R.id.edtLoginEmail);
        edtLoginPassword = findViewById(R.id.edtLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);

        // 3. Cấu hình giao diện (Bôi đỏ chữ, Nav Bar)
        if (tvGoToRegister != null) {
            setupStyledText();
        }
        setupBottomNavigation(R.id.bottomNavigation, R.id.nav_profile);

        // 4. Sự kiện chuyển sang trang Đăng ký
        if (tvGoToRegister != null) {
            tvGoToRegister.setOnClickListener(v -> {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        // 5. LOGIC ĐĂNG NHẬP THỰC TẾ
        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> {
                String email = edtLoginEmail.getText().toString().trim();
                String password = edtLoginPassword.getText().toString().trim();

                // Kiểm tra nhập liệu cơ bản
                if (email.isEmpty()) {
                    edtLoginEmail.setError("Vui lòng nhập email");
                    return;
                }
                if (password.isEmpty()) {
                    edtLoginPassword.setError("Vui lòng nhập mật khẩu");
                    return;
                }

                // Gửi yêu cầu đăng nhập lên Firebase
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                // THÀNH CÔNG: Chuyển về MainActivity
                                Toast.makeText(LoginActivity.this, "Chào mừng bạn quay trở lại!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // THẤT BẠI: Hiển thị lỗi (Sai mật khẩu, không có mạng, email chưa đăng ký...)
                                String errorMsg = task.getException() != null ? task.getException().getMessage() : "Lỗi đăng nhập";
                                Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " + errorMsg, Toast.LENGTH_LONG).show();
                            }
                        });
            });
        }
    }

    private void setupStyledText() {
        String text = "Chưa có tài khoản? Đăng ký ngay";
        SpannableString ss = new SpannableString(text);
        int start = text.indexOf("Đăng ký ngay");
        int end = start + "Đăng ký ngay".length();

        if (start != -1) {
            ss.setSpan(new ForegroundColorSpan(Color.parseColor("#FF4B4B")), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvGoToRegister.setText(ss);
        }
    }
}