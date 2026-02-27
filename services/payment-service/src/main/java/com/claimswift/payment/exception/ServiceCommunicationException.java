package com.claimswift.payment.exception;

/**
 * Service Communication Exception
 * 
 * Thrown when communication with other microservices fails
 */
public class ServiceCommunicationException extends PaymentException {

    public ServiceCommunicationException(String message) {
        super("SERVICE_COMMUNICATION_ERROR", message);
    }

    public ServiceCommunicationException(String message, Throwable cause) {
        super("SERVICE_COMMUNICATION_ERROR", message, cause);
    }
}
