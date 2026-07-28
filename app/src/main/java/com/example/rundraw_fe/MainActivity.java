package com.example.rundraw_fe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    private MaterialButton kakaoBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // OAuth 로그인 후 redirect 처리
        handleOAuthRedirect();

        kakaoBtn =
                findViewById(R.id.kakaoButton);

        kakaoBtn.setOnClickListener(v -> {
            String url = BuildConfig.OAUTH_URL;
            Intent intent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(url));
            startActivity(intent);
        });
    }

    private void handleOAuthRedirect() {

        Uri uri = getIntent().getData();

        // 일반 앱 실행이면 종료
        if (uri == null) return;

        // rundraw://login 인 경우만 처리
        if (!"rundraw".equals(uri.getScheme())) return;
        if (!"login".equals(uri.getHost())) return;

        String token = uri.getQueryParameter("accessToken");
        String isNewMember = uri.getQueryParameter("isNewMember");

        Log.d("OAuth", "token : " + token);
        Log.d("OAuth", "isNewMember : " + isNewMember);

        if (token == null) return;

        saveToken(token);

        boolean newMember = Boolean.parseBoolean(isNewMember);

        if (newMember) {
            // 신규 회원
            Intent intent = new Intent(
                    MainActivity.this,
                    SignActivity.class);
            startActivity(intent);
        } else {
            // 기존 회원
            Intent intent = new Intent(
                    MainActivity.this,
                    HomeActivity.class);
            startActivity(intent);
        }
        finish();
    }

    private void saveToken(String token) {
        SharedPreferences pref = getSharedPreferences(
                "auth",
                MODE_PRIVATE);
        pref.edit().putString("accessToken", token).apply();
    }
}