package com.example.a16adventure.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a16adventure.R;
import com.example.a16adventure.adapters.MediaAdapter;
import com.example.a16adventure.models.MediaDataManager;
import com.example.a16adventure.models.MediaItem;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class MediaLibraryActivity extends BaseActivity {

    private ImageView btnBack;
    private ChipGroup chipGroupCategory;
    private RecyclerView rvMedia;
    private MediaAdapter adapter;

    private List<MediaItem> fullList;
    private List<MediaItem> displayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_library);

        btnBack = findViewById(R.id.btnBack);
        chipGroupCategory = findViewById(R.id.chipGroupCategory);
        rvMedia = findViewById(R.id.rvMedia);

        btnBack.setOnClickListener(v -> finish());

        fullList = MediaDataManager.getMockMedia();
        displayList = new ArrayList<>(fullList);

        adapter = new MediaAdapter(this, displayList);
        // Lưới 2 cột
        rvMedia.setLayoutManager(new GridLayoutManager(this, 2));
        rvMedia.setAdapter(adapter);

        setupChips();
    }

    private void setupChips() {
        LayoutInflater inflater = LayoutInflater.from(this);
        String[] categories = {"Di tích", "Lễ hội", "Phong cảnh", "Ẩm thực"};

        for (int i = 0; i < categories.length; i++) {
            Chip chip = (Chip) inflater.inflate(R.layout.item_chip_filter, chipGroupCategory, false);
            chip.setText(categories[i]);
            chip.setId(View.generateViewId());
            chipGroupCategory.addView(chip);

            if (i == 0) {
                chipGroupCategory.check(chip.getId());
            }
        }

        chipGroupCategory.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            Chip selectedChip = findViewById(checkedIds.get(0));
            String filter = selectedChip.getText().toString();
            applyFilter(filter);
        });

        // Áp dụng bộ lọc đầu tiên (Di tích) ngay khi khởi tạo
        applyFilter(categories[0]);
    }

    private void applyFilter(String filter) {
        displayList.clear();
        for (MediaItem item : fullList) {
            if (item.getCategory().contains(filter)) {
                displayList.add(item);
            }
        }
        adapter.updateList(displayList);
    }
}
