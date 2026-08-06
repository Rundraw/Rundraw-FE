package com.example.rundraw_fe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rundraw_fe.adapter.GpsArtAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.rundraw_fe.api.RankingApi;
import com.example.rundraw_fe.api.RestaurantApi;
import com.example.rundraw_fe.auth.RetrofitClient;
import com.example.rundraw_fe.mypage.MyPageCoursesActivity;
import com.example.rundraw_fe.response.ApiResponse;
import com.example.rundraw_fe.response.GpsArtResponse;
import com.example.rundraw_fe.response.PaginationResponse;
import com.example.rundraw_fe.response.RestaurantResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends BaseActivity {

    private TextView tvRestaurantName;
    private TextView tvRestaurantAddress;
    private TextView tvRestaurantCourse;
    private TextView tvRestaurantRating;

    private LinearLayout sectionMyRoute;
    private LinearLayout sectionMyRestaurant;
    private LinearLayout sectionGpsArt;
    private RecyclerView gpsArtRecyclerView;
    private GpsArtAdapter gpsArtAdapter;
    private LinearLayout layoutRestaurantCard;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_home);

        initViews();
        loadSavedRestaurant();
        setupSectionClickListeners();
        loadGpsArt();
        setupBottomNavigation(R.id.navigation_home);

        layoutRestaurantCard.setOnClickListener(v -> {
            Log.d("HOME", "맛집 카드 클릭");
            Intent intent = new Intent(HomeActivity.this, RestaurantActivity.class);
            Object tag = tvRestaurantName.getTag();
            if(tag instanceof RestaurantResponse){
                RestaurantResponse restaurant = (RestaurantResponse) tag;
                intent.putExtra(
                        "restaurantCourseId",
                        restaurant.getRestaurantCourseId()
                );
            }
            startActivity(intent);
        });
    }

    private void initViews() {
        tvRestaurantName = findViewById(R.id.tvRestaurantName);
        tvRestaurantAddress = findViewById(R.id.tvRestaurantAddress);
        tvRestaurantCourse = findViewById(R.id.tvRestaurantCourse);
        tvRestaurantRating = findViewById(R.id.tvRestaurantRating);

        sectionMyRoute = findViewById(R.id.sectionMyRoute);
        sectionMyRestaurant = findViewById(R.id.sectionMyRestaurant);
        sectionGpsArt = findViewById(R.id.sectionGpsArt);
        gpsArtRecyclerView = findViewById(R.id.gpsArtRecyclerView);
        layoutRestaurantCard = findViewById(R.id.layoutRestaurantCard);
    }

    private void setupSectionClickListeners() {
        sectionMyRoute.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, MyPageCoursesActivity.class));
        });

        sectionMyRestaurant.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, RestaurantActivity.class));
        });

        sectionGpsArt.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, GpsArtActivity.class));
        });
    }

    // 참고: "저장한 맛집" 개인화 API는 백엔드에 아직 없어서, 임시로 전체 목록 중 첫 번째 항목을 표시함.
    // 주소(address), 평점(rating)도 RestaurantResDTO에 없는 필드라 비워둠.
    private void loadSavedRestaurant() {
        RestaurantApi apiService = RetrofitClient.getInstance(this).create(RestaurantApi.class);

        apiService.getRestaurant().enqueue(new Callback<ApiResponse<List<RestaurantResponse>>>() {
                    @Override
                    public void onResponse(
                            Call<ApiResponse<List<RestaurantResponse>>> call,
                            Response<ApiResponse<List<RestaurantResponse>>> response
                    ) {

                        if (response.isSuccessful() && response.body() != null) {
                            List<RestaurantResponse> restaurantList = response.body().getResult();

                            if(restaurantList != null && !restaurantList.isEmpty()) {
                                // 임시 첫 번째 맛집 표시
                                RestaurantResponse restaurant = restaurantList.get(0);
                                tvRestaurantName.setTag(restaurant);
                                tvRestaurantName.setText(
                                        restaurant.getRestaurantName()
                                );


                                tvRestaurantCourse.setText(
                                        restaurant.getCourseName() != null
                                                ? restaurant.getCourseName()
                                                : "코스 없음"
                                );


                                // 현재 DTO에는 주소 없음
                                tvRestaurantAddress.setText(
                                        restaurant.getPlaceId() != null
                                                ? restaurant.getPlaceId()
                                                : "장소 정보 없음"
                                );


                                // 현재 DTO에는 평점 없음
                                tvRestaurantRating.setText(
                                        ""
                                );

                            }

                        }
                    }



                    @Override
                    public void onFailure(
                            Call<ApiResponse<List<RestaurantResponse>>> call,
                            Throwable t
                    ) {

                        Log.e(
                                "Restaurant",
                                t.getMessage()
                        );

                    }

                });
    }

    private void loadGpsArt() {
        // 가로 스크롤 방식
        gpsArtRecyclerView.setLayoutManager(
                new GridLayoutManager(
                        this,
                        2
                )
        );


        gpsArtAdapter = new GpsArtAdapter(
                this,
                true,
                courseId -> {
                    Intent intent = new Intent(HomeActivity.this, CourseDetailActivity.class);
                    intent.putExtra("courseId", courseId);
                    startActivity(intent);
                }
        );
        gpsArtRecyclerView.setAdapter(gpsArtAdapter);
        RankingApi apiService = RetrofitClient.getInstance(this).create(RankingApi.class);
        apiService.getGpsArt(2, "-1").enqueue(
                new Callback<ApiResponse<PaginationResponse<GpsArtResponse>>>() {
                            @Override
                            public void onResponse(
                                    Call<ApiResponse<PaginationResponse<GpsArtResponse>>> call,
                                    Response<ApiResponse<PaginationResponse<GpsArtResponse>>> response
                            ) {
                                if(response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                    List<GpsArtResponse> list = response.body().getResult().getData();
                                    gpsArtAdapter.setItems(list);
                                    Log.d("HOME_GPS_ART", "조회 개수 : " + list.size());
                                } else {
                                    Log.e("HOME_GPS_ART", "응답 실패 : " + response.code());
                                }
                            }

                            @Override
                            public void onFailure(
                                    Call<ApiResponse<PaginationResponse<GpsArtResponse>>> call,
                                    Throwable t
                            ) {
                                Log.e("HOME_GPS_ART", "조회 실패", t);
                            }
                        }
                );
    }
}