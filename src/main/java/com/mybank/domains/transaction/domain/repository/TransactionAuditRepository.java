package com.mybank.domains.transaction.domain.repository;

import com.mybank.domains.transaction.domain.entity.TransactionAudit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionAuditRepository extends JpaRepository<TransactionAudit, Long> {

    // Buscar auditoría por ID de transacción
    List<TransactionAudit> findByTransactionIdOrderByCreatedAtDesc(Long transactionId);

    // Buscar auditoría por número de transacción
    List<TransactionAudit> findByTransactionNumberOrderByCreatedAtDesc(String transactionNumber);

    // Buscar auditoría por usuario
    List<TransactionAudit> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Buscar auditoría por tipo de evento
    List<TransactionAudit> findByEventTypeOrderByCreatedAtDesc(TransactionAudit.AuditEventType eventType);

    // Buscar auditoría por rango de fechas
    @Query("SELECT ta FROM TransactionAudit ta WHERE ta.createdAt BETWEEN :startDate AND :endDate ORDER BY ta.createdAt DESC")
    List<TransactionAudit> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                          @Param("endDate") LocalDateTime endDate);

    // Buscar auditoría por cuenta
    @Query("SELECT ta FROM TransactionAudit ta WHERE ta.accountId = :accountId OR ta.sourceAccountId = :accountId OR ta.destinationAccountId = :accountId ORDER BY ta.createdAt DESC")
    List<TransactionAudit> findByAccountId(@Param("accountId") Long accountId);

    // Buscar auditoría por número de cuenta
    @Query("SELECT ta FROM TransactionAudit ta WHERE ta.accountNumber = :accountNumber OR ta.sourceAccountNumber = :accountNumber OR ta.destinationAccountNumber = :accountNumber ORDER BY ta.createdAt DESC")
    List<TransactionAudit> findByAccountNumber(@Param("accountNumber") String accountNumber);

    // Buscar auditoría con paginación
    Page<TransactionAudit> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // Buscar auditoría por usuario con paginación
    Page<TransactionAudit> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // Buscar auditoría por tipo de evento con paginación
    Page<TransactionAudit> findByEventTypeOrderByCreatedAtDesc(TransactionAudit.AuditEventType eventType, Pageable pageable);

    // Buscar auditoría por rango de fechas con paginación
    @Query("SELECT ta FROM TransactionAudit ta WHERE ta.createdAt BETWEEN :startDate AND :endDate ORDER BY ta.createdAt DESC")
    Page<TransactionAudit> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                          @Param("endDate") LocalDateTime endDate, 
                                          Pageable pageable);

    // Buscar auditoría por transacción y tipo de evento
    List<TransactionAudit> findByTransactionIdAndEventTypeOrderByCreatedAtDesc(Long transactionId, 
                                                                              TransactionAudit.AuditEventType eventType);

    // Buscar auditoría por usuario y tipo de evento
    List<TransactionAudit> findByUserIdAndEventTypeOrderByCreatedAtDesc(Long userId, 
                                                                       TransactionAudit.AuditEventType eventType);

    // Buscar auditoría por cambios de estado específicos
    @Query("SELECT ta FROM TransactionAudit ta WHERE ta.previousStatus = :previousStatus AND ta.newStatus = :newStatus ORDER BY ta.createdAt DESC")
    List<TransactionAudit> findByStatusChange(@Param("previousStatus") com.mybank.domains.transaction.domain.entity.Transaction.TransactionStatus previousStatus,
                                             @Param("newStatus") com.mybank.domains.transaction.domain.entity.Transaction.TransactionStatus newStatus);

    // Contar eventos por tipo en un rango de fechas
    @Query("SELECT ta.eventType, COUNT(ta) FROM TransactionAudit ta WHERE ta.createdAt BETWEEN :startDate AND :endDate GROUP BY ta.eventType")
    List<Object[]> countEventsByTypeInDateRange(@Param("startDate") LocalDateTime startDate, 
                                               @Param("endDate") LocalDateTime endDate);

    // Buscar transacciones sospechosas (múltiples intentos fallidos)
    @Query("SELECT ta.transactionId, COUNT(ta) FROM TransactionAudit ta " +
           "WHERE ta.eventType = 'TRANSACTION_FAILED' AND ta.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY ta.transactionId HAVING COUNT(ta) >= :minFailures")
    List<Object[]> findSuspiciousTransactions(@Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate,
                                             @Param("minFailures") int minFailures);

    // Buscar auditoría por descripción (búsqueda de texto)
    @Query("SELECT ta FROM TransactionAudit ta WHERE ta.eventDescription LIKE %:searchTerm% ORDER BY ta.createdAt DESC")
    List<TransactionAudit> findByEventDescriptionContaining(@Param("searchTerm") String searchTerm);

    // Buscar auditoría por IP address
    List<TransactionAudit> findByIpAddressOrderByCreatedAtDesc(String ipAddress);

    // Buscar auditoría por session ID
    List<TransactionAudit> findBySessionIdOrderByCreatedAtDesc(String sessionId);
} 