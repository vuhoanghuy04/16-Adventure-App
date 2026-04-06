package com.example.a16adventure.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a16adventure.R;
import com.example.a16adventure.adapters.PhotoAdapter;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RecognitionActivity extends AppCompatActivity {

    private RecyclerView rvPhotos;
    private PhotoAdapter photoAdapter;
    private List<Uri> photoUris = new ArrayList<>();
    private View loadingLayout;
    
    private GenerativeModelFutures model;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognition);

        initViews();
        setupRecyclerView();
        setupGemini();

        findViewById(R.id.btnScan).setOnClickListener(v -> startRecognition());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void initViews() {
        rvPhotos = findViewById(R.id.rvPhotos);
        loadingLayout = findViewById(R.id.loadingLayout);
    }

    private void setupRecyclerView() {
        photoAdapter = new PhotoAdapter(photoUris, this::pickImages);
        rvPhotos.setLayoutManager(new GridLayoutManager(this, 3));
        rvPhotos.setAdapter(photoAdapter);
    }

    private void setupGemini() {
        // GIỮ NGUYÊN NHƯ BẠN YÊU CẦU
        String apiKey = "AIzaSyCBziMGwz6k1sQeyNtS34JPwItNwJ96hQ4";
        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash", apiKey);
        model = GenerativeModelFutures.from(gm);
    }

    private void pickImages() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        pickImagesLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> pickImagesLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    if (result.getData().getClipData() != null) {
                        int count = result.getData().getClipData().getItemCount();
                        for (int i = 0; i < count; i++) {
                            photoUris.add(result.getData().getClipData().getItemAt(i).getUri());
                        }
                    } else if (result.getData().getData() != null) {
                        photoUris.add(result.getData().getData());
                    }
                    photoAdapter.notifyDataSetChanged();
                }
            }
    );

    private void startRecognition() {
        if (photoUris.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ít nhất một ảnh", Toast.LENGTH_SHORT).show();
            return;
        }

        loadingLayout.setVisibility(View.VISIBLE);

        try {
            Uri imageUri = photoUris.get(0);
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);

            if (originalBitmap == null) {
                loadingLayout.setVisibility(View.GONE);
                Toast.makeText(this, "Không thể đọc tệp ảnh", Toast.LENGTH_SHORT).show();
                return;
            }

            Bitmap resizedBitmap = scaleBitmap(originalBitmap, 1024);

            Content content = new Content.Builder()
                    .addImage(resizedBitmap)
                    .addText("Đây là địa danh nào ở Hải Phòng? Hãy cho tôi biết tên địa danh và mô tả ngắn gọn về nó theo cấu trúc: 'Tên địa danh: Mô tả'.")
                    .build();

            ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
            Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                @Override
                public void onSuccess(GenerateContentResponse result) {
                    runOnUiThread(() -> {
                        loadingLayout.setVisibility(View.GONE);
                        String aiResponse = result.getText();
                        showResult(aiResponse, imageUri);
                    });
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.e("GeminiError", "Lỗi nhận diện chi tiết: ", t);
                    runOnUiThread(() -> {
                        loadingLayout.setVisibility(View.GONE);
                        Toast.makeText(RecognitionActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    });
                }
            }, executor);

        } catch (Exception e) {
            loadingLayout.setVisibility(View.GONE);
            Toast.makeText(this, "Lỗi xử lý ảnh", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap scaleBitmap(Bitmap bm, int maxDimension) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        if (width <= maxDimension && height <= maxDimension) return bm;
        float aspectRatio = (float) width / (float) height;
        if (width > height) {
            width = maxDimension;
            height = (int) (width / aspectRatio);
        } else {
            height = maxDimension;
            width = (int) (height * aspectRatio);
        }
        return Bitmap.createScaledBitmap(bm, width, height, true);
    }

    private void showResult(String info, Uri imageUri) {
        // CHUYỂN SANG MÀN HÌNH KẾT QUẢ MỚI
        Intent intent = new Intent(this, RecognitionResultActivity.class);
        intent.putExtra("RECOGNITION_RESULT", info);
        intent.putExtra("IMAGE_URI", imageUri.toString());
        startActivity(intent);
    }
}