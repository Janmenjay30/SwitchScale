package com.switchscale.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.switchscale.userservice.dto.AuthResponseDto;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Validation failed");

        return ResponseEntity.badRequest().body(AuthResponseDto.error(message));
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<?> handleDuplicateEmail(DuplicateEmailException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(AuthResponseDto.error(ex.getMessage()));
    }

    @ExceptionHandler(DuplicatePhoneException.class)
    public ResponseEntity<?> handleDuplicatePhone(DuplicatePhoneException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(AuthResponseDto.error(ex.getMessage()));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<?> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(AuthResponseDto.error(ex.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(AuthResponseDto.error(ex.getMessage()));
    }

    @ExceptionHandler(MissingCredentialsException.class)
    public ResponseEntity<?> handleMissingCredentials(MissingCredentialsException ex) {
        return ResponseEntity.badRequest().body(AuthResponseDto.error(ex.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntime(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(AuthResponseDto.error("An unexpected error occurred"));
    }
}