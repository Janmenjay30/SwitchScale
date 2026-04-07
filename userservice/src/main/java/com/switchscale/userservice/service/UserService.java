package com.switchscale.userservice.service;

import java.util.Locale;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.switchscale.userservice.config.JWTConfig;
import com.switchscale.userservice.exception.DuplicateEmailException;
import com.switchscale.userservice.exception.DuplicatePhoneException;
import com.switchscale.userservice.exception.InvalidCredentialsException;
import com.switchscale.userservice.exception.MissingCredentialsException;
import com.switchscale.userservice.exception.UserNotFoundException;
import com.switchscale.userservice.model.UserModel;
import com.switchscale.userservice.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTConfig jwtUtil;

    public UserModel registerUser(UserModel user) {
        String normalizedEmail = normalizeEmail(user.getEmail());
        String normalizedPhone = normalizePhone(user.getPhone());

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new DuplicateEmailException("Email already in use");
        }

        if (userRepository.existsByPhone(normalizedPhone)) {
            throw new DuplicatePhoneException("Phone already in use");
        }

        user.setEmail(normalizedEmail);
        user.setPhone(normalizedPhone);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public String login(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new MissingCredentialsException("Email and password are required");
        }

        UserModel user = userRepository.findByEmail(normalizeEmail(email))
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        return jwtUtil.generateToken(user);
    }

    public UserModel getUserProfile(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
    public Iterable<UserModel> getAllUser() {
        return userRepository.findAll();
    }
    public UserModel updateUserProfileModel(Long userId, UserModel updatedUser) {
        UserModel existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        existingUser.setName(updatedUser.getName());
        existingUser.setPhone(updatedUser.getPhone());
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        return userRepository.save(existingUser);
        
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizePhone(String phone) {
        return phone == null ? null : phone.trim();
    }
}
