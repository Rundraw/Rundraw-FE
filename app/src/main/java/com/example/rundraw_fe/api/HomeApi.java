package com.example.rundraw_fe.api;

import com.example.rundraw_fe.response.ApiResponse;
import com.example.rundraw_fe.response.GpsArtResponse;
import com.example.rundraw_fe.response.HomeMyCourseResponse;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface HomeApi {
    @GET("api/gpsart")
    Call<ApiResponse<List<GpsArtResponse>>> getPopularGpsArt(
            @Query("size") int size
    );

    @GET("api/user/me/course")
    Call<ApiResponse<List<HomeMyCourseResponse>>> getMyCourses(
            @Query("size") int size
    );
}
