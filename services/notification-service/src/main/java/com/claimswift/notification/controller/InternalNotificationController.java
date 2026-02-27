package com.claimswift.notification.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.claimswift.notification.dto.NotificationEventDTO;
import com.claimswift.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal/notifications")
@RequiredArgsConstructor
public class InternalNotificationController {

    private final NotificationService service;

    @PostMapping("/event")
    public ResponseEntity<String> receiveEvent(
            @RequestBody NotificationEventDTO dto) {

        service.processEvent(dto);
        return ResponseEntity.ok("Notification processed");
    }
}