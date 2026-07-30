package com.example.rundraw_fe.fragment;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import com.example.rundraw_fe.R;
import com.example.rundraw_fe.adapter.RankingAdapter;
import com.example.rundraw_fe.api.RankingApi;
import com.example.rundraw_fe.auth.RetrofitClient;
import com.example.rundraw_fe.response.ApiResponse;
import com.example.rundraw_fe.response.RankingResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RankingFragment extends Fragment {

    private RecyclerView recyclerView;
    private RankingAdapter adapter;
    private RankingApi rankingApi;
    public RankingFragment() {
        super(R.layout.ranking_fragment);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.rankingRecyclerView);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(
                        requireContext()
                )
        );
        adapter = new RankingAdapter();
        recyclerView.setAdapter(adapter);
        rankingApi = RetrofitClient.getInstance(requireContext()).create(RankingApi.class);
        getRanking();
    }

    private void getRanking(){
        rankingApi.getRanking(10, "-1").enqueue(
                new Callback<ApiResponse<RankingResponse>>() {
                    @Override
                    public void onResponse(
                            Call<ApiResponse<RankingResponse>> call,
                            Response<ApiResponse<RankingResponse>> response
                    ) {
                        if(response.body() == null){
                            return;
                        }
                        ApiResponse<RankingResponse> result = response.body();
                        if(result.isSuccess()){
                            adapter.setCourseList(
                                    result.getResult()
                                            .getData()
                            );
                        } else {
                            Log.e("RANKING_ERROR", result.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<RankingResponse>> call,
                            Throwable t
                    ) {
                        Log.e("NETWORK_ERROR", t.getMessage());
                    }
                });
    }
}