package com.switchscale.order.config;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.switchscale.order.dto.DeliveryCreateRequest;

@FeignClient(name = "delivery-service", url = "${delivery.service.url:http://localhost:8007}")
public interface DeliveryClient {

    @PostMapping("/deliveries")
    Object createDelivery(@RequestBody DeliveryCreateRequest request);
}
