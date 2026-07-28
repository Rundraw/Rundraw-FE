package com.example.rundraw_fe.api;

import com.example.rundraw_fe.response.ApiResponse;
import com.example.rundraw_fe.response.RankingResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RankingApi {

    // 랭킹순
    @GET("/api/ranking/courses/rank")
    Call<ApiResponse<RankingResponse>> getRanking(
            @Query("pageSize") Integer pageSize,
            @Query("cursor") String cursor
    );

    // 난이도별
    @GET("/api/ranking/courses")
    Call<ApiResponse<RankingResponse>> getLevelCourses(
            @Query("level") String level,
            @Query("pageSize") Integer pageSize,
            @Query("cursor") String cursor
    );
}
