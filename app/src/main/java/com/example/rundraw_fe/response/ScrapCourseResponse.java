package com.example.rundraw_fe.response;

import com.example.rundraw_fe.mypage.MyPageCourseItem;

public class ScrapCourseResponse implements MyPageCourseItem {
    private Long courseId;
    private String courseName;
    private Integer experienceCount;

    public Long getCourseId() { return courseId; }

    @Override
    public String getDisplayName() { return courseName; }

    @Override
    public Integer getCount() { return experienceCount; }
}