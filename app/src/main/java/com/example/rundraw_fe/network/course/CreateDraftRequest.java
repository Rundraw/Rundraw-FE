package com.example.rundraw_fe.network.course;

import java.util.List;

public class CreateDraftRequest {
    public String name;
    public Long memberId;
    public List<PointDTO> points;

    public CreateDraftRequest(String name, Long memberId, List<PointDTO> points) {
        this.name = name;
        this.memberId = memberId;
        this.points = points;
    }
}
