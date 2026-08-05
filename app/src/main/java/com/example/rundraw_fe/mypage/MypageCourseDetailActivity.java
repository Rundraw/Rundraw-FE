package com.example.rundraw_fe.mypage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rundraw_fe.R;
// 만약 내비게이션 화면 클래스가 있다면 임포트하세요.
// import com.example.rundraw_fe.navigation.NavigationActivity;

public class MypageCourseDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_course_detail); // 본인이 만든 레이아웃 xml 이름

        EditText editCourseTitle = findViewById(R.id.editCourseTitle);
        TextView textCourseLength = findViewById(R.id.textCourseLength);
        Button btnStartNavigation = findViewById(R.id.btnStartNavigation);
        ImageView imgSettingBtn = findViewById(R.id.imgSettingBtn);
        ImageView imgShareBtn = findViewById(R.id.imgShareBtn);

        // 이전 화면(내가 그린 코스)에서 넘겨준 코스 이름 받아오기
        String courseName = getIntent().getStringExtra("courseName");
        if (courseName != null) {
            editCourseTitle.setText(courseName);
        }

        // 1. '경로 안내' 버튼 클릭 시 -> 경로 안내 화면으로 이동
        btnStartNavigation.setOnClickListener(v -> {
            Toast.makeText(this, "경로 안내 화면으로 이동합니다.", Toast.LENGTH_SHORT).show();
            // TODO: 실제 경로 안내 Activity로 띄우는 Intent 작성
            // Intent intent = new Intent(CourseDetailActivity.this, NavigationActivity.class);
            // startActivity(intent);
        });

        // 2. 상단 '톱니바퀴(설정)' 아이콘 클릭 시 -> 코스 설정 화면(CourseSettingActivity)으로 이동
        imgSettingBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MypageCourseDetailActivity.this, CourseSettingActivity.class);
            // 필요하다면 코스 이름도 함께 넘겨줄 수 있습니다.
            intent.putExtra("courseName", courseName);
            startActivity(intent);
        });

        imgShareBtn.setOnClickListener(v -> {
            Toast.makeText(this, "공유 기능", Toast.LENGTH_SHORT).show();
        });
    }
}