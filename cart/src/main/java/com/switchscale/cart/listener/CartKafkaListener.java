package com.switchscale.cart.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.switchscale.cart.event.OrderPlacedEvent;
import com.switchscale.cart.service.CartService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CartKafkaListener {

    private static final Logger log = LoggerFactory.getLogger(CartKafkaListener.class);

    private final CartService cartService;

    // Listen to order placed events and clear user cart after successful checkout.
    @KafkaListener(topics = {"order_placed", "order-placed"}, groupId = "cart_group")
    public void consumeOrderPlacedEvent(OrderPlacedEvent event) {
        if (event == null || event.getUserId() == null || event.getUserId().isBlank()) {
            log.warn("Received invalid order placed event: {}", event);
            return;
        }

        String userId = event.getUserId();
        boolean cleared = cartService.clearCartIfExists(userId);
        if (cleared) {
            log.info("Cart cleared for userId={} after order placement", userId);
        } else {
            log.info("No cart found to clear for userId={} after order placement", userId);
        }
    }

}
