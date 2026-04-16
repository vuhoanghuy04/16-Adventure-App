package com.example.a16adventure.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.a16adventure.R;
import com.example.a16adventure.models.Event;

public class EventDetailActivity extends BaseActivity {

    private ImageView imgEventCover, btnBack;
    private TextView tvHeaderTitle, tvEventTitle, tvEventTime, tvEventLocation;
    private TextView tvEventDesc, tvOriginMeaning, tvMainActivities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        // Ánh xạ
        imgEventCover = findViewById(R.id.imgEventCover);
        btnBack = findViewById(R.id.btnBack);
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvEventTitle = findViewById(R.id.tvEventTitle);
        tvEventTime = findViewById(R.id.tvEventTime);
        tvEventLocation = findViewById(R.id.tvEventLocation);
        tvEventDesc = findViewById(R.id.tvEventDesc);
        tvOriginMeaning = findViewById(R.id.tvOriginMeaning);
        tvMainActivities = findViewById(R.id.tvMainActivities);

        btnBack.setOnClickListener(v -> finish());

        // Nhận dữ liệu
        Event event = (Event) getIntent().getSerializableExtra("EVENT_OBJECT");
        if (event != null) {
            tvHeaderTitle.setText(event.getName());
            tvEventTitle.setText(event.getName());
            tvEventTime.setText("🕰️ Thời gian: " + event.getDate());
            tvEventLocation.setText("📍 Địa điểm: " + event.getLocation());
            tvEventDesc.setText(event.getShortDescription());
            tvOriginMeaning.setText(event.getOriginMeaning());

            // Build activities list
            StringBuilder activitiesBuilder = new StringBuilder();
            if (event.getMainActivities() != null) {
                for (String act : event.getMainActivities()) {
                    activitiesBuilder.append("✔ ").append(act).append("\n");
                }
            }
            tvMainActivities.setText(activitiesBuilder.toString().trim());

            int resId = getResources().getIdentifier(event.getImageUrl(), "drawable", getPackageName());
            if (resId != 0) {
                Glide.with(this)
                        .load(resId)
                        .centerCrop()
                        .placeholder(R.drawable.bg_weather_default)
                        .into(imgEventCover);
            } else {
                Glide.with(this)
                        .load(event.getImageUrl())
                        .centerCrop()
                        .placeholder(R.drawable.bg_weather_default)
                        .into(imgEventCover);
            }
        }
    }
}
