package com.claimswift.reporting.service;

import com.claimswift.reporting.entity.ClaimMetrics;
import com.claimswift.reporting.repository.ClaimMetricsRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class PdfReportService {

    private final ClaimMetricsRepository repository;
    private final AuditService auditService;

    public byte[] generatePdf(String generatedBy) throws Exception {

        ClaimMetrics metrics =
                repository.findTopByOrderByCalculatedAtDesc();

        if (metrics == null) {
            throw new RuntimeException("No metrics available");
        }

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);

        document.open();
        document.add(new Paragraph("Claim Metrics Report"));
        document.add(new Paragraph("Total Claims: " + metrics.getTotalClaims()));
        document.add(new Paragraph("Approved: " + metrics.getApprovedClaims()));
        document.add(new Paragraph("---- PAYMENT METRICS ----"));
        document.add(new Paragraph("Total Payments: " + metrics.getTotalPayments()));
        document.add(new Paragraph("Total Amount Settled: ₹ " + metrics.getTotalAmountSettled()));
        document.add(new Paragraph("Avg Payment Processing Time: "
                + metrics.getAvgSettlementProcessingTime()));
        
        document.close();

        

        auditService.saveAudit(generatedBy, "PDF_REPORT");

        return out.toByteArray();
    }
}