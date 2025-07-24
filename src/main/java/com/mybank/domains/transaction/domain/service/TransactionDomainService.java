package com.mybank.domains.transaction.domain.service;

import com.mybank.domains.account.domain.entity.Account;
import com.mybank.domains.transaction.domain.entity.Transaction;
import com.mybank.domains.transaction.domain.entity.TransactionAudit;
import com.mybank.domains.transaction.domain.repository.TransactionRepository;
import com.mybank.domains.transaction.domain.valueobject.Money;
import com.mybank.domains.user.domain.entity.User;
import com.mybank.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionDomainService {

    private final TransactionRepository transactionRepository;
    private final TransactionAuditService transactionAuditService;

    public Transaction createTransaction(Transaction transaction) {
        log.info("Creating new transaction: {}", transaction.getTransactionNumber());
        
        // Validar que el número de transacción sea único
        if (transactionRepository.existsByTransactionNumber(transaction.getTransactionNumber())) {
            transactionAuditService.logValidationFailure(transaction, "Transaction number already exists");
            throw new BusinessException("Transaction number already exists", "TRANSACTION_NUMBER_EXISTS", "TRANSACTION");
        }

        // Validar el monto básico (sin validar fondos)
        try {
            validateBasicTransactionAmount(transaction);
        } catch (BusinessException e) {
            transactionAuditService.logValidationFailure(transaction, e.getMessage());
            throw e;
        }

        // Validar que el usuario tenga permisos para la cuenta
        try {
            validateUserAccountAccess(transaction);
        } catch (BusinessException e) {
            transactionAuditService.logValidationFailure(transaction, e.getMessage());
            throw e;
        }

        Transaction savedTransaction = transactionRepository.save(transaction);
        
        // Validar fondos después de guardar la transacción
        try {
            validateTransactionFunds(savedTransaction);
        } catch (BusinessException e) {
            // Si no hay fondos suficientes, marcar como fallida
            savedTransaction.setStatus(Transaction.TransactionStatus.FAILED);
            savedTransaction.setNotes("Insufficient funds: " + e.getMessage());
            savedTransaction = transactionRepository.save(savedTransaction);
            
            transactionAuditService.logValidationFailure(savedTransaction, e.getMessage());
            throw e;
        }
        
        // Registrar auditoría de creación
        transactionAuditService.logEvent(savedTransaction, 
                                       TransactionAudit.AuditEventType.TRANSACTION_CREATED, 
                                       "Transaction created successfully");
        
        log.info("Transaction created successfully with ID: {}", savedTransaction.getId());
        
        return savedTransaction;
    }

    public Transaction processTransaction(Long transactionId) {
        log.info("Processing transaction with ID: {}", transactionId);
        
        Transaction transaction = findById(transactionId);
        
        if (transaction.getStatus() != Transaction.TransactionStatus.PENDING) {
            transactionAuditService.logValidationFailure(transaction, "Transaction is not in pending status");
            throw new BusinessException("Transaction is not in pending status", "INVALID_TRANSACTION_STATUS", "TRANSACTION");
        }

        // Cambiar estado a procesando
        Transaction.TransactionStatus previousStatus = transaction.getStatus();
        transaction.setStatus(Transaction.TransactionStatus.PROCESSING);
        transaction = transactionRepository.save(transaction);
        
        // Registrar auditoría de cambio a procesando
        transactionAuditService.logStatusChange(transaction, previousStatus, 
                                              Transaction.TransactionStatus.PROCESSING, 
                                              "Transaction processing started");

        try {
            // Aquí iría la lógica específica según el tipo de transacción
            switch (transaction.getType()) {
                case DEPOSIT:
                    processDeposit(transaction);
                    break;
                case WITHDRAWAL:
                    processWithdrawal(transaction);
                    break;
                // case TRANSFER:
                //     processTransfer(transaction);
                //     break;
                case PAYMENT:
                    processPayment(transaction);
                    break;
                default:
                    transactionAuditService.logValidationFailure(transaction, "Unsupported transaction type: " + transaction.getType());
                    throw new BusinessException("Unsupported transaction type", "UNSUPPORTED_TRANSACTION_TYPE", "TRANSACTION");
            }

            // Marcar como completada
            previousStatus = transaction.getStatus();
            transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
            transaction.setProcessedAt(LocalDateTime.now());
            
            // Registrar auditoría de completado
            transactionAuditService.logStatusChange(transaction, previousStatus, 
                                                  Transaction.TransactionStatus.COMPLETED, 
                                                  "Transaction processed successfully");
            
        } catch (BusinessException e) {
            log.error("Error processing transaction: {}", e.getMessage());
            previousStatus = transaction.getStatus();
            transaction.setStatus(Transaction.TransactionStatus.FAILED);
            transaction.setNotes("Processing failed: " + e.getMessage());
            
            // Registrar auditoría de fallo
            transactionAuditService.logStatusChange(transaction, previousStatus, 
                                                  Transaction.TransactionStatus.FAILED, 
                                                  "Processing failed: " + e.getMessage());
            throw e; // Relanzar BusinessException
        } catch (Exception e) {
            log.error("Unexpected error processing transaction: {}", e.getMessage());
            previousStatus = transaction.getStatus();
            transaction.setStatus(Transaction.TransactionStatus.FAILED);
            transaction.setNotes("Processing failed: " + e.getMessage());
            
            // Registrar auditoría de error del sistema
            transactionAuditService.logSystemError(transaction, e.getMessage());
        }

        return transactionRepository.save(transaction);
    }

    public Transaction findById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Transaction not found", "TRANSACTION_NOT_FOUND", "TRANSACTION"));
    }

    public Optional<Transaction> findByTransactionNumber(String transactionNumber) {
        return transactionRepository.findByTransactionNumber(transactionNumber);
    }

    public List<Transaction> findByUserId(Long userId) {
        return transactionRepository.findByUserId(userId);
    }

    public List<Transaction> findByAccountId(Long accountId) {
        return transactionRepository.findByAccountId(accountId);
    }

    public List<Transaction> findPendingTransactions() {
        return transactionRepository.findPendingTransactions();
    }

    public List<Transaction> findByUserIdAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByUserIdAndDateRange(userId, startDate, endDate);
    }

    private void validateBasicTransactionAmount(Transaction transaction) {
        if (transaction.getAmount() == null || transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Transaction amount must be greater than zero", "INVALID_AMOUNT", "TRANSACTION");
        }
    }

    private void validateTransactionFunds(Transaction transaction) {
        // Validar límites según el tipo de transacción
        switch (transaction.getType()) {
            case WITHDRAWAL:
                validateWithdrawalAmount(transaction);
                break;
            // case TRANSFER:
            //     validateTransferAmount(transaction);
            //     break;
            case PAYMENT:
                validateWithdrawalAmount(transaction); // Los pagos también requieren fondos
                break;
        }
    }

    private void validateWithdrawalAmount(Transaction transaction) {
        Account account = transaction.getAccount();
        if (account == null) {
            throw new BusinessException("Account is required for withdrawal", "ACCOUNT_REQUIRED", "TRANSACTION");
        }

        Money currentBalance = new Money(account.getBalance());
        Money withdrawalAmount = new Money(transaction.getTotalAmount());

        if (withdrawalAmount.isGreaterThan(currentBalance)) {
            throw new BusinessException("Insufficient funds for withdrawal", "INSUFFICIENT_FUNDS", "TRANSACTION");
        }
    }

    // private void validateTransferAmount(Transaction transaction) {
    //     Account sourceAccount = transaction.getSourceAccount();
    //     if (sourceAccount == null) {
    //         throw new BusinessException("Source account is required for transfer", "SOURCE_ACCOUNT_REQUIRED", "TRANSACTION");
    //     }

    //     Money currentBalance = new Money(sourceAccount.getBalance());
    //     Money transferAmount = new Money(transaction.getTotalAmount());

    //     if (transferAmount.isGreaterThan(currentBalance)) {
    //         throw new BusinessException("Insufficient funds for transfer", "INSUFFICIENT_FUNDS", "TRANSACTION");
    //     }
    // }

    private void validateUserAccountAccess(Transaction transaction) {
        User user = transaction.getUser();
        if (user == null) {
            throw new BusinessException("User is required for transaction", "USER_REQUIRED", "TRANSACTION");
        }

        // Aquí se validaría que el usuario tenga acceso a las cuentas involucradas
        // Por ahora, asumimos que tiene acceso
    }

    private void processDeposit(Transaction transaction) {
        Account account = transaction.getAccount();
        if (account == null) {
            transactionAuditService.logValidationFailure(transaction, "Account is required for deposit");
            throw new BusinessException("Account is required for deposit", "ACCOUNT_REQUIRED", "TRANSACTION");
        }

        // Actualizar balance de la cuenta
        BigDecimal oldBalance = account.getBalance();
        BigDecimal newBalance = account.getBalance().add(transaction.getTotalAmount());
        account.setBalance(newBalance);
        
        // Registrar auditoría de actualización de balance
        transactionAuditService.logEvent(transaction, 
                                       TransactionAudit.AuditEventType.BALANCE_UPDATED, 
                                       String.format("Deposit processed: Account %s balance updated from %s to %s", 
                                                   account.getAccountNumber(), oldBalance, newBalance));
        
        log.info("Deposit processed: Account {} balance updated to {}", account.getAccountNumber(), newBalance);
    }

    private void processWithdrawal(Transaction transaction) {
        Account account = transaction.getAccount();
        if (account == null) {
            transactionAuditService.logValidationFailure(transaction, "Account is required for withdrawal");
            throw new BusinessException("Account is required for withdrawal", "ACCOUNT_REQUIRED", "TRANSACTION");
        }

        // Validar fondos nuevamente antes de procesar
        try {
            validateWithdrawalAmount(transaction);
        } catch (BusinessException e) {
            transactionAuditService.logValidationFailure(transaction, e.getMessage());
            throw e;
        }

        // Actualizar balance de la cuenta
        BigDecimal oldBalance = account.getBalance();
        BigDecimal newBalance = account.getBalance().subtract(transaction.getTotalAmount());
        account.setBalance(newBalance);
        
        // Registrar auditoría de actualización de balance
        transactionAuditService.logEvent(transaction, 
                                       TransactionAudit.AuditEventType.BALANCE_UPDATED, 
                                       String.format("Withdrawal processed: Account %s balance updated from %s to %s", 
                                                   account.getAccountNumber(), oldBalance, newBalance));
        
        log.info("Withdrawal processed: Account {} balance updated to {}", account.getAccountNumber(), newBalance);
    }

    // private void processTransfer(Transaction transaction) {
    //     Account sourceAccount = transaction.getSourceAccount();
    //     Account destinationAccount = transaction.getDestinationAccount();

    //     if (sourceAccount == null || destinationAccount == null) {
    //         transactionAuditService.logValidationFailure(transaction, "Both source and destination accounts are required for transfer");
    //         throw new BusinessException("Both source and destination accounts are required for transfer", "ACCOUNTS_REQUIRED", "TRANSACTION");
    //     }

    //     if (sourceAccount.getId().equals(destinationAccount.getId())) {
    //         transactionAuditService.logValidationFailure(transaction, "Cannot transfer to the same account");
    //         throw new BusinessException("Cannot transfer to the same account", "SAME_ACCOUNT_TRANSFER", "TRANSACTION");
    //     }

    //     // Validar fondos nuevamente antes de procesar
    //     try {
    //         validateTransferAmount(transaction);
    //     } catch (BusinessException e) {
    //         transactionAuditService.logValidationFailure(transaction, e.getMessage());
    //         throw e;
    //     }

    //     // Actualizar balances
    //     BigDecimal sourceOldBalance = sourceAccount.getBalance();
    //     BigDecimal destinationOldBalance = destinationAccount.getBalance();
    //     BigDecimal sourceNewBalance = sourceAccount.getBalance().subtract(transaction.getTotalAmount());
    //     BigDecimal destinationNewBalance = destinationAccount.getBalance().add(transaction.getAmount()); // Solo el monto principal, no las comisiones

    //     sourceAccount.setBalance(sourceNewBalance);
    //     destinationAccount.setBalance(destinationNewBalance);
        
    //     // Registrar auditoría de actualización de balances
    //     transactionAuditService.logEvent(transaction, 
    //                                    TransactionAudit.AuditEventType.BALANCE_UPDATED, 
    //                                    String.format("Transfer processed: Source account %s balance updated from %s to %s, " +
    //                                                "Destination account %s balance updated from %s to %s", 
    //                                                sourceAccount.getAccountNumber(), sourceOldBalance, sourceNewBalance,
    //                                                destinationAccount.getAccountNumber(), destinationOldBalance, destinationNewBalance));
        
    //     log.info("Transfer processed: From {} to {}, amount: {}", 
    //             sourceAccount.getAccountNumber(), 
    //             destinationAccount.getAccountNumber(), 
    //             transaction.getAmount());
    // }

    private void processPayment(Transaction transaction) {
        Account account = transaction.getAccount();
        if (account == null) {
            throw new BusinessException("Account is required for payment", "ACCOUNT_REQUIRED", "TRANSACTION");
        }

        // Validar fondos
        validateWithdrawalAmount(transaction);

        // Actualizar balance de la cuenta
        BigDecimal newBalance = account.getBalance().subtract(transaction.getTotalAmount());
        account.setBalance(newBalance);
        
        log.info("Payment processed: Account {} balance updated to {}", account.getAccountNumber(), newBalance);
    }
} 