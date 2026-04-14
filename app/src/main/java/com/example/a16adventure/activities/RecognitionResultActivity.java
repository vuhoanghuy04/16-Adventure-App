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

public class RecognitionResultActivity extends AppCompatActivity {

    private ViewPager2 viewPagerLandmark;
    private TabLayout tabIndicator;
    private ImagePagerAdapter pagerAdapter;
    private List<String> imageUrls;
    private TextView txtLandmarkName, txtLandmarkDesc, txtLandmarkLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognition_result);

        initViews();
        
        // Lấy dữ liệu từ Intent
        String resultText = getIntent().getStringExtra("RECOGNITION_RESULT");
        String userImageUri = getIntent().getStringExtra("IMAGE_URI");

        if (resultText != null) {
            parseAndShowResult(resultText);
            setupViewPager(txtLandmarkName.getText().toString(), userImageUri);
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
    }

    private void parseAndShowResult(String resultText) {
        String cleanText = resultText.replace("**", "").trim();
        String name = "";
        String description = cleanText;

        // Cố gắng tách Tên địa danh từ định dạng "Tên địa danh: Mô tả"
        if (cleanText.toLowerCase().contains("tên địa danh:")) {
            String[] lines = cleanText.split("\n");
            for (String line : lines) {
                if (line.toLowerCase().contains("tên địa danh:")) {
                    name = line.substring(line.toLowerCase().indexOf("tên địa danh:") + 13).trim();
                    if (name.startsWith(":")) name = name.substring(1).trim();
                    break;
                }
            }
            // Phần còn lại là mô tả
            description = cleanText.substring(cleanText.toLowerCase().indexOf("mô tả:") != -1 ? 
                          cleanText.toLowerCase().indexOf("mô tả:") + 6 : 
                          cleanText.indexOf("\n") + 1).trim();
            if (description.startsWith(":")) description = description.substring(1).trim();
        } else {
            // Nếu AI không trả về đúng định dạng, lấy dòng đầu tiên làm tên
            String[] parts = cleanText.split("\n", 2);
            name = parts[0].trim();
            if (parts.length > 1) description = parts[1].trim();
        }

        // Nếu tên quá dài (AI trả về cả câu), cắt bớt hoặc để mặc định
        if (name.length() > 50 || name.isEmpty()) {
            name = "Địa danh đã quét";
        }

        txtLandmarkName.setText(name);
        txtLandmarkDesc.setText(description);
        
        // Tự động cập nhật vị trí dựa trên tên địa danh
        updateLocation(name);
    }

    private void updateLocation(String name) {
        String location = "📍 Hải Phòng, Việt Nam"; // Mặc định
        String lowerName = name.toLowerCase();
        
        if (lowerName.contains("cát bà") || lowerName.contains("lan hạ") || lowerName.contains("cát cò")) {
            location = "📍 Cát Bà, Hải Phòng";
        } else if (lowerName.contains("đồ sơn") || lowerName.contains("hòn dấu")) {
            location = "📍 Đồ Sơn, Hải Phòng";
        } else if (lowerName.contains("nhà hát lớn") || lowerName.contains("quảng trường")) {
            location = "📍 Quận Hồng Bàng, Hải Phòng";
        } else if (lowerName.contains("tuyệt tình cốc")) {
            location = "📍 Thủy Nguyên, Hải Phòng";
        }
        
        txtLandmarkLocation.setText(location);
    }

    private void setupViewPager(String landmarkName, String userImageUri) {
        imageUrls = new ArrayList<>();
        
        if (userImageUri != null && !userImageUri.isEmpty()) {
            imageUrls.add(userImageUri);
        }
        
        // Thêm ảnh mẫu phù hợp với địa danh
        if (landmarkName.toLowerCase().contains("cát bà") || landmarkName.toLowerCase().contains("lan hạ")) {
            imageUrls.add("https://statics.vinpearl.com/vinh-lan-ha-1_1625732168.jpg");
            imageUrls.add("https://statics.vinwonders.com/vinh-lan-ha-cat-ba-2_1660127264.jpg");
        } else if (landmarkName.toLowerCase().contains("đồ sơn")) {
            imageUrls.add("https://Reviewvilla.vn/wp-content/uploads/2022/05/bien-do-son-1.jpg");
            imageUrls.add("https://vcdn1-dulich.vnecdn.net/2022/04/18/haiphong-1650275322-1650275333-8742-1650275681.jpg");
        } else {
            imageUrls.add("https://bcp.cdnchinhphu.vn/Uploaded/hoangdien/2021_04_24/HP.jpg");
            imageUrls.add("https://images2.thanhnien.vn/528068207945824256/2023/11/17/img2122-17002062634352125134767.jpg");
        }

        pagerAdapter = new ImagePagerAdapter(imageUrls);
        viewPagerLandmark.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabIndicator, viewPagerLandmark, (tab, position) -> {}).attach();
    }
}
