package com.example.a16adventure.activities;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.a16adventure.R;

public class PhotoViewerActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);

        ImageView btnBack = findViewById(R.id.btnBack);
        ImageView imgPhotoFull = findViewById(R.id.imgPhotoFull);

        btnBack.setOnClickListener(v -> finish());

        String url = getIntent().getStringExtra("IMAGE_URL");
        if (url != null) {
            int resId = getResources().getIdentifier(url, "drawable", getPackageName());
            if (resId != 0) {
                Glide.with(this).load(resId).into(imgPhotoFull);
            } else {
                Glide.with(this).load(url).into(imgPhotoFull);
            }
        }
    }
}
