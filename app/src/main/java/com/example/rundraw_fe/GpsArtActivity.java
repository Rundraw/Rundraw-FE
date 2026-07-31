package com.example.rundraw_fe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rundraw_fe.adapter.GpsArtAdapter;
import com.example.rundraw_fe.api.RankingApi;
import com.example.rundraw_fe.auth.RetrofitClient;
import com.example.rundraw_fe.response.ApiResponse;
import com.example.rundraw_fe.response.GpsArtResponse;
import com.example.rundraw_fe.response.PaginationResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GpsArtActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private GpsArtAdapter adapter;
    private RankingApi gpsArtApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpsart);
        recyclerView = findViewById(R.id.gpsArtRecyclerView);
        ImageButton btnBack = findViewById(R.id.btnBack);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );
        adapter = new GpsArtAdapter(
                this,
                courseId -> {
                    Intent intent =
                            new Intent(
                                    GpsArtActivity.this,
                                    CourseDetailActivity.class
                            );

                    intent.putExtra(
                            "courseId",
                            courseId
                    );
                    startActivity(intent);
                }
        );
        recyclerView.setAdapter(adapter);
        gpsArtApi = RetrofitClient.getInstance(this).create(RankingApi.class);

        // 홈으로 이동
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(GpsArtActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        loadGpsArt();
    }

    private void loadGpsArt(){

        gpsArtApi.getGpsArt(10, "-1")
                .enqueue(
                        new Callback<ApiResponse<PaginationResponse<GpsArtResponse>>>() {

                            @Override
                            public void onResponse(
                                    Call<ApiResponse<PaginationResponse<GpsArtResponse>>> call,
                                    Response<ApiResponse<PaginationResponse<GpsArtResponse>>> response
                            ) {

                                Log.d(
                                        "GPS_ART",
                                        "응답 코드 : " + response.code()
                                );

                                if(response.isSuccessful()
                                        && response.body()!=null){

                                    PaginationResponse<GpsArtResponse> result =
                                            response.body()
                                                    .getResult();

                                    List<GpsArtResponse> list =
                                            result.getData();

                                    Log.d(
                                            "GPS_ART",
                                            "데이터 개수 : " + list.size()
                                    );

                                    adapter.setItems(list);
                                }
                            }


                            @Override
                            public void onFailure(
                                    Call<ApiResponse<PaginationResponse<GpsArtResponse>>> call,
                                    Throwable t
                            ){

                                Log.e(
                                        "GPS_ART",
                                        "조회 실패",
                                        t
                                );
                            }
                        }
                );
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}