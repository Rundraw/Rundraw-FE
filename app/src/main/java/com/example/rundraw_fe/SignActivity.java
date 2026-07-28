package com.example.rundraw_fe;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.rundraw_fe.api.ApiResponse;
import com.example.rundraw_fe.api.MemberApi;
import com.example.rundraw_fe.auth.RetrofitClient;
import com.example.rundraw_fe.request.NicknameRequest;
import java.io.IOException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignActivity extends AppCompatActivity {

    private MemberApi api;
    private Button signupBtn;
    private Button nicknameBtn;
    private EditText nicknameEt;
    private TextView message;
    private boolean isNameChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        api = RetrofitClient.getInstance(this).create(MemberApi.class);
        signupBtn = findViewById(R.id.signupBtn);
        nicknameBtn = findViewById(R.id.nicknameBtn);
        nicknameEt = findViewById(R.id.nicknameEt);
        message = findViewById(R.id.message);

        // 닉네임 변경 시 중복 확인 초기화
        nicknameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after
            ){}

            @Override
            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count
            ){
                isNameChecked = false;
                message.setText("닉네임 중복 확인이 필요합니다.");
                message.setTextColor(Color.RED);
            }

            @Override
            public void afterTextChanged(Editable s){}
        });

        // 닉네임 중복 확인 버튼
        nicknameBtn.setOnClickListener(v -> {
            String nickname = nicknameEt.getText().toString().trim();
            if(nickname.isEmpty()){
                message.setText("닉네임을 입력해주세요.");
                message.setTextColor(Color.BLACK);
                return;
            }

            NicknameRequest request = new NicknameRequest(nickname);

            api.duplicateName(request).enqueue(new Callback<ApiResponse<String>>() {
                @Override
                public void onResponse(
                        Call<ApiResponse<String>> call,
                        Response<ApiResponse<String>> response
                ){
                    // 성공 응답
                    if(response.isSuccessful()&& response.body() != null){
                        ApiResponse<String> result = response.body();
                        if(result.isSuccess()){
                            isNameChecked = true;
                            message.setText("사용 가능한 닉네임입니다.");
                            message.setTextColor(Color.GREEN);
                        } else {
                            isNameChecked = false;
                            message.setText("이미 사용 중인 닉네임입니다.");
                            message.setTextColor(Color.RED);
                        }
                    } else {
                        // 400, 409 등 예외 응답
                        isNameChecked = false;
                        message.setText("이미 사용 중인 닉네임입니다.");
                        message.setTextColor(Color.RED);
                        Log.e("DUPLICATE_ERROR", "HTTP CODE : " + response.code());
                        if(response.errorBody() != null){
                            try {
                                Log.e("DUPLICATE_ERROR_BODY", response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                @Override
                public void onFailure(Call<ApiResponse<String>> call, Throwable t){
                    message.setText("네트워크 오류가 발생했습니다.");
                    Log.e("API_ERROR", t.toString());
                }
            });
        });

        // 회원가입 버튼
        signupBtn.setOnClickListener(v -> {
            // 중복 확인 여부 체크
            if(!isNameChecked){
                message.setText("닉네임 중복 확인을 먼저 해주세요.");
                message.setTextColor(Color.RED);
                return;
            }
            String nickname = nicknameEt.getText().toString().trim();
            NicknameRequest request = new NicknameRequest(nickname);

            // 닉네임 저장 요청
            api.updateName(request).enqueue(new Callback<ApiResponse<String>>() {
                @Override
                public void onResponse(
                        Call<ApiResponse<String>> call,
                        Response<ApiResponse<String>> response
                ){
                    if(response.isSuccessful() && response.body() != null){
                        ApiResponse<String> result = response.body();
                        if(result.isSuccess()){
                            Intent intent = new Intent(
                                    SignActivity.this,
                                    HomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            isNameChecked = false;
                            message.setText("중복된 닉네임입니다.");
                            message.setTextColor(Color.RED);
                        }
                    } else {
                        isNameChecked = false;
                        message.setText("중복된 닉네임입니다.");
                        message.setTextColor(Color.RED);
                    }
                }
                @Override
                public void onFailure(
                        Call<ApiResponse<String>> call,
                        Throwable t
                ){
                    message.setText("네트워크 오류가 발생했습니다.");
                    Log.e("UPDATE_ERROR", t.toString());
                }
            });
        });
    }
}