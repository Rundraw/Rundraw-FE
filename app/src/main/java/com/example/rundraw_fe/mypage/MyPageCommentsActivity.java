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
import com.example.rundraw_fe.response.MypageCommentResponse;
import com.example.rundraw_fe.response.MypageCommentListResponse;

import java.util.ArrayList;
import java.util.List;
import android.content.Intent;
import com.example.rundraw_fe.HomeActivity;
import com.example.rundraw_fe.RankingActivity;
import com.example.rundraw_fe.DrawCourseActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPageCommentsActivity extends AppCompatActivity {

    private RecyclerView rvComments;
    private MyPageCommentAdapter adapter;
    private final List<MypageCommentResponse> commentList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage_comments);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        rvComments = findViewById(R.id.rvComments);
        adapter = new MyPageCommentAdapter(commentList);
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        rvComments.setAdapter(adapter);

        loadComments();
        setupBottomNavigation();
    }

    private void loadComments() {
        MypageApiService apiService = RetrofitClient.getInstance(this)
                .create(MypageApiService.class);

        apiService.getComments().enqueue(new Callback<ApiResponse<MypageCommentListResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<MypageCommentListResponse>> call,
                                   Response<ApiResponse<MypageCommentListResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    commentList.clear();
                    commentList.addAll(response.body().getResult().getComments());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<MypageCommentListResponse>> call, Throwable t) {}
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