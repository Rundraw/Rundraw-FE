package com.example.rundraw_fe;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rundraw_fe.api.RankingApi;
import com.example.rundraw_fe.auth.RetrofitClient;
import com.example.rundraw_fe.response.ApiResponse;
import com.example.rundraw_fe.response.RankingResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CollectFragment extends Fragment {

    private RecyclerView recyclerView;
    private RankingAdapter adapter;
    private RankingApi api;
    private TextView beginnerBtn;
    private TextView intermediateBtn;
    private TextView advancedBtn;
    private String selectedLevel = null;

    public CollectFragment() {
        super(R.layout.collect_fragment);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.collectRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new RankingAdapter();
        recyclerView.setAdapter(adapter);
        beginnerBtn = view.findViewById(R.id.beginnerBtn);
        intermediateBtn = view.findViewById(R.id.intermediateBtn);
        advancedBtn = view.findViewById(R.id.advancedBtn);
        api = RetrofitClient.getInstance(requireContext()).create(RankingApi.class);

        // 초기 화면 -> 전체 코스 조회
        getCourses(null);

        beginnerBtn.setOnClickListener(v ->
                selectLevel(
                        beginnerBtn,
                        "BEGINNER"));

        intermediateBtn.setOnClickListener(v ->
                selectLevel(
                        intermediateBtn,
                        "INTERMEDIATE"));

        advancedBtn.setOnClickListener(v ->
                selectLevel(
                        advancedBtn,
                        "ADVANCED"));
    }

    // 난이도 선택
    private void selectLevel(
            TextView button,
            String level
    ) {
        // 선택된 버튼 다시 클릭 -> 전체 조회
        if(level.equals(selectedLevel)) {
            selectedLevel = null;
            resetButton();
            getCourses(null);
            return;
        }
        selectedLevel = level;
        resetButton();
        button.setBackgroundResource(R.drawable.level_selected);
        button.setTextColor(Color.WHITE);
        getCourses(level);
    }

    // 버큰 초기화
    private void resetButton() {
        beginnerBtn.setBackgroundResource(R.drawable.level_unselected);
        intermediateBtn.setBackgroundResource(R.drawable.level_unselected);
        advancedBtn.setBackgroundResource(R.drawable.level_unselected);
        beginnerBtn.setTextColor(Color.BLACK);
        intermediateBtn.setTextColor(Color.BLACK);
        advancedBtn.setTextColor(Color.BLACK);
    }

    // 코스 조회
    private void getCourses(String level){
        Call<ApiResponse<RankingResponse>> call;

        // 전체 조회
        if(level == null) {
            call = api.getLevelCourses(null, 10, "-1");
        }
        // 난이도 조회
        else {
            call = api.getLevelCourses(level, 10, "-1");
        }
        call.enqueue(new Callback<ApiResponse<RankingResponse>>() {
            @Override
            public void onResponse(
                    Call<ApiResponse<RankingResponse>> call,
                    Response<ApiResponse<RankingResponse>> response
            ) {
                if(response.body() == null) {
                    Log.e("COURSE_ERROR", "응답 데이터 없음");
                    return;
                }
                ApiResponse<RankingResponse> result = response.body();
                if(result.isSuccess()) {
                    adapter.setCourseList(result.getResult().getData());
                    Log.d("COURSE_API", "조회 개수 : " + result.getResult().getData().size());
                } else {
                    Log.e("COURSE_ERROR", result.getMessage());
                }
            }
            @Override
            public void onFailure(
                    Call<ApiResponse<RankingResponse>> call, Throwable t
            ) {
                Log.e("NETWORK_ERROR", t.getMessage());
            }

        });
    }
}