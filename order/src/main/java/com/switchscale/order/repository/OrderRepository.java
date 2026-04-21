package com.switchscale.order.repository;

import com.switchscale.order.model.OrderModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderModel, Long> {
    
    // This will be useful later when you want to build an "Order History" page
    List<OrderModel> findByUserId(String userId);
}