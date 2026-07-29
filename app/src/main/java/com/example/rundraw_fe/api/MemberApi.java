package com.example.rundraw_fe.api;

import com.example.rundraw_fe.auth.MemberResponse;
import com.example.rundraw_fe.request.NicknameRequest;
import com.example.rundraw_fe.response.ApiResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;

public interface MemberApi {

    @GET("api/users/me")
    Call<MemberResponse> getMyInfo();

    @PATCH("api/users/me/name")
    Call<ApiResponse<String>> updateName(
            @Body NicknameRequest request
    );

    @POST("api/users/me/name")
    Call<ApiResponse<String>> duplicateName(
            @Body NicknameRequest request
    );
}
