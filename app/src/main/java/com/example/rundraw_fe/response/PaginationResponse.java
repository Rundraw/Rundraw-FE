package com.example.rundraw_fe.response;

import java.util.List;

public class PaginationResponse {
    private List<GpsArtResponse> data;
    private boolean hasNext;
    private String nextCursor;
    private int pageSize;
    public List<GpsArtResponse> getData() {
        return data;
    }
    public boolean isHasNext() {
        return hasNext;
    }
    public String getNextCursor() {
        return nextCursor;
    }
    public int getPageSize() {
        return pageSize;
    }
}