package com.example.rundraw_fe.network.course;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface CourseApiService {
    @POST("/api/user/me/draft/course")
    Call<DraftDetailResponse> saveDraft(@Body CreateDraftRequest request);
}
