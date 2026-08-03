package com.example.rundraw_fe.api;

import com.example.rundraw_fe.response.RestaurantResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RestaurantApi {

    // 1. 전체 맛집 조회 API
    @GET("api/restaurants")
    Call<List<RestaurantResponse>> getAllRestaurants();

    // 2. 코스별 맛집 검색 API (GET /api/restaurants/search?courseId=1)
    @GET("api/restaurants/search")
    Call<List<RestaurantResponse>> getRestaurantsByCourse(
            @Query("courseId") Long courseId
    );
}