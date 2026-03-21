package com.example.a16adventure.activities;

// --- CÁC DÒNG IMPORT ĐANG THIẾU ---
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.a16adventure.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
// ---------------------------------

public class RegisterActivity extends AppCompatActivity {
    private EditText edtEmail, edtPassword, edtName;
    private Button btnRegister;
    private FirebaseAuth mAuth; // Đã rút gọn nhờ import ở trên

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Khởi tạo Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Ánh xạ View
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String name = edtName.getText().toString().trim();

            // Kiểm tra dữ liệu đầu vào
            if (email.isEmpty() || password.length() < 6 || name.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đủ thông tin và mật khẩu > 6 ký tự!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tiến hành đăng ký tài khoản trên Firebase
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Nếu tạo user thành công -> Cập nhật tên hiển thị
                            updateDisplayName(name);
                        } else {
                            // Nếu lỗi (ví dụ: email đã tồn tại, sai định dạng...)
                            Toast.makeText(this, "Lỗi: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void updateDisplayName(String name) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                            // Sau khi xong hết thì mới chuyển trang
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            finish(); // Đóng trang Register để không bấm quay lại được nữa
                        }
                    });
        }
    }
}