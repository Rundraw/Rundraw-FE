package com.example.rundraw_fe.api;

import com.example.rundraw_fe.request.RestaurantRequest;
import com.example.rundraw_fe.response.ApiResponse;
import com.example.rundraw_fe.response.MapRestaurantResponse;
import com.example.rundraw_fe.response.RestaurantResponse;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RestaurantApi {
    // 등록된 맛집 검색 (코스 이름 검색)
    @GET("api/restaurants/search")
    Call<ApiResponse<List<RestaurantResponse>>> searchRestaurant(
            @Query("search") String search
    );

    // 맛집 전체 조회
    @GET("api/restaurants")
    Call<ApiResponse<List<RestaurantResponse>>> getRestaurant();

    // 맛집 삭제
    @DELETE("api/restaurants/{courseRestaurantId}")
    Call<ApiResponse<Long>> deleteRestaurant(
            @Path("courseRestaurantId") Long courseRestaurantId
    );

    // 맛집 생성
    @POST("api/restaurants/{courseId}")
    Call<ApiResponse<Long>> createRestaurant(
            @Path("courseId") Long courseId,
            @Body RestaurantRequest request
    );

    // 지도용 맛집 조회
    @GET("api/restaurants/map")
    Call<ApiResponse<List<MapRestaurantResponse>>> getMap();

}