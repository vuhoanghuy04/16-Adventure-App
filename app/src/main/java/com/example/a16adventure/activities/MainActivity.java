package com.example.a16adventure.activities;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a16adventure.R;
import com.example.a16adventure.adapters.MonumentAdapter;
import com.example.a16adventure.models.Monument;
import com.example.a16adventure.models.MonumentDataManager;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class MainActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private MonumentAdapter adapter;
    private List<Monument> fullMonumentList;
    private List<Monument> displayList;
    private ChipGroup chipGroupDistricts;
    private TextView tvHeader;

    // --- BIẾN CHO NÚT THỜI TIẾT ---
    private TextView tvCurrentTemp;
    private ImageView imgWeatherBackground;
    private androidx.cardview.widget.CardView btnWeatherCurrent; // Khai báo theo CardView nguyên bản

    // Biến cho tính năng Pull-to-refresh ngang
    private View floatingReloadView;
    private ImageView imgReloadIcon;
    private boolean isReloadThresholdMet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerViewMonuments);
        chipGroupDistricts = findViewById(R.id.chipGroupDistricts);
        tvHeader = findViewById(R.id.tvHeader);

        // Ánh xạ View cho nút Thời Tiết
        tvCurrentTemp = findViewById(R.id.tvCurrentTemp);
        imgWeatherBackground = findViewById(R.id.imgWeatherBackground);
        btnWeatherCurrent = findViewById(R.id.btnWeatherCurrent);

        // Khởi động luồng tải thời tiết
        fetchHaiPhongWeather();

        // Bắt sự kiện bấm vào nút thời tiết -> Mở web thời tiết
        if (btnWeatherCurrent != null) {
            btnWeatherCurrent.setOnClickListener(v -> {
                String url = "https://www.google.com/search?q=thời+tiết+hải+phòng";
                android.content.Intent i = new android.content.Intent(android.content.Intent.ACTION_VIEW);
                i.setData(android.net.Uri.parse(url));
                startActivity(i);
            });
        }

        setupData();

        preloadImages();

        // Đúc các nút Lọc (Lúc này danh sách vẫn giữ nguyên thứ tự gốc)
        setupFilters();

        // Xáo trộn danh sách thẻ ngẫu nhiên
        java.util.Collections.shuffle(fullMonumentList);

        displayList = new ArrayList<>(fullMonumentList);
        adapter = new MonumentAdapter(this, displayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);

        // --- CẤU HÌNH ICON RELOAD NỔI TỪ NGOÀI MÀN HÌNH ---
        MaterialCardView cardReload = new MaterialCardView(this);
        cardReload.setRadius(dpToPx(28));
        cardReload.setCardElevation(dpToPx(8));

        cardReload.setCardBackgroundColor(Color.parseColor("#66FFFFFF"));

        FrameLayout.LayoutParams cardParams = new FrameLayout.LayoutParams(dpToPx(56), dpToPx(56));
        cardReload.setLayoutParams(cardParams);

        imgReloadIcon = new ImageView(this);
        imgReloadIcon.setImageResource(R.drawable.ic_reload_refresh);
        imgReloadIcon.setPadding(dpToPx(14), dpToPx(14), dpToPx(14), dpToPx(14));
        cardReload.addView(imgReloadIcon);

        ViewGroup mainLayout = findViewById(android.R.id.content);
        mainLayout.addView(cardReload);
        floatingReloadView = cardReload;

        // Tính toán vị trí Y chính xác giữa RecyclerView và đẩy X ra hẳn ngoài màn hình
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int[] rvLocation = new int[2];
                recyclerView.getLocationInWindow(rvLocation);
                int[] rootLocation = new int[2];
                mainLayout.getLocationInWindow(rootLocation);

                float relativeY = rvLocation[1] - rootLocation[1];
                float centerRvY = relativeY + (recyclerView.getHeight() / 2f);

                floatingReloadView.setY(centerRvY - (dpToPx(56) / 2f));
                floatingReloadView.setTranslationX(dpToPx(-80)); // Giấu hẳn ra ngoài lề trái

                recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        // --- KẾT THÚC CẤU HÌNH ---

        // Cấu hình Nam châm PagerSnapHelper
        androidx.recyclerview.widget.SnapHelper snapHelper = new androidx.recyclerview.widget.PagerSnapHelper();
        recyclerView.setOnFlingListener(null);
        snapHelper.attachToRecyclerView(recyclerView);

        // --- LÕI CẢM BIẾN VUỐT CHUẨN IOS ---
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            private float startX = 0f;
            private boolean isPulling = false;
            private final int PULL_THRESHOLD = 40; // Phải kéo 40px mới tính là cố tình kéo

            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                // Nếu không ở thẻ ngoài cùng bên trái, bỏ qua
                if (rv.canScrollHorizontally(-1)) return false;

                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = e.getX();
                        isPulling = false;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        float distance = e.getX() - startX;
                        // Chỉ cướp quyền điều khiển khi người dùng cố tình kéo sang PHẢI
                        if (distance > PULL_THRESHOLD) {
                            isPulling = true;
                            floatingReloadView.setVisibility(View.VISIBLE);
                            return true; // Chặn cuộn mặc định, tự tay xử lý!
                        }
                        break;
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                if (!isPulling) return;

                float distance = e.getX() - startX;

                switch (e.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        // Lực cản (friction) 0.4f để tạo cảm giác nặng/lún dây thun
                        float pullOffset = (distance - PULL_THRESHOLD) * 0.4f;
                        if (pullOffset < 0) pullOffset = 0f;

                        // 1. DI CHUYỂN CẢ DANH SÁCH (Kéo thẻ lún sang phải)
                        rv.setTranslationX(pullOffset);

                        // 2. DI CHUYỂN NÚT RELOAD VÀO TRONG (Hiệu ứng Parallax chạy nhanh hơn)
                        float iconTargetX = dpToPx(-80) + (pullOffset * 1.5f);
                        if (iconTargetX > dpToPx(16)) iconTargetX = dpToPx(16); // Khóa vị trí cách lề 16dp
                        floatingReloadView.setTranslationX(iconTargetX);

                        // 3. ĐỔI MÀU NẾU KÉO ĐỦ MẠNH (> 60px lún)
                        if (pullOffset > dpToPx(60) && !isReloadThresholdMet) {
                            animateColorChange(floatingReloadView);
                            setReloadIconStyle(true);
                            isReloadThresholdMet = true;
                        } else if (pullOffset <= dpToPx(60) && isReloadThresholdMet) {
                            animateColorReset(floatingReloadView);
                            setReloadIconStyle(false);
                            isReloadThresholdMet = false;
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        isPulling = false;

                        // Nếu đủ ngưỡng thì trộn bài
                        if (isReloadThresholdMet) {
                            animateShuffleAndReload();
                        }
                        // Trả mọi thứ về vị trí và màu sắc ban đầu (Đàn hồi)
                        resetUI(rv);
                        break;
                }
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
        });

        updateGreeting();
        setupBottomNavigation(R.id.bottomNavigation, R.id.nav_home);

        // Thay bằng CardView nguyên bản
        androidx.cardview.widget.CardView btnOpenQuizBanner = findViewById(R.id.btnOpenQuizBanner);
        if (btnOpenQuizBanner != null) {
            btnOpenQuizBanner.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, QuizActivity.class));
            });
        }
    }

    // --- HÀM GỌI API THỜI TIẾT ---
    private void fetchHaiPhongWeather() {
        // API của Open-Meteo lấy nhiệt độ và mã thời tiết tại Hải Phòng (Tọa độ: 20.86, 106.68)
        String apiUrl = "https://api.open-meteo.com/v1/forecast?latitude=20.8648&longitude=106.6835&current_weather=true";

        new Thread(() -> {
            try {
                java.net.URL url = new java.net.URL(apiUrl);
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();

                // Phân tích JSON trả về
                org.json.JSONObject jsonObject = new org.json.JSONObject(result.toString());
                org.json.JSONObject current = jsonObject.getJSONObject("current_weather");

                double temp = current.getDouble("temperature");
                int weatherCode = current.getInt("weathercode");

                // Cập nhật Giao diện trên Main Thread
                runOnUiThread(() -> updateWeatherUI(temp, weatherCode));

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    if (tvCurrentTemp != null) {
                        tvCurrentTemp.setText("Lỗi");
                    }
                });
            }
        }).start();
    }

    // --- HÀM CẬP NHẬT GIAO DIỆN THỜI TIẾT (TỐC ĐỘ 0 GIÂY - ẢNH OFFLINE) ---
    private void updateWeatherUI(double temp, int weatherCode) {
        if (tvCurrentTemp == null || imgWeatherBackground == null) return;

        // 1. Cập nhật nhiệt độ
        tvCurrentTemp.setText(Math.round(temp) + "°C");

        // 2. Chọn ảnh nội bộ cực nhanh
        int drawableResId = R.drawable.bg_weather_default; // Mặc định

        if (weatherCode == 0 || weatherCode == 1) {
            drawableResId = R.drawable.bg_weather_sunny;
        } else if (weatherCode == 2 || weatherCode == 3) {
            drawableResId = R.drawable.bg_weather_cloudy;
        } else if (weatherCode >= 51 && weatherCode <= 67) {
            drawableResId = R.drawable.bg_weather_rain;
        } else if (weatherCode >= 95) {
            drawableResId = R.drawable.bg_weather_storm;
        }

        // 3. Gắn thẳng ảnh vào nền (Hiện ngay lập tức)
        imgWeatherBackground.setImageResource(drawableResId);
    }


    private void setupData() {
        fullMonumentList = new ArrayList<>();
        try {
            java.io.InputStream is = getAssets().open("monuments.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String jsonString = new String(buffer, "UTF-8");
            org.json.JSONArray jsonArray = new org.json.JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                org.json.JSONObject obj = jsonArray.getJSONObject(i);
                Monument m = new Monument(
                        obj.getString("id"),
                        obj.getString("name"),
                        obj.getString("district"),
                        obj.getString("description"),
                        obj.getString("imageUrl"),
                        obj.getDouble("lat"),
                        obj.getDouble("lng")
                );
                fullMonumentList.add(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Đưa danh sách vừa load vào kho quản lý chung
        MonumentDataManager.getInstance().setMonumentList(fullMonumentList);

        MonumentDataManager.getInstance().setMonumentList(fullMonumentList);
        Log.d("DEBUG_SAVE", "Da nap " + fullMonumentList.size() + " dia danh vao kho chung");
    }

    private void setupFilters() {
        chipGroupDistricts.removeAllViews();

        // Khởi tạo "Máy đúc khuôn" từ file XML
        android.view.LayoutInflater inflater = android.view.LayoutInflater.from(this);

        // 1. Tạo Chip "Tất cả" bằng cách đúc từ khuôn item_chip_filter
        com.google.android.material.chip.Chip chipAll = (com.google.android.material.chip.Chip)
                inflater.inflate(R.layout.item_chip_filter, chipGroupDistricts, false);
        chipAll.setText("Tất cả");
        chipAll.setChecked(true); // Chọn sẵn
        chipGroupDistricts.addView(chipAll);

        // 2. Tự động quét và lọc ra các Quận/Huyện/Xã (Không bị trùng lặp)
        java.util.LinkedHashSet<String> uniqueDistricts = new java.util.LinkedHashSet<>();
        for (Monument m : fullMonumentList) {
            uniqueDistricts.add(m.getDistrict());
        }

        // 3. Vòng lặp tự động đúc hàng loạt Chip từ khuôn
        for (String districtName : uniqueDistricts) {
            com.google.android.material.chip.Chip chip = (com.google.android.material.chip.Chip)
                    inflater.inflate(R.layout.item_chip_filter, chipGroupDistricts, false);
            chip.setText(districtName);
            chipGroupDistricts.addView(chip);
        }

        // 4. Lắng nghe sự kiện click
        chipGroupDistricts.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;

            int checkedId = checkedIds.get(0);
            com.google.android.material.chip.Chip selectedChip = findViewById(checkedId);
            String selectedText = selectedChip.getText().toString();

            displayList.clear();

            if (selectedText.equals("Tất cả")) {
                displayList.addAll(fullMonumentList);
            } else {
                filterByDistrict(selectedText);
            }

            adapter.notifyDataSetChanged();
            if (!displayList.isEmpty()) {
                recyclerView.scrollToPosition(0);
            }
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

        tvHeader.setText("Chào buổi " + session + ", " + username + " 👋");
    }

    private void filterByDistrict(String districtName) {
        for (Monument m : fullMonumentList) {
            if (m.getDistrict().equals(districtName)) {
                displayList.add(m);
            }
        }
    }

    // --- CÁC HÀM PHỤ TRỢ (HELPER METHODS) ---

    // Đàn hồi trả giao diện về vị trí mặc định
    private void resetUI(RecyclerView rv) {
        // Đẩy RecyclerView về 0
        ObjectAnimator rvAnim = ObjectAnimator.ofFloat(rv, "translationX", rv.getTranslationX(), 0f);
        rvAnim.setDuration(300);
        rvAnim.start();

        // Giấu Icon ra ngoài màn hình
        ObjectAnimator iconAnim = ObjectAnimator.ofFloat(floatingReloadView, "translationX", floatingReloadView.getTranslationX(), dpToPx(-80));
        iconAnim.setDuration(300);
        iconAnim.start();

        // Reset trạng thái
        isReloadThresholdMet = false;
        setReloadIconStyle(false);
        ((MaterialCardView) floatingReloadView).setCardBackgroundColor(Color.parseColor("#66FFFFFF"));
    }

    private void animateColorChange(View view) {
        if (view instanceof MaterialCardView) {
            ValueAnimator animator = ValueAnimator.ofObject(new ArgbEvaluator(), Color.parseColor("#66FFFFFF"), Color.parseColor("#FF9A9E"));
            animator.setDuration(150);
            animator.addUpdateListener(animation -> ((MaterialCardView) view).setCardBackgroundColor((int) animation.getAnimatedValue()));
            animator.start();
        }
    }

    private void animateColorReset(View view) {
        if (view instanceof MaterialCardView) {
            ValueAnimator animator = ValueAnimator.ofObject(new ArgbEvaluator(), Color.parseColor("#FF9A9E"), Color.parseColor("#66FFFFFF"));
            animator.setDuration(150);
            animator.addUpdateListener(animation -> ((MaterialCardView) view).setCardBackgroundColor((int) animation.getAnimatedValue()));
            animator.start();
        }
    }

    private void setReloadIconStyle(boolean isRedStyle) {
        if (isRedStyle) {
            imgReloadIcon.setColorFilter(Color.WHITE);
        } else {
            imgReloadIcon.setColorFilter(Color.parseColor("#B3FFFFFF"));
        }
    }

    private void animateShuffleAndReload() {
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            Collections.shuffle(displayList);
            adapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(0);
            Toast.makeText(this, "Đã xáo trộn danh sách! 🎲", Toast.LENGTH_SHORT).show();
        }, 150); // Trễ một chút để cảm nhận độ nảy
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    // HÀM TẢI TRƯỚC ẢNH (TÀNG HÌNH) ĐỂ VUỐT MƯỢT MÀ
    private void preloadImages() {
        if (fullMonumentList == null) return;

        for (Monument m : fullMonumentList) {
            String imageUrl = m.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                // Yêu cầu Glide tải ảnh về máy nhưng KHÔNG vẽ lên màn hình
                com.bumptech.glide.Glide.with(this.getApplicationContext())
                        .load(imageUrl)
                        .preload();
            }
        }
    }
}