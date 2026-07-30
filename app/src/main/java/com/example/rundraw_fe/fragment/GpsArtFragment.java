package com.example.rundraw_fe.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rundraw_fe.R;
import com.example.rundraw_fe.adapter.GpsArtAdapter;
import com.example.rundraw_fe.api.RankingApi;
import com.example.rundraw_fe.auth.RetrofitClient;
import com.example.rundraw_fe.response.ApiResponse;
import com.example.rundraw_fe.response.PaginationResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GpsArtFragment extends Fragment {

    private RecyclerView recyclerView;
    private GpsArtAdapter adapter;
    private RankingApi api;

    public GpsArtFragment(){
        super(R.layout.gps_art_fragment);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ){
        super.onViewCreated(view,savedInstanceState);
        recyclerView = view.findViewById(R.id.gpsArtRecyclerView);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(
                        requireContext()
                )
        );

        adapter = new GpsArtAdapter(requireContext());
        recyclerView.setAdapter(adapter);

        api = RetrofitClient.getInstance(requireContext()).create(RankingApi.class);
        loadGpsArt();
    }

    private void loadGpsArt(){
        api.getGpsArt(10, "-1")
                .enqueue(new Callback<ApiResponse<PaginationResponse>>() {
                    @Override
                    public void onResponse(
                            Call<ApiResponse<PaginationResponse>> call,
                            Response<ApiResponse<PaginationResponse>> response
                    ){
                        if(response.isSuccessful() && response.body()!=null){
                            adapter.setItems(
                                    response.body()
                                            .getResult()
                                            .getData()
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<PaginationResponse>> call,
                            Throwable t
                    ){
                        Log.e("GPS_ART", "실패", t);
                    }
                });
    }
}