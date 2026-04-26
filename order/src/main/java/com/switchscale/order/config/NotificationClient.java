package com.switchscale.order.config;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.switchscale.order.dto.NotificationCreateRequest;

@FeignClient(name = "notification-service", url = "${notification.service.url:http://localhost:8008}")
public interface NotificationClient {

    @PostMapping("/notifications")
    Object sendNotification(@RequestBody NotificationCreateRequest request);
}
