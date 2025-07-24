package com.mybank.domains.transaction.domain.entity;

import com.mybank.domains.account.domain.entity.Account;
import com.mybank.domains.user.domain.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@EqualsAndHashCode(callSuper = false)
@EntityListeners(AuditingEntityListener.class)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String transactionNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TransactionStatus status = TransactionStatus.PENDING;

    @NotNull
    @DecimalMin("0.01")
    @Column(precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(precision = 19, scale = 2)
    private BigDecimal fee = BigDecimal.ZERO;

    @Column(precision = 19, scale = 2)
    private BigDecimal tax = BigDecimal.ZERO;

    @NotNull
    @Column(precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @Column(length = 500)
    private String description;

    @Column(length = 1000)
    private String notes;

    // Cuenta origen (para transferencias)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_account_id")
    private Account sourceAccount;

    // Cuenta destino (para transferencias)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_account_id")
    private Account destinationAccount;

    // Usuario que realiza la transacción
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Cuenta principal (para depósitos/retiros)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (transactionNumber == null) {
            transactionNumber = generateTransactionNumber();
        }
        if (totalAmount == null) {
            totalAmount = amount.add(fee).add(tax);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    private String generateTransactionNumber() {
        return "TXN" + System.currentTimeMillis() + String.format("%04d", (int) (Math.random() * 10000));
    }

    public enum TransactionType {
        DEPOSIT,           // Depósito
        WITHDRAWAL,        // Retiro
        // TRANSFER,          // Transferencia entre cuentas (NO IMPLEMENTADO)
        PAYMENT,           // Pago de servicios
        REFUND,           // Reembolso
        FEE_CHARGE        // Cargo por comisión
    }

    public enum TransactionStatus {
        PENDING,           // Pendiente de procesar
        PROCESSING,        // En proceso
        COMPLETED,         // Completada exitosamente
        FAILED,           // Falló
        CANCELLED,        // Cancelada
        REVERSED          // Revertida
    }
} 