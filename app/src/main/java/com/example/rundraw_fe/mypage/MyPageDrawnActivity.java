package com.example.rundraw_fe.mypage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rundraw_fe.R;

import java.util.ArrayList;
import java.util.List;

public class MyPageDrawnActivity extends AppCompatActivity {

    private final List<String> courseList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage_drawn);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        RecyclerView rvCourses = findViewById(R.id.rvCourses);
        MyPageCourseAdapter adapter = new MyPageCourseAdapter(courseList, MyPageCourseAdapter.MODE_ICON);
        rvCourses.setLayoutManager(new LinearLayoutManager(this));
        rvCourses.setAdapter(adapter);

        // 어댑터 아이템 클릭 시 상세 화면(CourseDetailActivity)으로 이동하도록 리스너 추가
        adapter.setOnItemClickListener((position, courseName) -> {
            Intent intent = new Intent(MyPageDrawnActivity.this, CourseDetailActivity.class);
            intent.putExtra("courseName", courseName);
            startActivity(intent);
        });

        // TODO: MypageService API 연동 - GET /mypage/drawn-courses
        // courseList.addAll(response.getDrawnCourses());

        // 서버에서 데이터를 아직 못 불렀거나 리스트가 텅 비어있을 때만 테스트용 더미 추가
        if (courseList.isEmpty()) {
            courseList.add("얼굴 코스 (더미)");
            courseList.add("한강 야경 코스(더미)");
            courseList.add("우리 동네 산책 코스(더미)");
            courseList.add("공원 러닝 코스(더미)");
        }

        adapter.notifyDataSetChanged();

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        LinearLayout navRanking = findViewById(R.id.navRanking);
        LinearLayout navHome = findViewById(R.id.navHome);
        LinearLayout navCourseSetting = findViewById(R.id.navCourseSetting);
        LinearLayout navMyPage = findViewById(R.id.navMyPage);

        navRanking.setOnClickListener(v -> { /* TODO */ });
        navHome.setOnClickListener(v -> { /* TODO */ });
        navCourseSetting.setOnClickListener(v -> { /* TODO */ });
        navMyPage.setOnClickListener(v -> finish());
    }
}