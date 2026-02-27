package com.claimswift.notification.service;

import java.util.List;


import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.claimswift.notification.dto.NotificationEventDTO;

import com.claimswift.notification.entity.Notification;
import com.claimswift.notification.entity.NotificationDelivery;

import com.claimswift.notification.repository.NotificationDeliveryRepository;
import com.claimswift.notification.repository.NotificationRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationDeliveryRepository deliveryRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public void processEvent(NotificationEventDTO dto) {

        // 1. Save notification
        Notification notification = new Notification();
        notification.setUserId(dto.getUserId());
        notification.setClaimId(dto.getClaimId());
        notification.setTitle(dto.getTitle());
        notification.setMessage(dto.getMessage());

        notification = notificationRepository.save(notification);

        try {

            // 2. Push via WebSocket
            messagingTemplate.convertAndSend(
                    "/topic/notifications/" + dto.getUserId(),
                    notification
            );

            // 3. Save delivery success
            saveDelivery(notification, "WEBSOCKET", "SENT");

        } catch (Exception e) {
            saveDelivery(notification, "WEBSOCKET", "FAILED");
        }
    }

    private void saveDelivery(Notification notification,
                              String channel,
                              String status) {

        NotificationDelivery delivery = new NotificationDelivery();
        delivery.setNotification(notification);
        delivery.setChannel(channel);
        delivery.setDeliveryStatus(status);

        deliveryRepository.save(delivery);
    }

    public void markAsRead(Long id) {

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    public List<Notification> getUnread(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalse(userId);
    }

    public Long countUnread(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }
}