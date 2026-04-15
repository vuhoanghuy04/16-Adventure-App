package com.example.a16adventure.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.a16adventure.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Liên kết với file fragment_map.xml bạn vừa tạo
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // Khởi tạo bản đồ
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Tọa độ Nhà Hát Lớn Hải Phòng (Trung tâm)
        LatLng haiPhong = new LatLng(20.8604, 106.6821);

        // Thêm dấu ghim và di chuyển camera tới đó
        mMap.addMarker(new MarkerOptions().position(haiPhong).title("Nhà Hát Lớn Hải Phòng"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(haiPhong, 15f));
    }
}