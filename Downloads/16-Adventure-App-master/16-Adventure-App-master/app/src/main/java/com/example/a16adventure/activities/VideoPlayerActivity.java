package com.example.a16adventure.activities;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.example.a16adventure.R;

public class VideoPlayerActivity extends BaseActivity {

    private VideoView videoView;
    private ProgressBar progressLoading;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        videoView = findViewById(R.id.videoView);
        progressLoading = findViewById(R.id.progressLoading);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        String url = getIntent().getStringExtra("VIDEO_URL");
        if (url != null) {
            Uri uri = Uri.parse(url);
            videoView.setVideoURI(uri);
            
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);
            videoView.setMediaController(mediaController);

            videoView.setOnPreparedListener(mp -> {
                progressLoading.setVisibility(View.GONE);
                videoView.start();
            });

            videoView.setOnErrorListener((mp, what, extra) -> {
                progressLoading.setVisibility(View.GONE);
                return false;
            });
        } else {
            progressLoading.setVisibility(View.GONE);
        }
    }
}
