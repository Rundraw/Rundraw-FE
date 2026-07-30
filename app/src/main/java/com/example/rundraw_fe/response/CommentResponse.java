package com.example.rundraw_fe.response;


public class CommentResponse {

    private Long id;

    private String memberName;

    private String content;

    private String createdAt;

    private Boolean isMine;

    public Boolean getIsMine(){
        return isMine;
    }

    public Long getId(){
        return id;
    }

    public String getMemberName(){
        return memberName;
    }

    public String getContent(){
        return content;
    }

    public String getCreatedAt(){
        return createdAt;
    }

}