package com.example.rundraw_fe.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// 앱 전역에서 재사용할 싱글톤
public class ApiClient {
    private static final String BASE_URL = "http://10.0.2.2:8080/"; // 에뮬레이터에서 로컬 서버 접근 주소
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
