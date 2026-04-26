package com.switchscale.notification.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.switchscale.notification.dto.CreateNotificationRequest;
import com.switchscale.notification.model.NotificationRecord;
import com.switchscale.notification.model.NotificationStatus;

@Service
public class NotificationService {

    private final JavaMailSender javaMailSender;
    private final Map<String, NotificationRecord> notificationStore = new ConcurrentHashMap<>();

    @Value("${spring.mail.username:}")
    private String fromAddress;

    public NotificationService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public NotificationRecord sendNotification(CreateNotificationRequest request) {
        NotificationRecord record = new NotificationRecord();
        record.setId("NTF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT));
        record.setOrderId(request.getOrderId());
        record.setUserId(request.getUserId());
        record.setTitle(request.getTitle());
        record.setMessage(request.getMessage());
        record.setChannel(resolveChannel(request.getChannel()));
        record.setEmail(request.getEmail());
        record.setCreatedAt(LocalDateTime.now());

        if ("EMAIL".equals(record.getChannel())) {
            sendEmail(record);
        } else {
            record.setStatus(NotificationStatus.SENT);
            record.setSentAt(LocalDateTime.now());
        }

        notificationStore.put(record.getId(), record);
        return record;
    }

    public NotificationRecord getById(String id) {
        NotificationRecord record = notificationStore.get(id);
        if (record == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found: " + id);
        }
        return record;
    }

    public List<NotificationRecord> getAll() {
        List<NotificationRecord> notifications = new ArrayList<>(notificationStore.values());
        notifications.sort(Comparator.comparing(NotificationRecord::getCreatedAt).reversed());
        return notifications;
    }

    public List<NotificationRecord> getByUserId(String userId) {
        List<NotificationRecord> notifications = new ArrayList<>();
        for (NotificationRecord record : notificationStore.values()) {
            if (userId.equals(record.getUserId())) {
                notifications.add(record);
            }
        }
        notifications.sort(Comparator.comparing(NotificationRecord::getCreatedAt).reversed());
        return notifications;
    }

    public List<NotificationRecord> getByOrderId(Long orderId) {
        List<NotificationRecord> notifications = new ArrayList<>();
        for (NotificationRecord record : notificationStore.values()) {
            if (orderId.equals(record.getOrderId())) {
                notifications.add(record);
            }
        }
        notifications.sort(Comparator.comparing(NotificationRecord::getCreatedAt).reversed());
        return notifications;
    }

    private String resolveChannel(String rawChannel) {
        if (rawChannel == null || rawChannel.isBlank()) {
            return "IN_APP";
        }
        return rawChannel.trim().toUpperCase(Locale.ROOT);
    }

    private void sendEmail(NotificationRecord record) {
        if (record.getEmail() == null || record.getEmail().isBlank()) {
            record.setStatus(NotificationStatus.FAILED);
            record.setFailureReason("email is required for EMAIL channel");
            return;
        }

        try {
            SimpleMailMessage emailMessage = new SimpleMailMessage();
            emailMessage.setTo(record.getEmail());
            emailMessage.setSubject(record.getTitle());
            emailMessage.setText(record.getMessage());
            if (fromAddress != null && !fromAddress.isBlank()) {
                emailMessage.setFrom(fromAddress);
            }
            javaMailSender.send(emailMessage);
            record.setStatus(NotificationStatus.SENT);
            record.setSentAt(LocalDateTime.now());
        } catch (Exception ex) {
            record.setStatus(NotificationStatus.FAILED);
            record.setFailureReason(ex.getMessage());
        }
    }
}
