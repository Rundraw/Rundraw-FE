package com.example.rundraw_fe.dto;

public class RestaurantRequestDTO {
    private String restaurantName;
    private String description;
    private Double latitude;
    private Double longitude;
    private String placeId;
    private String url;

    public RestaurantRequestDTO(String restaurantName, String description, Double latitude, Double longitude, String placeId, String url) {
        this.restaurantName = restaurantName;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.placeId = placeId;
        this.url = url;
    }

    public String getRestaurantName() { return restaurantName; }
    public String getDescription() { return description; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public String getPlaceId() { return placeId; }
    public String getUrl() { return url; }
}