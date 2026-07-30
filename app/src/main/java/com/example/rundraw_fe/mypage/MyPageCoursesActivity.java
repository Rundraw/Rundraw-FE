package com.example.rundraw_fe.mypage;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rundraw_fe.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MyPageCoursesActivity extends AppCompatActivity {

    private RecyclerView rvCourses;
    private MyPageCourseAdapter adapter;
    private final List<String> courseList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage_courses);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        rvCourses = findViewById(R.id.rvCourses);
        adapter = new MyPageCourseAdapter(courseList, MyPageCourseAdapter.MODE_STATUS_DOT);
        rvCourses.setLayoutManager(new LinearLayoutManager(this));
        rvCourses.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                loadCourses(tab.getPosition() == 0); // 0: 완주, 1: 체험
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        loadCourses(true);
        setupBottomNavigation();
    }

    // TODO: MypageService API 연동 - GET /mypage/courses?type=completed|attempted
    private void loadCourses(boolean isCompleted) {
        courseList.clear();
        // courseList.addAll(response.getCourses());
        adapter.notifyDataSetChanged();
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