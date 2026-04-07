package com.switchscale.catalog.dto;

import java.time.Instant;

public class ApiErrorResponse {

    private final boolean success;
    private final String message;
    private final Instant timestamp;

    public ApiErrorResponse(String message) {
        this.success = false;
        this.message = message;
        this.timestamp = Instant.now();
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}