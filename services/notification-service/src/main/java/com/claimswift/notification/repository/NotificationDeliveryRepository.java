package com.claimswift.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.claimswift.notification.entity.NotificationDelivery;

public interface NotificationDeliveryRepository
extends JpaRepository<NotificationDelivery, Long> {
}