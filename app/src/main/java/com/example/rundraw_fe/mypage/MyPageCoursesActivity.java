package com.example.rundraw_fe.mypage;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rundraw_fe.BaseActivity;
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

public class MyPageCoursesActivity extends BaseActivity {

    private RecyclerView rvCourses;
    private MyPageCourseAdapter adapter;
    private final List<String> courseList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_mypage_courses);

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

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        loadCourses(true);
        setupBottomNavigation(R.id.navigation_my);
    }

    private void loadCourses(boolean isCompleted) {
        MypageApiService apiService = RetrofitClient.getInstance(this)
                .create(MypageApiService.class);

        // String type 대신 boolean 값을 그대로 넘겨줍니다. (탭이 0번째면 true, 아니면 false)
        apiService.getCourses(isCompleted).enqueue(new Callback<ApiResponse<CourseRecordListResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<CourseRecordListResponse>> call,
                                   Response<ApiResponse<CourseRecordListResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    courseList.clear();
                    for (CourseRecordResponse course : response.body().getResult().getCourseRecords()) {
                        courseList.add(course.getCourseName());
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CourseRecordListResponse>> call, Throwable t) {
            }
        });
    }
}