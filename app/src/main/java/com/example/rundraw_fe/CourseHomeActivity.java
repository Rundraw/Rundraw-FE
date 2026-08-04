package com.example.rundraw_fe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rundraw_fe.adapter.CourseAdapter;
import com.example.rundraw_fe.auth.RetrofitClient;
import com.example.rundraw_fe.api.CourseApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourseHomeActivity extends AppCompatActivity {

    private CourseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_home);

        RecyclerView listView = findViewById(R.id.courseListView);
        listView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CourseAdapter(course -> {
            Intent intent = new Intent(this, CourseDetailActivity.class);
            intent.putExtra("courseId", course.getCourseId());
            startActivity(intent);
        });
        listView.setAdapter(adapter);

        findViewById(R.id.drawCourseButton).setOnClickListener(v ->
                startActivity(new Intent(this, DrawCourseActivity.class))
        );

        EditText searchInput = findViewById(R.id.searchInput);
        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            String keyword = searchInput.getText().toString();
            if (!keyword.isEmpty()) {
                Intent intent = new Intent(this, CourseSearchActivity.class);
                intent.putExtra("keyword", keyword);
                startActivity(intent);
            }
            return true;
        });

        loadNearbyCourses();
    }

    private void loadNearbyCourses() {
        CourseApiService api = RetrofitClient.getInstance(this).create(CourseApiService.class);
        // 임시로 서울 좌표 고정
        double lat = 37.5665, lng = 126.9780, radius = 5.0;

        api.getByLocation(lat, lng, radius).enqueue(new Callback<List<CourseApiService.CourseSummaryDto>>() {
            @Override
            public void onResponse(Call<List<CourseApiService.CourseSummaryDto>> call, Response<List<CourseApiService.CourseSummaryDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setItems(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<CourseApiService.CourseSummaryDto>> call, Throwable t) {
                // 실패 시 무시하거나 Toast
            }
        });
    }
}
