package com.example.demo.exception;

public class AccessDeniedForBranchException extends RuntimeException {
    public AccessDeniedForBranchException(String message) {
        super(message);
    }
}
