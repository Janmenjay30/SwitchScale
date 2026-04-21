package com.switchscale.order.controller;

import com.switchscale.order.model.OrderModel;
import com.switchscale.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout/{userId}")
    public ResponseEntity<OrderModel> checkout(@PathVariable String userId, @RequestParam Long addressId) {
        // e.g. POST http://localhost:8083/orders/checkout/1?addressId=4
        return ResponseEntity.ok(orderService.placeOrder(userId, addressId));
    }
}