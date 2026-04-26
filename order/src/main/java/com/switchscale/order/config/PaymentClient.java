package com.switchscale.order.config;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.switchscale.order.dto.PaymentCreateRequest;

@FeignClient(name = "payment-service", url = "${payment.service.url:http://localhost:8006}")
public interface PaymentClient {

    @PostMapping("/payments")
    Object createPayment(@RequestBody PaymentCreateRequest request);
}
