package com.mybank.domains.transaction.application.dto;

import com.mybank.domains.transaction.domain.entity.TransactionAudit;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionAuditResponse {
    private Long id;
    private Long transactionId;
    private String transactionNumber;
    private String previousStatus;
    private String newStatus;
    private String transactionType;
    private BigDecimal amount;
    private BigDecimal totalAmount;
    private Long userId;
    private String userUsername;
    private Long accountId;
    private String accountNumber;
    private Long sourceAccountId;
    private String sourceAccountNumber;
    private Long destinationAccountId;
    private String destinationAccountNumber;
    private String auditEventType;
    private String eventDescription;
    private String additionalData;
    private String ipAddress;
    private String userAgent;
    private String sessionId;
    private LocalDateTime createdAt;

    public static TransactionAuditResponse fromEntity(TransactionAudit audit) {
        TransactionAuditResponse response = new TransactionAuditResponse();
        response.setId(audit.getId());
        response.setTransactionId(audit.getTransactionId());
        response.setTransactionNumber(audit.getTransactionNumber());
        response.setPreviousStatus(audit.getPreviousStatus() != null ? audit.getPreviousStatus().name() : null);
        response.setNewStatus(audit.getNewStatus() != null ? audit.getNewStatus().name() : null);
        response.setTransactionType(audit.getTransactionType() != null ? audit.getTransactionType().name() : null);
        response.setAmount(audit.getAmount());
        response.setTotalAmount(audit.getTotalAmount());
        response.setUserId(audit.getUserId());
        response.setUserUsername(audit.getUserUsername());
        response.setAccountId(audit.getAccountId());
        response.setAccountNumber(audit.getAccountNumber());
        response.setSourceAccountId(audit.getSourceAccountId());
        response.setSourceAccountNumber(audit.getSourceAccountNumber());
        response.setDestinationAccountId(audit.getDestinationAccountId());
        response.setDestinationAccountNumber(audit.getDestinationAccountNumber());
        response.setAuditEventType(audit.getEventType() != null ? audit.getEventType().name() : null);
        response.setEventDescription(audit.getEventDescription());
        response.setAdditionalData(audit.getAdditionalData());
        response.setIpAddress(audit.getIpAddress());
        response.setUserAgent(audit.getUserAgent());
        response.setSessionId(audit.getSessionId());
        response.setCreatedAt(audit.getCreatedAt());
        return response;
    }
} 