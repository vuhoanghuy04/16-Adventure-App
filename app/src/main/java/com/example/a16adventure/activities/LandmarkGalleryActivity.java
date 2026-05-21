package com.example.a16adventure.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.a16adventure.R;
import com.example.a16adventure.models.LandmarkGallery;
import com.example.a16adventure.util.AppConstants;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

/**
 * LandmarkGalleryActivity
 * – Hiển thị ảnh giới thiệu di tích (swipeable horizontal list)
 * – Click ảnh → PhotoViewerActivity (toàn màn hình)
 * – Card video → VideoPlayerActivity (nếu di tích có video)
 */
public class LandmarkGalleryActivity extends BaseActivity {

    public static final String EXTRA_GALLERY = "LANDMARK_GALLERY";

    private ImageView        btnBack;
    private LinearLayout     layoutPhotoGallery;
    private TextView         tvDistrict, tvLandmarkName, tvDescription;
    private LinearLayout     layoutVideoSection;
    private MaterialCardView cardVideo;
    private ImageView        imgVideoThumbnail;
    private TextView         tvVideoTitle;

    private LandmarkGallery gallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landmark_gallery);

        gallery = (LandmarkGallery) getIntent().getSerializableExtra(EXTRA_GALLERY);
        if (gallery == null) {
            finish();
            return;
        }

        initViews();
        setupPhotoGrid();
        setupInfo();
        setupVideo();
    }

    private void initViews() {
        btnBack            = findViewById(R.id.btnBack);
        layoutPhotoGallery = findViewById(R.id.layoutPhotoGallery);
        tvDistrict         = findViewById(R.id.tvDistrict);
        tvLandmarkName     = findViewById(R.id.tvLandmarkName);
        tvDescription      = findViewById(R.id.tvDescription);
        layoutVideoSection = findViewById(R.id.layoutVideoSection);
        cardVideo          = findViewById(R.id.cardVideo);
        imgVideoThumbnail  = findViewById(R.id.imgVideoThumbnail);
        tvVideoTitle       = findViewById(R.id.tvVideoTitle);

        btnBack.setOnClickListener(v -> finish());
    }

    private void setupPhotoGrid() {
        List<String> urls = gallery.getImageUrls();
        if (urls == null || urls.isEmpty()) return;

        layoutPhotoGallery.removeAllViews();

        float density = getResources().getDisplayMetrics().density;
        int   width   = (int) (getResources().getDisplayMetrics().widthPixels * 0.85f);
        int   height  = (int) (220 * density);
        int   margin  = (int) (12  * density);

        for (int i = 0; i < urls.size(); i++) {
            final int index = i;
            String url = urls.get(i);

            MaterialCardView card = new MaterialCardView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
            params.setMargins(0, 0, margin, 0);
            card.setLayoutParams(params);
            card.setRadius(16 * density);
            card.setCardElevation(4 * density);
            card.setStrokeWidth(0);

            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new android.view.ViewGroup.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            card.addView(imageView);
            loadImage(imageView, url);
            card.setOnClickListener(v -> openPhoto(urls, index));
            layoutPhotoGallery.addView(card);
        }
    }

    /** Load drawable local (by name) hoặc URL bằng Glide */
    private void loadImage(ImageView iv, String url) {
        int resId = getResources().getIdentifier(url, "drawable", getPackageName());
        if (resId != 0) {
            Glide.with(this).load(resId).centerCrop()
                    .placeholder(R.drawable.bg_weather_default).into(iv);
        } else {
            Glide.with(this).load(url).centerCrop()
                    .placeholder(R.drawable.bg_weather_default)
                    .error(R.drawable.bg_weather_default).into(iv);
        }
    }

    private void openPhoto(List<String> urls, int position) {
        Intent intent = new Intent(this, PhotoViewerActivity.class);
        intent.putExtra(AppConstants.Extras.IMAGE_URL, urls.get(position));
        startActivity(intent);
    }

    private void setupInfo() {
        tvLandmarkName.setText(gallery.getName());
        tvDescription.setText(gallery.getDescription());

        String district = gallery.getDistrict();
        if (district != null && !district.isEmpty()) {
            tvDistrict.setText("📍 " + district);
            tvDistrict.setVisibility(View.VISIBLE);
        } else {
            tvDistrict.setVisibility(View.GONE);
        }
    }

    private void setupVideo() {
        if (!gallery.hasVideo()) {
            layoutVideoSection.setVisibility(View.GONE);
            return;
        }

        layoutVideoSection.setVisibility(View.VISIBLE);

        final int    rawId      = gallery.getVideoRawId();
        final String videoTitle = gallery.getVideoTitle() != null
                ? gallery.getVideoTitle() : gallery.getName();

        tvVideoTitle.setText(videoTitle);

        List<String> imgs = gallery.getImageUrls();
        if (imgs != null && !imgs.isEmpty()) {
            loadImage(imgVideoThumbnail, imgs.get(0));
        }

        cardVideo.setOnClickListener(v -> {
            Intent intent = new Intent(this, VideoPlayerActivity.class);
            intent.putExtra(VideoPlayerActivity.EXTRA_RAW_ID, rawId);
            intent.putExtra(VideoPlayerActivity.EXTRA_TITLE,  videoTitle);
            startActivity(intent);
        });
    }
}
