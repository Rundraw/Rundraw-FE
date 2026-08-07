package com.example.rundraw_fe.mypage;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rundraw_fe.R;
import com.example.rundraw_fe.api.MypageApiService;
import com.example.rundraw_fe.api.RankingApi;
import com.example.rundraw_fe.auth.RetrofitClient;
import com.example.rundraw_fe.request.CourseSettingReqDTO;
import com.example.rundraw_fe.response.ApiResponse;
import com.example.rundraw_fe.response.CourseDetailResponse;

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

public class CourseSettingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "COURSE_SETTING";

    private EditText etSettingCourseName, etSettingCourseDesc, etRestaurantSearch;
    private TextView tvEditName, tvEditDesc;
    private Button btnLevelHigh, btnLevelMid, btnLevelLow, btnSaveCourse, btnDeleteCourse;
    private ImageView btnAddRestaurant, btnRemoveRestaurant;

    private long courseId = 1L;
    private String selectedLevel = "INTERMEDIATE";

    private GoogleMap mMap;
    private boolean isMapReady = false;
    private final List<LatLng> coursePoints = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "CourseSettingActivity가 실행되었습니다!");

        setContentView(R.layout.activity_course_setting);

        courseId = getIntent().getLongExtra("courseId", 1L);
        Log.d(TAG, "전달받은 courseId = " + courseId);

        etSettingCourseName = findViewById(R.id.etSettingCourseName);
        etSettingCourseDesc = findViewById(R.id.etSettingCourseDesc);
        etRestaurantSearch = findViewById(R.id.etRestaurantSearch);

        tvEditName = findViewById(R.id.tvEditName);
        tvEditDesc = findViewById(R.id.tvEditDesc);

        btnLevelHigh = findViewById(R.id.btnLevelHigh);
        btnLevelMid = findViewById(R.id.btnLevelMid);
        btnLevelLow = findViewById(R.id.btnLevelLow);
        btnSaveCourse = findViewById(R.id.btnSaveCourse);
        btnDeleteCourse = findViewById(R.id.btnDeleteCourse);
        btnAddRestaurant = findViewById(R.id.btnAddRestaurant);
        btnRemoveRestaurant = findViewById(R.id.btnRemoveRestaurant);

        setEditable(etSettingCourseName, false);
        setEditable(etSettingCourseDesc, false);

        tvEditName.setOnClickListener(v -> {
            if (etSettingCourseName.isEnabled()) {
                setEditable(etSettingCourseName, false);
                tvEditName.setText("수정");
            } else {
                setEditable(etSettingCourseName, true);
                tvEditName.setText("완료");
            }
        });

        tvEditDesc.setOnClickListener(v -> {
            if (etSettingCourseDesc.isEnabled()) {
                setEditable(etSettingCourseDesc, false);
                tvEditDesc.setText("수정");
            } else {
                setEditable(etSettingCourseDesc, true);
                tvEditDesc.setText("완료");
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // 👉 불러오기: RankingApi 사용 (points 포함되는 쪽)
        loadExistingCourseData();

        btnLevelHigh.setOnClickListener(v -> {
            selectedLevel = "ADVANCED";
            Toast.makeText(this, "상급 코스로 선택되었습니다.", Toast.LENGTH_SHORT).show();
        });

        btnLevelMid.setOnClickListener(v -> {
            selectedLevel = "INTERMEDIATE";
            Toast.makeText(this, "중급 코스로 선택되었습니다.", Toast.LENGTH_SHORT).show();
        });

        btnLevelLow.setOnClickListener(v -> {
            selectedLevel = "BEGINNER";
            Toast.makeText(this, "하급 코스로 선택되었습니다.", Toast.LENGTH_SHORT).show();
        });

        // 👉 저장: MypageApiService의 PATCH 사용
        btnSaveCourse.setOnClickListener(v -> {
            String title = etSettingCourseName.getText().toString().trim();
            String description = etSettingCourseDesc.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(this, "코스 이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            MypageApiService apiService = RetrofitClient.getInstance(this).create(MypageApiService.class);
            CourseSettingReqDTO requestDto = new CourseSettingReqDTO(title, description, selectedLevel);

            apiService.updateCourse(courseId, requestDto).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(CourseSettingActivity.this, "코스 설정이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(CourseSettingActivity.this, "수정 실패: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(CourseSettingActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // ⚠️ 삭제하기: 아직 실제 서버 호출 없음 (아래 설명 참고)
        btnDeleteCourse.setOnClickListener(v -> {
            Toast.makeText(this, "코스가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        isMapReady = true;
        drawCourseLine();
    }

    /**
     * 서버에서 기존 코스 정보(이름, 설명, 난이도, 경로 좌표)를 불러옴
     * RankingApi 사용 이유: points(경로 좌표)가 이쪽 응답에 포함됨
     */
    private void loadExistingCourseData() {
        Log.d(TAG, "loadExistingCourseData() 호출됨, courseId = " + courseId);
        if (courseId == 0L) return;

        RankingApi rankingApi = RetrofitClient.getInstance(this).create(RankingApi.class);

        rankingApi.getCourseDetail(courseId).enqueue(new Callback<ApiResponse<CourseDetailResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<CourseDetailResponse>> call, Response<ApiResponse<CourseDetailResponse>> response) {
                Log.d(TAG, "API 응답 code = " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    CourseDetailResponse data = response.body().getResult(); // ⚠️ getter명 확인 필요

                    if (data == null) {
                        Log.e(TAG, "응답 body는 있지만 data가 null입니다.");
                        return;
                    }

                    if (data.getName() != null) {
                        etSettingCourseName.setText(data.getName());
                    }

                    if (data.getDescription() != null && !data.getDescription().isEmpty()) {
                        etSettingCourseDesc.setText(data.getDescription());
                    } else if (data.getContent() != null && !data.getContent().isEmpty()) {
                        etSettingCourseDesc.setText(data.getContent());
                    }

                    if (data.getLevelTagName() != null) {
                        selectedLevel = data.getLevelTagName();
                    }

                    List<CourseDetailResponse.Point> points = data.getPoints();
                    if (points != null && !points.isEmpty()) {
                        coursePoints.clear();
                        for (int i = 0; i < points.size(); i++) {
                            CourseDetailResponse.Point point = points.get(i);
                            coursePoints.add(new LatLng(point.getLatitude(), point.getLongitude()));
                        }

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
            public void onFailure(Call<ApiResponse<CourseDetailResponse>> call, Throwable t) {
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

    private void setEditable(EditText editText, boolean enabled) {
        editText.setEnabled(enabled);
        editText.setFocusable(enabled);
        editText.setFocusableInTouchMode(enabled);
        if (enabled) {
            editText.requestFocus();
            editText.setSelection(editText.getText().length());
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
    }
}