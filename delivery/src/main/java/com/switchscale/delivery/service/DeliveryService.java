package com.switchscale.delivery.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.switchscale.delivery.dto.CreateDeliveryRequest;
import com.switchscale.delivery.model.DeliveryOrder;
import com.switchscale.delivery.model.DeliveryStatus;
import com.switchscale.delivery.repository.DeliveryRepository;

@Service
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    public DeliveryService(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    @Transactional
    public DeliveryOrder createDelivery(CreateDeliveryRequest request) {
        return deliveryRepository.findByOrderId(request.getOrderId()).orElseGet(() -> {
            DeliveryOrder deliveryOrder = new DeliveryOrder();
            deliveryOrder.setOrderId(request.getOrderId());
            deliveryOrder.setUserId(request.getUserId());
            deliveryOrder.setAddressId(request.getAddressId());
            deliveryOrder.setStatus(DeliveryStatus.PLACED);
            deliveryOrder.setTrackingId("DLV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT));
            deliveryOrder.setEstimatedDeliveryAt(LocalDateTime.now().plusMinutes(resolveEtaMinutes(request.getEtaMinutes())));
            deliveryOrder.setDeliveryPartner("SwitchScale Express");
            return deliveryRepository.save(deliveryOrder);
        });
    }

    public DeliveryOrder getById(Long id) {
        return deliveryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Delivery not found: " + id));
    }

    public DeliveryOrder getByOrderId(Long orderId) {
        return deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Delivery not found for orderId: " + orderId));
    }

    public List<DeliveryOrder> getByUserId(String userId) {
        return deliveryRepository.findByUserId(userId);
    }

    public List<DeliveryOrder> getAll() {
        return deliveryRepository.findAll();
    }

    @Transactional
    public DeliveryOrder updateStatus(Long id, DeliveryStatus status) {
        DeliveryOrder deliveryOrder = getById(id);
        deliveryOrder.setStatus(status);
        return deliveryRepository.save(deliveryOrder);
    }

    private int resolveEtaMinutes(Integer etaMinutes) {
        if (etaMinutes == null || etaMinutes <= 0) {
            return 20;
        }
        return etaMinutes;
    }
}
