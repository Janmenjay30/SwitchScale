package com.switchscale.order.Event;

public class OrderPlacedEvent {
    
    public OrderPlacedEvent(Long orderId, String userId) {
        this.orderId = orderId;
        this.userId = userId;
    }
    
    private Long orderId;
    private String userId;
    
}
