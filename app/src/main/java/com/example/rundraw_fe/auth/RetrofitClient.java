package com.example.rundraw_fe.auth;

import android.content.Context;
import com.example.rundraw_fe.BuildConfig;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;

    public static Retrofit getInstance(Context context){
        if(retrofit == null){
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(
                    new AuthInterceptor(context.getApplicationContext()))
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.LOCAL_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}