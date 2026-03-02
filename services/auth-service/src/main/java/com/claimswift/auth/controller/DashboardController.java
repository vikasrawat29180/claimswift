package com.claimswift.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class DashboardController {

    /* =========================
       USER DASHBOARD
       ========================= */
    @GetMapping("/user/dashboard")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> userDashboard() {
        return ResponseEntity.ok("Welcome to User Dashboard!");
    }

    /* =========================
       MANAGER DASHBOARD
       ========================= */
    @GetMapping("/manager/dashboard")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> managerDashboard() {
        return ResponseEntity.ok("Welcome to Manager Dashboard!");
    }

    /* =========================
       USER SETTINGS (optional)
       ========================= */
    @GetMapping("/user/settings")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> userSettings() {
        return ResponseEntity.ok("User settings page");
    }
}
