package com.example.demo.dto.response;

import java.time.LocalDateTime;
import java.util.Map;

public class ValidationErrorResponse {

    private boolean success = false;
    private String message = "Validation failed";
    private Map<String, String> fieldErrors;
    private LocalDateTime timestamp = LocalDateTime.now();

    public ValidationErrorResponse(Map<String, String> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
