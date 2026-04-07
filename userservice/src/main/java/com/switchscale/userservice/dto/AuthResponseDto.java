package com.switchscale.userservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponseDto {

    private boolean success;
    private String message;
    private String token;
    private String email;

    public AuthResponseDto() {
    }

    public AuthResponseDto(boolean success, String message, String token, String email) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.email = email;
    }

    public static AuthResponseDto success(String message, String token, String email) {
        return new AuthResponseDto(true, message, token, email);
    }

    public static AuthResponseDto error(String message) {
        return new AuthResponseDto(false, message, null, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}