package com.claimswift.payment.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Audit Payment Entity - Tracks all payment-related actions
 * 
 * Database: payment_db
 * Table: audit_payments
 */
@Entity
@Table(name = "audit_payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "payment_id", nullable = false)
    private Long paymentId;

    @Column(name = "action", nullable = false, length = 100)
    private String action;

    @Column(name = "performed_by")
    private Long performedBy;

    @Column(name = "previous_value", length = 500)
    private String previousValue;

    @Column(name = "new_value", length = 500)
    private String newValue;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "description", length = 1000)
    private String description;

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}
