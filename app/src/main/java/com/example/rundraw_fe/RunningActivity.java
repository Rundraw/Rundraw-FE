package com.example.rundraw_fe;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.rundraw_fe.api.CourseApiService;
import com.example.rundraw_fe.auth.RetrofitClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RunningActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "RunningActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;

    private GoogleMap mMap;
    private Button btnPause, btnResume, btnFinish;
    private LinearLayout layoutPausedButtons;
    private TextView tvElapsedTime, tvRunningDistance, tvPace, tvDestinationName;

    private CourseApiService apiService;
    private CourseApiService.FinishRecordResponse finishResult = null;
    private Long courseDraftId;
    private Long recordId = -1L;
    private int pointSequence = 1;

    private boolean isPaused = false;

    // 타이머 및 시간 계산을 위한 변수들
    private long startTimeMillis = 0L;
    private long pausedDurationMillis = 0L;
    private long pauseStartedAt = 0L;
    private Handler timerHandler = new Handler(Looper.getMainLooper());
    private Runnable timerRunnable;

    // ★ 흐름 끊김 방지: 경로 안내 및 시작 시점의 안전한 더미/기본 좌표 세팅
    private double currentLat = 37.5665;
    private double currentLng = 126.9780;
    private boolean isRealLocationReceived = false; // 실제 GPS가 들어왔는지 체크하는 플래그

    private FusedLocationProviderClient fusedLocationClient;

    // ★ 실시간 위치 추적 및 실시간 경로선(Polyline) 그리기 위한 변수 추가
    private LocationCallback locationCallback;
    private Polyline userRunningPolyline;
    private List<LatLng> userPathPoints = new ArrayList<>();
    private float totalDistanceMeters = 0.0f;
    private Location lastLocation = null;

    // TTS 초기화 + navigation 로드 + 트리거 체크
    private android.speech.tts.TextToSpeech tts;
    private List<CourseApiService.InstructionDto> instructions = new ArrayList<>();
    private final List<Boolean> instructionPlayed = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);

        courseDraftId = getIntent().getLongExtra("courseDraftId", 1L);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        btnPause = findViewById(R.id.btnPause);
        layoutPausedButtons = findViewById(R.id.layoutPausedButtons);
        btnResume = findViewById(R.id.btnResume);
        btnFinish = findViewById(R.id.btnFinish);

        tvElapsedTime = findViewById(R.id.tvElapsedTime);
        tvRunningDistance = findViewById(R.id.tvRunningDistance);
        tvPace = findViewById(R.id.tvPace);
        tvDestinationName = findViewById(R.id.tvDestinationName);

        apiService = RetrofitClient.getInstance(this).create(CourseApiService.class);

        tts = new android.speech.tts.TextToSpeech(this, status -> {
            if (status == android.speech.tts.TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.KOREAN);
            }
        });

        loadNavigation(courseDraftId);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // ★ [로그 추가] 버튼 객체가 널인지, 클릭 리스너가 정상 등록되는지 확인
        if (btnPause != null) {
            Log.d(TAG, "🟢 btnPause 뷰 바인딩 성공, 클릭 리스너 장착 완료");
            btnPause.setOnClickListener(v -> {
                Log.d(TAG, "👉 [클릭 감지] 일시정지(btnPause) 버튼이 눌렸습니다!");
                pauseRecord();
            });
        } else {
            Log.e(TAG, "❌ [에러] btnPause가 null입니다! XML ID를 확인하세요.");
        }

        if (btnResume != null) {
            btnResume.setOnClickListener(v -> {
                Log.d(TAG, "👉 [클릭 감지] 재개(btnResume) 버튼이 눌렸습니다!");
                resumeRecord();
            });
        }

        if (btnFinish != null) {
            btnFinish.setOnClickListener(v -> {
                Log.d(TAG, "👉 [클릭 감지] 종료(btnFinish) 버튼이 눌렸습니다!");
                finishRecord();
            });
        }

        // ★ GPS 실시간 추적 콜백 정의 (3초 간격 위치 수신)
        setupLocationCallback();

        // 러닝 화면 진입 시 코스 기록 시작 API 호출
        startCourseRecord(courseDraftId);
    }

    private void loadNavigation(Long courseId) {
        apiService.getNavigation(courseId).enqueue(new Callback<CourseApiService.NavigationResponse>() {
            @Override
            public void onResponse(Call<CourseApiService.NavigationResponse> call, Response<CourseApiService.NavigationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    instructions = response.body().getInstructions();
                    for (int i = 0; i < instructions.size(); i++) {
                        instructionPlayed.add(false);
                    }
                    Log.d(TAG, "내비게이션 안내 " + instructions.size() + "개 로드 완료");
                }
            }
            @Override
            public void onFailure(Call<CourseApiService.NavigationResponse> call, Throwable t) {
                Log.e(TAG, "내비게이션 로드 실패: " + t.getMessage());
            }
        });
    }

    // ★ 실시간 위치 변경 감지 및 처리 콜백
    private void setupLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (isPaused) return; // 일시정지 중일 경우 위치 갱신 스킵

                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        currentLat = location.getLatitude();
                        currentLng = location.getLongitude();
                        isRealLocationReceived = true;

                        LatLng newPoint = new LatLng(currentLat, currentLng);
                        userPathPoints.add(newPoint);

                        // 1. 실시간 경로 선 업데이트
                        if (userRunningPolyline != null) {
                            userRunningPolyline.setPoints(userPathPoints);
                        }

                        // 2. 누적 이동 거리 및 페이스 실시간 계산
                        if (lastLocation != null) {
                            totalDistanceMeters += lastLocation.distanceTo(location);
                            updateDistanceAndPaceUI();
                        }
                        lastLocation = location;

                        // 3. 백엔드 POST /point 좌표 전송
                        sendCurrentLocation(currentLat, currentLng);
                        checkNavigationTriggers(currentLat, currentLng);
                    }
                }
            }
        };
    }

    private void checkNavigationTriggers(double lat, double lng) {
        for (int i = 0; i < instructions.size(); i++) {
            if (instructionPlayed.get(i)) continue;

            CourseApiService.InstructionDto instruction = instructions.get(i);
            float[] result = new float[1];
            android.location.Location.distanceBetween(
                    lat, lng, instruction.getLatitude(), instruction.getLongitude(), result);

            if (result[0] <= instruction.getTriggerDistanceM()) {
                tts.speak(instruction.getText(), android.speech.tts.TextToSpeech.QUEUE_FLUSH, null, null);
                instructionPlayed.set(i, true);
                Log.d(TAG, "음성 안내 재생: " + instruction.getText());
            }
        }
    }

    // 거리 및 페이스 실시간 UI 반영
    private void updateDistanceAndPaceUI() {
        double distanceKm = totalDistanceMeters / 1000.0;
        if (tvRunningDistance != null) {
            tvRunningDistance.setText(String.format(Locale.getDefault(), "%.2f km", distanceKm));
        }

        long currentMillis = System.currentTimeMillis();
        long elapsedMillis = (currentMillis - startTimeMillis) - pausedDurationMillis;
        double elapsedMinutes = (elapsedMillis / 1000.0) / 60.0;

        if (distanceKm > 0.05 && tvPace != null) { // 50m 이상 이동했을 때 계산
            double paceMinutes = elapsedMinutes / distanceKm;
            int pMin = (int) paceMinutes;
            int pSec = (int) ((paceMinutes - pMin) * 60);
            tvPace.setText(String.format(Locale.getDefault(), "%d:%02d", pMin, pSec));
        }
    }

    // 실시간 GPS 추적 시작 요청
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000)
                    .setMinUpdateIntervalMillis(2000)
                    .build();

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
            Log.d(TAG, "📡 실시간 GPS 추적 시작됨");
        }
    }

    // [API 1] 코스 기록 시작 (POST /api/user/me/course/record)
    private void startCourseRecord(Long draftId) {
        CourseApiService.StartRecordRequest request = new CourseApiService.StartRecordRequest(draftId);

        apiService.startRecord(request).enqueue(new Callback<CourseApiService.StartRecordResponse>() {
            @Override
            public void onResponse(Call<CourseApiService.StartRecordResponse> call, Response<CourseApiService.StartRecordResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    recordId = response.body().getCourseRecordId();

                    Log.d(TAG, "코스 기록 시작 성공! recordId: " + recordId);
                    Toast.makeText(RunningActivity.this, "러닝이 시작되었습니다.", Toast.LENGTH_SHORT).show();

                    // 현재 시스템 시간 기준으로 타이머 시작
                    initTimerWithServerTime(null);

                    // GPS 실시간 추적 시작
                    startLocationUpdates();
                    fetchLastKnownLocation();
                } else {
                    Log.w(TAG, "⚠️ 코스 기록 시작 실패. 코드: " + response.code());
                    try {
                        if (response.errorBody() != null) {
                            Log.w(TAG, "⚠️ Error Body: " + response.errorBody().string()); // ★ 추가
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error body 파싱 실패: " + e.getMessage());
                    }
                    Toast.makeText(RunningActivity.this, "기록 시작에 실패했습니다. 임시 모드로 진행합니다.", Toast.LENGTH_LONG).show();
                    setDummyRunningUI();
                    initTimerWithServerTime(null);
                    startLocationUpdates();
                }
            }

            @Override
            public void onFailure(Call<CourseApiService.StartRecordResponse> call, Throwable t) {
                Log.e(TAG, "❌ 통신 오류: " + t.getClass().getSimpleName() + " - " + t.getMessage(), t);
                Toast.makeText(RunningActivity.this, "서버에 연결할 수 없어 임시 모드로 진행합니다.", Toast.LENGTH_LONG).show();
                setDummyRunningUI();
                initTimerWithServerTime(null);
                startLocationUpdates();
            }
        });
    }

    // 진입 직후 기기의 실제 최근 위치를 가져오는 메서드
    private void fetchLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    currentLat = location.getLatitude();
                    currentLng = location.getLongitude();
                    isRealLocationReceived = true;
                    Log.d(TAG, "실제 GPS 위치 획득 성공: " + currentLat + ", " + currentLng);
                }
            });
        }
    }

    private void initTimerWithServerTime(String startAtStr) {
        startTimeMillis = System.currentTimeMillis();
        startTimer();
    }

    private void startTimer() {
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isPaused) {
                    long currentMillis = System.currentTimeMillis();
                    long elapsedMillis = (currentMillis - startTimeMillis) - pausedDurationMillis;
                    if (elapsedMillis < 0) elapsedMillis = 0;

                    int totalSec = (int) (elapsedMillis / 1000);
                    int minutes = totalSec / 60;
                    int seconds = totalSec % 60;

                    String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

                    // 텍스트 반영을 메인 스레드 안전 영역에서 처리
                    runOnUiThread(() -> {
                        if (tvElapsedTime != null) {
                            tvElapsedTime.setText(timeFormatted);
                        }
                    });
                }
                // 메인 스레드 부하를 줄이도록 1초 뒤 지연 실행 예약
                timerHandler.postDelayed(this, 1000);
            }
        };
        // 최초 실행 예약
        timerHandler.postDelayed(timerRunnable, 1000);
    }

    private void setDummyRunningUI() {
        if (tvRunningDistance != null) tvRunningDistance.setText("3.24 km");
        if (tvPace != null) tvPace.setText("7:18");
        if (tvDestinationName != null) tvDestinationName.setText("덕수궁 돌담길");
    }

    // [API 2] 실시간 위치 저장 (POST /api/user/me/course/point)
    private void sendCurrentLocation(double lat, double lng) {
        if (!isRealLocationReceived) {
            currentLat = lat;
            currentLng = lng;
        }

        if (recordId == null || recordId == -1L) {
            Log.w(TAG, "recordId가 아직 없습니다. 위치 전송 스킵");
            return;
        }

        CourseApiService.PointRequest request = new CourseApiService.PointRequest(recordId, pointSequence++, lat, lng);

        apiService.savePoint(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "위치 전송 성공 [Seq: " + (pointSequence - 1) + "]");

                    if (mMap != null) {
                        LatLng currentLatLng = new LatLng(lat, lng);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17f));
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "위치 전송 실패: " + t.getMessage());
            }
        });
    }

    // [API 3] 코스 기록 일시정지 (POST /api/user/me/course/record/{recordId}/pause)
    private void pauseRecord() {
        Log.d(TAG, "🛠️ pauseRecord() 메서드 진입 시작");

        pauseStartedAt = System.currentTimeMillis();
        isPaused = true;

        // 버튼 가시성 변경 로그
        if (btnPause != null && layoutPausedButtons != null) {
            btnPause.setVisibility(View.GONE);
            layoutPausedButtons.setVisibility(View.VISIBLE);
            Log.d(TAG, "👁️ UI 변경 완료: btnPause 숨김, layoutPausedButtons 표시");
        } else {
            Log.e(TAG, "❌ [에러] 일시정지 관련 버튼 또는 레이아웃 뷰가 null입니다!");
        }

        // 지도 줌인
        if (mMap != null && currentLat != 0.0 && currentLng != 0.0) {
            LatLng currentLatLng = new LatLng(currentLat, currentLng);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18.5f));
            Log.d(TAG, "🗺️ 지도 줌인 실행 완료");
        } else {
            Log.w(TAG, "⚠️ mMap이 null이거나 좌표가 유효하지 않아 줌인 스킵");
        }

        if (recordId == null || recordId == -1L) {
            Log.w(TAG, "⚠️ recordId가 -1이므로 더미 모드로 일시정지 처리됨");
            Toast.makeText(RunningActivity.this, "일시정지 되었습니다. (더미 모드)", Toast.LENGTH_SHORT).show();
            return;
        }

        // 서버 전송용 Request 객체 생성
        CourseApiService.PauseRequest request = new CourseApiService.PauseRequest(recordId, currentLat, currentLng);
        Log.d(TAG, "📡 일시정지 API 호출 시도 [recordId: " + recordId + ", lat: " + currentLat + ", lng: " + currentLng + "]");

        apiService.pauseRecord(recordId, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "✅ 서버 통신 성공: 기록 일시정지 완료");
                } else {
                    Log.e(TAG, "❌ 서버 통신 실패 [Code: " + response.code() + "]");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "❌ 네트워크 오류 발생: " + t.getMessage(), t);
                Toast.makeText(RunningActivity.this, "네트워크 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // [API 4] 코스 기록 재개 (PATCH /api/user/me/course/record/{recordId}/resume)
    private void resumeRecord() {
        Log.d(TAG, "🛠️ resumeRecord() 메서드 진입");
        if (pauseStartedAt > 0) {
            pausedDurationMillis += (System.currentTimeMillis() - pauseStartedAt);
            pauseStartedAt = 0L;
        }
        isPaused = false;
        lastLocation = null; // 재개 시 페이스 재계산 기준점 리셋

        layoutPausedButtons.setVisibility(View.GONE);
        btnPause.setVisibility(View.VISIBLE);

        // 지도 줌 복구
        if (mMap != null && currentLat != 0.0 && currentLng != 0.0) {
            LatLng currentLatLng = new LatLng(currentLat, currentLng);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17f));
        }

        if (recordId == null || recordId == -1L) {
            Toast.makeText(RunningActivity.this, "러닝을 재개합니다! (더미 모드)", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.resumeRecord(recordId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "✅ 기록 재개 성공");
                } else {
                    Log.e(TAG, "❌ 기록 재개 실패 [Code: " + response.code() + "]");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "❌ 재개 통신 오류: " + t.getMessage(), t);
                Toast.makeText(RunningActivity.this, "네트워크 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // [API 5] 코스 기록 종료 (PATCH /api/user/me/course/record/{recordId}/finish)
    private void finishRecord() {
        Log.d(TAG, "🛠️ finishRecord() 메서드 진입");
        if (timerHandler != null && timerRunnable != null) {
            timerHandler.removeCallbacks(timerRunnable);
        }

        // GPS 수신 중단
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }

        // 서버 통신이 유효한 경우 백그라운드로 요청만 던져두기
        if (recordId == null || recordId == -1L) {
            Log.d(TAG, "💡 [더미 모드] recordId가 -1이므로 즉시 팝업을 띄웁니다.");
            try {
                showSuccessDialog();
                Log.d(TAG, "✅ showSuccessDialog() 호출 완료, 예외 없음");
            } catch (Exception e) {
                Log.e(TAG, "🔥🔥🔥 팝업 띄우다가 터짐: " + e.getMessage(), e);
            }
            return;
        }

        Log.d(TAG, "📡 종료 API 호출 시작 [recordId: " + recordId + "]");
        apiService.finishRecord(recordId).enqueue(new Callback<CourseApiService.FinishRecordResponse>() {
            @Override
            public void onResponse(Call<CourseApiService.FinishRecordResponse> call, Response<CourseApiService.FinishRecordResponse> response) {
                Log.d(TAG, "📥 종료 API 응답 수신 - 코드: " + response.code() + ", 성공 여부(isSuccessful): " + response.isSuccessful());

                if (response.isSuccessful() && response.body() != null) {
                    finishResult = response.body(); // ★ 결과 저장
                    double distance = finishResult.getDistanceKm();
                    int durationSec = finishResult.getDurationSec();
                    Log.d(TAG, "✅ 기록 종료 통신 성공 [Distance: " + distance + "km, Time: " + durationSec + "s]");
                    showSuccessDialog();
                } else {
                    Log.e(TAG, "❌ 종료 서버 응답 비정상 [Code: " + response.code() + ", Message: " + response.message() + "]");
                    try {
                        if (response.errorBody() != null) {
                            Log.e(TAG, "❌ Error Body: " + response.errorBody().string());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "❌ Error Body 파싱 실패: " + e.getMessage());
                    }
                    finishResult = null;
                    showSuccessDialog();
                }
            }

            @Override
            public void onFailure(Call<CourseApiService.FinishRecordResponse> call, Throwable t) {
                Log.e(TAG, "❌ 종료 통신 예외 발생 (onFailure): " + t.getMessage(), t);
                showSuccessDialog();
            }
        });
    }

    // ★ 성공 팝업창을 띄워주는 커스텀 오버레이 메서드
    private void showSuccessDialog() {
        Log.d(TAG, "🔎 showSuccessDialog() 진입 (루트 뷰 오버레이 방식)");

        // 1. 액티비티의 최상위 루트 뷰 가져오기
        android.view.ViewGroup rootView = findViewById(android.R.id.content);
        if (rootView == null) {
            Log.e(TAG, "❌ 최상위 루트 뷰를 찾을 수 없습니다.");
            return;
        }

        // 2. 반투명 배경(딤드 처리)을 위한 컨테이너 레이아웃 생성
        android.widget.FrameLayout overlayContainer = new android.widget.FrameLayout(this);
        overlayContainer.setLayoutParams(new android.view.ViewGroup.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT
        ));
        overlayContainer.setBackgroundColor(Color.parseColor("#99000000")); // 투명한 검은색 (Dim 배경)
        overlayContainer.setClickable(true); // 배경 터치 시 뒤쪽 지도로 이벤트가 새나가는 것 방지

        // 3. dialog_success.xml 레이아웃 인플레이트
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_success, overlayContainer, false);

        // 레이아웃이 화면 중앙에 오도록 칠드런 파라미터 설정
        android.widget.FrameLayout.LayoutParams params = new android.widget.FrameLayout.LayoutParams(
                android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                android.widget.FrameLayout.LayoutParams.WRAP_CONTENT
        );
        params.gravity = android.view.Gravity.CENTER;
        dialogView.setLayoutParams(params);

        // 4. 버튼 리스너 연결
        Button btnRegisterNow = dialogView.findViewById(R.id.btnRegisterNow);
        if (btnRegisterNow != null) {
            btnRegisterNow.setOnClickListener(v -> {
                rootView.removeView(overlayContainer);
                Toast.makeText(this, "지금 등록하기 클릭", Toast.LENGTH_SHORT).show();

                // ★ RecordDetailActivity로 이동하며 recordId 전달
                Intent intent = new Intent(RunningActivity.this, RecordDetailActivity.class);
                intent.putExtra("recordId", recordId);

                // ★ finish 결과를 직접 전달 (API 재호출 방지)
                if (finishResult != null) {
                    intent.putExtra("distanceKm", finishResult.getDistanceKm());
                    intent.putExtra("durationSec", finishResult.getDurationSec());
                    intent.putExtra("isCompleted", finishResult.isCompleted());
                    if (finishResult.getPoints() != null) {
                        intent.putExtra("pointsJson", new com.google.gson.Gson().toJson(finishResult.getPoints()));
                    }
                }


                startActivity(intent);
                finish();
            });
        }

        Button btnRegisterLater = dialogView.findViewById(R.id.btnRegisterLater);
        if (btnRegisterLater != null) {
            btnRegisterLater.setOnClickListener(v -> {
                rootView.removeView(overlayContainer);
                Toast.makeText(this, "나중에 등록하기 클릭", Toast.LENGTH_SHORT).show();
                finish();
            });
        }

        // 5. 오버레이 컨테이너에 다이얼로그 뷰를 얹고, 루트 뷰에 최종 부착
        overlayContainer.addView(dialogView);
        rootView.addView(overlayContainer);

        Log.d(TAG, "✅ 루트 뷰 오버레이 팝업 부착 완료");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

        // 코스 기본 안내선 (연한 가이드라인)
        LatLng startPoint = new LatLng(37.5665, 126.9780);
        LatLng midPoint = new LatLng(37.5670, 126.9790);
        LatLng endPoint = new LatLng(37.5680, 126.9800);

        PolylineOptions courseLine = new PolylineOptions()
                .add(startPoint)
                .add(midPoint)
                .add(endPoint)
                .color(Color.parseColor("#A5D6A7")) // 연한 초록 가이드
                .width(10f);

        mMap.addPolyline(courseLine);

        // ★ 사용자가 실시간으로 이동하는 경로선 (진한 초록색)
        PolylineOptions userLineOptions = new PolylineOptions()
                .color(Color.parseColor("#4CAF50"))
                .width(14f);
        userRunningPolyline = mMap.addPolyline(userLineOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPoint, 15f));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (mMap != null) {
                        mMap.setMyLocationEnabled(true);
                    }
                    startLocationUpdates();
                }
            } else {
                Toast.makeText(this, "위치 권한이 거부되어 내 위치를 표시할 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timerHandler != null && timerRunnable != null) {
            timerHandler.removeCallbacks(timerRunnable);
        }
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
}