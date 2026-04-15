package com.example.a16adventure.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.a16adventure.R;
import com.example.a16adventure.adapters.ImagePagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognition_result);

        storage = FirebaseStorage.getInstance();
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
        String code = "";
        String description = "";

        // Tách dữ liệu từ cấu trúc nâng cấp của AI
        String[] lines = cleanText.split("\n");
        for (String line : lines) {
            if (line.toLowerCase().startsWith("tên:")) {
                name = line.substring(line.indexOf(":") + 1).trim();
            } else if (line.toLowerCase().startsWith("mã:")) {
                code = line.substring(line.indexOf(":") + 1).trim();
            } else if (line.toLowerCase().startsWith("mô tả:")) {
                description = line.substring(line.indexOf(":") + 1).trim();
            }
        }

        // Fallback nếu AI trả về định dạng cũ
        if (code.isEmpty() && !cleanText.isEmpty()) {
            String[] parts = cleanText.split("\n", 2);
            name = parts[0].trim();
            description = parts.length > 1 ? parts[1].trim() : "";
        }

        txtLandmarkName.setText(name);
        txtLandmarkDesc.setText(description);
        
        updateLandmarkDetails(name);
        
        // Sử dụng Mã (code) để lấy ảnh chính xác từ Firebase Storage
        if (!code.isEmpty()) {
            fetchImageByCode(code);
        } else {
            fetchImageByCode(name); // Thử dùng tên nếu không có mã
        }
    }

    private void fetchImageByCode(String code) {
        imageUrls = new ArrayList<>();
        
        // Chuẩn hóa mã thành viết liền không dấu (nếu AI chưa làm chuẩn)
        String fileName = code.toLowerCase().replaceAll("\\s+", "");
        
        // Thử tìm file .webp trong Firebase Storage
        StorageReference storageRef = storage.getReference().child(fileName + ".webp");

        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            imageUrls.add(uri.toString());
            updateViewPager();
        }).addOnFailureListener(e -> {
            Log.e("FirebaseError", "Không tìm thấy file: " + fileName + ".webp");
            // Thử tìm với định dạng .jpg nếu .webp không có
            storage.getReference().child(fileName + ".jpg").getDownloadUrl().addOnSuccessListener(uri -> {
                imageUrls.add(uri.toString());
                updateViewPager();
            }).addOnFailureListener(e2 -> {
                // Nếu vẫn không có, dùng ảnh mặc định
                imageUrls.add("https://bcp.cdnchinhphu.vn/Uploaded/hoangdien/2021_04_24/HP.jpg");
                updateViewPager();
            });
        });
    }

    private void updateViewPager() {
        pagerAdapter = new ImagePagerAdapter(imageUrls);
        viewPagerLandmark.setAdapter(pagerAdapter);
        new TabLayoutMediator(tabIndicator, viewPagerLandmark, (tab, position) -> {}).attach();
    }

    private void updateLandmarkDetails(String name) {
        String lowerName = name.toLowerCase();
        Random random = new Random();

        if (lowerName.contains("tuyệt tình cốc") || lowerName.contains("hồ đá")) {
            txtLandmarkLocation.setText("📍 Thủy Nguyên, Hải Phòng");
            txtTag1.setText("Thiên nhiên");
            txtTag2.setText("Khám phá");
            txtLandmarkHours.setText("07:00 - 18:00");
        } else if (lowerName.contains("cát bà") || lowerName.contains("lan hạ")) {
            txtLandmarkLocation.setText("📍 Cát Bà, Hải Phòng");
            txtTag1.setText("Biển đảo");
            txtTag2.setText("Kỳ quan");
            txtLandmarkHours.setText("Mở cả ngày");
        } else {
            txtLandmarkLocation.setText("📍 Hải Phòng, Việt Nam");
            txtTag1.setText("Du lịch");
            txtTag2.setText("Hải Phòng");
            txtLandmarkHours.setText("08:00 - 21:00");
        }
        
        txtTag3.setText("Địa danh");
        txtLandmarkVisits.setText((5 + random.nextInt(15)) + ".2K / tháng");
        txtLandmarkRating.setText("4." + (6 + random.nextInt(4)));
        txtLandmarkDistance.setText((10 + random.nextInt(15)) + " km");
    }
}
