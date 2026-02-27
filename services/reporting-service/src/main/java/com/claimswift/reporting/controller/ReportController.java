package com.claimswift.reporting.controller;

import com.claimswift.reporting.entity.ClaimMetrics;
import com.claimswift.reporting.repository.ClaimMetricsRepository;
import com.claimswift.reporting.service.PdfReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ClaimMetricsRepository repository;
    private final PdfReportService pdfService;

    @GetMapping("/metrics")
    public ResponseEntity<ClaimMetrics> getMetrics() {
        return ResponseEntity.ok(
                repository.findTopByOrderByCalculatedAtDesc()
        );
    }

    @GetMapping("/metrics/pdf")
    public ResponseEntity<byte[]> downloadPdf(
            @RequestParam String user) throws Exception {

        byte[] pdf = pdfService.generatePdf(user);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=claim-report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}