package com.example.rundraw_fe.network.course;

public class PointDTO {
    public Integer sequence;
    public Double latitude;
    public Double longitude;

    public PointDTO(Integer sequence, Double latitude, Double longitude) {
        this.sequence = sequence;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
