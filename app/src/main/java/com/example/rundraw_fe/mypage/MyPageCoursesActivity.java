package com.example.rundraw_fe.mypage;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rundraw_fe.R;
import com.example.rundraw_fe.api.MypageApiService;
import com.example.rundraw_fe.auth.RetrofitClient;
import com.example.rundraw_fe.response.ApiResponse;
import com.example.rundraw_fe.response.CourseRecordListResponse;
import com.example.rundraw_fe.response.CourseRecordResponse;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import com.example.rundraw_fe.HomeActivity;
import com.example.rundraw_fe.RankingActivity;
import com.example.rundraw_fe.DrawCourseActivity;

public class MyPageCoursesActivity extends AppCompatActivity {

    private RecyclerView rvCourses;
    private MyPageCourseAdapter adapter;
    private final List<CourseRecordResponse> courseList = new ArrayList<>();

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

    private void loadCourses(boolean isCompleted) {
        MypageApiService apiService = RetrofitClient.getInstance(this)
                .create(MypageApiService.class);

        String type = isCompleted ? "completed" : "attempted";

        apiService.getCourses(type).enqueue(new Callback<ApiResponse<CourseRecordListResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<CourseRecordListResponse>> call,
                                   Response<ApiResponse<CourseRecordListResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    courseList.clear();
                    courseList.addAll(response.body().getResult().getCourseRecords());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CourseRecordListResponse>> call, Throwable t) {}
        });
    }

    private void setupBottomNavigation() {
        LinearLayout navRanking = findViewById(R.id.navRanking);
        LinearLayout navHome = findViewById(R.id.navHome);
        LinearLayout navCourseSetting = findViewById(R.id.navCourseSetting);
        LinearLayout navMyPage = findViewById(R.id.navMyPage);

        navRanking.setOnClickListener(v -> {
            startActivity(new Intent(this, RankingActivity.class));
            finish();
        });

        navHome.setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });

        navCourseSetting.setOnClickListener(v -> {
            startActivity(new Intent(this, DrawCourseActivity.class));
            finish();
        });

        navMyPage.setOnClickListener(v -> finish());
    }
}