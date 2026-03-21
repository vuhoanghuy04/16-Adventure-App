package com.example.a16adventure.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a16adventure.R;
import com.google.android.material.chip.ChipGroup;
import com.example.a16adventure.adapters.MonumentAdapter;
import com.example.a16adventure.models.Monument;
import java.util.ArrayList;
import java.util.List;
import android.widget.TextView;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MonumentAdapter adapter;
    private List<Monument> fullMonumentList; // Danh sách gốc chứa TẤT CẢ di tích
    private List<Monument> displayList;      // Danh sách dùng để hiển thị (thay đổi khi lọc)
    private ChipGroup chipGroupDistricts;
    private TextView tvHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Kiểm tra xem đã đăng nhập chưa
        com.google.firebase.auth.FirebaseAuth mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            // Nếu chưa, đá sang trang Login/Register
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
            return;
        }


        setContentView(R.layout.activity_main);

        // 1. Ánh xạ view
        recyclerView = findViewById(R.id.recyclerViewMonuments);
        chipGroupDistricts = findViewById(R.id.chipGroupDistricts);
        tvHeader = findViewById(R.id.tvHeader);

        // 2. Khởi tạo dữ liệu
        setupData();

        // 3. Cấu hình RecyclerView
        displayList = new ArrayList<>(fullMonumentList); // Ban đầu hiển thị tất cả
        adapter = new MonumentAdapter(this, displayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        // Note: Mình để HORIZONTAL (cuộn ngang) cho giống giao diện thẻ quẹt hơn
        recyclerView.setAdapter(adapter);

        // 4. Xử lý sự kiện bấm nút Lọc (Chip)
        setupFilters();

        updateGreeting();

        // =========================================================
        // XỬ LÝ BOTTOM NAVIGATION TẠI MAIN ACTIVITY (TRANG CHỦ)
        // =========================================================
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);

        // 1. TẠO MÀU ĐỎ KHI CHỌN TAB
        int[][] states = new int[][] {
                new int[] { android.R.attr.state_checked},
                new int[] {-android.R.attr.state_checked}
        };
        int[] colors = new int[] {
                android.graphics.Color.parseColor("#FF4B4B"), // Màu đỏ
                android.graphics.Color.parseColor("#808080")  // Màu xám
        };
        android.content.res.ColorStateList colorStateList = new android.content.res.ColorStateList(states, colors);
        bottomNavigation.setItemIconTintList(colorStateList);
        bottomNavigation.setItemTextColor(colorStateList);

        // Đảm bảo tab Home đang được chọn khi ở trang này
        bottomNavigation.setSelectedItemId(R.id.nav_home);

        // 2. LOGIC CHUYỂN SANG TRANG MAP
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                // Đang ở Home thì giữ nguyên, không làm gì cả
                return true;

            } else if (itemId == R.id.nav_map) {
                // MỞ HẲN MỘT TRANG MỚI (MapActivity)
                android.content.Intent intent = new android.content.Intent(MainActivity.this, com.example.a16adventure.activities.MapActivity.class);
                startActivity(intent);

                // Tắt hiệu ứng trượt màn hình mặc định của Android để tạo cảm giác "chuyển tab" mượt mà
                overridePendingTransition(0, 0);

                // Trả về false để icon Map ở trang Home này không sáng lên (vì đằng nào nó cũng bị trang Map đè lên rồi)
                return false;
            }

            return true;
        });
        // =========================================================
    }

    private void setupData() {
        fullMonumentList = new ArrayList<>();
        // Đổ dữ liệu mẫu
        fullMonumentList.add(new Monument("1", "Nhà hát lớn Hải Phòng", "Hồng Bàng", "Biểu tượng kiến trúc Pháp.", "https://example.com/nhahat.jpg", 20.8601, 106.6823));
        fullMonumentList.add(new Monument("2", "Đền Nghè", "Lê Chân", "Nơi thờ nữ tướng Lê Chân.", "https://example.com/dennghe.jpg", 20.8550, 106.6780));
        fullMonumentList.add(new Monument("3", "Bãi biển Đồ Sơn", "Đồ Sơn", "Khu du lịch biển nổi tiếng.", "https://example.com/doson.jpg", 20.7095, 106.7865));
        fullMonumentList.add(new Monument("4", "Chợ Tam Bạc", "Hồng Bàng", "Chợ truyền thống lâu đời.", "https://example.com/cho.jpg", 20.8580, 106.6770));
    }

    private void setupFilters() {
        chipGroupDistricts.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;

            int checkedId = checkedIds.get(0);
            displayList.clear();

            if (checkedId == R.id.chipAll) {
                // Nếu chọn "Tất cả", copy lại toàn bộ danh sách gốc
                displayList.addAll(fullMonumentList);
            } else if (checkedId == R.id.chipHongBang) {
                filterByDistrict("Hồng Bàng");
            } else if (checkedId == R.id.chipLeChan) {
                filterByDistrict("Lê Chân");
            } else if (checkedId == R.id.chipDoSon) {
                filterByDistrict("Đồ Sơn");
            }

            // Báo cho Adapter biết dữ liệu đã thay đổi để vẽ lại giao diện
            adapter.notifyDataSetChanged();
        });
    }

    private void updateGreeting() {
        // 1. LẤY TÊN TỪ FIREBASE THAY CHO CHỮ "admin" CỐ ĐỊNH
        com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
        String username = "Bạn hữu"; // Giá trị dự phòng nếu chẳng may không lấy được tên

        if (user != null && user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
            username = user.getDisplayName();
        }

        // 2. Lấy giờ hiện tại của hệ thống
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);

        String session;

        // Phân loại buổi trong ngày
        if (hour >= 5 && hour < 12) {
            session = "sáng";
        } else if (hour >= 12 && hour < 14) {
            session = "trưa";
        } else if (hour >= 14 && hour < 18) {
            session = "chiều";
        } else {
            session = "tối";
        }

        // 3. Cập nhật text hiển thị lên màn hình
        String greetingText = "Chào buổi " + session + ", " + username + " 👋";
        tvHeader.setText(greetingText);
    }
    private void filterByDistrict(String districtName) {
        for (Monument m : fullMonumentList) {
            if (m.getDistrict().equals(districtName)) {
                displayList.add(m);
            }
        }
    }
}