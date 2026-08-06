package com.example.rundraw_fe.mypage;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rundraw_fe.BaseActivity;
import com.example.rundraw_fe.R;
import com.example.rundraw_fe.api.MypageApiService;
import com.example.rundraw_fe.auth.RetrofitClient;
import com.example.rundraw_fe.response.ApiResponse;
import com.example.rundraw_fe.response.MypageCommentListResponse;
import com.example.rundraw_fe.response.MypageCommentResponse;

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
                    public void onResponse(Call<ApiResponse<MypageCommentListResponse>> call, Response<ApiResponse<MypageCommentListResponse>> response) {
                        if(response.isSuccessful() && response.body() != null) {
                            ApiResponse<MypageCommentListResponse> apiResponse = response.body();
                            if(apiResponse.isSuccess() && apiResponse.getResult() != null) {
                                List<MypageCommentResponse> comments = apiResponse.getResult().getComments();
                                if(comments != null) {
                                    commentList.clear();
                                    commentList.addAll(comments);
                                    adapter.notifyDataSetChanged();
                                    Log.d("CommentAPI", "댓글 불러오기 성공 : " + comments.size() + "개");
                                }
                            } else {
                                Log.e("CommentAPI", apiResponse.getMessage());
                            }
                        } else {
                            Log.e("CommentAPI", "통신 실패 코드 : " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<MypageCommentListResponse>> call, Throwable t
                    ){
                        Log.e("CommentAPI", "네트워크 오류 : " + t.getMessage());
                    }
                });
    }
}