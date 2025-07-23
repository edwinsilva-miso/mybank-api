package com.mybank.domains.transaction.infrastructure.persistence;

import com.mybank.domains.transaction.domain.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionJpaRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByTransactionNumber(String transactionNumber);
    
    List<Transaction> findByUserId(Long userId);
    
    List<Transaction> findByAccountId(Long accountId);
    
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND t.createdAt BETWEEN :startDate AND :endDate")
    List<Transaction> findByUserIdAndDateRange(@Param("userId") Long userId, 
                                              @Param("startDate") LocalDateTime startDate, 
                                              @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT t FROM Transaction t WHERE t.account.id = :accountId AND t.createdAt BETWEEN :startDate AND :endDate")
    List<Transaction> findByAccountIdAndDateRange(@Param("accountId") Long accountId, 
                                                 @Param("startDate") LocalDateTime startDate, 
                                                 @Param("endDate") LocalDateTime endDate);
    
    List<Transaction> findByStatus(Transaction.TransactionStatus status);
    
    List<Transaction> findByType(Transaction.TransactionType type);
    
    boolean existsByTransactionNumber(String transactionNumber);
    
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND t.type = :type")
    List<Transaction> findTransactionsByUserAndType(@Param("userId") Long userId, 
                                                   @Param("type") Transaction.TransactionType type);
    
    @Query("SELECT t FROM Transaction t WHERE t.account.id = :accountId AND t.type = :type")
    List<Transaction> findTransactionsByAccountAndType(@Param("accountId") Long accountId, 
                                                      @Param("type") Transaction.TransactionType type);
    
    @Query("SELECT t FROM Transaction t WHERE t.status = 'PENDING'")
    List<Transaction> findPendingTransactions();
    
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND t.status = 'PENDING'")
    List<Transaction> findPendingTransactionsByUserId(@Param("userId") Long userId);
} 