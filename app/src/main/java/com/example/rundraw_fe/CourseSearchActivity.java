package com.example.rundraw_fe;

import android.content.Intent;
import android.os.Bundle;

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

public class CourseSearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_search);

        String keyword = getIntent().getStringExtra("keyword");

        RecyclerView listView = findViewById(R.id.searchResultView);
        listView.setLayoutManager(new LinearLayoutManager(this));
        CourseAdapter adapter = new CourseAdapter(course -> {
            Intent intent = new Intent(this, CourseDetailActivity.class);
            intent.putExtra("courseId", course.getCourseId());
            startActivity(intent);
        });
        listView.setAdapter(adapter);

        CourseApiService api = RetrofitClient.getInstance(this).create(CourseApiService.class);
        api.search(keyword, "popular", null, null).enqueue(new Callback<List<CourseApiService.CourseSummaryDto>>() {
            @Override
            public void onResponse(Call<List<CourseApiService.CourseSummaryDto>> call, Response<List<CourseApiService.CourseSummaryDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setItems(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<CourseApiService.CourseSummaryDto>> call, Throwable t) {}
        });
    }
}
