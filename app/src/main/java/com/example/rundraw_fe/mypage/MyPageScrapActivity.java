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
import com.example.rundraw_fe.response.ScrapCourseListResponse;
import com.example.rundraw_fe.response.ScrapCourseResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPageScrapActivity extends BaseActivity {

    private final List<ScrapCourseResponse> courseList = new ArrayList<>();
    private MyPageCourseAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_mypage_scrap);

        RecyclerView rvCourses = findViewById(R.id.rvCourses);
        adapter = new MyPageCourseAdapter(courseList, MyPageCourseAdapter.MODE_COUNT);
        rvCourses.setLayoutManager(new LinearLayoutManager(this));
        rvCourses.setAdapter(adapter);

        loadScraps();
        setupBottomNavigation(R.id.navigation_my);
    }

    private void loadScraps() {
        MypageApiService apiService = RetrofitClient.getInstance(this)
                .create(MypageApiService.class);

        apiService.getScraps().enqueue(new Callback<ApiResponse<ScrapCourseListResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ScrapCourseListResponse>> call,
                                   Response<ApiResponse<ScrapCourseListResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    courseList.clear();
                    courseList.addAll(response.body().getResult().getScrapCourses());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ScrapCourseListResponse>> call, Throwable t) {}
        });
    }
}