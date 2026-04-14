package com.example.a16adventure.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.a16adventure.R;
import com.example.a16adventure.adapters.ImagePagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RecognitionResultActivity extends AppCompatActivity {

    private ViewPager2 viewPagerLandmark;
    private TabLayout tabIndicator;
    private ImagePagerAdapter pagerAdapter;
    private List<String> imageUrls;
    private TextView txtLandmarkName, txtLandmarkDesc, txtLandmarkLocation;
    private TextView txtTag1, txtTag2, txtTag3;
    private TextView txtLandmarkRating, txtLandmarkHours, txtLandmarkVisits, txtLandmarkDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognition_result);

        initViews();
        
        String resultText = getIntent().getStringExtra("RECOGNITION_RESULT");

        if (resultText != null) {
            parseAndShowResult(resultText);
        }

        findViewById(R.id.btnResultBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnResultDone).setOnClickListener(v -> {
            Intent intent = new Intent(this, AiActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void initViews() {
        viewPagerLandmark = findViewById(R.id.viewPagerLandmark);
        tabIndicator = findViewById(R.id.tabIndicator);
        txtLandmarkName = findViewById(R.id.txtLandmarkName);
        txtLandmarkDesc = findViewById(R.id.txtLandmarkDesc);
        txtLandmarkLocation = findViewById(R.id.txtLandmarkLocation);
        
        txtTag1 = findViewById(R.id.txtTag1);
        txtTag2 = findViewById(R.id.txtTag2);
        txtTag3 = findViewById(R.id.txtTag3);
        
        txtLandmarkRating = findViewById(R.id.txtLandmarkRating);
        txtLandmarkHours = findViewById(R.id.txtLandmarkHours);
        txtLandmarkVisits = findViewById(R.id.txtLandmarkVisits);
        txtLandmarkDistance = findViewById(R.id.txtLandmarkDistance);
    }

    private void parseAndShowResult(String resultText) {
        String cleanText = resultText.replace("**", "").trim();
        String name = "Địa danh";
        String description = "";
        List<String> aiImageUrls = new ArrayList<>();

        // Tách dữ liệu từ cấu trúc của AI
        String[] lines = cleanText.split("\n");
        for (String line : lines) {
            String lowerLine = line.toLowerCase();
            if (lowerLine.contains("tên địa danh:")) {
                name = line.substring(line.indexOf(":") + 1).trim();
            } else if (lowerLine.contains("mô tả:")) {
                description = line.substring(line.indexOf(":") + 1).trim();
            } else if (lowerLine.contains("hình ảnh:")) {
                String urlsPart = line.substring(line.indexOf(":") + 1).trim();
                String[] urls = urlsPart.split(",");
                for (String url : urls) {
                    if (url.trim().startsWith("http")) {
                        aiImageUrls.add(url.trim());
                    }
                }
            }
        }

        // Nếu không tách được theo định dạng, dùng fallback cũ
        if (description.isEmpty() && cleanText.contains("\n")) {
            String[] parts = cleanText.split("\n", 2);
            name = parts[0].trim();
            description = parts[1].trim();
        }

        txtLandmarkName.setText(name);
        txtLandmarkDesc.setText(description);
        
        updateLandmarkDetails(name);
        setupViewPagerWithAiUrls(aiImageUrls);
    }

    private void updateLandmarkDetails(String name) {
        String lowerName = name.toLowerCase();
        Random random = new Random();

        // Cập nhật vị trí thông minh
        if (lowerName.contains("cát bà") || lowerName.contains("lan hạ")) {
            txtLandmarkLocation.setText("📍 Cát Bà, Hải Phòng");
            txtTag1.setText("Biển đảo");
            txtTag2.setText("Kỳ quan");
        } else if (lowerName.contains("đồ sơn")) {
            txtLandmarkLocation.setText("📍 Đồ Sơn, Hải Phòng");
            txtTag1.setText("Bãi biển");
            txtTag2.setText("Giải trí");
        } else {
            txtLandmarkLocation.setText("📍 Hải Phòng, Việt Nam");
            txtTag1.setText("Du lịch");
            txtTag2.setText("Khám phá");
        }
        
        txtTag3.setText("Hải Phòng");
        txtLandmarkHours.setText("08:00 - 21:00");
        txtLandmarkVisits.setText((5 + random.nextInt(15)) + ".0K / tháng");
        txtLandmarkRating.setText("4." + (6 + random.nextInt(4)));
        txtLandmarkDistance.setText((5 + random.nextInt(20)) + " km");
    }

    private void setupViewPagerWithAiUrls(List<String> urls) {
        imageUrls = new ArrayList<>();
        
        if (urls != null && !urls.isEmpty()) {
            imageUrls.addAll(urls);
        } else {
            // Ảnh mặc định nếu AI không trả về link ảnh
            imageUrls.add("https://bcp.cdnchinhphu.vn/Uploaded/hoangdien/2021_04_24/HP.jpg");
            imageUrls.add("https://images2.thanhnien.vn/528068207945824256/2023/11/17/img2122-17002062634352125134767.jpg");
        }

        pagerAdapter = new ImagePagerAdapter(imageUrls);
        viewPagerLandmark.setAdapter(pagerAdapter);
        new TabLayoutMediator(tabIndicator, viewPagerLandmark, (tab, position) -> {}).attach();
    }
}
