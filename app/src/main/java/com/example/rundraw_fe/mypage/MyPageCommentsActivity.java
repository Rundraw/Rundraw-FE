package com.example.rundraw_fe.mypage;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rundraw_fe.R;

import java.util.ArrayList;
import java.util.List;

public class MyPageCommentsActivity extends AppCompatActivity {

    private RecyclerView rvComments;
    private MyPageCommentAdapter adapter;
    private final List<String> commentList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage_comments);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        rvComments = findViewById(R.id.rvComments);
        adapter = new MyPageCommentAdapter(commentList);
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        rvComments.setAdapter(adapter);

        loadComments();
        setupBottomNavigation();
    }

    // TODO: MypageService API 연동 - GET /mypage/comments
    private void loadComments() {
        // commentList.addAll(response.getComments());
        // adapter.notifyDataSetChanged();
    }

    private void setupBottomNavigation() {
        LinearLayout navRanking = findViewById(R.id.navRanking);
        LinearLayout navHome = findViewById(R.id.navHome);
        LinearLayout navCourseSetting = findViewById(R.id.navCourseSetting);
        LinearLayout navMyPage = findViewById(R.id.navMyPage);

        navRanking.setOnClickListener(v -> { /* TODO */ });
        navHome.setOnClickListener(v -> { /* TODO */ });
        navCourseSetting.setOnClickListener(v -> { /* TODO */ });
        navMyPage.setOnClickListener(v -> finish());
    }
}