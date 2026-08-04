package com.example.rundraw_fe.response;

import com.example.rundraw_fe.mypage.MyPageCourseItem;

public class CourseRecordResponse implements MyPageCourseItem {
    private Long experienceRecordId;
    private Long courseDraftId;
    private String courseName;
    private Boolean isCompleted;
    private String startAt;
    private String endAt;

    public Long getExperienceRecordId() { return experienceRecordId; }
    public Long getCourseDraftId() { return courseDraftId; }
    public String getCourseName() { return courseName; }
    public String getStartAt() { return startAt; }
    public String getEndAt() { return endAt; }

    @Override
    public String getDisplayName() { return courseName; }

    @Override
    public Boolean getIsCompleted() { return isCompleted; }
}