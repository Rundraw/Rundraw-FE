package com.example.rundraw_fe.response;

import com.google.gson.annotations.SerializedName;

public class DraftCourseResponse {
    @SerializedName("draftCourseId")
    private Long draftCourseId;

    @SerializedName("name")
    private String name;

    @SerializedName("isSharing")
    private Boolean isSharing;

    public Long getDraftCourseId() { return draftCourseId; }
    public String getName() { return name; }
    public Boolean getIsSharing() { return isSharing; }
}