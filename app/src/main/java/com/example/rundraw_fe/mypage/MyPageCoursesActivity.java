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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.view.View;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MyPageCoursesActivity extends BaseActivity {

    private RecyclerView rvCourses;
    private MyPageCourseAdapter adapter;
    private final List<CourseRecordResponse> courseList = new ArrayList<>();
    private TextView tabCompleted;
    private TextView tabExperience;
    private View tabUnderline;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_mypage_courses);

        rvCourses = findViewById(R.id.rvCourses);
        adapter = new MyPageCourseAdapter(courseList, MyPageCourseAdapter.MODE_STATUS_DOT);
        rvCourses.setLayoutManager(new LinearLayoutManager(this));
        rvCourses.setAdapter(adapter);


        tabCompleted = findViewById(R.id.tabCompleted);
        tabExperience = findViewById(R.id.tabExperience);
        tabUnderline = findViewById(R.id.tabUnderline);


// 기본 선택
        loadCourses(true);

        tabCompleted.setOnClickListener(v -> {
            moveUnderline(tabCompleted);
            loadCourses(true);
        });

        tabExperience.setOnClickListener(v -> {
            moveUnderline(tabExperience);
            loadCourses(false);
        });

        loadCourses(true);
        setupBottomNavigation(R.id.navigation_my);
    }

    private void loadCourses(boolean isCompleted) {
        MypageApiService apiService =
                RetrofitClient.getInstance(this)
                        .create(MypageApiService.class);

        apiService.getCourses(isCompleted)
                .enqueue(new Callback<ApiResponse<CourseRecordListResponse>>() {
                    @Override
                    public void onResponse(
                            Call<ApiResponse<CourseRecordListResponse>> call,
                            Response<ApiResponse<CourseRecordListResponse>> response
                    ) {
                        if(response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            courseList.clear();
                            List<CourseRecordResponse> records = response.body().getResult().getCourseRecords();
                            for(CourseRecordResponse course : records) {
                                // 완주 코스
                                if(isCompleted && Boolean.TRUE.equals(course.getIsCompleted())) {
                                    courseList.add(course);
                                }

                                // 체험한 코스
                                if(!isCompleted && Boolean.FALSE.equals(course.getIsCompleted())) {
                                    courseList.add(course);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }


                    @Override
                    public void onFailure(Call<ApiResponse<CourseRecordListResponse>> call, Throwable t) {
                    }
                });
    }
    private void moveUnderline(View target) {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) tabUnderline.getLayoutParams();
        params.startToStart = target.getId();
        params.endToEnd = target.getId();
        tabUnderline.setLayoutParams(params);
    }
}