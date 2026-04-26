package com.switchscale.order.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.switchscale.order.Event.OrderPlacedEvent;
import com.switchscale.order.config.CartClient;
import com.switchscale.order.config.DeliveryClient;
import com.switchscale.order.config.NotificationClient;
import com.switchscale.order.config.PaymentClient;
import com.switchscale.order.dto.CartDTO;
import com.switchscale.order.dto.DeliveryCreateRequest;
import com.switchscale.order.dto.NotificationCreateRequest;
import com.switchscale.order.dto.PaymentCreateRequest;
import com.switchscale.order.model.OrderItem;
import com.switchscale.order.model.OrderModel;
import com.switchscale.order.repository.OrderRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    
    private final CartClient cartClient;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;
    private final OrderRepository orderRepository;
    private final PaymentClient paymentClient;
    private final DeliveryClient deliveryClient;
    private final NotificationClient notificationClient;

    @Value("${order.events.kafka.enabled:false}")
    private boolean kafkaEventsEnabled;

    @Transactional
    public OrderModel placeOrder(String userId,Long addressId){


        // Fetch Cart data synchronously using Feign Client
        CartDTO cart=cartClient.getCart(userId);

        if(cart == null || cart.getCartItems()==null || cart.getCartItems().isEmpty()){
            throw new RuntimeException("Cart is empty. Cannot place order.");
        }
        
        OrderModel order = new OrderModel();
        order.setUserId(userId);
        order.setAddressId(addressId);
        order.setTotalAmount(cart.getCartTotal());
        order.setStatus("CONFIRMED");

        List<OrderItem> orderItems=cart.getCartItems().stream().map(cartItem->{
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductName(cartItem.getProductName());
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(cartItem.getPrice());
            return orderItem;
        }).collect(Collectors.toList());

        order.setItems(orderItems);

        OrderModel savedOrder=orderRepository.save(order);

        // Fire the Kafka event after successful order placement
        
        if (kafkaEventsEnabled) {
            try {
                OrderPlacedEvent event = new OrderPlacedEvent(savedOrder.getId(), savedOrder.getUserId());
                kafkaTemplate.send("order-placed", event);
            } catch (Exception ex) {
                // Order creation should not fail if event infrastructure is temporarily unavailable.
                log.warn("Order {} created but event publish failed: {}", savedOrder.getId(), ex.getMessage());
            }
        }

        createPayment(savedOrder);
        createDelivery(savedOrder);
        sendOrderNotification(savedOrder);

        try {
            cartClient.clearCart(userId);
        } catch (Exception ex) {
            // Keep order successful even if cart clear hits a transient cart-service issue.
            log.warn("Order {} created but cart clear failed for user {}: {}", savedOrder.getId(), userId, ex.getMessage());
        }

        return savedOrder;
        
    }

    private void createPayment(OrderModel order) {
        try {
            PaymentCreateRequest request = new PaymentCreateRequest(
                    order.getId(),
                    order.getUserId(),
                    order.getTotalAmount(),
                    "COD");
            paymentClient.createPayment(request);
        } catch (Exception ex) {
            log.warn("Order {} created but payment creation failed: {}", order.getId(), ex.getMessage());
        }
    }

    private void createDelivery(OrderModel order) {
        try {
            DeliveryCreateRequest request = new DeliveryCreateRequest(
                    order.getId(),
                    order.getUserId(),
                    order.getAddressId(),
                    20);
            deliveryClient.createDelivery(request);
        } catch (Exception ex) {
            log.warn("Order {} created but delivery creation failed: {}", order.getId(), ex.getMessage());
        }
    }

    private void sendOrderNotification(OrderModel order) {
        try {
            NotificationCreateRequest request = new NotificationCreateRequest(
                    order.getId(),
                    order.getUserId(),
                    "Order Confirmed",
                    "Your order #" + order.getId() + " is confirmed and being prepared.",
                    "IN_APP",
                    null);
            notificationClient.sendNotification(request);
        } catch (Exception ex) {
            log.warn("Order {} created but notification dispatch failed: {}", order.getId(), ex.getMessage());
        }
    }





    
}
