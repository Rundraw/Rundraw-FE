package com.example.rundraw_fe.api;

import com.example.rundraw_fe.response.ApiResponse;
import com.example.rundraw_fe.response.CourseRecordListResponse;
import com.example.rundraw_fe.response.DraftCourseListResponse;
import com.example.rundraw_fe.response.MypageCommentListResponse;
import com.example.rundraw_fe.response.ScrapCourseListResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MypageApiService {

    @GET("/mypage/comments")
    Call<ApiResponse<MypageCommentListResponse>> getComments();

    @GET("/mypage/courses")
    Call<ApiResponse<CourseRecordListResponse>> getCourses(@Query("type") String type);

    @GET("/mypage/scraps")
    Call<ApiResponse<ScrapCourseListResponse>> getScraps();

    @GET("/mypage/drawn-courses")
    Call<ApiResponse<DraftCourseListResponse>> getDrawnCourses();
}