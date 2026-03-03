package com.example.demo.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import com.example.demo.dto.ClaimDto;
import com.example.demo.entity.Claim;
import com.example.demo.entity.ClaimStatus;
import com.example.demo.entity.ClaimStatusHistory;
import com.example.demo.service.ClaimWorkflowservice;

import java.util.List;

@RestController
@RequestMapping("/claims")
@RequiredArgsConstructor
public class ClaimController {

    private final ClaimWorkflowservice service;

    // ================================
    // 1️⃣ Submit Claim
    // ================================
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public Claim submit(@RequestBody ClaimDto dto) {
        return service.submitClaim(dto);
    }

    // ================================
    // 2️⃣ Update Claim Status
    // ================================
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('MANAGER')")
    public Claim updateStatus(
            @PathVariable Long id,
            @RequestParam ClaimStatus status) {

        return service.updateStatus(id, status);
    }

    // ================================
    // 3️⃣ Get Current Status
    // ================================
    @GetMapping("/{id}/status")
    public ClaimStatus getStatus(@PathVariable Long id) {
        return service.getStatus(id);
    }

    // ================================
    // 4️⃣ Get Status History
    // ================================
    @GetMapping("/{id}/history")
    public List<ClaimStatusHistory> getHistory(
            @PathVariable Long id) {

        return service.getHistory(id);
    }

    // ================================
    // 5️⃣ 🔥 Status-Based Filter
    // ================================
    @GetMapping("/status")
    public List<Claim> getByStatus(
            @RequestParam ClaimStatus status) {

        return service.getClaimsByStatus(status);
    }
    
    @GetMapping("/test")
    public String test(Authentication auth){
        return "Authorized access for user: " + auth.getName();
    }
}