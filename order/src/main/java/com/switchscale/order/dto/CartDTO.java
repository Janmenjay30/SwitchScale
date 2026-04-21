package com.switchscale.order.dto;

import java.util.ArrayList;
import java.util.List;

public class CartDTO {
    private String userId;
    private List<CartItemDto> items = new ArrayList<>();
    private Double cartTotal;

    public CartDTO() {
    }

    public CartDTO(String userId) {
        this.userId = userId;
    }

    public CartDTO(String userId, List<CartItemDto> items, Double cartTotal) {
        this.userId = userId;
        this.items = items;
        this.cartTotal = cartTotal;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<CartItemDto> getItems() {
        return items;
    }

    public void setItems(List<CartItemDto> items) {
        this.items = items;
    }

    public List<CartItemDto> getCartItems() {
        return items;
    }

    public void setCartItems(List<CartItemDto> cartItems) {
        this.items = cartItems;
    }

    public Double getCartTotal() {
        return cartTotal;
    }

    public void setCartTotal(Double cartTotal) {
        this.cartTotal = cartTotal;
    }

    public static class CartItemDto {
        private String productId;
        private String productName;
        private Double price;
        private int quantity;

        public CartItemDto() {
        }

        public CartItemDto(String productId, String productName, Double price, int quantity) {
            this.productId = productId;
            this.productName = productName;
            this.price = price;
            this.quantity = quantity;
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}