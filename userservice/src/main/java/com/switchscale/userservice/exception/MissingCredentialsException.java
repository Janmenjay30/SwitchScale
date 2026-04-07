package com.switchscale.userservice.exception;

public class MissingCredentialsException extends RuntimeException {
    public MissingCredentialsException(String message) {
        super(message);
    }
}
