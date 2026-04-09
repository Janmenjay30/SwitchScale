package com.switchscale.cart.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value="cart",timeToLive = 86400)
public class CartModel {
    
    @Id
    private String userId;

    private List<Cartitem> items=new ArrayList<>();
    private Double cartTotal=0.0;

    public CartModel() {
    }

    public CartModel(String userId, List<Cartitem> items, Double cartTotal) {
        this.userId = userId;
        this.items = items;
        this.cartTotal = cartTotal;
    }

    public void calculateTotal(){
        this.cartTotal=items.stream().mapToDouble(item->item.getPrice() * item.getQuantity())
                .sum();    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<Cartitem> getItems() {
        return items;
    }

    public void setItems(List<Cartitem> items) {
        this.items = items;
    }

    public Double getCartTotal() {
        return cartTotal;
    }

    public void setCartTotal(Double cartTotal) {
        this.cartTotal = cartTotal;
    }


}
