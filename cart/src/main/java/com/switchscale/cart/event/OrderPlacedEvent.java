package com.switchscale.cart.event;

public class OrderPlacedEvent {

    private Long orderId;
    private String userId;

    public OrderPlacedEvent() {
    }

    public OrderPlacedEvent(Long orderId, String userId) {
        this.orderId = orderId;
        this.userId = userId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "OrderPlacedEvent{" +
                "orderId=" + orderId +
                ", userId='" + userId + '\'' +
                '}';
    }
}
