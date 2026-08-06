package com.example.rundraw_fe.mypage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rundraw_fe.R;
import com.example.rundraw_fe.api.CourseApiService;
import com.example.rundraw_fe.auth.RetrofitClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPageCourseDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "COURSE_DETAIL";

    private Long courseId = 0L;
    private EditText editCourseTitle;
    private TextView textCourseLength;

    private GoogleMap mMap;
    private boolean isMapReady = false;
    private final List<LatLng> coursePoints = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_course_detail);

        editCourseTitle = findViewById(R.id.editCourseTitle);
        textCourseLength = findViewById(R.id.textCourseLength);
        Button btnStartNavigation = findViewById(R.id.btnStartNavigation);
        ImageView imgSettingBtn = findViewById(R.id.imgSettingBtn);
        ImageView imgShareBtn = findViewById(R.id.imgShareBtn);
        ImageView imgBackBtn = findViewById(R.id.imgBackBtn);

        courseId = getIntent().getLongExtra("courseId", 0L);
        String courseName = getIntent().getStringExtra("courseName");

        Log.d(TAG, "받은 courseId = " + courseId + ", courseName = " + courseName);

        if (courseName != null) {
            editCourseTitle.setText(courseName);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        imgBackBtn.setOnClickListener(v -> finish());
        btnStartNavigation.setOnClickListener(v -> Toast.makeText(this, "경로 안내 화면으로 이동합니다.", Toast.LENGTH_SHORT).show());

        // 설정 화면으로 이동 (코스 이름, 설명, 난이도 수정 등을 담당)
        imgSettingBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MyPageCourseDetailActivity.this, CourseSettingActivity.class);
            intent.putExtra("courseId", courseId);
            intent.putExtra("courseName", editCourseTitle.getText().toString());
            startActivity(intent);
        });

        imgShareBtn.setOnClickListener(v -> Toast.makeText(this, "공유 기능", Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 설정 화면(CourseSettingActivity)에서 수정하고 돌아왔을 때 변경된 데이터를 반영하기 위해 호출
        loadCourseDetail();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        isMapReady = true;
        drawCourseLine();
    }

    private void loadCourseDetail() {
        Log.d(TAG, "loadCourseDetail() 호출됨, courseId = " + courseId);
        if (courseId == 0L) {
            Log.w(TAG, "courseId가 0이라서 API 호출을 하지 않습니다.");
            return;
        }

        CourseApiService apiService = RetrofitClient.getInstance(this).create(CourseApiService.class);

        apiService.getCourseDraft(courseId).enqueue(new Callback<CourseApiService.CourseDetailResponse>() {
            @Override
            public void onResponse(Call<CourseApiService.CourseDetailResponse> call,
                                   Response<CourseApiService.CourseDetailResponse> response) {
                Log.d(TAG, "API 응답 code = " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    CourseApiService.CourseDetailResponse data = response.body();

                    // 코스 이름 세팅
                    if (data.getName() != null) {
                        editCourseTitle.setText(data.getName());
                    }

                    // 포인트 좌표들을 이용해 지도 선 세팅 및 총 거리 계산
                    List<CourseApiService.DraftPointDto> points = data.getPoints();
                    if (points != null && !points.isEmpty()) {
                        coursePoints.clear();
                        double totalDistanceMeters = 0;

                        for (int i = 0; i < points.size(); i++) {
                            CourseApiService.DraftPointDto point = points.get(i);
                            coursePoints.add(new LatLng(point.getLatitude(), point.getLongitude()));

                            if (i > 0) {
                                float[] result = new float[1];
                                android.location.Location.distanceBetween(
                                        points.get(i - 1).getLatitude(), points.get(i - 1).getLongitude(),
                                        point.getLatitude(), point.getLongitude(),
                                        result);
                                totalDistanceMeters += result[0];
                            }
                        }

                        // 거리 표시 (km 단위, 소수점 2자리)
                        double distanceKm = totalDistanceMeters / 1000.0;
                        textCourseLength.setText(String.format(Locale.getDefault(), "%.2fkm", distanceKm));

                        // 지도가 이미 준비되어 있으면 바로 그림
                        if (isMapReady) {
                            drawCourseLine();
                        }
                    } else {
                        Log.w(TAG, "포인트 데이터가 비어있습니다.");
                    }
                } else {
                    Log.e(TAG, "상세 정보 불러오기 실패: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<CourseApiService.CourseDetailResponse> call, Throwable t) {
                Log.e(TAG, "상세 정보 네트워크 오류", t);
            }
        });
    }

    private void drawCourseLine() {
        if (mMap == null || coursePoints.isEmpty()) return;

        mMap.clear();

        PolylineOptions polylineOptions = new PolylineOptions()
                .addAll(coursePoints)
                .width(12f)
                .color(getResources().getColor(android.R.color.holo_blue_dark));

        mMap.addPolyline(polylineOptions);

        if (coursePoints.size() == 1) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coursePoints.get(0), 15f));
        } else {
            com.google.android.gms.maps.model.LatLngBounds.Builder boundsBuilder =
                    new com.google.android.gms.maps.model.LatLngBounds.Builder();
            for (LatLng point : coursePoints) {
                boundsBuilder.include(point);
            }
            try {
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 100));
            } catch (IllegalStateException e) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coursePoints.get(0), 15f));
            }
        }
    }
}