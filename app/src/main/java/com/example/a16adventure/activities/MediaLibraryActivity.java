package com.example.a16adventure.activities;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a16adventure.R;
import com.example.a16adventure.adapters.MediaAdapter;
import com.example.a16adventure.models.MediaDataManager;
import com.example.a16adventure.models.MediaItem;

import java.util.ArrayList;
import java.util.List;

public class MediaLibraryActivity extends BaseActivity {

    private ImageView btnBack;
    private RecyclerView rvMedia;
    private MediaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_library);

        btnBack = findViewById(R.id.btnBack);
        rvMedia = findViewById(R.id.rvMedia);

        btnBack.setOnClickListener(v -> finish());

        // Chỉ lấy các item thuộc danh mục "Di tích"
        List<MediaItem> diTichList = new ArrayList<>();
        for (MediaItem item : MediaDataManager.getMockMedia()) {
            if ("Di tích".equalsIgnoreCase(item.getCategory())) {
                diTichList.add(item);
            }
        }

        adapter = new MediaAdapter(this, diTichList);
        rvMedia.setLayoutManager(new GridLayoutManager(this, 2));
        rvMedia.setAdapter(adapter);
    }
}
