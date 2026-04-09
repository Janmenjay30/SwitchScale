package com.switchscale.cart.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

import com.switchscale.cart.dto.AddToCartRequest;
import com.switchscale.cart.model.CartModel;
import com.switchscale.cart.service.CartService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/cart")
@Validated
public class CartController {
        
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<CartModel> getCartByUserId(@PathVariable @NotBlank(message = "userId is required") String userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PostMapping("/{userId}/add")
    public ResponseEntity<CartModel> addToCart(
            @PathVariable @NotBlank(message = "userId is required") String userId,
            @Valid @RequestBody AddToCartRequest payload) {
        return ResponseEntity.ok(cartService.addItemToCart(userId, payload.getProductId(), payload.getQuantity()));
    }

    @DeleteMapping("/{userId}/remove/{productId}")
    public ResponseEntity<CartModel> removeFromCart(
            @PathVariable @NotBlank(message = "userId is required") String userId,
            @PathVariable @NotBlank(message = "productId is required") String productId) {
        return ResponseEntity.ok(cartService.removeItem(userId, productId));
    }
    
    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<String> clearCart(@PathVariable @NotBlank(message = "userId is required") String userId){
        cartService.removeCart(userId);
        return ResponseEntity.ok("Cart cleared successfully");
    }
    
}
