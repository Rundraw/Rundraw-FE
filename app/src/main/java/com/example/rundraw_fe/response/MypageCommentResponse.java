package com.example.rundraw_fe.response;

import com.google.gson.annotations.SerializedName;

public class MypageCommentResponse {
    @SerializedName("commentId")
    private Long commentId;

    @SerializedName("courseId")
    private Long courseId;

    @SerializedName("courseName")
    private String courseName;

    @SerializedName("content")
    private String content;

    @SerializedName("createdAt")
    private String createdAt;

    public Long getCommentId() { return commentId; }
    public Long getCourseId() { return courseId; }
    public String getCourseName() { return courseName; }
    public String getContent() { return content; }
    public String getCreatedAt() { return createdAt; }
}