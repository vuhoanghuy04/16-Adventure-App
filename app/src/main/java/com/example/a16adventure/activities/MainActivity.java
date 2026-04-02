package com.example.a16adventure.activities;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.material.card.MaterialCardView; // Import thêm thư viện này

public class MainActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private MonumentAdapter adapter;
    private List<Monument> fullMonumentList;
    private List<Monument> displayList;
    private ChipGroup chipGroupDistricts;
    private TextView tvHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Ánh xạ view
        recyclerView = findViewById(R.id.recyclerViewMonuments);
        chipGroupDistricts = findViewById(R.id.chipGroupDistricts);
        tvHeader = findViewById(R.id.tvHeader);

        // 2. Khởi tạo dữ liệu
        setupData();

        // 3. Cấu hình RecyclerView
        displayList = new ArrayList<>(fullMonumentList);
        adapter = new MonumentAdapter(this, displayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);

        androidx.recyclerview.widget.SnapHelper snapHelper = new androidx.recyclerview.widget.PagerSnapHelper();
        recyclerView.setOnFlingListener(null); // Xóa listener cũ (nếu có) để tránh lỗi crash khi reload
        snapHelper.attachToRecyclerView(recyclerView);

        // 4. Xử lý sự kiện bấm nút Lọc (Chip)
        setupFilters();

        updateGreeting();

        // 5. CẤU HÌNH THANH ĐIỀU HƯỚNG CHỈ VỚI 1 DÒNG
        setupBottomNavigation(R.id.bottomNavigation, R.id.nav_home);

        // 6. Xử lý sự kiện bấm Banner Quiz
        MaterialCardView btnOpenQuizBanner = findViewById(R.id.btnOpenQuizBanner);
        if (btnOpenQuizBanner != null) {
            btnOpenQuizBanner.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                startActivity(intent);
            });
        }
    }

    private void setupData() {
        fullMonumentList = new ArrayList<>();
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
                displayList.addAll(fullMonumentList);
            } else if (checkedId == R.id.chipHongBang) {
                filterByDistrict("Hồng Bàng");
            } else if (checkedId == R.id.chipLeChan) {
                filterByDistrict("Lê Chân");
            } else if (checkedId == R.id.chipDoSon) {
                filterByDistrict("Đồ Sơn");
            }

            adapter.notifyDataSetChanged();
        });
    }

    private void updateGreeting() {
        com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
        String username = "Bạn hữu";

        if (user != null && user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
            username = user.getDisplayName();
        }

        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        String session;

        if (hour >= 5 && hour < 10) {
            session = "sáng";
        } else if (hour >= 10 && hour < 14) {
            session = "trưa";
        } else if (hour >= 14 && hour < 18) {
            session = "chiều";
        } else {
            session = "tối";
        }

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