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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_mypage_drawn);

        RecyclerView rvCourses = findViewById(R.id.rvCourses);
        adapter = new MyPageCourseAdapter(courseList, MyPageCourseAdapter.MODE_ICON);
        rvCourses.setLayoutManager(new LinearLayoutManager(this));
        rvCourses.setAdapter(adapter);

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
                    courseList.addAll(response.body().getResult().getDraftCourses());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<DraftCourseListResponse>> call, Throwable t) {}
        });
    }
}