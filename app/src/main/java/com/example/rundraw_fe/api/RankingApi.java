package com.example.rundraw_fe.api;

import com.example.rundraw_fe.request.CreateCommentRequest;
import com.example.rundraw_fe.request.UpdateCommentRequest;
import com.example.rundraw_fe.response.ApiResponse;
import com.example.rundraw_fe.response.CommentResponse;
import com.example.rundraw_fe.response.CourseDetailResponse;
import com.example.rundraw_fe.response.GpsArtResponse;
import com.example.rundraw_fe.response.PaginationResponse;
import com.example.rundraw_fe.response.RankingResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RankingApi {

    // 랭킹순
    @GET("api/ranking/courses/rank")
    Call<ApiResponse<RankingResponse>> getRanking(
            @Query("pageSize") Integer pageSize,
            @Query("cursor") String cursor
    );

    // 난이도별
    @GET("api/ranking/courses")
    Call<ApiResponse<RankingResponse>> getLevelCourses(
            @Query("level") String level,
            @Query("pageSize") Integer pageSize,
            @Query("cursor") String cursor
    );

    @GET("api/ranking/art")
    Call<ApiResponse<PaginationResponse<GpsArtResponse>>> getGpsArt(
            @Query("pageSize")
            int pageSize,
            @Query("cursor")
            String cursor
    );

    // 좋아요 생성
    @POST("api/courses/{courseId}/like")
    Call<ApiResponse<Object>> createLike(
            @Path("courseId") Long courseId
    );


    // 좋아요 삭제
    @DELETE("api/courses/{courseId}/like")
    Call<ApiResponse<Object>> deleteLike(
            @Path("courseId") Long courseId
    );

    // 코스 상세 조회
    @GET("api/ranking/courses/{courseId}")
    Call<ApiResponse<CourseDetailResponse>> getCourseDetail(
            @Path("courseId") Long courseId
    );

    // 북마크 생성
    @POST("api/courses/{courseId}/bookmark")
    Call<ApiResponse<Object>> createBookmark(
            @Path("courseId") Long courseId
    );

    // 북마크 삭제
    @DELETE("api/courses/{courseId}/bookmark")
    Call<ApiResponse<Object>> deleteBookmark(
            @Path("courseId") Long courseId
    );

    // 댓글 작성
    @POST("api/courses/{courseId}/comments")
    Call<ApiResponse<Object>> createComment(
            @Path("courseId") Long courseId,
            @Body CreateCommentRequest request
    );


    // 댓글 조회
    @GET("api/courses/{courseId}/comments")
    Call<ApiResponse<PaginationResponse<CommentResponse>>> getComments(
            @Path("courseId") Long courseId,
            @Query("pageSize") Integer pageSize,
            @Query("cursor") String cursor,
            @Query("query") String query
    );


    // 댓글 삭제
    @DELETE("api/courses/{courseId}/comments/{commentId}")
    Call<ApiResponse<Object>> deleteComment(
            @Path("courseId") Long courseId,
            @Path("commentId") Long commentId
    );


    // 댓글 수정
    @PATCH("api/courses/{courseId}/comments/{commentId}")
    Call<ApiResponse<Object>> updateComment(
            @Path("courseId") Long courseId,
            @Path("commentId") Long commentId,
            @Body UpdateCommentRequest request
    );

    // 댓글 삭제
    @DELETE("api/mypage/comments/{commentId}")
    Call<ApiResponse<Object>> deleteMyComment(
            @Path("commentId") Long commentId
    );
}
