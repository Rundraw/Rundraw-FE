package com.example.rundraw_fe.api;

public class ApiResponse<T> {
    private boolean isSuccess;
    private String code;
    private String message;
    private T result;

    public boolean isSuccess() {
        return isSuccess;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getResult() {
        return result;
    }
}
