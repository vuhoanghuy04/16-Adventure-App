package com.example.a16adventure.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a16adventure.R;
import com.example.a16adventure.util.AppConstants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.net.Uri;
import android.provider.MediaStore;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * Màn hình hồ sơ người dùng.
 * Quản lý thông tin cá nhân, thống kê và ảnh đại diện thông qua Firebase Auth/Database/Storage.
 */
public class ProfileActivity extends BaseActivity {

    private TextView tvProfileName, tvProfileEmail;
    private TextView tvStatVisited, tvStatQuizPoints;
    private Button btnLogout;
    private ImageView imgProfile;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private DatabaseReference userRef;
    private ValueEventListener quizListener;
    private ValueEventListener savedIdsListener;
    
    private ActivityResultLauncher<Intent> pickImageLauncher;

    /**
     * Khởi tạo giao diện hồ sơ, nút chức năng và nạp dữ liệu người dùng.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // 1. Khởi tạo Firebase Auth, Storage và Ánh xạ View
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        tvStatVisited = findViewById(R.id.tvStatVisited);
        tvStatQuizPoints = findViewById(R.id.tvStatQuizPoints);
        btnLogout = findViewById(R.id.btnLogout);
        imgProfile = findViewById(R.id.imgProfile);
        
        // --- KHỞI TẠO BỘ CHỌN ẢNH ---
        setupImagePicker();

        if (imgProfile != null) {
            imgProfile.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickImageLauncher.launch(intent);
            });
        }
        
        // --- ÁNH XẠ VÀ THIẾT LẬP CÁC NÚT MỚI ---
        setupProfileButtons();

        // 2. Cấu hình Bottom Navigation
        setupBottomNavigation(R.id.bottomNavigation, R.id.nav_profile);

        // 3. LẤY DỮ LIỆU NGƯỜI DÙNG TỪ FIREBASE
        loadUserData();

        // 4. XỬ LÝ ĐĂNG XUẤT
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                mAuth.signOut();
                Toast.makeText(this, R.string.logout_success, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }
    }

    /**
     * Khởi tạo launcher chọn ảnh từ thư viện cho ảnh đại diện.
     */
    private void setupImagePicker() {
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        uploadImageToFirebase(imageUri);
                    }
                }
        );
    }

    /**
     * Tải ảnh đại diện mới lên Firebase Storage.
     * API ngoài: Firebase Storage putFile/getDownloadUrl.
     */
    private void uploadImageToFirebase(Uri imageUri) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        Toast.makeText(this, "Đang tải ảnh lên...", Toast.LENGTH_SHORT).show();

        StorageReference fileRef = storage.getReference()
                .child("profile_images")
                .child(user.getUid() + ".jpg");

        // API Storage: upload ảnh profile vào profile_images/{uid}.jpg.
        fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                updateProfilePicture(uri);
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Tải ảnh thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Cập nhật photoUrl của user trên Firebase Auth và phản chiếu lên UI.
     * API ngoài: Firebase Auth updateProfile.
     */
    private void updateProfilePicture(Uri uri) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();

        user.updateProfile(profileUpdates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Glide.with(this).load(uri).into(imgProfile);
                Toast.makeText(this, "Cập nhật ảnh đại diện thành công!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Cấu hình giao diện và hành vi các nút chức năng trong hồ sơ.
     */
    private void setupProfileButtons() {
        // 1. Địa danh đã lưu
        View btnSaved = findViewById(R.id.btnSavedMonumentsItem);
        updateButtonUI(btnSaved, android.R.drawable.star_on, "Địa danh đã lưu", "#2196F3");
        if (btnSaved != null) {
            btnSaved.setOnClickListener(v -> startActivity(new Intent(this, SavedMonumentsActivity.class)));
        }

        // 2. Nhật ký hành trình
        View btnJournal = findViewById(R.id.btnTravelJournal);
        updateButtonUI(btnJournal, android.R.drawable.ic_menu_camera, "Nhật ký hành trình", "#9C27B0");
        if (btnJournal != null) {
            btnJournal.setOnClickListener(v -> startActivity(new Intent(this, JournalListActivity.class)));
        }

        // 3. Bản đồ ngoại tuyến
        View btnOffline = findViewById(R.id.btnOfflineMaps);
        updateButtonUI(btnOffline, android.R.drawable.ic_menu_mapmode, "Bản đồ ngoại tuyến", "#4CAF50");
        if (btnOffline != null) {
            btnOffline.setOnClickListener(v -> startActivity(new Intent(this, OfflineMapActivity.class)));
        }

        // 4. Chỉnh sửa hồ sơ
        View btnEdit = findViewById(R.id.btnEditProfile);
        updateButtonUI(btnEdit, android.R.drawable.ic_menu_edit, "Chỉnh sửa hồ sơ", "#FF9800");

        // 5. Ngôn ngữ
        View btnLang = findViewById(R.id.btnLanguage);
        updateButtonUI(btnLang, android.R.drawable.ic_menu_sort_alphabetically, "Ngôn ngữ", "#607D8B");

        // 6. Chế độ tối
        View btnDark = findViewById(R.id.btnDarkMode);
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        int currentMode = prefs.getInt("ThemeMode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        
        String modeName = "Theo máy";
        if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) modeName = "Chế độ tối";
        else if (currentMode == AppCompatDelegate.MODE_NIGHT_NO) modeName = "Chế độ sáng";
        
        updateButtonUI(btnDark, android.R.drawable.ic_menu_view, "Giao diện: " + modeName, "#3F51B5");
        
        if (btnDark != null) {
            btnDark.setOnClickListener(v -> {
                String[] options = {"Chế độ sáng", "Chế độ tối", "Theo máy"};
                int checkedItem = 2; // Default to "Follow System"
                if (currentMode == AppCompatDelegate.MODE_NIGHT_NO) checkedItem = 0;
                else if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) checkedItem = 1;

                new AlertDialog.Builder(this)
                        .setTitle("Chọn chế độ giao diện")
                        .setSingleChoiceItems(options, checkedItem, (dialog, which) -> {
                            int newMode;
                            if (which == 0) newMode = AppCompatDelegate.MODE_NIGHT_NO;
                            else if (which == 1) newMode = AppCompatDelegate.MODE_NIGHT_YES;
                            else newMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;

                            prefs.edit().putInt("ThemeMode", newMode).apply();
                            AppCompatDelegate.setDefaultNightMode(newMode);
                            
                            dialog.dismiss();
                            
                            // Restart để áp dụng thay đổi toàn diện
                            Intent intent = new Intent(this, ProfileActivity.class);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(0, 0);
                        })
                        .show();
            });
        }

        // 7. Liên hệ hỗ trợ
        View btnSupport = findViewById(R.id.btnContactSupport);
        updateButtonUI(btnSupport, android.R.drawable.ic_menu_call, "Liên hệ hỗ trợ", "#F44336");

        // 8. Đánh giá ứng dụng
        View btnRate = findViewById(R.id.btnRateApp);
        updateButtonUI(btnRate, android.R.drawable.btn_star_big_on, "Đánh giá ứng dụng", "#FFC107");

        // 9. Chia sẻ ứng dụng
        View btnShare = findViewById(R.id.btnShareApp);
        updateButtonUI(btnShare, android.R.drawable.ic_menu_share, "Chia sẻ ứng dụng", "#00BCD4");
    }

    /**
     * Cập nhật icon, text và màu cho từng nút trong danh sách tiện ích.
     */
    private void updateButtonUI(View view, int iconRes, String text, String colorHex) {
        if (view == null) return;
        ImageView icon = view.findViewById(R.id.btnIcon);
        TextView tv = view.findViewById(R.id.btnText);
        if (icon != null) {
            icon.setImageResource(iconRes);
            icon.setColorFilter(android.graphics.Color.parseColor(colorHex));
        }
        if (tv != null) tv.setText(text);
    }

    /**
     * Hiển thị thông báo tính năng đang phát triển.
     */
    private void showUnderDevToast(String featureName) {
        Toast.makeText(this, featureName + " đang được phát triển", Toast.LENGTH_SHORT).show();
    }

    /**
     * Nạp thông tin hiển thị theo trạng thái đăng nhập hiện tại.
     * API ngoài: Firebase Auth + Realtime Database.
     */
    private void loadUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        View cardStats = findViewById(R.id.cardStats);
        View tvTitleJournal = findViewById(R.id.tvTitleJournal);
        View btnSaved = findViewById(R.id.btnSavedMonumentsItem);
        View btnJournal = findViewById(R.id.btnTravelJournal);
        View btnOffline = findViewById(R.id.btnOfflineMaps);
        View btnEdit = findViewById(R.id.btnEditProfile);
        Button btnLogin = findViewById(R.id.btnLogin);

        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUri = user.getPhotoUrl();

            if (tvProfileName != null) {
                tvProfileName.setText(name != null && !name.isEmpty() ? name : "Người dùng");
            }
            if (tvProfileEmail != null) {
                tvProfileEmail.setText(email);
            }
            if (photoUri != null && imgProfile != null) {
                Glide.with(this).load(photoUri).into(imgProfile);
            }

            // Load Stats from Firebase
            userRef = FirebaseDatabase.getInstance()
                    .getReference(AppConstants.FirebasePaths.USERS)
                    .child(user.getUid());
            loadUserStats();
            
            // Hiện đầy đủ tính năng
            if (cardStats != null) cardStats.setVisibility(View.VISIBLE);
            if (tvTitleJournal != null) tvTitleJournal.setVisibility(View.VISIBLE);
            if (btnSaved != null) btnSaved.setVisibility(View.VISIBLE);
            if (btnJournal != null) btnJournal.setVisibility(View.VISIBLE);
            if (btnOffline != null) btnOffline.setVisibility(View.VISIBLE);
            if (btnEdit != null) btnEdit.setVisibility(View.VISIBLE);
            if (btnLogout != null) btnLogout.setVisibility(View.VISIBLE);
            if (btnLogin != null) btnLogin.setVisibility(View.GONE);
        } else {
            // TRẠNG THÁI CHƯA ĐĂNG NHẬP
            if (tvProfileName != null) tvProfileName.setText("Khách");
            if (tvProfileEmail != null) tvProfileEmail.setText("Đăng nhập để lưu hành trình");
            if (imgProfile != null) imgProfile.setImageResource(R.drawable.ic_profile);

            // Ẩn các tính năng cá nhân
            if (cardStats != null) cardStats.setVisibility(View.GONE);
            if (tvTitleJournal != null) tvTitleJournal.setVisibility(View.GONE);
            if (btnSaved != null) btnSaved.setVisibility(View.GONE);
            if (btnJournal != null) btnJournal.setVisibility(View.GONE);
            if (btnOffline != null) btnOffline.setVisibility(View.GONE);
            if (btnEdit != null) btnEdit.setVisibility(View.GONE);
            if (btnLogout != null) btnLogout.setVisibility(View.GONE);
            
            // Hiện nút Đăng nhập
            if (btnLogin != null) {
                btnLogin.setVisibility(View.VISIBLE);
                btnLogin.setOnClickListener(v -> {
                    startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                });
            }
        }
    }

    /**
     * Đăng ký listener để lấy điểm quiz và số địa danh đã lưu.
     * API ngoài: Firebase Realtime Database ValueEventListener.
     */
    private void loadUserStats() {
        // 1. Load Quiz Score
        quizListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChild("score")) {
                    Long score = snapshot.child("score").getValue(Long.class);
                    if (tvStatQuizPoints != null && score != null) tvStatQuizPoints.setText(String.valueOf(score));
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {}
        };
        // API Realtime Database: theo dõi users/{uid}/quiz.
        userRef.child(AppConstants.FirebasePaths.QUIZ).addValueEventListener(quizListener);

        // 2. Load Saved/Visited (Dựa trên số lượng địa danh đã lưu)
        savedIdsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                long count = snapshot.getChildrenCount();
                if (tvStatVisited != null) tvStatVisited.setText(String.valueOf(count));
            }
            @Override
            public void onCancelled(DatabaseError error) {}
        };
        // API Realtime Database: theo dõi users/{uid}/saved_ids.
        userRef.child(AppConstants.FirebasePaths.SAVED_IDS).addValueEventListener(savedIdsListener);
    }

    /**
     * Hủy các listener Firebase khi màn hình bị destroy để tránh rò rỉ tài nguyên.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userRef != null) {
            if (quizListener != null) {
                userRef.child(AppConstants.FirebasePaths.QUIZ).removeEventListener(quizListener);
            }
            if (savedIdsListener != null) {
                userRef.child(AppConstants.FirebasePaths.SAVED_IDS).removeEventListener(savedIdsListener);
            }
        }
    }
}
