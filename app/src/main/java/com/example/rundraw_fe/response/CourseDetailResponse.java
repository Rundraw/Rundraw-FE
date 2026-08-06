package com.example.rundraw_fe.response;

import java.util.List;


public class CourseDetailResponse {

    private Long courseId;
    private String name;
    private String content;
    private String levelType;
    private String user;
    private Boolean isLike;
    private Boolean isBookmark;
    private Integer commentCount;
    private Integer likeCount;
    private Integer bookmarkCount;
    private Long courseDraftId;

    public Long getCourseId(){
        return courseId;
    }

    public String getName(){
        return name;
    }

    public String getContent(){
        return content;
    }

    public String getLevelType(){
        return levelType;
    }

    public String getUser(){
        return user;
    }

    public Boolean getIsLike(){
        return isLike;
    }

    public Boolean getIsBookmark(){
        return isBookmark;
    }

    public Integer getCommentCount(){
        return commentCount;
    }

    public Integer getLikeCount(){
        return likeCount;
    }

    public Integer getBookmarkCount(){
        return bookmarkCount;
    }

    public Long getCourseDraftId(){
        return courseDraftId;
    }

    private List<Point> points;

    public List<Point> getPoints(){
        return points;
    }

    public static class Point{
        private Double latitude;
        private Double longitude;
        public Double getLatitude(){
            return latitude;
        }
        public Double getLongitude(){
            return longitude;
        }
    }
}