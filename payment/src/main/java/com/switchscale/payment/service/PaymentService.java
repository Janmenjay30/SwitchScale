package com.switchscale.payment.service;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.switchscale.payment.dto.CreatePaymentRequest;
import com.switchscale.payment.model.PaymentStatus;
import com.switchscale.payment.model.PaymentTransaction;
import com.switchscale.payment.repository.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public PaymentTransaction createPayment(CreatePaymentRequest request) {
        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "amount must be greater than 0");
        }

        return paymentRepository.findByOrderId(request.getOrderId()).orElseGet(() -> {
            PaymentTransaction transaction = new PaymentTransaction();
            transaction.setOrderId(request.getOrderId());
            transaction.setUserId(request.getUserId());
            transaction.setAmount(request.getAmount());
            transaction.setPaymentMethod(request.getPaymentMethod().trim().toUpperCase(Locale.ROOT));
            transaction.setStatus(resolveInitialStatus(transaction.getPaymentMethod()));
            transaction.setTransactionReference("PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT));
            return paymentRepository.save(transaction);
        });
    }

    public PaymentTransaction getById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found: " + id));
    }

    public PaymentTransaction getByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found for orderId: " + orderId));
    }

    public List<PaymentTransaction> getByUserId(String userId) {
        return paymentRepository.findByUserId(userId);
    }

    public List<PaymentTransaction> getAll() {
        return paymentRepository.findAll();
    }

    @Transactional
    public PaymentTransaction updateStatus(Long id, PaymentStatus status) {
        PaymentTransaction payment = getById(id);
        payment.setStatus(status);
        return paymentRepository.save(payment);
    }

    private PaymentStatus resolveInitialStatus(String paymentMethod) {
        if ("COD".equalsIgnoreCase(paymentMethod)) {
            return PaymentStatus.SUCCESS;
        }
        return PaymentStatus.PENDING;
    }
}
