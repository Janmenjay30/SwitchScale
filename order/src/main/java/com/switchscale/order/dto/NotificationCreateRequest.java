package com.switchscale.order.dto;

public class NotificationCreateRequest {

    private Long orderId;
    private String userId;
    private String title;
    private String message;
    private String channel;
    private String email;

    public NotificationCreateRequest() {
    }

    public NotificationCreateRequest(Long orderId, String userId, String title, String message, String channel, String email) {
        this.orderId = orderId;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.channel = channel;
        this.email = email;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
