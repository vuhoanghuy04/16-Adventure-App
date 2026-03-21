package com.example.a16adventure.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.a16adventure.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // 1. Tải bản đồ
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // 2. Cấu hình thanh Navigation y hệt MainActivity
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigationMap);

        // Màu đỏ khi chọn tab
        int[][] states = new int[][] {
                new int[] { android.R.attr.state_checked},
                new int[] {-android.R.attr.state_checked}
        };
        int[] colors = new int[] {
                android.graphics.Color.parseColor("#FF4B4B"),
                android.graphics.Color.parseColor("#808080")
        };
        android.content.res.ColorStateList colorStateList = new android.content.res.ColorStateList(states, colors);
        bottomNavigation.setItemIconTintList(colorStateList);

        // Cố định tab Map đang được chọn (Sáng màu đỏ)
        bottomNavigation.setSelectedItemId(R.id.nav_map);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                // Quay về Trang chủ
                Intent intent = new Intent(MapActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); // Mẹo: Dùng lại MainActivity cũ thay vì tạo mới
                startActivity(intent);
                overridePendingTransition(0, 0); // Tắt hiệu ứng chuyển cảnh để giống chuyển tab
                finish(); // Đóng trang Map này lại
                return true;
            }
            return true;
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        LatLng haiPhong = new LatLng(20.8604, 106.6821);
        mMap.addMarker(new MarkerOptions().position(haiPhong).title("Nhà Hát Lớn Hải Phòng"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(haiPhong, 15f));
    }
}