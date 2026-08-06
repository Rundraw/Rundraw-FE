package com.example.rundraw_fe.request;

public class RestaurantRequest {

    private String restaurantName;
    private String placeId;
    private Double longitude;
    private Double latitude;

    public RestaurantRequest(
            String restaurantName,
            String placeId,
            Double longitude,
            Double latitude
    ) {
        this.restaurantName = restaurantName;
        this.placeId = placeId;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getPlaceId() {
        return placeId;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }
}