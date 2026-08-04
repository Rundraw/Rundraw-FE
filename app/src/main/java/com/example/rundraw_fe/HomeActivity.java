package com.example.rundraw_fe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class HomeActivity extends BaseActivity {

    private TextView tvRestaurantName;
    private TextView tvRestaurantAddress;
    private TextView tvRestaurantCourse;
    private TextView tvRestaurantRating;

    private LinearLayout sectionMyRoute;
    private LinearLayout sectionMyRestaurant;
    private LinearLayout sectionGpsArt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_home);

        initViews();
        loadSavedRestaurant();
        setupSectionClickListeners();
        setupBottomNavigation(R.id.navigation_home);
    }

    private void initViews() {
        tvRestaurantName = findViewById(R.id.tvRestaurantName);
        tvRestaurantAddress = findViewById(R.id.tvRestaurantAddress);
        tvRestaurantCourse = findViewById(R.id.tvRestaurantCourse);
        tvRestaurantRating = findViewById(R.id.tvRestaurantRating);

        sectionMyRoute = findViewById(R.id.sectionMyRoute);
        sectionMyRestaurant = findViewById(R.id.sectionMyRestaurant);
        sectionGpsArt = findViewById(R.id.sectionGpsArt);
    }

    private void setupSectionClickListeners() {
        sectionMyRoute.setOnClickListener(v -> {
            // TODO: 내 경로 목록 화면 아직 없음 - 만들어지면 연결
            // startActivity(new Intent(HomeActivity.this, MyRouteListActivity.class));
        });

        sectionMyRestaurant.setOnClickListener(v -> {
            // TODO: 저장한 맛집 목록 화면 아직 없음 - 만들어지면 연결
            // startActivity(new Intent(HomeActivity.this, MyRestaurantListActivity.class));
        });

        sectionGpsArt.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, GpsArtActivity.class));
        });
    }

    // TODO: 실제로는 서버 API 응답으로 채워야 함 (MypageService 쪽 API 붙일 자리)
    private void loadSavedRestaurant() {
        // 데이터 연동 전까지는 비워둠
        // tvRestaurantName.setText(restaurant.getName());
        // tvRestaurantAddress.setText(restaurant.getAddress());
        // tvRestaurantCourse.setText(restaurant.getCourse());
        // tvRestaurantRating.setText(restaurant.getRating());
    }
}