package com.claimswift.notification.controller;

import java.util.List;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import com.claimswift.notification.entity.Notification;
import com.claimswift.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    @PutMapping("/{id}/read")
    public ResponseEntity<String> markAsRead(@PathVariable Long id) {
        service.markAsRead(id);
        return ResponseEntity.ok("Marked as read");
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<Notification>> getUnread(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getUnread(userId));
    }

    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Long> count(@PathVariable Long userId) {
        return ResponseEntity.ok(service.countUnread(userId));
    }
}