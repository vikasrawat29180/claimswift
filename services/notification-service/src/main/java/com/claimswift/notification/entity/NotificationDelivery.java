package com.claimswift.notification.entity;


import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "notification_delivery")
@Getter
@Setter
public class NotificationDelivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "notification_id")
    private Notification notification;

    @Column(nullable = false)
    private String channel; // WEBSOCKET, EMAIL etc.

    @Column(nullable = false)
    private String deliveryStatus; // SENT, FAILED

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @PrePersist
    public void onCreate() {
        this.timestamp = LocalDateTime.now();
    }
}