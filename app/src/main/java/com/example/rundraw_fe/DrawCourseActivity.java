package com.example.rundraw_fe;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class DrawCourseActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST = 1001;

    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationClient;
    private final List<LatLng> waypoints = new ArrayList<>(); // 그린 좌표들을 순서대로 저장
    private Polyline currentPolyline; // 현재 그려진 선 (매번 지우고 다시 그리기 위해 참조 유지)

    private EditText courseNameInput;
    private TextView distanceText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_course);

        courseNameInput = findViewById(R.id.courseNameInput);
        distanceText = findViewById(R.id.distanceText);
        Button saveButton = findViewById(R.id.saveButton);
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this); // 지도가 준비되면 onMapReady 호출됨

        saveButton.setOnClickListener(v -> {
            // 다음 단계(서버 저장 API 연동)에서 채울 부분
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        moveToCurrentLocation();

        map.setOnMapClickListener(latLng -> {
            waypoints.add(latLng);
            map.addMarker(new MarkerOptions().position(latLng));
            redrawPolyline();
            updateDistanceText();
        });
    }

    private void moveToCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 16));
            } else {
                // 위치를 못 가져온 경우(에뮬레이터 등) 서울로 대체
                LatLng seoul = new LatLng(37.5665, 126.9780);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 15));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                          @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            moveToCurrentLocation();
        }
    }

    private void redrawPolyline() {
        if (currentPolyline != null) currentPolyline.remove();
        currentPolyline = map.addPolyline(new PolylineOptions()
                .addAll(waypoints)
                .color(0xFF1A4D3A)  // 다크그린
                .width(10f)
        );
    }

    private void updateDistanceText() {
        double totalMeters = 0;
        for (int i = 1; i < waypoints.size(); i++) {
            float[] result = new float[1];
            android.location.Location.distanceBetween(
                    waypoints.get(i - 1).latitude, waypoints.get(i - 1).longitude,
                    waypoints.get(i).latitude, waypoints.get(i).longitude, result);
            totalMeters += result[0];
        }
        distanceText.setText(String.format("%.2f km", totalMeters / 1000));
    }
}
