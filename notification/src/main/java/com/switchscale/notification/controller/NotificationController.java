package com.switchscale.notification.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.switchscale.notification.dto.CreateNotificationRequest;
import com.switchscale.notification.model.NotificationRecord;
import com.switchscale.notification.service.NotificationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/notifications")
@Validated
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<NotificationRecord> sendNotification(@Valid @RequestBody CreateNotificationRequest request) {
        return ResponseEntity.ok(notificationService.sendNotification(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationRecord> getById(@PathVariable String id) {
        return ResponseEntity.ok(notificationService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<NotificationRecord>> listNotifications(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) Long orderId) {

        if (orderId != null) {
            return ResponseEntity.ok(notificationService.getByOrderId(orderId));
        }

        if (userId != null && !userId.isBlank()) {
            return ResponseEntity.ok(notificationService.getByUserId(userId));
        }

        return ResponseEntity.ok(notificationService.getAll());
    }
}
