package com.example.rundraw_fe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rundraw_fe.adapter.GpsArtAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.rundraw_fe.api.HomeApi;
import com.example.rundraw_fe.api.MemberApi;
import com.example.rundraw_fe.api.RankingApi;
import com.example.rundraw_fe.api.RestaurantApi;
import com.example.rundraw_fe.auth.RetrofitClient;
import com.example.rundraw_fe.mypage.MyPageCoursesActivity;
import com.example.rundraw_fe.response.ApiResponse;
import com.example.rundraw_fe.response.GpsArtResponse;
import com.example.rundraw_fe.response.HomeMyCourseResponse;
import com.example.rundraw_fe.response.PaginationResponse;
import com.example.rundraw_fe.response.RestaurantResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends BaseActivity {

    private TextView tvRestaurantName;
    private TextView[] tvRestaurantNames = new TextView[3];
    private TextView[] tvRestaurantCourses = new TextView[3];
    private LinearLayout[] restaurantCards = new LinearLayout[3];

    private LinearLayout sectionMyRoute;
    private LinearLayout sectionMyRestaurant;
    private LinearLayout sectionGpsArt;
    private RecyclerView gpsArtRecyclerView;
    private GpsArtAdapter gpsArtAdapter;
    private LinearLayout layoutRestaurantCard;
    private LinearLayout myRouteContainer;
    private TextView memberName;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_home);

        initViews();
        loadMemberName();
        loadSavedRestaurant();
        loadMyCourses();
        setupSectionClickListeners();
        loadGpsArt();
        setupBottomNavigation(R.id.navigation_home);

        for(int i = 0; i < 3; i++){
            final int index = i;
            restaurantCards[index]
                    .setOnClickListener(v -> {
                        Object tag = tvRestaurantNames[index].getTag();
                        if(tag instanceof RestaurantResponse){
                            RestaurantResponse restaurant = (RestaurantResponse) tag;
                            Intent intent = new Intent(HomeActivity.this, RestaurantActivity.class);
                            intent.putExtra("restaurantCourseId", restaurant.getRestaurantCourseId());
                            startActivity(intent);
                        }
                    });
        }
    }

    private void initViews() {
        tvRestaurantNames[0] = findViewById(R.id.tvRestaurantName1);
        tvRestaurantNames[1] = findViewById(R.id.tvRestaurantName2);
        tvRestaurantNames[2] = findViewById(R.id.tvRestaurantName3);
        tvRestaurantCourses[0] = findViewById(R.id.tvRestaurantCourse1);
        tvRestaurantCourses[1] = findViewById(R.id.tvRestaurantCourse2);
        tvRestaurantCourses[2] = findViewById(R.id.tvRestaurantCourse3);
        restaurantCards[0] = findViewById(R.id.restaurantCard1);
        restaurantCards[1] = findViewById(R.id.restaurantCard2);
        restaurantCards[2] = findViewById(R.id.restaurantCard3);
        sectionMyRoute = findViewById(R.id.sectionMyRoute);
        sectionMyRestaurant = findViewById(R.id.sectionMyRestaurant);
        sectionGpsArt = findViewById(R.id.sectionGpsArt);
        gpsArtRecyclerView = findViewById(R.id.gpsArtRecyclerView);
        layoutRestaurantCard = findViewById(R.id.layoutRestaurantCard);
        myRouteContainer = findViewById(R.id.myRouteContainer);
        memberName = findViewById(R.id.memberName);
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
        apiService.getRestaurant()
                .enqueue(new Callback<ApiResponse<List<RestaurantResponse>>>() {
                    @Override
                    public void onResponse(
                            Call<ApiResponse<List<RestaurantResponse>>> call,
                            Response<ApiResponse<List<RestaurantResponse>>> response
                    ) {
                        if(response.isSuccessful() && response.body() != null) {
                            List<RestaurantResponse> list = response.body().getResult();
                            if(list == null) return;
                            for(int i = 0; i < 3; i++){
                                if(i < list.size()){
                                    RestaurantResponse restaurant = list.get(i);
                                    tvRestaurantNames[i].setText(restaurant.getRestaurantName());
                                    tvRestaurantCourses[i].setText(restaurant.getCourseName() != null ? restaurant.getCourseName() : "코스 없음");
                                    // 카드 클릭 이동용 데이터 저장
                                    tvRestaurantNames[i].setTag(restaurant);
                                }else{
                                    tvRestaurantNames[i].setText("맛집 없음");
                                    tvRestaurantCourses[i].setText("");
                                }
                            }
                        }
                    }
                    @Override
                    public void onFailure(
                            Call<ApiResponse<List<RestaurantResponse>>> call,
                            Throwable t
                    ) {
                        Log.e("Restaurant", t.getMessage()
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
    private void loadMyCourses() {
        HomeApi apiService = RetrofitClient.getInstance(this).create(HomeApi.class);
        apiService.getMyCourses(3)
                .enqueue(new Callback<ApiResponse<List<HomeMyCourseResponse>>>() {

                    @Override
                    public void onResponse(
                            Call<ApiResponse<List<HomeMyCourseResponse>>> call,
                            Response<ApiResponse<List<HomeMyCourseResponse>>> response
                    ) {
                        if(response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            List<HomeMyCourseResponse> courses = response.body().getResult();
                            myRouteContainer.removeAllViews();
                            for(HomeMyCourseResponse course : courses){
                                View card = createCourseCard(course);
                                myRouteContainer.addView(card);
                            }
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<List<HomeMyCourseResponse>>> call,
                            Throwable t
                    ) {
                        Log.e("MY_COURSE", t.getMessage());
                    }
                });
    }

    private View createCourseCard(HomeMyCourseResponse course) {

        View card = LayoutInflater.from(this)
                .inflate(
                        R.layout.item_my_course,
                        myRouteContainer,
                        false
                );
        TextView tvCourseName = card.findViewById(R.id.tvCourseName);
        View viewStatusDot = card.findViewById(R.id.viewStatusDot);
        tvCourseName.setText(course.getCourseName());
        if(Boolean.TRUE.equals(course.getIsCompleted())) {
            viewStatusDot.setBackgroundResource(R.drawable.dot_green);
        } else {
            viewStatusDot.setBackgroundResource(R.drawable.dot_red);
        }
        return card;
    }

    private void loadMemberName() {
        MemberApi apiService = RetrofitClient.getInstance(this).create(MemberApi.class);
        apiService.getName().enqueue(new Callback<ApiResponse<String>>() {
                    @Override
                    public void onResponse(
                            Call<ApiResponse<String>> call,
                            Response<ApiResponse<String>> response
                    ) {
                        if(response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            String name = response.body().getResult();
                            memberName.setText(name + "님, 반가워요!");
                        } else {
                            memberName.setText("사용자님, 반가워요!");
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<String>> call,
                            Throwable t
                    ) {
                        Log.e("MEMBER_NAME", t.getMessage());
                        memberName.setText("사용자님, 반가워요!");
                    }
                });
    }
}