package com.mybank.domains.transaction.application.dto;

import com.mybank.domains.transaction.domain.entity.Transaction;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionResponse {

    private Long id;
    private String transactionNumber;
    private Transaction.TransactionType type;
    private Transaction.TransactionStatus status;
    private BigDecimal amount;
    private BigDecimal fee;
    private BigDecimal tax;
    private BigDecimal totalAmount;
    private String description;
    private String notes;
    private Long userId;
    private String userUsername;
    private Long accountId;
    private String accountNumber;
    private Long sourceAccountId;
    private String sourceAccountNumber;
    private Long destinationAccountId;
    private String destinationAccountNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime processedAt;

    public static TransactionResponse fromEntity(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setTransactionNumber(transaction.getTransactionNumber());
        response.setType(transaction.getType());
        response.setStatus(transaction.getStatus());
        response.setAmount(transaction.getAmount());
        response.setFee(transaction.getFee());
        response.setTax(transaction.getTax());
        response.setTotalAmount(transaction.getTotalAmount());
        response.setDescription(transaction.getDescription());
        response.setNotes(transaction.getNotes());
        response.setCreatedAt(transaction.getCreatedAt());
        response.setUpdatedAt(transaction.getUpdatedAt());
        response.setProcessedAt(transaction.getProcessedAt());

        // Información del usuario
        if (transaction.getUser() != null) {
            response.setUserId(transaction.getUser().getId());
            response.setUserUsername(transaction.getUser().getUsername());
        }

        // Información de la cuenta principal
        if (transaction.getAccount() != null) {
            response.setAccountId(transaction.getAccount().getId());
            response.setAccountNumber(transaction.getAccount().getAccountNumber());
        }

        // Información de la cuenta origen (para transferencias)
        if (transaction.getSourceAccount() != null) {
            response.setSourceAccountId(transaction.getSourceAccount().getId());
            response.setSourceAccountNumber(transaction.getSourceAccount().getAccountNumber());
        }

        // Información de la cuenta destino (para transferencias)
        if (transaction.getDestinationAccount() != null) {
            response.setDestinationAccountId(transaction.getDestinationAccount().getId());
            response.setDestinationAccountNumber(transaction.getDestinationAccount().getAccountNumber());
        }

        return response;
    }
} 