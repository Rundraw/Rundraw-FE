package com.example.rundraw_fe.response;

public class HomeMyCourseResponse {
    private Long courseRecordId;
    private Long courseDraftId;
    private String courseName;
    private Boolean isCompleted;
    private String recordedAt;


    public Long getCourseRecordId() {
        return courseRecordId;
    }

    public Long getCourseDraftId() {
        return courseDraftId;
    }

    public String getCourseName() {
        return courseName;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public String getRecordedAt() {
        return recordedAt;
    }
}
