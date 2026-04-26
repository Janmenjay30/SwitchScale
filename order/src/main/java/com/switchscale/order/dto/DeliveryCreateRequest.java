package com.switchscale.order.dto;

public class DeliveryCreateRequest {

    private Long orderId;
    private String userId;
    private Long addressId;
    private Integer etaMinutes;

    public DeliveryCreateRequest() {
    }

    public DeliveryCreateRequest(Long orderId, String userId, Long addressId, Integer etaMinutes) {
        this.orderId = orderId;
        this.userId = userId;
        this.addressId = addressId;
        this.etaMinutes = etaMinutes;
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

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public Integer getEtaMinutes() {
        return etaMinutes;
    }

    public void setEtaMinutes(Integer etaMinutes) {
        this.etaMinutes = etaMinutes;
    }
}
