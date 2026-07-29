package com.example.rundraw_fe.request;

public class NicknameRequest {
    private String nickname;
    public NicknameRequest(String nickname){
        this.nickname = nickname;
    }
    public String getNickname(){
        return nickname;
    }
}
