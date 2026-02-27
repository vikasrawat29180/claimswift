package com.claimswift.notification.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.claimswift.notification.entity.Notification;
import com.claimswift.notification.enums.NotificationStatus;

public interface NotificationRepository 
extends JpaRepository<Notification, Long> {

List<Notification> findByUserIdAndIsReadFalse(Long userId);

Long countByUserIdAndIsReadFalse(Long userId);
}

