package com.example.a16adventure.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.text.Normalizer;
import java.util.regex.Pattern;

public class RecognitionResultActivity extends AppCompatActivity {

    private static final String TAG = "RecognitionResult";

    private ViewPager2 viewPagerLandmark;
    private TabLayout tabIndicator;
    private ImagePagerAdapter pagerAdapter;
    private List<String> imageUrls;
    private TextView txtLandmarkName, txtLandmarkDesc, txtLandmarkLocation;
    private TextView txtTag1, txtTag2, txtTag3;
    private TextView txtLandmarkRating, txtLandmarkHours, txtLandmarkVisits, txtLandmarkDistance;
    private FirebaseStorage storage;
    private String selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognition_result);

        storage = FirebaseStorage.getInstance();
        initViews();
        selectedImageUri = getIntent().getStringExtra("IMAGE_URI");

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
        ParsedResult parsed = parseResultFields(resultText);
        String name = parsed.name;
        String description = parsed.description;

        txtLandmarkName.setText(name);
        txtLandmarkDesc.setText(description);
        
        updateLandmarkDetails(name);
        showSelectedImageFirst();

        LinkedHashSet<String> candidateSet = new LinkedHashSet<>();
        String normalizedCode = normalizeLandmarkCode(parsed.code);
        String normalizedName = normalizeLandmarkCode(parsed.name);
        String normalizedFirstLine = normalizeLandmarkCode(parsed.firstLine);

        if (!normalizedCode.isEmpty()) candidateSet.add(normalizedCode);
        if (!normalizedName.isEmpty()) candidateSet.add(normalizedName);
        if (!normalizedFirstLine.isEmpty()) candidateSet.add(normalizedFirstLine);

        if (!candidateSet.isEmpty()) {
            fetchImageByCandidates(new ArrayList<>(candidateSet), 0);
        } else if (imageUrls == null || imageUrls.isEmpty()) {
            imageUrls = new ArrayList<>();
            imageUrls.add("https://bcp.cdnchinhphu.vn/Uploaded/hoangdien/2021_04_24/HP.jpg");
            updateViewPager();
        }
    }

    private void showSelectedImageFirst() {
        if (selectedImageUri == null || selectedImageUri.trim().isEmpty()) return;

        imageUrls = new ArrayList<>();
        imageUrls.add(selectedImageUri);
        updateViewPager();
    }

    private void fetchImageByCandidates(List<String> candidates, int index) {
        if (index >= candidates.size()) {
            // Không tìm thấy trên Firebase: giữ ảnh người dùng nếu đã có.
            if (imageUrls == null || imageUrls.isEmpty()) {
                imageUrls = new ArrayList<>();
                imageUrls.add("https://bcp.cdnchinhphu.vn/Uploaded/hoangdien/2021_04_24/HP.jpg");
                updateViewPager();
            }
            return;
        }

        fetchImageByCode(candidates.get(index), candidates, index);
    }

    private void fetchImageByCode(String code, List<String> candidates, int currentIndex) {
        if (imageUrls == null) {
            imageUrls = new ArrayList<>();
        }

        String fileName = normalizeLandmarkCode(code);
        if (fileName.isEmpty()) {
            fetchImageByCandidates(candidates, currentIndex + 1);
            return;
        }

        Log.d(TAG, "Thu image code: " + fileName);

        // Thử tìm file .webp trong Firebase Storage
        StorageReference storageRef = storage.getReference().child(fileName + ".webp");

        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            imageUrls.clear();
            imageUrls.add(uri.toString());
            updateViewPager();
        }).addOnFailureListener(e -> {
            Log.e("FirebaseError", "Không tìm thấy file: " + fileName + ".webp");
            // Thử tìm với định dạng .jpg nếu .webp không có
            storage.getReference().child(fileName + ".jpg").getDownloadUrl().addOnSuccessListener(uri -> {
                imageUrls.clear();
                imageUrls.add(uri.toString());
                updateViewPager();
            }).addOnFailureListener(e2 -> {
                Log.e("FirebaseError", "Không tìm thấy file: " + fileName + ".jpg");
                fetchImageByCandidates(candidates, currentIndex + 1);
            });
        });
    }

    private ParsedResult parseResultFields(String resultText) {
        String cleanText = resultText == null ? "" : resultText.replace("**", "").trim();
        String name = "Địa danh";
        String code = "";
        String description = "";
        String firstLine = "";

        String[] lines = cleanText.split("\\n");
        for (String rawLine : lines) {
            if (rawLine == null) continue;
            String line = rawLine.trim();
            if (line.isEmpty()) continue;
            if (firstLine.isEmpty()) firstLine = line;

            int separator = line.indexOf(':');
            if (separator < 0) separator = line.indexOf('：');
            if (separator < 0) continue;

            String key = line.substring(0, separator).trim();
            String value = line.substring(separator + 1).trim();
            String normalizedKey = removeAccents(key).toLowerCase(Locale.ROOT).replaceAll("\\s+", "");

            if (normalizedKey.startsWith("ten") && !value.isEmpty()) {
                name = value;
            } else if (normalizedKey.startsWith("ma") && !value.isEmpty()) {
                code = value;
            } else if (normalizedKey.startsWith("mota") && !value.isEmpty()) {
                description = value;
            }
        }

        if ((code.isEmpty() || description.isEmpty()) && !cleanText.isEmpty()) {
            String[] parts = cleanText.split("\\n", 2);
            if (!parts[0].trim().isEmpty()) {
                name = parts[0].trim();
            }
            if (description.isEmpty() && parts.length > 1) {
                description = parts[1].trim();
            }
        }

        return new ParsedResult(name, code, description, firstLine);
    }

    private String normalizeLandmarkCode(String input) {
        if (input == null) return "";
        String normalized = removeAccents(input)
                .toLowerCase(Locale.ROOT)
                .trim()
                .replaceAll("[^a-z0-9]", "");
        return normalized;
    }

    private String removeAccents(String text) {
        if (text == null) return "";
        String temp = Normalizer.normalize(text, Normalizer.Form.NFD);
        return Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
                .matcher(temp)
                .replaceAll("")
                .replace('đ', 'd')
                .replace('Đ', 'D');
    }

    private static class ParsedResult {
        final String name;
        final String code;
        final String description;
        final String firstLine;

        ParsedResult(String name, String code, String description, String firstLine) {
            this.name = name;
            this.code = code;
            this.description = description;
            this.firstLine = firstLine;
        }
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
