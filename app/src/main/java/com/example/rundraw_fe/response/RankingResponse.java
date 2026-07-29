package com.example.rundraw_fe.response;

import java.util.List;

public class RankingResponse {
    private List<Course> data;
    private boolean hasNext;
    private String nextCursor;
    private Integer pageSize;

    public List<Course> getData() {
        return data;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public String getNextCursor() {
        return nextCursor;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public static class Course {
        private Long id;
        private String name;
        private Integer experienceCount;

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Integer getExperienceCount() {
            return experienceCount;
        }
    }
}