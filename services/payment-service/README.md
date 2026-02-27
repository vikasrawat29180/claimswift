# Payment Service - ClaimSwift Insurance

## Table of Contents
1. [Project Overview](#project-overview)
2. [Technology Stack](#technology-stack)
3. [Architecture](#architecture)
4. [Database Design](#database-design)
5. [API Endpoints](#api-endpoints)
6. [Integration Points](#integration-points)
7. [Configuration](#configuration)
8. [Building and Running](#building-and-running)
9. [Testing](#testing)
10. [Project Structure](#project-structure)
11. [Error Handling](#error-handling)
12. [Security](#security)
13. [Swagger Documentation](#swagger-documentation)

---

## Project Overview

**Payment Service** is a Spring Boot microservice designed to process payments for approved insurance claims in the ClaimSwift system. It acts as the financial processing layer, handling the entire payment lifecycle from initiation to completion, including integration with external services like Claim Service and Notification Service.

### Purpose
The service enables ClaimSwift to:
- Process payments for approved insurance claims
- Maintain transaction records for all payment activities
- Provide audit trails for compliance and regulatory requirements
- Send notifications to policyholders upon payment completion
- Handle payment failures and provide retry mechanisms

### Key Features
- Payment processing for approved claims
- Transaction management with bank reference tracking
- Comprehensive audit logging
- Automatic claim status updates after successful payments
- Notification triggers for payment events
- Payment retry functionality for failed transactions
- RESTful API with standardized response format
- Swagger/OpenAPI documentation

---

## Technology Stack

| Category | Technology | Version |
|----------|------------|---------|
| **Framework** | Spring Boot | 3.4.2 |
| **Language** | Java | 17 |
| **Build Tool** | Maven | 3.8+ |
| **Database** | MySQL | 8.0+ |
| **Test Database** | H2 Database | - |
| **API Documentation** | SpringDoc OpenAPI | 2.3.0 |
| **REST Client** | Spring Cloud OpenFeign | 4.3.0 |
| **ORM** | Spring Data JPA | - |
| **Security** | Spring Security | - |
| **Validation** | Spring Validation | - |
| **Lombok** | Project Lombok | - |

---

## Architecture

The Payment Service follows a layered architecture pattern typical of Spring Boot applications:

```
┌─────────────────────────────────────────────────────────────┐
│                    Payment Service                          │
├─────────────────────────────────────────────────────────────┤
│  Controller Layer (REST API)                                │
│  - PaymentController                                       │
├─────────────────────────────────────────────────────────────┤
│  Service Layer (Business Logic)                            │
│  - PaymentService                                          │
│  - PaymentServiceImpl                                      │
├─────────────────────────────────────────────────────────────┤
│  Repository Layer (Data Access)                            │
│  - PaymentRepository                                        │
│  - TransactionRepository                                    │
│  - AuditPaymentRepository                                   │
├─────────────────────────────────────────────────────────────┤
│  Client Layer (External Services)                          │
│  - ClaimClient (Feign)                                      │
│  - NotificationClient (Feign)                              │
├─────────────────────────────────────────────────────────────┤
│  Entity Layer (JPA)                                        │
│  - Payment                                                  │
│  - Transaction                                              │
│  - AuditPayment                                             │
└─────────────────────────────────────────────────────────────┘
```

### Integration Architecture

```
┌──────────────┐      ┌─────────────────┐      ┌──────────────────┐
│   Client     │─────▶│  Payment Service │─────▶│  Claim Service   │
│  (Mobile/    │      │    (Port 8084)    │      │  (Port 8081)     │
│   Web)       │◀─────│                  │◀─────│                  │
└──────────────┘      └────────┬────────┘      └──────────────────┘
                               │
                               ▼
                        ┌─────────────────┐
                        │ Notification    │
                        │ Service         │
                        │ (Port 8085)     │
                        └─────────────────┘
```

---

## Database Design

### Database: `payment_db`

The service uses three main tables to manage payment data:

#### 1. Payments Table (`payments`)
Stores payment records for approved insurance claims.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| payment_id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique payment identifier |
| claim_id | BIGINT | NOT NULL | Reference to claim |
| approved_amount | DECIMAL(15,2) | NOT NULL | Payment amount |
| payment_reference | VARCHAR(100) | UNIQUE | Unique payment reference (e.g., PAY-XXXXXXXX) |
| status | VARCHAR(20) | NOT NULL | Payment status (INITIATED/SUCCESS/FAILED) |
| processed_at | DATETIME | NULLABLE | Timestamp when payment was processed |
| created_at | DATETIME | NOT NULL, auto-generated | Record creation timestamp |
| updated_at | DATETIME | auto-generated | Record update timestamp |

#### 2. Transactions Table (`transactions`)
Stores transaction details from payment gateway.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| transaction_id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique transaction identifier |
| payment_id | BIGINT | FOREIGN KEY | Reference to payment |
| bank_reference | VARCHAR(100) | NOT NULL | Bank/Payment gateway reference |
| transaction_status | VARCHAR(20) | NOT NULL | Transaction status (COMPLETED/FAILED) |
| failure_reason | TEXT | NULLABLE | Reason for failure if applicable |
| transaction_time | DATETIME | NOT NULL | Transaction timestamp |

#### 3. Audit Payments Table (`audit_payments`)
Stores audit trail for compliance and regulatory requirements.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| audit_id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique audit record identifier |
| payment_id | BIGINT | NOT NULL | Reference to payment |
| action | VARCHAR(50) | NOT NULL | Action performed (e.g., PAYMENT_INITIATED, PAYMENT_SUCCESS) |
| performed_by | BIGINT | NULLABLE | User who performed the action |
| description | TEXT | NOT NULL | Description of the action |
| previous_value | TEXT | NULLABLE | Previous value (for updates) |
| timestamp | DATETIME | NOT NULL | When the action occurred |

---

## API Endpoints

### Base URL
```
http://localhost:8084/api/v1/payments
```

### Endpoints

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| **POST** | `/api/v1/payments` | Process a new payment | PaymentRequest | PaymentResponse |
| **GET** | `/api/v1/payments/{paymentId}` | Get payment by ID | - | PaymentResponse |
| **GET** | `/api/v1/payments/claim/{claimId}` | Get payment by claim ID | - | PaymentResponse |
| **GET** | `/api/v1/payments` | Get all payments | - | List<PaymentResponse> |
| **POST** | `/api/v1/payments/{paymentId}/retry` | Retry failed payment | - | PaymentResponse |
| **GET** | `/api/v1/payments/health` | Health check | - | String |

### API Response Structures

#### Success Response
```json
{
  "success": true,
  "timestamp": "2026-02-24T18:30:00",
  "data": {
    "paymentId": 1,
    "claimId": 100,
    "approvedAmount": 50000.00,
    "paymentReference": "PAY-A1B2C3D4",
    "status": "SUCCESS",
    "processedAt": "2026-02-24T18:30:00",
    "createdAt": "2026-02-24T18:25:00"
  },
  "error": null
}
```

#### Error Response
```json
{
  "success": false,
  "timestamp": "2026-02-24T18:30:00",
  "data": null,
  "error": {
    "code": "CLAIM_NOT_FOUND",
    "message": "Claim ID does not exist"
  }
}
```

### Request/Response DTOs

#### PaymentRequest
```json
{
  "claimId": 100,
  "approvedAmount": 50000.00
}
```

#### PaymentResponse
```json
{
  "paymentId": 1,
  "claimId": 100,
  "approvedAmount": 50000.00,
  "paymentReference": "PAY-A1B2C3D4",
  "status": "SUCCESS",
  "processedAt": "2026-02-24T18:30:00",
  "createdAt": "2026-02-24T18:25:00",
  "transaction": {
    "transactionId": 1,
    "bankReference": "TXN-1234567890",
    "transactionStatus": "COMPLETED",
    "transactionTime": "2026-02-24T18:30:00",
    "failureReason": null
  }
}
```

---

## Integration Points

The Payment Service integrates with two external microservices:

### 1. Claim Service (Port 8081)

**Purpose:** Verify claim status and update to PAID after successful payment

**Endpoints Used:**
- `GET /api/v1/claims/{claimId}` - Fetch claim details
- `PUT /api/v1/claims/{claimId}/status` - Update claim status

**Integration Rules:**
- Payment is allowed only if claim status = APPROVED
- After successful payment, claim status is updated to PAID
- If claim status update fails, compensation logic is applied

**Feign Client:** [`ClaimClient.java`](src/main/java/com/claimswift/payment/client/ClaimClient.java)

### 2. Notification Service (Port 8085)

**Purpose:** Send notifications to policyholders about payment events

**Endpoints Used:**
- `POST /api/v1/notifications` - Send notification

**Integration Rules:**
- Notifications are sent after successful payment
- Notifications are sent after payment failure
- Notification failures are non-blocking (payment continues)

**Feign Client:** [`NotificationClient.java`](src/main/java/com/claimswift/payment/client/NotificationClient.java)

---

## Configuration

### Application Properties

The service is configured via [`application.yml`](src/main/resources/application.yml):

```yaml
server:
  port: 8084

spring:
  application:
    name: payment-service

  datasource:
    url: jdbc:mysql://localhost:3306/payment_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
    username: root
    password: 12345
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
        format_sql: true

# Service URLs for inter-service communication
services:
  claim-service:
    url: http://localhost:8081
  notification-service:
    url: http://localhost:8085

# Logging configuration
logging:
  level:
    com.claimswift.payment: DEBUG
    org.springframework.cloud.openfeign: DEBUG

# Swagger/OpenAPI documentation
springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
```

### Configuration Parameters

| Parameter | Description | Default Value |
|-----------|-------------|---------------|
| `server.port` | Service port | 8084 |
| `spring.datasource.url` | Database connection URL | - |
| `spring.datasource.username` | Database username | root |
| `spring.datasource.password` | Database password | - |
| `spring.jpa.hibernate.ddl-auto` | Hibernate auto DDL | update |
| `services.claim-service.url` | Claim Service URL | http://localhost:8081 |
| `services.notification-service.url` | Notification Service URL | http://localhost:8085 |

---

## Building and Running

### Prerequisites

Before running the service, ensure you have:
- **Java Development Kit (JDK)** 17 or higher
- **Maven** 3.8 or higher
- **MySQL** 8.0 or higher (running on localhost:3306)
- **Internet connection** (for Maven dependencies)

### Build the Project

```bash
# Navigate to project directory
cd payment-service

# Build the project
mvn clean install
```

### Run the Service

```bash
# Run using Maven
mvn spring-boot:run
```

The service will start on `http://localhost:8084`

### Run Tests

```bash
# Run all tests
mvn test

# Run tests with coverage
mvn test -Dcoverage
```

---

## Testing

### Test Classes

| Test Class | Description |
|------------|-------------|
| [`PaymentServiceApplicationTests.java`](src/test/java/com/claimswift/payment/PaymentServiceApplicationTests.java) | Main application context test |
| [`PaymentControllerTest.java`](src/test/java/com/claimswift/payment/controller/PaymentControllerTest.java) | Controller layer tests |
| [`PaymentServiceTest.java`](src/test/java/com/claimswift/payment/service/PaymentServiceTest.java) | Service layer tests |
| [`DtoTest.java`](src/test/java/com/claimswift/payment/dto/DtoTest.java) | DTO tests |
| [`EntityTest.java`](src/test/java/com/claimswift/payment/entity/EntityTest.java) | Entity tests |

### Test Configuration

Tests use H2 in-memory database configured in [`src/test/resources/application.yml`](src/test/resources/application.yml)

---

## Project Structure

```
payment-service/
├── pom.xml                           # Maven configuration
├── README.md                         # Project documentation
├── src/
│   ├── main/
│   │   ├── java/com/claimswift/payment/
│   │   │   ├── PaymentServiceApplication.java    # Main application
│   │   │   ├── controller/
│   │   │   │   └── PaymentController.java         # REST endpoints
│   │   │   ├── service/
│   │   │   │   ├── PaymentService.java             # Service interface
│   │   │   │   └── PaymentServiceImpl.java         # Service implementation
│   │   │   ├── repository/
│   │   │   │   ├── PaymentRepository.java          # Payment data access
│   │   │   │   ├── TransactionRepository.java      # Transaction data access
│   │   │   │   └── AuditPaymentRepository.java     # Audit data access
│   │   │   ├── entity/
│   │   │   │   ├── Payment.java                    # Payment entity
│   │   │   │   ├── PaymentStatus.java               # Payment status enum
│   │   │   │   ├── Transaction.java                 # Transaction entity
│   │   │   │   ├── TransactionStatus.java           # Transaction status enum
│   │   │   │   └── AuditPayment.java               # Audit entity
│   │   │   ├── dto/
│   │   │   │   ├── ApiResponse.java                 # Generic API response
│   │   │   │   ├── PaymentRequest.java              # Payment request DTO
│   │   │   │   ├── PaymentResponse.java             # Payment response DTO
│   │   │   │   ├── ClaimResponse.java               # Claim response DTO
│   │   │   │   ├── ClaimStatusUpdateRequest.java    # Status update DTO
│   │   │   │   └── NotificationRequest.java         # Notification DTO
│   │   │   ├── client/
│   │   │   │   ├── ClaimClient.java                 # Claim Service Feign client
│   │   │   │   └── NotificationClient.java           # Notification Service Feign client
│   │   │   ├── config/
│   │   │   │   ├── FeignConfig.java                 # Feign configuration
│   │   │   │   ├── SecurityConfig.java               # Security configuration
│   │   │   │   └── SwaggerConfig.java               # Swagger configuration
│   │   │   └── exception/
│   │   │       ├── PaymentException.java            # Base payment exception
│   │   │       ├── PaymentNotFoundException.java   # Payment not found
│   │   │       ├── PaymentAlreadyExistsException.java # Duplicate payment
│   │   │       ├── InvalidPaymentStateException.java # Invalid payment state
│   │   │       ├── ClaimNotFoundException.java      # Claim not found
│   │   │       ├── InvalidClaimStatusException.java # Invalid claim status
│   │   │       ├── ServiceCommunicationException.java # External service error
│   │   │       └── GlobalExceptionHandler.java      # Global exception handler
│   │   └── resources/
│   │       ├── application.properties               # Application properties
│   │       ├── application.yml                      # Application YAML config
│   │       ├── schema.sql                           # Database schema
│   │       └── postman-collection.json              # Postman collection
│   └── test/
│       ├── java/com/claimswift/payment/
│       │   ├── PaymentServiceApplicationTests.java
│       │   ├── controller/
│       │   │   └── PaymentControllerTest.java
│       │   ├── service/
│       │   │   └── PaymentServiceTest.java
│       │   ├── dto/
│       │   │   └── DtoTest.java
│       │   └── entity/
│       │       └── EntityTest.java
│       └── resources/
│           └── application.yml                      # Test configuration
```

---

## Error Handling

### Exception Hierarchy

```
PaymentException (Base)
├── PaymentNotFoundException
├── PaymentAlreadyExistsException
├── InvalidPaymentStateException
├── ClaimNotFoundException
├── InvalidClaimStatusException
└── ServiceCommunicationException
```

### Error Codes

| Error Code | Description | HTTP Status |
|------------|-------------|--------------|
| CLAIM_NOT_FOUND | Claim ID does not exist | 400 |
| PAYMENT_NOT_FOUND | Payment not found | 404 |
| PAYMENT_ALREADY_EXISTS | Payment already exists for claim | 400 |
| INVALID_CLAIM_STATUS | Claim status is not APPROVED | 400 |
| INVALID_PAYMENT_STATE | Payment cannot be processed in current state | 400 |
| SERVICE_COMMUNICATION_ERROR | Failed to communicate with external service | 500 |
| VALIDATION_ERROR | Request validation failed | 400 |
| INTERNAL_ERROR | Unexpected internal error | 500 |

### Global Exception Handler

All exceptions are handled by [`GlobalExceptionHandler.java`](src/main/java/com/claimswift/payment/exception/GlobalExceptionHandler.java) which returns standardized API error responses.

---

## Security

### Security Configuration

The service uses Spring Security for authentication and authorization. See [`SecurityConfig.java`](src/main/java/com/claimswift/payment/config/SecurityConfig.java) for configuration details.

### Default Security Settings
- All endpoints require authentication by default
- CSRF protection enabled
- Session management configured
- Role-based access control (RBAC) implemented

---

## Swagger Documentation

### Accessing Swagger UI

Once the service is running, access the Swagger documentation at:

```
http://localhost:8084/swagger-ui.html
```

### API Docs JSON

```
http://localhost:8084/api-docs
```

### Swagger Configuration

The Swagger/OpenAPI documentation is configured in:
- [`SwaggerConfig.java`](src/main/java/com/claimswift/payment/config/SwaggerConfig.java)
- `springdoc` configuration in `application.yml`

---

## Payment Processing Flow

```
1. Client sends POST /api/v1/payments with claimId and approvedAmount
        │
        ▼
2. PaymentService verifies claim status = APPROVED via ClaimClient
        │
        ▼
3. Check if payment already exists for this claim
        │
        ▼
4. Create payment record with status = INITIATED
        │
        ▼
5. Create audit record for PAYMENT_INITIATED
        │
        ▼
6. Process payment via payment gateway (simulated)
        │
        ├──▶ SUCCESS ──▶ Update status to SUCCESS
        │                    │
        │                    ▼
        │               Create transaction record
        │                    │
        │                    ▼
        │               Update claim status to PAID
        │                    │
        │                    ▼
        │               Send notification
        │                    │
        │                    ▼
        │               Create audit record for PAYMENT_SUCCESS
        │                    │
        │                    ▼
        └──────────────────▶ Return PaymentResponse
        │
        └──▶ FAILED ──▶ Update status to FAILED
                           │
                           ▼
                      Create transaction record
                           │
                           ▼
                      Create audit record for PAYMENT_FAILED
                           │
                           ▼
                      Return PaymentResponse
```

---

## Global Status Definitions

### Payment Status
| Status | Description |
|--------|-------------|
| INITIATED | Payment has been initiated |
| SUCCESS | Payment processed successfully |
| FAILED | Payment processing failed |

### Transaction Status
| Status | Description |
|--------|-------------|
| COMPLETED | Transaction completed successfully |
| FAILED | Transaction failed |

### Claim Status (from Claim Service)
| Status | Description |
|--------|-------------|
| SUBMITTED | Claim submitted |
| UNDER_REVIEW | Claim under review |
| APPROVED | Claim approved (required for payment) |
| REJECTED | Claim rejected |
| PAID | Payment completed |

---

## Postman Collection

A Postman collection is available for testing the API:

**File:** [`src/main/resources/postman-collection.json`](src/main/resources/postman-collection.json)

### Importing the Collection
1. Open Postman
2. Click "Import" button
3. Select the JSON file from `src/main/resources/postman-collection.json`
4. The collection will be imported with all API endpoints

---

## License

This project is proprietary software for ClaimSwift Insurance.

---

## Testing the API

You can test the Payment Service using multiple methods: Browser (Swagger UI), Postman, or curl commands.

### Prerequisites

Before testing, ensure:
1. **MySQL** is running on `localhost:3306` with database `payment_db`
2. **Claim Service** is running on `http://localhost:8081` (for payment processing)
3. **Notification Service** is running on `http://localhost:8085` (for notifications)
4. The Payment Service is running on `http://localhost:8084`

### Method 1: Using Swagger UI (Browser)

The easiest way to test is through the built-in Swagger documentation.

1. **Start the service:**
   ```bash
   mvn spring-boot:run
   ```

2. **Open browser and navigate to:**
   ```
   http://localhost:8084/swagger-ui.html
   ```

3. **Test endpoints:**
   - Click on the endpoint you want to test (e.g., `POST /api/v1/payments`)
   - Click "Try it out"
   - Enter the request body:
     ```json
     {
       "claimId": 1,
       "approvedAmount": 50000.00
     }
     ```
   - Click "Execute"
   - View the response below

### Method 2: Using Postman

1. **Import the collection:**
   - Open Postman
   - Click "Import" → "File"
   - Select `src/main/resources/postman-collection.json`

2. **Test endpoints:**

   **Health Check:**
   ```
   GET http://localhost:8084/api/v1/payments/health
   ```

   **Get All Payments:**
   ```
   GET http://localhost:8084/api/v1/payments
   ```

   **Process Payment:**
   ```
   POST http://localhost:8084/api/v1/payments
   ```
   Body (JSON):
   ```json
   {
     "claimId": 1,
     "approvedAmount": 50000.00
   }
   ```

   **Get Payment by ID:**
   ```
   GET http://localhost:8084/api/v1/payments/1
   ```

   **Get Payment by Claim ID:**
   ```
   GET http://localhost:8084/api/v1/payments/claim/1
   ```

   **Retry Failed Payment:**
   ```
   POST http://localhost:8084/api/v1/payments/1/retry
   ```

### Method 3: Using curl (Command Line)

**Health Check:**
```bash
curl -X GET http://localhost:8084/api/v1/payments/health
```

**Get All Payments:**
```bash
curl -X GET http://localhost:8084/api/v1/payments
```

**Process New Payment:**
```bash
curl -X POST http://localhost:8084/api/v1/payments \
  -H "Content-Type: application/json" \
  -d '{
    "claimId": 1,
    "approvedAmount": 50000.00
  }'
```

**Get Payment by ID:**
```bash
curl -X GET http://localhost:8084/api/v1/payments/1
```

**Get Payment by Claim ID:**
```bash
curl -X GET http://localhost:8084/api/v1/payments/claim/1
```

**Retry Failed Payment:**
```bash
curl -X POST http://localhost:8084/api/v1/payments/1/retry
```

### Method 4: Using Browser (Direct URL)

For simple GET requests, you can test directly in the browser:

- Health: `http://localhost:8084/api/v1/payments/health`
- Get All: `http://localhost:8084/api/v1/payments`
- Get by ID: `http://localhost:8084/api/v1/payments/1`
- Get by Claim ID: `http://localhost:8084/api/v1/payments/claim/1`

### Expected Responses

**Success Response (POST /payments):**
```json
{
  "success": true,
  "timestamp": "2026-02-26T11:00:00",
  "data": {
    "paymentId": 1,
    "claimId": 1,
    "approvedAmount": 50000.00,
    "paymentReference": "PAY-A1B2C3D4",
    "status": "SUCCESS",
    "processedAt": "2026-02-26T11:00:00",
    "createdAt": "2026-02-26T10:59:00"
  },
  "error": null
}
```

**Error Response (Claim Not Approved):**
```json
{
  "success": false,
  "timestamp": "2026-02-26T11:00:00",
  "data": null,
  "error": {
    "code": "INVALID_CLAIM_STATUS",
    "message": "Claim status must be APPROVED for payment. Current status: UNDER_REVIEW"
  }
}
```

### Testing Without External Services

If Claim Service or Notification Service are not running, you can:

1. **Mock the services** using tools like WireMock or MockMvc in tests
2. **Use the unit tests** included in the project:
   ```bash
   mvn test
   ```
3. **Disable external calls** by commenting out the Feign client calls in service (for development only)

### API Documentation (JSON)

Get the raw OpenAPI spec:
```
http://localhost:8084/api-docs
```

---

## Support

For issues or questions, please contact the ClaimSwift development team.
