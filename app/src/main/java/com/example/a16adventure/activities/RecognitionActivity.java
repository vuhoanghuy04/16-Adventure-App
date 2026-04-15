package com.example.a16adventure.activities;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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
    private ImageButton btnCamera;
    private Uri camUri;
    
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
        
        btnCamera.setOnClickListener(v -> checkCameraPermissionAndTakePhoto());
    }

    private void initViews() {
        rvPhotos = findViewById(R.id.rvPhotos);
        loadingLayout = findViewById(R.id.loadingLayout);
        btnCamera = findViewById(R.id.btnCamera);
    }

    private void setupRecyclerView() {
        photoAdapter = new PhotoAdapter(photoUris, this::pickImages);
        rvPhotos.setLayoutManager(new GridLayoutManager(this, 3));
        rvPhotos.setAdapter(photoAdapter);
    }

    private void setupGemini() {
        // Sử dụng API Key của bạn
        String apiKey = "AIzaSyCBziMGwz6k1sQeyNtS34JPwItNwJ96hQ4";
        GenerativeModel gm = new GenerativeModel("gemini-2.5-flash", apiKey);
        model = GenerativeModelFutures.from(gm);
    }

    private void checkCameraPermissionAndTakePhoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            takePhoto();
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private final ActivityResultLauncher<String> requestCameraPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    takePhoto();
                } else {
                    Toast.makeText(this, "Bạn cần cấp quyền Camera để sử dụng tính năng này", Toast.LENGTH_SHORT).show();
                }
            }
    );

    private void takePhoto() {
        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
            camUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, camUri);
            takePhotoLauncher.launch(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Không thể mở máy ảnh", Toast.LENGTH_SHORT).show();
        }
    }

    private final ActivityResultLauncher<Intent> takePhotoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (camUri != null) {
                        photoUris.add(camUri);
                        photoAdapter.notifyDataSetChanged();
                    }
                }
            }
    );

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

            // Nâng cấp Prompt: Yêu cầu AI trả về Mã định danh ngắn gọn để khớp với tên file Firebase
            Content content = new Content.Builder()
                    .addImage(resizedBitmap)
                    .addText("Xác định địa danh trong ảnh tại Hải Phòng. Trả về kết quả theo đúng định dạng sau:\n" +
                            "Tên: [Tên hiển thị đầy đủ]\n" +
                            "Mã: [Tên viết liền không dấu, ngắn gọn nhất, ví dụ: tuyettinhcoc, baitamcatco, bachdanggiang]\n" +
                            "Mô tả: [Mô tả ngắn gọn hấp dẫn]\n" +
                            "Lưu ý: Tuyệt đối không lời dẫn, không ký hiệu lạ.")
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
                    Log.e("GeminiError", "Lỗi nhận diện: ", t);
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
        Intent intent = new Intent(this, RecognitionResultActivity.class);
        intent.putExtra("RECOGNITION_RESULT", info);
        intent.putExtra("IMAGE_URI", imageUri.toString());
        intent.setData(imageUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }
}
