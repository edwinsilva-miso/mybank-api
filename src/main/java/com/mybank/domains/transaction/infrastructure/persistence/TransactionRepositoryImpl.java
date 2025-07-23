package com.mybank.domains.transaction.infrastructure.persistence;

import com.mybank.domains.transaction.domain.entity.Transaction;
import com.mybank.domains.transaction.domain.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TransactionRepositoryImpl implements TransactionRepository {

    private final TransactionJpaRepository transactionJpaRepository;

    @Override
    public Transaction save(Transaction transaction) {
        return transactionJpaRepository.save(transaction);
    }

    @Override
    public Optional<Transaction> findById(Long id) {
        return transactionJpaRepository.findById(id);
    }

    @Override
    public Optional<Transaction> findByTransactionNumber(String transactionNumber) {
        return transactionJpaRepository.findByTransactionNumber(transactionNumber);
    }

    @Override
    public List<Transaction> findByUserId(Long userId) {
        return transactionJpaRepository.findByUserId(userId);
    }

    @Override
    public List<Transaction> findByAccountId(Long accountId) {
        return transactionJpaRepository.findByAccountId(accountId);
    }

    @Override
    public List<Transaction> findByUserIdAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return transactionJpaRepository.findByUserIdAndDateRange(userId, startDate, endDate);
    }

    @Override
    public List<Transaction> findByAccountIdAndDateRange(Long accountId, LocalDateTime startDate, LocalDateTime endDate) {
        return transactionJpaRepository.findByAccountIdAndDateRange(accountId, startDate, endDate);
    }

    @Override
    public List<Transaction> findByStatus(Transaction.TransactionStatus status) {
        return transactionJpaRepository.findByStatus(status);
    }

    @Override
    public List<Transaction> findByType(Transaction.TransactionType type) {
        return transactionJpaRepository.findByType(type);
    }

    @Override
    public List<Transaction> findAll() {
        return transactionJpaRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        transactionJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByTransactionNumber(String transactionNumber) {
        return transactionJpaRepository.existsByTransactionNumber(transactionNumber);
    }

    @Override
    public List<Transaction> findTransactionsByUserAndType(Long userId, Transaction.TransactionType type) {
        return transactionJpaRepository.findTransactionsByUserAndType(userId, type);
    }

    @Override
    public List<Transaction> findTransactionsByAccountAndType(Long accountId, Transaction.TransactionType type) {
        return transactionJpaRepository.findTransactionsByAccountAndType(accountId, type);
    }

    @Override
    public List<Transaction> findPendingTransactions() {
        return transactionJpaRepository.findPendingTransactions();
    }

    @Override
    public List<Transaction> findPendingTransactionsByUserId(Long userId) {
        return transactionJpaRepository.findPendingTransactionsByUserId(userId);
    }
} 