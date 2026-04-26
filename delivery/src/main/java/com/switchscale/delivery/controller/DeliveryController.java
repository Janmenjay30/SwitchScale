package com.switchscale.delivery.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.switchscale.delivery.dto.CreateDeliveryRequest;
import com.switchscale.delivery.dto.UpdateDeliveryStatusRequest;
import com.switchscale.delivery.model.DeliveryOrder;
import com.switchscale.delivery.service.DeliveryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/deliveries")
@Validated
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @PostMapping
    public ResponseEntity<DeliveryOrder> createDelivery(@Valid @RequestBody CreateDeliveryRequest request) {
        return ResponseEntity.ok(deliveryService.createDelivery(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryOrder> getById(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryService.getById(id));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<DeliveryOrder> getByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(deliveryService.getByOrderId(orderId));
    }

    @GetMapping
    public ResponseEntity<List<DeliveryOrder>> listDeliveries(@RequestParam(required = false) String userId) {
        if (userId != null && !userId.isBlank()) {
            return ResponseEntity.ok(deliveryService.getByUserId(userId));
        }
        return ResponseEntity.ok(deliveryService.getAll());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<DeliveryOrder> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDeliveryStatusRequest request) {
        return ResponseEntity.ok(deliveryService.updateStatus(id, request.getStatus()));
    }
}
