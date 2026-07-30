package com.example.rundraw_fe.mypage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rundraw_fe.R;

public class MyPageActivity extends AppCompatActivity {

    private TextView tvNickname;
    private TextView tvLogout;
    private TextView btnMyComments;
    private TextView btnMyScrap;
    private TextView btnMyCourses;
    private TextView btnMyDrawn;

    private LinearLayout navRanking, navHome, navCourseSetting, navMyPage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        initViews();
        loadMemberInfo();
        setupMenuClicks();
        setupBottomNavigation();
    }

    private void initViews() {
        tvNickname = findViewById(R.id.tvNickname);
        tvLogout = findViewById(R.id.tvLogout);
        btnMyComments = findViewById(R.id.btnMyComments);
        btnMyScrap = findViewById(R.id.btnMyScrap);
        btnMyCourses = findViewById(R.id.btnMyCourses);
        btnMyDrawn = findViewById(R.id.btnMyDrawn);

        navRanking = findViewById(R.id.navRanking);
        navHome = findViewById(R.id.navHome);
        navCourseSetting = findViewById(R.id.navCourseSetting);
        navMyPage = findViewById(R.id.navMyPage);
    }

    // TODO: MypageService API 연동 후 실제 닉네임 채우기
    private void loadMemberInfo() {
        // tvNickname.setText(member.getNickname());
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

        tvLogout.setOnClickListener(v -> {
            // TODO: 로그아웃 처리 (토큰 삭제, 로그인 화면 이동 등)
        });
    }

    private void setupBottomNavigation() {
        navRanking.setOnClickListener(v -> {
            // startActivity(new Intent(this, RankingActivity.class));
        });
        navHome.setOnClickListener(v -> {
            // startActivity(new Intent(this, HomeActivity.class));
        });
        navCourseSetting.setOnClickListener(v -> {
            // TODO
        });
        navMyPage.setOnClickListener(v -> {
            // 이미 마이페이지
        });
    }
}