package com.example.rundraw_fe.response;

public class MypageCommentResponse {
    private Long commentId;
    private Long courseId;
    private String courseName;
    private String content;
    private String createdAt;

    public Long getCommentId() { return commentId; }
    public Long getCourseId() { return courseId; }
    public String getCourseName() { return courseName; }
    public String getContent() { return content; }
    public String getCreatedAt() { return createdAt; }
}