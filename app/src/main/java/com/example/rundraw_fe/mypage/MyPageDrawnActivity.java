package com.example.rundraw_fe.mypage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rundraw_fe.BaseActivity;
import com.example.rundraw_fe.CourseDetailActivity;
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

    private final List<DraftCourseResponse> courseList = new ArrayList<>();
    private MyPageCourseAdapter adapter;
    private MypageApiService apiService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_mypage_drawn);

        apiService = RetrofitClient.getInstance(this).create(MypageApiService.class);

        RecyclerView rvCourses = findViewById(R.id.rvCourses);
        adapter = new MyPageCourseAdapter(courseList, MyPageCourseAdapter.MODE_ICON);
        rvCourses.setLayoutManager(new LinearLayoutManager(this));
        rvCourses.setAdapter(adapter);

        adapter.setOnItemClickListener((position, courseName) -> {
            DraftCourseResponse draft = courseList.get(position);

            if (draft.getCourseId() != null) {
                // 완주 완료 → 정식 코스 상세 화면
                Intent intent = new Intent(MyPageDrawnActivity.this, CourseDetailActivity.class);
                intent.putExtra("courseId", draft.getCourseId());
                startActivity(intent);
            } else {
                // 완주 전 (그린 코스) → Draft 코스 상세 화면
                Intent intent = new Intent(MyPageDrawnActivity.this, MyPageCourseDetailActivity.class);
                intent.putExtra("courseId", draft.getDraftCourseId()); // MyPageCourseDetailActivity가 받는 키 이름이 "courseId"라 그대로 유지
                intent.putExtra("courseName", courseName);
                startActivity(intent);
            }
        });

        // 공유 아이콘 클릭 시 공유 상태 토글
        adapter.setOnShareClickListener(this::toggleSharing);

        loadDrawnCourses();
        setupBottomNavigation(R.id.navigation_my);
    }

    private void loadDrawnCourses() {
        apiService.getDrawnCourses().enqueue(new Callback<ApiResponse<DraftCourseListResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<DraftCourseListResponse>> call,
                                   Response<ApiResponse<DraftCourseListResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    if (response.body().getResult() != null && response.body().getResult().getDraftCourses() != null) {
                        courseList.clear();
                        courseList.addAll(response.body().getResult().getDraftCourses());
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<DraftCourseListResponse>> call, Throwable t) {
                Toast.makeText(MyPageDrawnActivity.this, "목록을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleSharing(int position) {
        DraftCourseResponse draft = courseList.get(position);

        apiService.toggleDraftSharing(draft.getDraftCourseId())
                .enqueue(new Callback<ApiResponse<Object>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            // 서버 반영 성공 -> 목록 다시 불러와서 최신 상태 표시
                            loadDrawnCourses();
                        } else {
                            // 완주 기록 없음 등 서버에서 막은 경우
                            Toast.makeText(MyPageDrawnActivity.this,
                                    "공유할 수 없습니다. 완주 후 다시 시도해주세요.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                        Toast.makeText(MyPageDrawnActivity.this, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}