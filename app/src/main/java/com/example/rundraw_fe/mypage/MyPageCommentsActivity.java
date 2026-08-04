package com.example.rundraw_fe.mypage;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rundraw_fe.BaseActivity;
import com.example.rundraw_fe.R;
import com.example.rundraw_fe.api.MypageApiService;
import com.example.rundraw_fe.auth.RetrofitClient;
import com.example.rundraw_fe.response.ApiResponse;
import com.example.rundraw_fe.response.MypageCommentResponse;
import com.example.rundraw_fe.response.MypageCommentListResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPageCommentsActivity extends BaseActivity {

    private RecyclerView rvComments;
    private MyPageCommentAdapter adapter;
    private final List<MypageCommentResponse> commentList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_mypage_comments);

        rvComments = findViewById(R.id.rvComments);
        adapter = new MyPageCommentAdapter(commentList);
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        rvComments.setAdapter(adapter);

        loadComments();
        setupBottomNavigation(R.id.navigation_my);
    }

    private void loadComments() {
        MypageApiService apiService = RetrofitClient.getInstance(this)
                .create(MypageApiService.class);

        apiService.getComments().enqueue(new Callback<ApiResponse<MypageCommentListResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<MypageCommentListResponse>> call,
                                   Response<ApiResponse<MypageCommentListResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    commentList.clear();
                    commentList.addAll(response.body().getResult().getComments());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<MypageCommentListResponse>> call, Throwable t) {}
        });
    }
}