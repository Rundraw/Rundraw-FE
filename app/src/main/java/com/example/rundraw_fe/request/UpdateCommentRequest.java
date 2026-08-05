package com.example.rundraw_fe.request;


public class UpdateCommentRequest {
    private String comment;

    public UpdateCommentRequest(String comment){
        this.comment = comment;
    }

    public String getComment(){
        return comment;
    }
}