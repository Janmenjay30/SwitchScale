package com.switchscale.userservice.exception;

public class DuplicatePhoneException extends RuntimeException {
    public DuplicatePhoneException(String message) {
        super(message);
    }
}
