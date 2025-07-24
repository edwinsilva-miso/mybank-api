package com.mybank.domains.transaction.application.dto;

import com.mybank.domains.transaction.domain.entity.Transaction;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateTransactionRequest {

    @NotNull(message = "Transaction type is required")
    private Transaction.TransactionType type;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    private BigDecimal fee = BigDecimal.ZERO;

    private BigDecimal tax = BigDecimal.ZERO;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;

    // Para depósitos y retiros
    private Long accountId;

    // Para transferencias
    private Long sourceAccountId;
    private Long destinationAccountId;

    // Validaciones específicas según el tipo de transacción
    public void validate() {
        switch (type) {
            case DEPOSIT:
            case WITHDRAWAL:
                if (accountId == null) {
                    throw new IllegalArgumentException("Account ID is required for " + type + " transactions");
                }
                break;
            // case TRANSFER:
            //     if (sourceAccountId == null || destinationAccountId == null) {
            //         throw new IllegalArgumentException("Both source and destination account IDs are required for transfers");
            //     }
            //     if (sourceAccountId.equals(destinationAccountId)) {
            //         throw new IllegalArgumentException("Source and destination accounts cannot be the same");
            //     }
            //     break;
            case PAYMENT:
                if (accountId == null) {
                    throw new IllegalArgumentException("Account ID is required for payment transactions");
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported transaction type: " + type);
        }
    }
} 