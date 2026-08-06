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
    private Long coursedraftId;

    // 코스 ID(PK)
    public Long getCourseId(){
        return courseId;
    }

    // 코스 이름
    public String getName(){
        return name;
    }

    // 코스 부연 설명
    public String getContent(){
        return content;
    }

    // 난이도
    /*
    "BEGINNER":"초급"
    "INTERMEDIATE":"중급"
    "ADVANCED":"상급"
    */
    public String getLevelType(){
        return levelType;
    }

    // 등록한 사용자
    public String getUser(){
        return user;
    }

    // 유저별 좋아요 체크(있으면 true, 없으면 false)
    public Boolean getIsLike(){
        return isLike;
    }

    // 유저별 북마크 체크(있으면 true, 없으면 false)
    public Boolean getIsBookmark(){
        return isBookmark;
    }

    // 댓글 수
    public Integer getCommentCount(){
        return commentCount;
    }

    //좋아요 수
    public Integer getLikeCount(){
        return likeCount;
    }

    // 북마크 수
    public Integer getBookmarkCount(){
        return bookmarkCount;
    }

    //coursedraftId
    public Long getCoursedraftId(){
        return coursedraftId;
    }
    // 해당 코스 포인트 리스트
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