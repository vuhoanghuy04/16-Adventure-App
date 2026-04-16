package com.example.a16adventure.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.a16adventure.R;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class DetailActivity extends BaseActivity {

    private ImageView imgDetailCover;
    private TextView tvDetailDistrict, tvDetailDesc;
    private CollapsingToolbarLayout collapsingToolbar;
    private ExtendedFloatingActionButton fabNavigate;
    private Toolbar toolbar;

    private double destLat = 0;
    private double destLng = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // 1. Ánh xạ View
        imgDetailCover = findViewById(R.id.imgDetailCover);
        tvDetailDistrict = findViewById(R.id.tvDetailDistrict);
        tvDetailDesc = findViewById(R.id.tvDetailDesc);
        collapsingToolbar = findViewById(R.id.collapsingToolbar); // Lỗi dòng này thì xem lưu ý bên dưới
        fabNavigate = findViewById(R.id.fabNavigate);
        toolbar = findViewById(R.id.toolbarDetail);

        // 2. Thiết lập nút Back trên Toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        // 3. Nhận dữ liệu (Intent Extras) từ Adapter gửi sang
        Intent intent = getIntent();
        if (intent != null) {
            String name = intent.getStringExtra("EXTRA_NAME");
            String district = intent.getStringExtra("EXTRA_DISTRICT");
            String desc = intent.getStringExtra("EXTRA_DESC");
            String imageUrl = intent.getStringExtra("EXTRA_IMAGE");
            destLat = intent.getDoubleExtra("EXTRA_LAT", 0);
            destLng = intent.getDoubleExtra("EXTRA_LNG", 0);

            // 4. Đổ dữ liệu lên giao diện
            if (collapsingToolbar != null) {
                collapsingToolbar.setTitle(name != null ? name : "Chi tiết");
            }

            tvDetailDistrict.setText(district);
            tvDetailDesc.setText(desc);

            // Tải ảnh mượt mà bằng Glide
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(android.R.color.darker_gray)
                    .into(imgDetailCover);
        }

        // 5. Xử lý sự kiện bấm nút Chỉ đường (Mở Google Maps)
        fabNavigate.setOnClickListener(v -> {
            if (destLat != 0 && destLng != 0) {
                // Tạo link URI gọi ứng dụng Bản đồ (Sẽ mở Google Maps nếu máy có cài)
                String uri = "geo:" + destLat + "," + destLng + "?q=" + destLat + "," + destLng + "(Mục+tiêu)";
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                mapIntent.setPackage("com.google.android.apps.maps"); // Ưu tiên gọi thẳng Google Maps

                // Kiểm tra xem máy có Google Maps không, nếu không thì gọi trình duyệt/app bản đồ khác
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    Intent genericMapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(genericMapIntent);
                }
            }
        });
    }
}