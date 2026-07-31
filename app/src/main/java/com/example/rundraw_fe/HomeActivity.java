package com.example.rundraw_fe;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class HomeActivity extends BaseActivity {

    private TextView tvRestaurantName;
    private TextView tvRestaurantAddress;
    private TextView tvRestaurantCourse;
    private TextView tvRestaurantRating;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_home);

        initViews();
        loadSavedRestaurant();
        setupBottomNavigation(R.id.navigation_home);
    }

    private void initViews() {
        tvRestaurantName = findViewById(R.id.tvRestaurantName);
        tvRestaurantAddress = findViewById(R.id.tvRestaurantAddress);
        tvRestaurantCourse = findViewById(R.id.tvRestaurantCourse);
        tvRestaurantRating = findViewById(R.id.tvRestaurantRating);
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