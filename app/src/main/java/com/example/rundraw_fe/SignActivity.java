package com.example.rundraw_fe;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign);

        Uri uri = getIntent().getData();

        if (uri != null) {
            String token = uri.getQueryParameter("accessToken");

            if (token != null) {
                getSharedPreferences("auth", MODE_PRIVATE)
                        .edit()
                        .putString("accessToken", token)
                        .apply();

                // 저장이 잘 되었는지 확인용 (개발 중에만)
                Log.d("TOKEN", token);
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}