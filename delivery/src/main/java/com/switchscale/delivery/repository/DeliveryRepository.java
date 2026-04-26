package com.switchscale.delivery.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.switchscale.delivery.model.DeliveryOrder;

public interface DeliveryRepository extends JpaRepository<DeliveryOrder, Long> {

    Optional<DeliveryOrder> findByOrderId(Long orderId);

    List<DeliveryOrder> findByUserId(String userId);
}
