package com.example.rundraw_fe.request; // 본인 프로젝트 패키지 경로에 맞게 확인!

public class CourseSettingReqDTO {
    private String name;
    private String description;
    private String levelTagName;

    public CourseSettingReqDTO(String name, String description, String levelTagName) {
        this.name = name;
        this.description = description;
        this.levelTagName = levelTagName;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getLevelTagName() { return levelTagName; }
}