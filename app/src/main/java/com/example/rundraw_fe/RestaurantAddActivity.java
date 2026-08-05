package com.example.rundraw_fe.mypage;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rundraw_fe.R;

// TODO: 나중에 레스토랑 브랜치와 병합(Merge) 후 아래 임포트 주석을 해제하세요.
// import com.example.rundraw_fe.api.RestaurantApi;
// import com.example.rundraw_fe.api.RetrofitClient;
// import com.example.rundraw_fe.dto.RestaurantRequestDTO;
// import retrofit2.Call;
// import retrofit2.Callback;
// import retrofit2.Response;

public class RestaurantAddActivity extends AppCompatActivity {

    private EditText etRestaurantName;
    private TextView tvSelectedLocation;
    private Button btnCompleteAddRestaurant;

    private long courseId; // 전달받을 코스 ID
    private double selectedLatitude = 37.4979;   // 지도 핑 연동 전 임시 위도
    private double selectedLongitude = 127.0276; // 지도 핑 연동 전 임시 경도

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_add);

        etRestaurantName = findViewById(R.id.etRestaurantName);
        tvSelectedLocation = findViewById(R.id.tvSelectedLocation);
        btnCompleteAddRestaurant = findViewById(R.id.btnCompleteAddRestaurant);

        // 이전 화면(CourseSettingActivity)에서 넘어온 courseId 받기
        courseId = getIntent().getLongExtra("courseId", 1L);

        // [맛집 등록하기] 버튼 클릭 시 동작
        btnCompleteAddRestaurant.setOnClickListener(v -> {
            String restaurantName = etRestaurantName.getText().toString().trim();

            if (restaurantName.isEmpty()) {
                Toast.makeText(this, "맛집 이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            sendRestaurantDataToApi(courseId, restaurantName);
        });
    }

    private void sendRestaurantDataToApi(long courseId, String restaurantName) {
        /* TODO: 레스토랑 브랜치 병합 후 아래 레트로핏 통신 로직을 사용하세요.
        RestaurantRequestDTO requestDTO = new RestaurantRequestDTO(
                restaurantName,
                "코스 내 등록된 맛집",
                selectedLatitude,
                selectedLongitude,
                "ChIJplace_id_example",
                "https://maps.google.com"
        );

        RestaurantApi restaurantApi = RetrofitClient.getClient().create(RestaurantApi.class);
        restaurantApi.registerRestaurant(courseId, requestDTO).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RestaurantAddActivity.this, "맛집이 성공적으로 등록되었습니다!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(RestaurantAddActivity.this, "맛집 등록 실패 (코드: " + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(RestaurantAddActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        */

        // 임시 더미 동작 (현재 브랜치 빌드/화면 테스트용)
        Toast.makeText(this, "[코스 ID: " + courseId + "] " + restaurantName + " 등록 완료!", Toast.LENGTH_SHORT).show();
        finish(); // 등록 후 화면 닫기
    }
}

//// ✨ RestaurantApi에 이 코드 추가하기 - 맛집 등록 API (이 코드를 여기에 추가하세요!)
//    @POST("api/restaurants/course/{courseId}")
//    Call<ApiResponse<Void>> registerRestaurant(
//            @Path("courseId") Long courseId,
//            @Body RestaurantRequestDTO requestDTO
//    );