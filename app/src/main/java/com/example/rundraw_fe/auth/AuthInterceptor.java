package com.example.rundraw_fe.auth;

import android.content.Context;
import android.content.SharedPreferences;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private final Context context;
    public AuthInterceptor(Context context){
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        SharedPreferences pref = context.getSharedPreferences(
                "auth",
                Context.MODE_PRIVATE);

        String token = pref.getString(
                "accessToken",
                "");

        Request.Builder builder = chain.request().newBuilder();

        if(!token.isEmpty()){
            builder.addHeader(
                    "Authorization",
                    "Bearer " + token);
        }
        Request request = builder.build();
        return chain.proceed(request);
    }
}