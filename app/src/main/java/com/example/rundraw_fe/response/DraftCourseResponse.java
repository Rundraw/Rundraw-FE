package com.example.rundraw_fe.response;

import com.example.rundraw_fe.mypage.MyPageCourseItem;

public class DraftCourseResponse implements MyPageCourseItem {
    private Long draftCourseId;
    private String name;
    private Boolean isSharing;

    public Long getDraftCourseId() { return draftCourseId; }

    @Override
    public String getDisplayName() { return name; }

    @Override
    public Boolean getIsSharing() { return isSharing; }
}