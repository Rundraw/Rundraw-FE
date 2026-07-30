package com.example.rundraw_fe.request;


public class CreateCommentRequest {
    private String comment;

    public CreateCommentRequest(String comment){
        this.comment = comment;
    }

    public String getComment(){
        return comment;
    }
}