package com.example.a16adventure.activities;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.a16adventure.R;

public class RecognitionResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognition_result);

        ImageView imgLandmark = findViewById(R.id.imgLandmark);
        TextView txtLandmarkName = findViewById(R.id.txtLandmarkName);
        TextView txtLandmarkDesc = findViewById(R.id.txtLandmarkDesc);
        TextView btnBack = findViewById(R.id.btnResultBack);
        TextView btnDone = findViewById(R.id.btnResultDone);

        // Lấy dữ liệu từ Intent
        String resultText = getIntent().getStringExtra("RECOGNITION_RESULT");
        String imageUriString = getIntent().getStringExtra("IMAGE_URI");

        if (imageUriString != null) {
            imgLandmark.setImageURI(Uri.parse(imageUriString));
        }

        // Tách tên địa danh và mô tả (Giả sử AI trả về dạng "Tên: Mô tả")
        if (resultText != null) {
            if (resultText.contains(":") || resultText.contains("\n")) {
                String[] parts = resultText.split("[:\n]", 2);
                txtLandmarkName.setText(parts[0].trim());
                txtLandmarkDesc.setText(parts[1].trim());
            } else {
                txtLandmarkName.setText("Địa danh đã nhận diện");
                txtLandmarkDesc.setText(resultText);
            }
        }

        btnBack.setOnClickListener(v -> finish());
        btnDone.setOnClickListener(v -> finish());
    }
}