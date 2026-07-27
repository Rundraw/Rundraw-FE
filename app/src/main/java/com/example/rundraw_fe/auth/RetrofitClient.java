package com.example.rundraw_fe.auth;

import android.content.Context;

import com.example.rundraw_fe.BuildConfig;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;

    public static Retrofit getInstance(Context context){
        String localUrl = BuildConfig.LOCAL_URL;
        if(retrofit == null){
            OkHttpClient client =
                    new OkHttpClient.Builder()
                            .addInterceptor(new AuthInterceptor(context))
                            .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(localUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
