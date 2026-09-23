package com.porfolio.gravity.shared.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {
    private int status;
    private String error;
    private String message;
    private String path;
    private long timestamp;

    public static ApiResponse of(int status, String error, String message, String path) {
        return new ApiResponse(status, error, message, path, System.currentTimeMillis());
    }
}
