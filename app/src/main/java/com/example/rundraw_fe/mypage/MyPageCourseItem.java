package com.example.rundraw_fe.mypage;

public interface MyPageCourseItem {
    String getDisplayName();

    default Boolean getIsCompleted() { return null; }
    default Integer getCount() { return null; }
    default Boolean getIsSharing() { return null; }
}