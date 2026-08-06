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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rundraw_fe.R;
import com.example.rundraw_fe.api.MypageApiService;
import com.example.rundraw_fe.api.RankingApi;
import com.example.rundraw_fe.auth.RetrofitClient;
import com.example.rundraw_fe.request.CourseSettingReqDTO;
import com.example.rundraw_fe.response.ApiResponse;
import com.example.rundraw_fe.response.CourseDetailResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourseSettingActivity extends AppCompatActivity {

    private EditText etSettingCourseName, etSettingCourseDesc, etRestaurantSearch;
    private TextView tvEditName, tvEditDesc; // 수정 버튼 텍스트뷰 추가
    private Button btnLevelHigh, btnLevelMid, btnLevelLow, btnSaveCourse, btnDeleteCourse;
    private ImageView btnAddRestaurant, btnRemoveRestaurant;

    private long courseId = 1L;
    private String selectedLevel = "INTERMEDIATE";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 👇 액티비티가 실행될 때 로그가 찍히는지 확인하기 위한 코드 추가
        Log.d("COURSE_SETTING", "CourseSettingActivity가 실행되었습니다!");

        setContentView(R.layout.activity_course_setting);

        // 뷰 초기화
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

        // 0. 처음에는 수정 못 하도록 EditText 잠그기 (읽기 전용 상태)
        setEditable(etSettingCourseName, false);
        setEditable(etSettingCourseDesc, false);

        // '코스 이름' 수정 버튼 클릭 시
        tvEditName.setOnClickListener(v -> {
            setEditable(etSettingCourseName, true);
            tvEditName.setText("완료");
        });

        // '코스 설명' 수정 버튼 클릭 시
        tvEditDesc.setOnClickListener(v -> {
            setEditable(etSettingCourseDesc, true);
            tvEditDesc.setText("완료");
        });

        // 1. 이전 화면에서 courseId 받기
        courseId = getIntent().getLongExtra("courseId", 1L);
        Log.d("COURSE_SETTING", "전달받은 courseId = " + courseId);

        // 2. 서버에서 기존 코스 정보 불러오기 (이때 기존 설명과 이름이 박스에 셋팅됨)
        loadExistingCourseData();

        // 난이도 버튼 클릭 이벤트
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

        // 경로 수정 및 저장하기 버튼
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

        // 삭제하기 버튼
        btnDeleteCourse.setOnClickListener(v -> {
            Toast.makeText(this, "코스가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
            finish();
        });

    }

    /**
     * EditText 활성화/비활성화 및 키보드 제어 함수
     */
    private void setEditable(EditText editText, boolean enabled) {
        editText.setEnabled(enabled);
        editText.setFocusable(enabled);
        editText.setFocusableInTouchMode(enabled);
        if (enabled) {
            editText.requestFocus();
            // 커서를 문장 맨 끝으로 이동
            editText.setSelection(editText.getText().length());
            // 키보드 올리기
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    /**
     * 서버에서 기존 코스 정보를 GET으로 불러와서 이름, 설명, 난이도를 꽂아주는 함수
     */
    private void loadExistingCourseData() {
        RankingApi rankingApi = RetrofitClient.getInstance(this).create(RankingApi.class);

        rankingApi.getCourseDetail(courseId).enqueue(new Callback<ApiResponse<CourseDetailResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<CourseDetailResponse>> call, Response<ApiResponse<CourseDetailResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CourseDetailResponse data = response.body().getResult();

                    if (data != null) {
                        Log.d("DEBUG_COURSE", "받아온 이름: " + data.getName());
                        Log.d("DEBUG_COURSE", "받아온 description: " + data.getContent());
                        Log.d("DEBUG_COURSE", "받아온 levelTagName: " + data.getLevelType());

                        // 1. 기존 코스 이름 셋팅
                        if (data.getName() != null) {
                            etSettingCourseName.setText(data.getName());
                        }

                        // 2. 기존 설명 셋팅 (서버 응답 데이터 반영)
                        if (data.getContent() != null && !data.getContent().isEmpty()) {
                            etSettingCourseDesc.setText(data.getContent());
                        } else if (data.getContent() != null && !data.getContent().isEmpty()) {
                            etSettingCourseDesc.setText(data.getContent());
                        }

                        // 3. 기존 난이도 셋팅
                        if (data.getLevelType() != null) {
                            selectedLevel = data.getLevelType();
                        } else if (data.getLevelType() != null) {
                            selectedLevel = data.getLevelType();
                        }

                        Log.d("COURSE_SETTING", "기존 데이터 불러오기 성공: " + data.getName());
                    }
                } else {
                    Log.e("COURSE_SETTING", "기존 데이터 불러오기 실패 코드: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CourseDetailResponse>> call, Throwable t) {
                Log.e("COURSE_SETTING", "기존 데이터 불러오기 네트워크 오류", t);
            }
        });
    }
}