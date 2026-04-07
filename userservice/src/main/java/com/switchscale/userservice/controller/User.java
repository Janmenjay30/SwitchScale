package com.switchscale.userservice.controller;


import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.switchscale.userservice.config.JWTConfig;
import com.switchscale.userservice.dto.AuthResponseDto;
import com.switchscale.userservice.model.UserModel;
import com.switchscale.userservice.service.UserService;

import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class User {
    
    private final UserService userService;
    private final JWTConfig jwtconfig;

    @GetMapping("/hello")
    public String checkHelath() {
        return new String("Hello from User Service");
    }
    

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserModel user) {
        UserModel createdUser=userService.registerUser(user);
        return ResponseEntity.ok(AuthResponseDto.success("User registered successfully", null, createdUser.getEmail()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");
        String token = userService.login(email, password);
        return ResponseEntity.ok(AuthResponseDto.success("Login successful", token, email));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long userId){
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }

    @GetMapping("/allUser")
    public ResponseEntity<?> getAllUser(){
        return ResponseEntity.ok(userService.getAllUser());
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        boolean isValid=jwtconfig.validationToken(token);
        if(!isValid){
            return ResponseEntity.status(401).body(AuthResponseDto.error("Invalid or expired token"));
        }
        return ResponseEntity.ok(AuthResponseDto.success("Token is valid", null, null));
    }
    
    

}
