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
import com.switchscale.order.dto.CartDTO;
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

        try {
            cartClient.clearCart(userId);
        } catch (Exception ex) {
            // Keep order successful even if cart clear hits a transient cart-service issue.
            log.warn("Order {} created but cart clear failed for user {}: {}", savedOrder.getId(), userId, ex.getMessage());
        }

        return savedOrder;
        
    }





    
}
