package com.example.rundraw_fe.response;

public class MapRestaurantResponse {

    private Long id;
    private String placeId;
    private Double latitude;
    private Double longitude;


    public Long getId() {
        return id;
    }

    public String getPlaceId() {
        return placeId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}