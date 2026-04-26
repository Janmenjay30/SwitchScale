package com.switchscale.payment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.switchscale.payment.model.PaymentTransaction;

public interface PaymentRepository extends JpaRepository<PaymentTransaction, Long> {

    Optional<PaymentTransaction> findByOrderId(Long orderId);

    List<PaymentTransaction> findByUserId(String userId);
}
