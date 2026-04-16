package com.example.a16adventure.activities;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.a16adventure.R;

/**
 * VideoPlayerActivity – phát video từ res/raw.
 *
 * Caller truyền:
 *   intent.putExtra(EXTRA_RAW_ID, R.raw.ten_file)  // int – bắt buộc
 *   intent.putExtra(EXTRA_TITLE,  "Tiêu đề")        // String – tuỳ chọn
 */
public class VideoPlayerActivity extends BaseActivity {

    public static final String EXTRA_RAW_ID = "VIDEO_RAW_ID";
    public static final String EXTRA_TITLE  = "VIDEO_TITLE";

    private VideoView   videoView;
    private ProgressBar progressLoading;
    private ImageView   btnBack;
    private TextView    tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_video_player);
        hideSystemUI();

        videoView       = findViewById(R.id.videoView);
        progressLoading = findViewById(R.id.progressLoading);
        btnBack         = findViewById(R.id.btnBack);
        tvTitle         = findViewById(R.id.tvVideoPlayerTitle);

        btnBack.setOnClickListener(v -> finish());

        String title = getIntent().getStringExtra(EXTRA_TITLE);
        if (title != null && !title.isEmpty()) {
            tvTitle.setText(title);
            tvTitle.setVisibility(View.VISIBLE);
        } else {
            tvTitle.setVisibility(View.GONE);
        }

        int rawId = getIntent().getIntExtra(EXTRA_RAW_ID, 0);
        if (rawId == 0) {
            Toast.makeText(this, "Không tìm thấy file video", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + rawId);
        playVideo(uri);
    }

    private void playVideo(Uri uri) {
        progressLoading.setVisibility(View.VISIBLE);

        videoView.setVideoURI(uri);

        MediaController mc = new MediaController(this);
        mc.setAnchorView(videoView);
        videoView.setMediaController(mc);

        videoView.setOnPreparedListener(mp -> {
            progressLoading.setVisibility(View.GONE);
            mp.setLooping(false);
            videoView.start();
        });

        videoView.setOnErrorListener((mp, what, extra) -> {
            progressLoading.setVisibility(View.GONE);
            Toast.makeText(this,
                    "Không thể phát video (err " + what + "/" + extra + ")",
                    Toast.LENGTH_LONG).show();
            finish();
            return true;
        });

        videoView.requestFocus();
    }

    private void hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(false);
            WindowInsetsController ctrl = getWindow().getInsetsController();
            if (ctrl != null) {
                ctrl.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                ctrl.setSystemBarsBehavior(
                        WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            //noinspection deprecation
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView != null && videoView.isPlaying()) {
            videoView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoView != null) {
            videoView.stopPlayback();
        }
    }
}
