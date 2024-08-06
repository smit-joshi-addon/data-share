package com.track.share.exceptions;

import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

public class ApiErrorResponse {

    private HttpStatus httpStatus;
    private String message;
    private String path;
    private String api;
    private ZonedDateTime timestamp;

    public ApiErrorResponse(HttpStatus httpStatus, String message, String path, String api, ZonedDateTime timestamp) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.path = path;
        this.api = api;
        this.timestamp = timestamp;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public String getApi() {
        return api;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

}
