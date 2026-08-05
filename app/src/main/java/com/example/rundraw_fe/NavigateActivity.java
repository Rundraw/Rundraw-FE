package com.example.rundraw_fe;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rundraw_fe.api.CourseApiService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NavigateActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "NavigateActivity_Log";

    private GoogleMap mMap;
    private CourseApiService apiService;

    private EditText etStart;
    private EditText etDestination;
    private TextView tvDistance;
    private TextView tvArrivalTime;
    private Button btnStartNavigation;
    private LinearLayout layoutLoading;

    private Long courseDraftId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate_course);

        etStart = findViewById(R.id.etStart);
        etDestination = findViewById(R.id.etDestination);
        tvDistance = findViewById(R.id.tvDistance);
        tvArrivalTime = findViewById(R.id.tvArrivalTime);
        btnStartNavigation = findViewById(R.id.btnStartNavigation);
        layoutLoading = findViewById(R.id.layoutLoading);

        // 인텐트로 넘어온 ID 받기 (없으면 기본값 1L)
        courseDraftId = getIntent().getLongExtra("courseDraftId", 1L);
        Log.d(TAG, "1. 전달받은 courseDraftId: " + courseDraftId);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(CourseApiService.class);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        btnStartNavigation.setOnClickListener(v -> {
            Log.d(TAG, "경로 안내 시작 버튼 클릭됨!");
            Toast.makeText(this, "경로 안내를 시작합니다. (Course ID: " + courseDraftId + ")", Toast.LENGTH_SHORT).show();

            // TODO: 나중에 RunningActivity로 화면 전환 코드가 필요하다면 여기에 추가하면 돼!
            Intent intent = new Intent(NavigateActivity.this, RunningActivity.class);
            intent.putExtra("courseDraftId", courseDraftId);
            startActivity(intent);
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d(TAG, "2. 지도(onMapReady) 준비 완료됨");

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setZoomGesturesEnabled(true);
        uiSettings.setScrollGesturesEnabled(true);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.5665, 126.9780), 15f));

        if (layoutLoading != null) {
            layoutLoading.setVisibility(View.VISIBLE);
        }

        // 서버 데이터 호출 시작
        loadAndDrawCourse(courseDraftId);
    }

    private void loadAndDrawCourse(Long draftId) {
        Log.d(TAG, "3. 서버로 API 요청 전송 시작 (Draft ID: " + draftId + ")");

        apiService.getCourseDraft(draftId).enqueue(new Callback<CourseApiService.CourseDetailResponse>() {
            @Override
            public void onResponse(Call<CourseApiService.CourseDetailResponse> call, Response<CourseApiService.CourseDetailResponse> response) {
                if (layoutLoading != null) {
                    layoutLoading.setVisibility(View.GONE);
                }

                Log.d(TAG, "4. 서버 응답 도착! 통신 성공 여부(isSuccessful): " + response.isSuccessful());
                Log.d(TAG, "응답 코드: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    CourseApiService.CourseDetailResponse data = response.body();
                    Log.d(TAG, "5. 서버 응답 데이터 바디 존재함. 코스 이름: " + data.getName());

                    List<CourseApiService.DraftPointDto> points = data.getPoints();
                    if (points == null) {
                        Log.e(TAG, "❌ 에러: points 리스트가 아예 NULL입니다!");
                        Toast.makeText(NavigateActivity.this, "포인트 데이터가 NULL입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Log.d(TAG, "6. 불러온 포인트 개수: " + points.size());
                    if (points.isEmpty()) {
                        Log.e(TAG, "❌ 에러: points 리스트가 비어있습니다 (Size: 0)");
                        Toast.makeText(NavigateActivity.this, "불러올 코스 포인트가 없습니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    PolylineOptions polylineOptions = new PolylineOptions()
                            .color(Color.parseColor("#4CAF50"))
                            .width(14f);

                    LatLng firstPoint = null;
                    LatLng lastPoint = null;
                    double totalDistanceMeters = 0;
                    LatLng previousPoint = null;

                    for (int i = 0; i < points.size(); i++) {
                        CourseApiService.DraftPointDto p = points.get(i);
                        Log.d(TAG, "포인트[" + i + "] -> 위도: " + p.getLatitude() + ", 경도: " + p.getLongitude());

                        LatLng latLng = new LatLng(p.getLatitude(), p.getLongitude());
                        polylineOptions.add(latLng);

                        if (i == 0) {
                            firstPoint = latLng;
                        }
                        if (i == points.size() - 1) {
                            lastPoint = latLng;
                        }

                        if (previousPoint != null) {
                            float[] results = new float[1];
                            android.location.Location.distanceBetween(
                                    previousPoint.latitude, previousPoint.longitude,
                                    latLng.latitude, latLng.longitude,
                                    results
                            );
                            totalDistanceMeters += results[0];
                        }
                        previousPoint = latLng;
                    }

                    if (!polylineOptions.getPoints().isEmpty()) {
                        mMap.addPolyline(polylineOptions);
                        Log.d(TAG, "7. 지도에 Polyline 선 그리기 완료!");
                    }

                    // 출발지 세팅
                    if (etStart != null && firstPoint != null) {
                        String startAddr = getKoreanAddress(firstPoint.latitude, firstPoint.longitude);
                        etStart.setText(startAddr);
                        Log.d(TAG, "8. 출발지 텍스트 설정 완료: " + startAddr);
                    }

                    // 목적지 세팅
                    if (etDestination != null) {
                        String destName = data.getName();
                        if (destName == null || destName.trim().isEmpty()) {
                            if (lastPoint != null) {
                                destName = getKoreanAddress(lastPoint.latitude, lastPoint.longitude);
                            }
                        }
                        etDestination.setText((destName != null && !destName.trim().isEmpty()) ? destName : "목적지");
                        Log.d(TAG, "9. 목적지 텍스트 설정 완료: " + etDestination.getText().toString());
                    }

                    // 거리 세팅
                    double distanceKm = totalDistanceMeters / 1000.0;
                    if (tvDistance != null) {
                        tvDistance.setText(String.format(Locale.getDefault(), "%.2f km", distanceKm));
                        Log.d(TAG, "10. 거리 설정 완료: " + distanceKm + " km");
                    }
                    if (tvArrivalTime != null) {
                        tvArrivalTime.setText("13 : 30");
                    }

                    // 카메라 이동
                    if (firstPoint != null) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstPoint, 15f));
                        Log.d(TAG, "11. 카메라 위치 첫 번째 포인트로 이동 완료");
                    }
                } else {
                    // ==========================================
                    // ★ [임시 테스트용] 서버 실패(401 등) 시 화면에 더미 경로를 강제로 띄워주는 코드 구간
                    // 시험해보고 나중에 서버 연동 시 이 else 블록 전체를 삭제하면 됨!
                    // ==========================================
                    Log.w(TAG, "⚠️ 서버 통신 실패 또는 401 발생! 임시 더미 데이터로 화면을 대체합니다.");

                    PolylineOptions dummyPolyline = new PolylineOptions()
                            .color(Color.parseColor("#FF5722")) // 눈에 띄는 주황색 더미 선
                            .width(14f);

                    // 서울시청 주변 가상 좌표 4개
                    LatLng d1 = new LatLng(37.5665, 126.9780);
                    LatLng d2 = new LatLng(37.5675, 126.9790);
                    LatLng d3 = new LatLng(37.5690, 126.9800);
                    LatLng d4 = new LatLng(37.5700, 126.9770);

                    dummyPolyline.add(d1, d2, d3, d4);
                    mMap.addPolyline(dummyPolyline);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(d1, 15f));

                    if (etStart != null) etStart.setText("서울시청 (더미 출발지)");
                    if (etDestination != null) etDestination.setText("덕수궁 돌담길 (더미 목적지)");
                    if (tvDistance != null) tvDistance.setText("1.25 km");
                    if (tvArrivalTime != null) tvArrivalTime.setText("14 : 00");

                    Toast.makeText(NavigateActivity.this, "인증 실패로 인해 더미 경로가 표시됩니다.", Toast.LENGTH_SHORT).show();
                    // ==========================================
                }
            }

            @Override
            public void onFailure(Call<CourseApiService.CourseDetailResponse> call, Throwable t) {
                if (layoutLoading != null) {
                    layoutLoading.setVisibility(View.GONE);
                }

                // ==========================================
                // ★ [임시 테스트용] 네트워크 통신 오류 시에도 더미 데이터로 강제 렌더링
                // 이 역시 나중에 서버 정상화 시 삭제하면 되는 구간!
                // ==========================================
                Log.e(TAG, "❌❌ Retrofit 통신 실패, 더미 데이터로 대체 실행: " + t.getMessage());

                PolylineOptions dummyPolyline = new PolylineOptions()
                        .color(Color.parseColor("#FF5722"))
                        .width(14f);

                LatLng d1 = new LatLng(37.5665, 126.9780);
                LatLng d2 = new LatLng(37.5680, 126.9790);
                dummyPolyline.add(d1, d2);
                mMap.addPolyline(dummyPolyline);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(d1, 15f));

                if (etStart != null) etStart.setText("테스트 출발지");
                if (etDestination != null) etDestination.setText("테스트 목적지");
                if (tvDistance != null) tvDistance.setText("0.80 km");
                if (tvArrivalTime != null) tvArrivalTime.setText("13 : 50");
                // ==========================================

                Toast.makeText(NavigateActivity.this, "통신 오류 발생 (더미 모드 작동)", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getKoreanAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this, Locale.KOREA);
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                if (address.getAddressLine(0) != null) {
                    return address.getAddressLine(0);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "지오코딩 변환 중 예외 발생: " + e.getMessage());
        }
        return String.format(Locale.getDefault(), "위도: %.4f, 경도: %.4f", lat, lng);
    }
}