package com.example.rundraw_fe.api;

import com.example.rundraw_fe.request.CourseSettingReqDTO;
import com.example.rundraw_fe.response.ApiResponse;
import com.example.rundraw_fe.response.CourseDetailResponse; // 💡 임포트 추가 완료
import com.example.rundraw_fe.response.CourseRecordListResponse;
import com.example.rundraw_fe.response.DraftCourseListResponse;
import com.example.rundraw_fe.response.DraftCourseResponse;
import com.example.rundraw_fe.response.MypageCommentListResponse;
import com.example.rundraw_fe.response.ScrapCourseListResponse;

import retrofit2.http.Body;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MypageApiService {

    @GET("api/mypage/comment")
    Call<ApiResponse<MypageCommentListResponse>> getComments();

    @GET("api/mypage/course")
    Call<ApiResponse<CourseRecordListResponse>> getCourses(@Query("completedOnly") boolean completedOnly);

    @GET("api/mypage/scarppedcourse")
    Call<ApiResponse<ScrapCourseListResponse>> getScraps();

    @GET("api/mypage/draft/course")
    Call<ApiResponse<DraftCourseListResponse>> getDrawnCourses();

    // 그린 코스 공유 상태 토글 (백엔드 경로: draft/courses, 복수형)
    @PATCH("api/mypage/draft/courses/{draftCourseId}/share")
    Call<ApiResponse<Object>> toggleDraftSharing(@Path("draftCourseId") Long draftCourseId);
    // 코스 수정 API 연동
    @PATCH("api/course/{courseId}")
    Call<Void> updateCourse(
            @Path("courseId") long courseId,
            @Body CourseSettingReqDTO request
    );

    @GET("api/course/draft/{courseDraftId}")
    Call<ApiResponse<DraftCourseResponse>> getDraftCourseDetail(@Path("courseDraftId") Long courseDraftId);

    // 💡 코스 상세 조회 API 연동 (URL, GET, Path 추가 완료)
    @GET("api/course/{courseId}")
    Call<ApiResponse<CourseDetailResponse>> getCourseDetail(@Path("courseId") long courseId);
}