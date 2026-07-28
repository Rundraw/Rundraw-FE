package com.example.rundraw_fe;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RankingActivity extends AppCompatActivity {
    private TextView rankingTab;
    private TextView collectTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        rankingTab = findViewById(R.id.rankingTab);
        collectTab = findViewById(R.id.collectTab);

        // 최초 화면
        if (savedInstanceState == null) {
            showRankingFragment();
        }
        // 랭킹 클릭
        rankingTab.setOnClickListener(v -> {
            showRankingFragment();
        });
        // 모아보기 클릭
        collectTab.setOnClickListener(v -> {
            showCollectFragment();
        });
    }

    private void showRankingFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.courseContainer,
                        new RankingFragment()
                )
                .commit();
    }

    private void showCollectFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.courseContainer,
                        new CollectFragment()
                )
                .commit();
    }
}