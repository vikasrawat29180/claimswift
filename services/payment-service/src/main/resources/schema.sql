-- Payment Service Database Schema
-- Database: payment_db

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS payment_db;
USE payment_db;

-- =============================================
-- PAYMENTS TABLE
-- Stores payment records for approved claims
-- =============================================
CREATE TABLE IF NOT EXISTS payments (
    payment_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    claim_id BIGINT NOT NULL,
    approved_amount DECIMAL(15, 2) NOT NULL,
    payment_reference VARCHAR(100) UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'INITIATED',
    processed_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_claim_id (claim_id),
    INDEX idx_payment_reference (payment_reference),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- TRANSACTIONS TABLE
-- Stores transaction details for each payment
-- =============================================
CREATE TABLE IF NOT EXISTS transactions (
    transaction_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    payment_id BIGINT NOT NULL,
    bank_reference VARCHAR(100),
    transaction_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    transaction_time DATETIME,
    failure_reason VARCHAR(500),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_transactions_payment 
        FOREIGN KEY (payment_id) REFERENCES payments(payment_id) ON DELETE CASCADE,
    
    INDEX idx_payment_id (payment_id),
    INDEX idx_bank_reference (bank_reference)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- AUDIT_PAYMENTS TABLE
-- Tracks all payment-related actions for compliance
-- =============================================
CREATE TABLE IF NOT EXISTS audit_payments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    payment_id BIGINT NOT NULL,
    action VARCHAR(100) NOT NULL,
    performed_by BIGINT,
    previous_value VARCHAR(500),
    new_value VARCHAR(500),
    description VARCHAR(1000),
    timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_payment_id (payment_id),
    INDEX idx_performed_by (performed_by),
    INDEX idx_timestamp (timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- SAMPLE DATA
-- =============================================
-- (No sample data - payment records are created during processing)

-- =============================================
-- NOTES
-- =============================================
-- 1. claim_id is BIGINT - consistent with global ID strategy
-- 2. All tables use utf8mb4 encoding for proper Unicode support
-- 3. Foreign key constraint ensures referential integrity
-- 4. Indexes are created for frequently queried columns
-- 5. Audit trail is maintained for compliance
