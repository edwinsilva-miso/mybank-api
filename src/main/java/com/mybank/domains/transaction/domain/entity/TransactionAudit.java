package com.mybank.domains.transaction.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_audit_logs")
@Data
@EqualsAndHashCode(callSuper = false)
@EntityListeners(AuditingEntityListener.class)
public class TransactionAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_id", nullable = false)
    private Long transactionId;

    @Column(name = "transaction_number", nullable = false)
    private String transactionNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status")
    private Transaction.TransactionStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false)
    private Transaction.TransactionStatus newStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private Transaction.TransactionType transactionType;

    @Column(name = "amount", precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "total_amount", precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_username", nullable = false)
    private String userUsername;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "source_account_id")
    private Long sourceAccountId;

    @Column(name = "source_account_number")
    private String sourceAccountNumber;

    @Column(name = "destination_account_id")
    private Long destinationAccountId;

    @Column(name = "destination_account_number")
    private String destinationAccountNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "audit_event_type", nullable = false)
    private AuditEventType eventType;

    @Column(name = "event_description", length = 500)
    private String eventDescription;

    @Column(name = "additional_data", length = 1000)
    private String additionalData;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "session_id", length = 100)
    private String sessionId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum AuditEventType {
        TRANSACTION_CREATED,           // Transacción creada
        TRANSACTION_PROCESSING,        // Transacción en procesamiento
        TRANSACTION_COMPLETED,         // Transacción completada
        TRANSACTION_FAILED,            // Transacción falló
        TRANSACTION_CANCELLED,         // Transacción cancelada
        TRANSACTION_REVERSED,          // Transacción revertida
        BALANCE_UPDATED,               // Balance actualizado
        VALIDATION_FAILED,             // Validación falló
        AUTHORIZATION_REQUIRED,        // Autorización requerida
        FRAUD_DETECTED,                // Fraude detectado
        COMPLIANCE_CHECK,              // Verificación de cumplimiento
        SYSTEM_ERROR                   // Error del sistema
    }

    // Constructor para facilitar la creación
    public static TransactionAudit fromTransaction(Transaction transaction, 
                                                  AuditEventType eventType, 
                                                  String eventDescription) {
        TransactionAudit audit = new TransactionAudit();
        audit.setTransactionId(transaction.getId());
        audit.setTransactionNumber(transaction.getTransactionNumber());
        audit.setTransactionType(transaction.getType());
        audit.setAmount(transaction.getAmount());
        audit.setTotalAmount(transaction.getTotalAmount());
        audit.setUserId(transaction.getUser().getId());
        audit.setUserUsername(transaction.getUser().getUsername());
        audit.setEventType(eventType);
        audit.setEventDescription(eventDescription);
        
        // Establecer el estado actual de la transacción como newStatus
        audit.setNewStatus(transaction.getStatus());
        
        // Configurar información de cuentas según el tipo de transacción
        if (transaction.getAccount() != null) {
            audit.setAccountId(transaction.getAccount().getId());
            audit.setAccountNumber(transaction.getAccount().getAccountNumber());
        }
        
        if (transaction.getSourceAccount() != null) {
            audit.setSourceAccountId(transaction.getSourceAccount().getId());
            audit.setSourceAccountNumber(transaction.getSourceAccount().getAccountNumber());
        }
        
        if (transaction.getDestinationAccount() != null) {
            audit.setDestinationAccountId(transaction.getDestinationAccount().getId());
            audit.setDestinationAccountNumber(transaction.getDestinationAccount().getAccountNumber());
        }
        
        return audit;
    }

    // Constructor para cambios de estado
    public static TransactionAudit statusChange(Transaction transaction, 
                                               Transaction.TransactionStatus previousStatus,
                                               Transaction.TransactionStatus newStatus,
                                               String reason) {
        TransactionAudit audit = fromTransaction(transaction, 
            getEventTypeForStatusChange(newStatus), 
            "Status changed from " + previousStatus + " to " + newStatus + ": " + reason);
        audit.setPreviousStatus(previousStatus);
        audit.setNewStatus(newStatus);
        return audit;
    }

    private static AuditEventType getEventTypeForStatusChange(Transaction.TransactionStatus status) {
        return switch (status) {
            case PENDING -> AuditEventType.TRANSACTION_CREATED;
            case PROCESSING -> AuditEventType.TRANSACTION_PROCESSING;
            case COMPLETED -> AuditEventType.TRANSACTION_COMPLETED;
            case FAILED -> AuditEventType.TRANSACTION_FAILED;
            case CANCELLED -> AuditEventType.TRANSACTION_CANCELLED;
            case REVERSED -> AuditEventType.TRANSACTION_REVERSED;
        };
    }
} 