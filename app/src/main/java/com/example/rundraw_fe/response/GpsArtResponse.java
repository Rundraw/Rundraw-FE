package com.example.rundraw_fe.response;

import java.util.List;


public class GpsArtResponse {

    private Long id;
    private String name;
    private Integer likeCount;
    private List<PointResponse> points;
    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public Integer getLikeCount() {
        return likeCount;
    }
    public List<PointResponse> getPoints() {
        return points;
    }

}