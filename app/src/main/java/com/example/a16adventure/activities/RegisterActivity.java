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
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.example.a16adventure.R;
import com.example.a16adventure.presentation.auth.AuthViewModel;

public class RegisterActivity extends BaseActivity {

    private AuthViewModel authViewModel;
    private EditText edtRegisterName, edtRegisterEmail, edtRegisterPassword;
    private Button btnRegister;
    private TextView tvGoToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 1. Khởi tạo AuthViewModel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // 2. Ánh xạ View
        edtRegisterName = findViewById(R.id.edtRegisterName);
        edtRegisterEmail = findViewById(R.id.edtRegisterEmail);
        edtRegisterPassword = findViewById(R.id.edtRegisterPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);

        // 3. Cấu hình giao diện
        setupBottomNavigation(R.id.bottomNavigation, R.id.nav_profile);
        setupStyledText();
        observeViewModel();

        // 4. Chuyển sang trang Login
        if (tvGoToLogin != null) {
            tvGoToLogin.setOnClickListener(v -> {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        // 5. LOGIC ĐĂNG KÝ TÀI KHOẢN
        if (btnRegister != null) {
            btnRegister.setOnClickListener(v -> {
                String name = edtRegisterName.getText().toString().trim();
                String email = edtRegisterEmail.getText().toString().trim();
                String password = edtRegisterPassword.getText().toString().trim();

                // Kiểm tra nhập liệu
                if (name.isEmpty()) {
                    edtRegisterName.setError("Vui lòng nhập họ tên");
                    return;
                }
                if (email.isEmpty()) {
                    edtRegisterEmail.setError("Vui lòng nhập email");
                    return;
                }
                if (password.length() < 6) {
                    edtRegisterPassword.setError("Mật khẩu phải ít nhất 6 ký tự");
                    return;
                }

                // Gửi yêu cầu tạo tài khoản qua ViewModel/UseCase/Repository
                authViewModel.register(name, email, password);
            });
        }
    }

    private void observeViewModel() {
        authViewModel.getCurrentUserLiveData().observe(this, user -> {
            if (user == null) return;
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        authViewModel.getSuccessMessage().observe(this, message -> {
            if (message != null && !message.trim().isEmpty()) {
                Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });

        authViewModel.getErrorMessage().observe(this, message -> {
            if (message != null && !message.trim().isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupStyledText() {
        if (tvGoToLogin == null) return;
        String text = "Đã có tài khoản? Đăng nhập ngay";
        SpannableString ss = new SpannableString(text);
        int start = text.indexOf("Đăng nhập ngay");
        int end = start + "Đăng nhập ngay".length();

        if (start != -1) {
            ss.setSpan(new ForegroundColorSpan(Color.parseColor("#FF4B4B")), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvGoToLogin.setText(ss);
        }
    }
}
