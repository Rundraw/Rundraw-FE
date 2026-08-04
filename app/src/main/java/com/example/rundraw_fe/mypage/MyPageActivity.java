package com.example.rundraw_fe.mypage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.rundraw_fe.BaseActivity;
import com.example.rundraw_fe.R;
import com.example.rundraw_fe.api.MemberApi;
import com.example.rundraw_fe.auth.MemberResponse;
import com.example.rundraw_fe.auth.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPageActivity extends BaseActivity {

    private TextView tvNickname;
    private TextView tvLogout;
    private TextView btnMyComments;
    private TextView btnMyScrap;
    private TextView btnMyCourses;
    private TextView btnMyDrawn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_mypage);

        initViews();
        loadMemberInfo();
        setupMenuClicks();
        setupBottomNavigation(R.id.navigation_my);
    }

    private void initViews() {
        tvNickname = findViewById(R.id.tvNickname);
        tvLogout = findViewById(R.id.tvLogout);
        btnMyComments = findViewById(R.id.btnMyComments);
        btnMyScrap = findViewById(R.id.btnMyScrap);
        btnMyCourses = findViewById(R.id.btnMyCourses);
        btnMyDrawn = findViewById(R.id.btnMyDrawn);
    }

    private void loadMemberInfo() {
        MemberApi apiService = RetrofitClient.getInstance(this).create(MemberApi.class);

        apiService.getMyInfo().enqueue(new Callback<MemberResponse>() {
            @Override
            public void onResponse(Call<MemberResponse> call, Response<MemberResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tvNickname.setText(response.body().getName());
                }
            }

            @Override
            public void onFailure(Call<MemberResponse> call, Throwable t) {}
        });
    }

    private void setupMenuClicks() {
        btnMyComments.setOnClickListener(v ->
                startActivity(new Intent(this, MyPageCommentsActivity.class)));

        btnMyScrap.setOnClickListener(v ->
                startActivity(new Intent(this, MyPageScrapActivity.class)));

        btnMyCourses.setOnClickListener(v ->
                startActivity(new Intent(this, MyPageCoursesActivity.class)));

        btnMyDrawn.setOnClickListener(v ->
                startActivity(new Intent(this, MyPageDrawnActivity.class)));

        // TODO: 로그아웃 처리 (카카오 로그인 붙인 후 구현 예정)
        tvLogout.setOnClickListener(v -> {
        });
    }
}