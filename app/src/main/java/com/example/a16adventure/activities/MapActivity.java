package com.example.a16adventure.activities;

import android.os.Bundle;
import com.example.a16adventure.R;
import com.example.a16adventure.models.Monument;
import com.example.a16adventure.models.MonumentRepository;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends BaseActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private List<Monument> monumentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // 1. Cấu hình Bottom Navigation (Tab Map sáng đỏ)
        setupBottomNavigation(R.id.bottomNavigation, R.id.nav_map);

        // 2. Khởi tạo dữ liệu di tích (Bạn có thể lấy từ Database sau này)
        setupData();

        // 3. Kết nối với Fragment bản đồ trong XML
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void setupData() {
        monumentList = new ArrayList<>(MonumentRepository.loadFromAssets(this));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Tọa độ trung tâm Hải Phòng
        LatLng haiPhong = new LatLng(20.8449, 106.6881);

        // Duyệt danh sách di tích và cắm Marker
        for (Monument m : monumentList) {
            LatLng location = new LatLng(m.getLatitude(), m.getLongitude());
            mMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title(m.getName())
                    .snippet(m.getDistrict()));
        }

        // Di chuyển camera đến Hải Phòng với độ phóng thu (Zoom) là 12
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(haiPhong, 12f));

        // Cho phép hiện nút zoom
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }
}
