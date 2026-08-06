package com.example.rundraw_fe.mypage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rundraw_fe.BaseActivity;
import com.example.rundraw_fe.R;
import com.example.rundraw_fe.api.MypageApiService;
import com.example.rundraw_fe.auth.RetrofitClient;
import com.example.rundraw_fe.response.ApiResponse;
import com.example.rundraw_fe.response.DraftCourseListResponse;
import com.example.rundraw_fe.response.DraftCourseResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPageDrawnActivity extends BaseActivity {

    private static final String TAG = "MYPAGE_DRAWN";

    private final List<DraftCourseResponse> courseList = new ArrayList<>();
    private MyPageCourseAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_mypage_drawn);

        RecyclerView rvCourses = findViewById(R.id.rvCourses);
        adapter = new MyPageCourseAdapter(courseList, MyPageCourseAdapter.MODE_ICON);
        rvCourses.setLayoutManager(new LinearLayoutManager(this));
        rvCourses.setAdapter(adapter);

        // 어댑터 아이템 클릭 시 상세 화면(CourseDetailActivity)으로 이동
        adapter.setOnItemClickListener((position, courseName) -> {
            if (position >= 0 && position < courseList.size()) {
                DraftCourseResponse clickedItem = courseList.get(position);
                Long courseId = clickedItem.getDraftCourseId(); // 👈 Response 클래스에 정의된 이름인 getDraftCourseId()로 변경!

                // 디버깅용 로그: courseId가 실제로 넘어가는지 확인
                Log.d(TAG, "클릭한 코스 courseId = " + courseId + ", courseName = " + courseName);

                if (courseId == null) {
                    Log.e(TAG, "courseId가 null입니다. 서버 응답에 draftCourseId가 없는지 확인 필요");
                    return;
                }

                Intent intent = new Intent(MyPageDrawnActivity.this, MyPageCourseDetailActivity.class);
                intent.putExtra("courseId", courseId);
                intent.putExtra("courseName", courseName);
                startActivity(intent);
            }
        });

        loadDrawnCourses();
        setupBottomNavigation(R.id.navigation_my);
    }

    private void loadDrawnCourses() {
        MypageApiService apiService = RetrofitClient.getInstance(this)
                .create(MypageApiService.class);

        apiService.getDrawnCourses().enqueue(new Callback<ApiResponse<DraftCourseListResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<DraftCourseListResponse>> call,
                                   Response<ApiResponse<DraftCourseListResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    courseList.clear();
                    // 백엔드 명세의 리스트 이름에 맞춰 호출 (예시: getDraftCourses 또는 getDrawnCourses)
                    if (response.body().getResult() != null && response.body().getResult().getDraftCourses() != null) {
                        courseList.addAll(response.body().getResult().getDraftCourses());
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<DraftCourseListResponse>> call, Throwable t) {
                // 에러 처리
            }
        });
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