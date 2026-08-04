package com.example.rundraw_fe.mypage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rundraw_fe.R;

public class CourseSettingActivity extends AppCompatActivity {

    private EditText etSettingCourseName, etSettingCourseDesc, etRestaurantSearch;
    private Button btnLevelHigh, btnLevelMid, btnLevelLow, btnSaveCourse, btnDeleteCourse;
    private ImageView btnAddRestaurant, btnRemoveRestaurant;

    // 코스 ID 변수 (필요에 따라 인텐트나 서버에서 받아온 값으로 사용하세요)
    private long courseId = 1L; // 예시 ID

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_setting);

        // 뷰 초기화
        etSettingCourseName = findViewById(R.id.etSettingCourseName);
        etSettingCourseDesc = findViewById(R.id.etSettingCourseDesc);
        etRestaurantSearch = findViewById(R.id.etRestaurantSearch);
        btnLevelHigh = findViewById(R.id.btnLevelHigh);
        btnLevelMid = findViewById(R.id.btnLevelMid);
        btnLevelLow = findViewById(R.id.btnLevelLow);
        btnSaveCourse = findViewById(R.id.btnSaveCourse);
        btnDeleteCourse = findViewById(R.id.btnDeleteCourse);
        btnAddRestaurant = findViewById(R.id.btnAddRestaurant);
        btnRemoveRestaurant = findViewById(R.id.btnRemoveRestaurant);

        // 이전 화면에서 코스 이름이 넘어왔다면 세팅하기
        String courseName = getIntent().getStringExtra("courseName");
        if (courseName != null) {
            etSettingCourseName.setText(courseName);
        }

        // 난이도 버튼 클릭 이벤트
        btnLevelHigh.setOnClickListener(v -> Toast.makeText(this, "상급 코스로 선택되었습니다.", Toast.LENGTH_SHORT).show());
        btnLevelMid.setOnClickListener(v -> Toast.makeText(this, "중급 코스로 선택되었습니다.", Toast.LENGTH_SHORT).show());
        btnLevelLow.setOnClickListener(v -> Toast.makeText(this, "하급 코스로 선택되었습니다.", Toast.LENGTH_SHORT).show());

        // ✨ 맛집 추가 버튼 (+) 클릭 시 -> RestaurantAddActivity로 이동하며 courseId 전달
        btnAddRestaurant.setOnClickListener(v -> {
            Intent intent = new Intent(CourseSettingActivity.this, RestaurantAddActivity.class);
            intent.putExtra("courseId", courseId); // 어떤 코스에 달릴 맛집인지 ID 전달
            startActivity(intent);
        });

        // 맛집 삭제 버튼 (-) 클릭 시
        btnRemoveRestaurant.setOnClickListener(v -> {
            Toast.makeText(this, "맛집 삭제 API 연동 지점", Toast.LENGTH_SHORT).show();
            // TODO: DELETE /api/restaurants/{id} API 호출 로직 작성
        });

        // 경로 저장하기 버튼
        btnSaveCourse.setOnClickListener(v -> {
            Toast.makeText(this, "코스 설정이 저장되었습니다.", Toast.LENGTH_SHORT).show();
            finish();
        });

        // 삭제하기 버튼
        btnDeleteCourse.setOnClickListener(v -> {
            Toast.makeText(this, "코스가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}