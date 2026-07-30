package com.example.rundraw_fe.mypage;

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

public class MyPageScrapActivity extends AppCompatActivity {

    private final List<String> courseList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage_scrap);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        RecyclerView rvCourses = findViewById(R.id.rvCourses);
        MyPageCourseAdapter adapter = new MyPageCourseAdapter(courseList, MyPageCourseAdapter.MODE_COUNT);
        rvCourses.setLayoutManager(new LinearLayoutManager(this));
        rvCourses.setAdapter(adapter);

        // TODO: MypageService API 연동 - GET /mypage/scraps
        // courseList.addAll(response.getScraps());
        // adapter.notifyDataSetChanged();

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