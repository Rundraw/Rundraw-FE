package com.example.rundraw_fe;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public abstract class BaseActivity extends AppCompatActivity {

    private FrameLayout contentContainer;
    private BottomNavigationView bottomNavigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);

        contentContainer = findViewById(R.id.contentContainer);
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }

    // 각 Activity 화면 삽입
    protected void setContentLayout(int layoutResID) {

        LayoutInflater inflater = LayoutInflater.from(this);

        inflater.inflate(
                layoutResID,
                contentContainer,
                true
        );
    }


    // BottomNavigation 설정
    protected void setupBottomNavigation(int selectedItem) {

        bottomNavigation.setSelectedItemId(selectedItem);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_home) {
                if (!(this instanceof HomeActivity)) {
                    startActivity(new Intent(this, HomeActivity.class));
                    finish();
                }
                return true;
            }

            if (id == R.id.navigation_rank) {
                if (!(this instanceof RankingActivity)) {
                    startActivity(new Intent(this, RankingActivity.class));
                    finish();
                }
                return true;
            }

            if (id == R.id.navigation_route) {
                if (!(this instanceof DrawCourseActivity)) {
                    startActivity(new Intent(this, DrawCourseActivity.class));
                    finish();
                }
                return true;
            }

            if (id == R.id.navigation_my) {
                if (!(this instanceof GpsArtActivity)) {
                    startActivity(new Intent(this, GpsArtActivity.class));
                    finish();
                }
                return true;
            }
            return false;
        });
    }
}