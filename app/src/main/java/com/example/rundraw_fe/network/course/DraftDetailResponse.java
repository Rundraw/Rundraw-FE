package com.example.rundraw_fe.network.course;

import java.util.List;

public class DraftDetailResponse {
    public Long courseDraftId;
    public String name;
    public Boolean isSharing;
    public List<PointDTO> points;
    public String createdAt;
}
