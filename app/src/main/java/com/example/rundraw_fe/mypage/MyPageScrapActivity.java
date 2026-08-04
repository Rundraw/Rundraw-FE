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
import com.example.rundraw_fe.response.ScrapCourseListResponse;
import com.example.rundraw_fe.response.ScrapCourseResponse;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import com.example.rundraw_fe.HomeActivity;
import com.example.rundraw_fe.RankingActivity;
import com.example.rundraw_fe.DrawCourseActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPageScrapActivity extends AppCompatActivity {

    private final List<ScrapCourseResponse> courseList = new ArrayList<>();
    private MyPageCourseAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage_scrap);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        RecyclerView rvCourses = findViewById(R.id.rvCourses);
        adapter = new MyPageCourseAdapter(courseList, MyPageCourseAdapter.MODE_COUNT);
        rvCourses.setLayoutManager(new LinearLayoutManager(this));
        rvCourses.setAdapter(adapter);

        loadScraps();
        setupBottomNavigation();
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