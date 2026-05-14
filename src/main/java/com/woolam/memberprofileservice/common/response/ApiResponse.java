package com.woolam.memberprofileservice.common.response;

public record ApiResponse<T>(ResponseStatus status, String message, T data) {
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(ResponseStatus.SUCCESS, message, data);
    }

    public static ApiResponse<Void> failure(String message) {
        return new ApiResponse<>(ResponseStatus.FAIL, message, null);
    }

    public static <T> ApiResponse<T> failure(String message, T data) {
        return new ApiResponse<>(ResponseStatus.FAIL, message, data);
    }
}
