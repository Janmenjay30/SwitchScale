package com.switchscale.userservice.controller;


import com.switchscale.userservice.model.AddressModel;
import com.switchscale.userservice.model.UserModel;
import com.switchscale.userservice.repository.AddressRepository;
import com.switchscale.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/addresses")
@RequiredArgsConstructor
public class Address {
    
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    @PostMapping
    public ResponseEntity<?> addAddress(@PathVariable Long userId, @RequestBody AddressModel address) {
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        address.setUser(user);
        AddressModel savedAddress = addressRepository.save(address);
        return ResponseEntity.ok(savedAddress);
    }

    @GetMapping
    public ResponseEntity<?> getAddresses(@PathVariable Long userId) {
        List<AddressModel> addresses = addressRepository.findByUserId(userId);
        return ResponseEntity.ok(addresses);
    }


}
