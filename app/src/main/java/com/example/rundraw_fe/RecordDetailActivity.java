package com.example.rundraw_fe;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rundraw_fe.api.CourseApiService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecordDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "RecordDetailActivity";

    private GoogleMap mMap;
    private Long recordId;
    private TextView tvDetailDistance, tvDetailTime, tvDetailAvgPace, tvDetailBestPace;

    // 지도에 그릴 좌표 리스트 (API 응답의 points 데이터)
    private List<LatLng> routePoints = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_detail);

        // 이전 화면(RunningActivity)에서 넘겨준 데이터 받기
        if (getIntent() != null) {
            recordId = getIntent().getLongExtra("recordId", -1L);
        }
        Log.d(TAG, "전달받은 recordId: " + recordId);

        // XML 뷰 아이디 매핑
        tvDetailDistance = findViewById(R.id.tvDetailDistance);
        tvDetailTime = findViewById(R.id.tvDetailTime);
        tvDetailAvgPace = findViewById(R.id.tvDetailAvgPace);
        tvDetailBestPace = findViewById(R.id.tvDetailBestPace);

        // 구글 지도 세팅 연결
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapDetail);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // 데이터 로드 (서버 API 연동 또는 안전한 더미 데이터 Fallback)
        loadRecordResultData();
    }

    private void loadRecordResultData() {
        if (recordId == null || recordId == -1L) {
            // [더미 모드] recordId가 없을 때 기본 예시 데이터 세팅
            Log.w(TAG, "⚠️ 유효한 recordId가 없어 더미 데이터를 로드합니다.");
            setUIData(1.19, 1421);
            setDefaultRoutePoints();
        } else {
            // [서버 연동 모드] 백엔드 API 호출
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:8080")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            CourseApiService apiService = retrofit.create(CourseApiService.class);

            apiService.finishRecord(recordId).enqueue(new Callback<CourseApiService.FinishRecordResponse>() {
                @Override
                public void onResponse(Call<CourseApiService.FinishRecordResponse> call, Response<CourseApiService.FinishRecordResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        CourseApiService.FinishRecordResponse result = response.body();

                        double distance = result.getDistanceKm();
                        int duration = result.getDurationSec();

                        Log.d(TAG, "✅ 서버 데이터 로드 성공 [Distance: " + distance + "km, Duration: " + duration + "s]");
                        setUIData(distance, duration);
                        setDefaultRoutePoints(); // 서버에서 좌표 리스트를 따로 주지 않는 경우 기본 좌표 사용
                    } else {
                        // [더미 Fallback] 서버 응답이 비정상일 경우 앱 터짐 방지용 더미 데이터 출력
                        Log.w(TAG, "⚠️ 서버 응답 비정상 [Code: " + response.code() + "], 안전하게 더미 데이터를 출력합니다.");
                        setUIData(1.19, 1421);
                        setDefaultRoutePoints();
                    }
                }

                @Override
                public void onFailure(Call<CourseApiService.FinishRecordResponse> call, Throwable t) {
                    // [더미 Fallback] 통신 실패(서버 꺼짐 등) 시 더미 데이터 출력
                    Log.e(TAG, "❌ 서버 통신 실패 (" + t.getMessage() + "), 안전하게 더미 데이터를 출력합니다.");
                    setUIData(1.19, 1421);
                    setDefaultRoutePoints();
                }
            });
        }
    }

    // UI에 거리 및 시간(초 -> MM:SS 변환) 적용
    private void setUIData(double distanceKm, int durationSec) {
        if (tvDetailDistance != null) {
            tvDetailDistance.setText(String.format("%.2f km", distanceKm));
        }

        int minutes = durationSec / 60;
        int seconds = durationSec % 60;
        if (tvDetailTime != null) {
            tvDetailTime.setText(String.format("%02d:%02d", minutes, seconds));
        }

        if (distanceKm > 0) {
            double paceMinPerKm = (durationSec / 60.0) / distanceKm;
            int paceMin = (int) paceMinPerKm;
            int paceSec = (int) ((paceMinPerKm - paceMin) * 60);

            if (tvDetailAvgPace != null) {
                tvDetailAvgPace.setText(String.format("%02d'%02d\"", paceMin, paceSec));
            }
            if (tvDetailBestPace != null) {
                tvDetailBestPace.setText(String.format("%02d'%02d\"", paceMin > 1 ? paceMin - 1 : paceMin, paceSec));
            }
        } else {
            if (tvDetailAvgPace != null) tvDetailAvgPace.setText("00'00\"");
            if (tvDetailBestPace != null) tvDetailBestPace.setText("00'00\"");
        }
    }

    // 테스트용 경로 포인트 세팅
    private void setDefaultRoutePoints() {
        routePoints.clear();
        routePoints.add(new LatLng(37.5665, 126.9780)); // sequence 1
        routePoints.add(new LatLng(37.5670, 126.9790)); // sequence 2

        // 만약 지도가 이미 준비되어 있다면 경로를 즉시 그려줌
        if (mMap != null && !routePoints.isEmpty()) {
            drawRouteOnMap();
        }
    }

    private void drawRouteOnMap() {
        PolylineOptions polylineOptions = new PolylineOptions()
                .addAll(routePoints)
                .width(12f)
                .color(0xFF2196F3); // 파란색 선
        mMap.addPolyline(polylineOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(routePoints.get(0), 16f));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (!routePoints.isEmpty()) {
            drawRouteOnMap();
        } else {
            // 데이터가 아직 안 들어왔을 때의 기본 위치 (서울 시청)
            LatLng defaultLocation = new LatLng(37.5665, 126.9780);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15f));
        }
    }
}