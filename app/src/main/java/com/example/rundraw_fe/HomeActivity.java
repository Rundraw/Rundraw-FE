package com.example.rundraw_fe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private TextView tvRestaurantName;
    private TextView tvRestaurantAddress;
    private TextView tvRestaurantCourse;
    private TextView tvRestaurantRating;

    private LinearLayout navRanking;
    private LinearLayout navHome;
    private LinearLayout navCourseSetting;
    private LinearLayout navMyPage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initViews();
        loadSavedRestaurant();
        setupBottomNavigation();
    }

    private void initViews() {
        tvRestaurantName = findViewById(R.id.tvRestaurantName);
        tvRestaurantAddress = findViewById(R.id.tvRestaurantAddress);
        tvRestaurantCourse = findViewById(R.id.tvRestaurantCourse);
        tvRestaurantRating = findViewById(R.id.tvRestaurantRating);

        navRanking = findViewById(R.id.navRanking);
        navHome = findViewById(R.id.navHome);
        navCourseSetting = findViewById(R.id.navCourseSetting);
        navMyPage = findViewById(R.id.navMyPage);
    }

    // TODO: 실제로는 서버 API 응답으로 채워야 함 (MypageService 쪽 API 붙일 자리)
    private void loadSavedRestaurant() {
        // 데이터 연동 전까지는 비워둠
        // tvRestaurantName.setText(restaurant.getName());
        // tvRestaurantAddress.setText(restaurant.getAddress());
        // tvRestaurantCourse.setText(restaurant.getCourse());
        // tvRestaurantRating.setText(restaurant.getRating());
    }

    private void setupBottomNavigation() {
        navRanking.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, RankingActivity.class));
        });

        navHome.setOnClickListener(v -> {
            // 이미 홈이므로 아무 동작 없음
        });

        navCourseSetting.setOnClickListener(v -> {
            // TODO: CourseSettingActivity 아직 없음 - 만들어지면 연결
            // startActivity(new Intent(HomeActivity.this, CourseSettingActivity.class));
        });

        navMyPage.setOnClickListener(v -> {
            // TODO: MyPageActivity 아직 없음 - MypageController 쪽 화면 만들어지면 연결
            // startActivity(new Intent(HomeActivity.this, MyPageActivity.class));
        });
    }
}