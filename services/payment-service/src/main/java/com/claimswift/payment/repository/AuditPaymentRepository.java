package com.claimswift.payment.repository;

import com.claimswift.payment.entity.AuditPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Audit Payment Repository
 * 
 * JPA Repository for AuditPayment entity
 */
@Repository
public interface AuditPaymentRepository extends JpaRepository<AuditPayment, Long> {

    List<AuditPayment> findByPaymentIdOrderByTimestampDesc(Long paymentId);

    List<AuditPayment> findByPerformedBy(Long performedBy);
}
