package com.mybank.domains.transaction.domain.repository;

import com.mybank.domains.transaction.domain.entity.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository {

    Transaction save(Transaction transaction);
    
    Optional<Transaction> findById(Long id);
    
    Optional<Transaction> findByTransactionNumber(String transactionNumber);
    
    List<Transaction> findByUserId(Long userId);
    
    List<Transaction> findByAccountId(Long accountId);
    
    List<Transaction> findByUserIdAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate);
    
    List<Transaction> findByAccountIdAndDateRange(Long accountId, LocalDateTime startDate, LocalDateTime endDate);
    
    List<Transaction> findByStatus(Transaction.TransactionStatus status);
    
    List<Transaction> findByType(Transaction.TransactionType type);
    
    List<Transaction> findAll();
    
    void deleteById(Long id);
    
    boolean existsByTransactionNumber(String transactionNumber);
    
    // Métodos para reportes y analytics
    List<Transaction> findTransactionsByUserAndType(Long userId, Transaction.TransactionType type);
    
    List<Transaction> findTransactionsByAccountAndType(Long accountId, Transaction.TransactionType type);
    
    // Métodos para transacciones pendientes
    List<Transaction> findPendingTransactions();
    
    List<Transaction> findPendingTransactionsByUserId(Long userId);
} 