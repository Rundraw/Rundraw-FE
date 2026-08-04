package com.example.rundraw_fe.mypage;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.rundraw_fe.R;

import java.util.Locale;

public class CourseResultActivity extends AppCompatActivity {

    private TextView tvDistance;
    private TextView tvDuration;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ★ 올바른 레이아웃 파일명(activity_course_record)으로 연결합니다.
        setContentView(R.layout.activity_course_record);

        // 1. 툴바 뒤로가기 버튼 처리
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        // 2. 레이아웃 뷰 연결
        tvDistance = findViewById(R.id.tvDistance);
        tvDuration = findViewById(R.id.tvDuration);

        // 3. 이전 화면(목록)에서 넘어온 데이터(Intent)가 있는지 확인
        double extraDistance = getIntent().getDoubleExtra("distance", -1.0);
        long extraDuration = getIntent().getLongExtra("duration", -1L);

        // 4. 데이터 존재 여부에 따른 분기 처리 (실제 데이터 vs 더미 데이터)
        if (extraDistance >= 0 && extraDuration >= 0) {
            String distanceStr = String.format(Locale.getDefault(), "%.2f km", extraDistance);
            tvDistance.setText(distanceStr);
            tvDuration.setText(formatDuration(extraDuration));
        } else {
            setDummyData();
        }
    }

    /**
     * 데이터가 없을 때 보여줄 더미 데이터 설정 메서드
     */
    private void setDummyData() {
        double dummyDistance = 4.36;
        long dummyDurationSec = 221; // 3분 41초

        String distanceStr = String.format(Locale.getDefault(), "%.2f km", dummyDistance);
        if (tvDistance != null) {
            tvDistance.setText(distanceStr);
        }
        if (tvDuration != null) {
            tvDuration.setText(formatDuration(dummyDurationSec));
        }
    }

    // 초(sec)를 "MM:SS" 형태의 문자열로 바꿔주는 헬퍼 메서드
    private String formatDuration(long totalSeconds) {
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }
}