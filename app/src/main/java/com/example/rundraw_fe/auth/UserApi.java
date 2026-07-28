package com.example.rundraw_fe.auth;

import retrofit2.Call;
import retrofit2.http.GET;
public interface UserApi {

    @GET("/users/me")
    Call<MemberResponse> getMyInfo();
}
