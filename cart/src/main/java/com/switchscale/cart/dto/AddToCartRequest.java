package com.switchscale.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class AddToCartRequest {

    @NotBlank(message = "productId is required")
    private String productId;

    @Min(value = 1, message = "quantity must be at least 1")
    private int quantity;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
