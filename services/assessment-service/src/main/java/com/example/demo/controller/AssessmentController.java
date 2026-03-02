package com.example.demo.controller;

import com.example.demo.entity.Assessment;
import com.example.demo.service.AssessmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/assessments")
public class AssessmentController {

    private final AssessmentService service;

    @PostMapping
    public Assessment create(@RequestParam Long claimId) {
        return service.createAssessment(claimId);
    }

    @PostMapping("/assign")
    public Assessment assign(@RequestParam Long claimId,
                             @RequestParam Long adjusterId,
                             @RequestParam Long managerId) {
        return service.assignAdjuster(claimId, adjusterId, managerId);
    }

    @PostMapping("/approve")
    public Assessment approve(@RequestParam Long claimId,
                              @RequestParam Double amount,
                              @RequestParam Long userId) {
        return service.approve(claimId, amount, userId);
    }

    @PostMapping("/disapprove")
    public Assessment reject(@RequestParam Long claimId,
                             @RequestParam Long userId) {
        return service.reject(claimId, userId);
    }

    @PostMapping("/adjust")
    public Assessment adjust(@RequestParam Long claimId,
                             @RequestParam Double newAmount,
                             @RequestParam Long userId) {
        return service.adjust(claimId, newAmount, userId);
    }
}