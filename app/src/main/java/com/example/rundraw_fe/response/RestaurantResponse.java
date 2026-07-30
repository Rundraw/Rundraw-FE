package com.example.rundraw_fe.response;

public class RestaurantResponse {
    private Long id;
    private String restaurantName;
    private Double latitude;
    private Double longitude;
    private String courseTitle;
    private Long courseId;

    // Getter 메서드들 (서버 데이터를 읽어오기 위해 필요해요)
    public Long getId() { return id; }
    public String getRestaurantName() { return restaurantName; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public String getCourseTitle() { return courseTitle; }
    public Long getCourseId() { return courseId; }
}