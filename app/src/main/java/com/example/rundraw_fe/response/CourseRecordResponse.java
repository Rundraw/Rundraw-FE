package com.example.rundraw_fe.response;

import com.example.rundraw_fe.mypage.MyPageCourseItem;
import com.google.gson.annotations.SerializedName;

public class CourseRecordResponse implements MyPageCourseItem {
    @SerializedName("experienceRecordId")
    private Long experienceRecordId;

    @SerializedName("courseDraftId")
    private Long courseDraftId;

    @SerializedName("courseName")
    private String courseName;

    @SerializedName("isCompleted")
    private Boolean isCompleted;

    @SerializedName("startAt")
    private String startAt;

    @SerializedName("endAt")
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