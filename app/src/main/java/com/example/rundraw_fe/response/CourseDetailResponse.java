package com.example.rundraw_fe.response;

import java.util.List;

public class CourseDetailResponse {

    private Long courseId;
    private String name;
    private String user;             // 작성자
    private Integer experienceCount;
    private String description;      // 스웨거 기준 설명
    private String levelTagName;     // 스웨거 기준 난이도
    private Integer likeCount;
    private Boolean isLike;
    private Integer bookmarkCount;
    private Boolean isBookmark;
    private Integer commentCount;
    private Long courseDraftId;      // 체험하기 -> 네비게이션 이동 시 필요
    private List<Point> points;      // 지도 좌표 리스트

    public Long getCourseId() {
        return courseId;
    }

    public String getName() {
        return name;
    }

    public String getUser() {
        return user;
    }

    public Integer getExperienceCount() {
        return experienceCount;
    }

    public String getDescription() {
        return description;
    }

    // 기존 코드(getContent) 호환용
    public String getContent() {
        return description;
    }

    public String getLevelTagName() {
        return levelTagName;
    }

    // 기존 코드(getLevelType) 호환용
    public String getLevelType() {
        return levelTagName;
    }

    public Integer getLikeCount() {
        return likeCount != null ? likeCount : 0;
    }

    public Boolean getIsLike() {
        return isLike != null ? isLike : false;
    }

    public Long getCourseDraftId() {
        return courseDraftId;
    }

    public Integer getBookmarkCount() {
        return bookmarkCount != null ? bookmarkCount : 0;
    }

    public Boolean getIsBookmark() {
        return isBookmark != null ? isBookmark : false;
    }

    public Integer getCommentCount() {
        return commentCount != null ? commentCount : 0;
    }

    public List<Point> getPoints() {
        return points;
    }

    // 내부 Point 클래스 (지도 경로용)
    public static class Point {
        private double latitude;
        private double longitude;

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }
    }
}