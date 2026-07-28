package com.example.rundraw_fe;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class DrawCourseActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_course);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this); // 지도가 준비되면 onMapReady 호출됨
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        // 초기 위치: 서울 시청 근처 (테스트용, 나중에 실제 GPS 위치로 교체)
        LatLng seoul = new LatLng(37.5665, 126.9780);
        map.moveCamera(
                com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(seoul, 15)
        );
    }
}
