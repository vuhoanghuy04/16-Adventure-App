package com.example.a16adventure.activities;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a16adventure.R;

public class OfflineMapActivity extends AppCompatActivity {

    private ImageView btnBack;
    private Button btnDownload;
    private ProgressBar pbDownload;
    private TextView tvStatus;
    private boolean isDownloaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_map);

        btnBack = findViewById(R.id.btnBack);
        btnDownload = findViewById(R.id.btnDownloadMap);
        pbDownload = findViewById(R.id.pbDownload);
        tvStatus = findViewById(R.id.tvStatus);

        btnBack.setOnClickListener(v -> finish());

        btnDownload.setOnClickListener(v -> {
            if (isDownloaded) {
                deleteMap();
            } else {
                startDownload();
            }
        });
    }

    private void startDownload() {
        btnDownload.setEnabled(false);
        pbDownload.setVisibility(View.VISIBLE);
        tvStatus.setText("Đang tải xuống... 0%");
        
        final Handler handler = new Handler();
        new Thread(() -> {
            for (int i = 0; i <= 100; i += 5) {
                final int progress = i;
                try {
                    Thread.sleep(200); // Giả lập tốc độ tải
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.post(() -> {
                    pbDownload.setProgress(progress);
                    tvStatus.setText("Đang tải xuống... " + progress + "%");
                    if (progress == 100) {
                        completeDownload();
                    }
                });
            }
        }).start();
    }

    private void completeDownload() {
        isDownloaded = true;
        pbDownload.setVisibility(View.GONE);
        btnDownload.setEnabled(true);
        btnDownload.setText("Xóa bản đồ đã tải");
        btnDownload.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFF4B4B));
        tvStatus.setText("Đã lưu ngoại tuyến (45.2 MB)");
        Toast.makeText(this, "Tải bản đồ Hải Phòng thành công!", Toast.LENGTH_SHORT).show();
    }

    private void deleteMap() {
        isDownloaded = false;
        btnDownload.setText("Tải bản đồ xuống");
        btnDownload.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF4CAF50));
        tvStatus.setText("Chưa tải xuống");
        Toast.makeText(this, " Đã xóa bản đồ ngoại tuyến", Toast.LENGTH_SHORT).show();
    }
}
