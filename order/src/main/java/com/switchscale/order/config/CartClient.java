package com.switchscale.order.config;

import com.switchscale.order.dto.CartDTO;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "cart-service", url = "${cart.service.url:http://localhost:8004}")
public interface CartClient {
    @GetMapping("/cart/{userId}")
    CartDTO getCart(@PathVariable("userId") String userId);

    @DeleteMapping("/cart/{userId}/clear")
    void clearCart(@PathVariable("userId") String userId);
}