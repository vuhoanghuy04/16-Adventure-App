package com.example.a16adventure.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.a16adventure.R;
import com.example.a16adventure.adapters.MapMonumentAdapter;
import com.example.a16adventure.models.Monument;
import com.example.a16adventure.models.MonumentDataManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class MapActivity extends BaseActivity implements OnMapReadyCallback, MapMonumentAdapter.OnMonumentClickListener {

    private GoogleMap mMap;
    private List<Monument> fullMonumentList;
    private List<Monument> currentList;
    private RecyclerView recyclerMapLocations;
    private MapMonumentAdapter adapter;
    private Map<String, Marker> markerMap = new HashMap<>();
    private ChipGroup chipGroupMap;
    private FloatingActionButton btnZoomIn, btnZoomOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        setupBottomNavigation(R.id.bottomNavigation, R.id.nav_map);

        fullMonumentList = MonumentDataManager.getInstance().getMonumentList();
        if (fullMonumentList == null) fullMonumentList = new ArrayList<>();
        currentList = new ArrayList<>(fullMonumentList);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        recyclerMapLocations = findViewById(R.id.recyclerMapLocations);
        adapter = new MapMonumentAdapter(this, currentList, this);
        recyclerMapLocations.setAdapter(adapter);

        chipGroupMap = findViewById(R.id.chipGroupMap);
        setupFilters();

        btnZoomIn = findViewById(R.id.btnZoomIn);
        btnZoomOut = findViewById(R.id.btnZoomOut);

        btnZoomIn.setOnClickListener(v -> {
            if (mMap != null) {
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
            }
        });

        btnZoomOut.setOnClickListener(v -> {
            if (mMap != null) {
                mMap.animateCamera(CameraUpdateFactory.zoomOut());
            }
        });
    }

    private void setupFilters() {
        chipGroupMap.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        // 1. Đúc Chip "Tất cả"
        Chip chipAll = (Chip) inflater.inflate(R.layout.item_chip_filter, chipGroupMap, false);
        chipAll.setText("Tất cả");
        chipAll.setId(View.generateViewId());
        chipAll.setChecked(true);
        chipGroupMap.addView(chipAll);

        // 2. Lấy danh sách các Quận/Huyện duy nhất từ JSON
        LinkedHashSet<String> uniqueDistricts = new LinkedHashSet<>();
        for (Monument m : fullMonumentList) {
            if (m.getDistrict() != null) {
                uniqueDistricts.add(m.getDistrict());
            }
        }

        // 3. Đúc Chip cho từng Quận/Huyện
        for (String districtName : uniqueDistricts) {
            Chip chip = (Chip) inflater.inflate(R.layout.item_chip_filter, chipGroupMap, false);
            chip.setText(districtName);
            chip.setId(View.generateViewId());
            chipGroupMap.addView(chip);
        }

        // 4. Lắng nghe sự kiện click
        chipGroupMap.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;

            int checkedId = checkedIds.get(0);
            Chip selectedChip = findViewById(checkedId);
            if (selectedChip == null) return;
            
            String selectedText = selectedChip.getText().toString();

            if (selectedText.equals("Tất cả")) {
                filterByDistrict("All");
            } else {
                filterByDistrict(selectedText);
            }
        });
    }

    private void filterByDistrict(String district) {
        currentList.clear();
        if (district.equals("All")) {
            currentList.addAll(fullMonumentList);
        } else {
            for (Monument m : fullMonumentList) {
                if (m.getDistrict() != null && m.getDistrict().equals(district)) {
                    currentList.add(m);
                }
            }
        }
        adapter.notifyDataSetChanged();
        updateMapMarkers();
        zoomToMarkers();
    }

    private void zoomToMarkers() {
        if (mMap == null || currentList.isEmpty()) return;

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        boolean hasPoints = false;
        for (Monument m : currentList) {
            if (m.getLatitude() != 0 && m.getLongitude() != 0) {
                builder.include(new LatLng(m.getLatitude(), m.getLongitude()));
                hasPoints = true;
            }
        }

        if (hasPoints) {
            LatLngBounds bounds = builder.build();
            int padding = 150;
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);

        LatLng haiPhong = new LatLng(20.8449, 106.6881);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(haiPhong, 11f));

        updateMapMarkers();
    }

    private void updateMapMarkers() {
        if (mMap == null) return;
        mMap.clear();
        markerMap.clear();

        for (Monument m : currentList) {
            if (m.getLatitude() != 0 && m.getLongitude() != 0) {
                LatLng location = new LatLng(m.getLatitude(), m.getLongitude());
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(location)
                        .title(m.getName())
                        .snippet(m.getDistrict()));
                
                if (marker != null && m.getId() != null) {
                    markerMap.put(m.getId(), marker);
                }
            }
        }
    }

    @Override
    public void onMonumentClick(Monument monument) {
        if (mMap == null) return;
        LatLng location = new LatLng(monument.getLatitude(), monument.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));
        Marker marker = markerMap.get(monument.getId());
        if (marker != null) {
            marker.showInfoWindow();
        }
    }

    @Override
    public void onDirectionsClick(Monument monument) {
        if (monument.getLatitude() == 0 || monument.getLongitude() == 0) {
            Toast.makeText(this, "Không có dữ liệu vị trí!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cách dùng Android Intent: geo:0,0?q=latitude,longitude(label)
        // Đây là cách tốt nhất để mở Google Maps và ghim chính xác địa danh kèm tên.
        String geoUri = "geo:0,0?q=" + monument.getLatitude() + "," + monument.getLongitude() 
                        + "(" + Uri.encode(monument.getName()) + ")";
        
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
        
        // Cố gắng mở bằng ứng dụng Google Maps nếu có
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            // Nếu không có Google Maps, mở bằng bất kỳ ứng dụng bản đồ nào khác hoặc trình duyệt
            Intent fallbackIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
            startActivity(fallbackIntent);
        }
    }
}
