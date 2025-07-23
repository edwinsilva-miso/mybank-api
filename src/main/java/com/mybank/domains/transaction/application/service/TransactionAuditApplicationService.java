package com.mybank.domains.transaction.application.service;

import com.mybank.domains.transaction.application.dto.TransactionAuditResponse;
import com.mybank.domains.transaction.domain.entity.TransactionAudit;
import com.mybank.domains.transaction.domain.service.TransactionAuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TransactionAuditApplicationService {

    private final TransactionAuditService transactionAuditService;

    /**
     * Obtiene el historial de auditoría de una transacción
     */
    public List<TransactionAuditResponse> getTransactionAuditHistory(Long transactionId) {
        log.info("Getting audit history for transaction: {}", transactionId);
        return transactionAuditService.getTransactionAuditHistory(transactionId)
                .stream()
                .map(TransactionAuditResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene el historial de auditoría de una transacción por número
     */
    public List<TransactionAuditResponse> getTransactionAuditHistory(String transactionNumber) {
        log.info("Getting audit history for transaction number: {}", transactionNumber);
        return transactionAuditService.getTransactionAuditHistory(transactionNumber)
                .stream()
                .map(TransactionAuditResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene el historial de auditoría de un usuario
     */
    public List<TransactionAuditResponse> getUserAuditHistory(Long userId) {
        log.info("Getting audit history for user: {}", userId);
        return transactionAuditService.getUserAuditHistory(userId)
                .stream()
                .map(TransactionAuditResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene el historial de auditoría de un usuario con paginación
     */
    public Page<TransactionAuditResponse> getUserAuditHistory(Long userId, Pageable pageable) {
        log.info("Getting paginated audit history for user: {}", userId);
        return transactionAuditService.getUserAuditHistory(userId, pageable)
                .map(TransactionAuditResponse::fromEntity);
    }

    /**
     * Obtiene el historial de auditoría de una cuenta
     */
    public List<TransactionAuditResponse> getAccountAuditHistory(Long accountId) {
        log.info("Getting audit history for account: {}", accountId);
        return transactionAuditService.getAccountAuditHistory(accountId)
                .stream()
                .map(TransactionAuditResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene el historial de auditoría de una cuenta por número
     */
    public List<TransactionAuditResponse> getAccountAuditHistory(String accountNumber) {
        log.info("Getting audit history for account number: {}", accountNumber);
        return transactionAuditService.getAccountAuditHistory(accountNumber)
                .stream()
                .map(TransactionAuditResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene auditoría por rango de fechas
     */
    public List<TransactionAuditResponse> getAuditHistoryByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Getting audit history from {} to {}", startDate, endDate);
        return transactionAuditService.getAuditHistoryByDateRange(startDate, endDate)
                .stream()
                .map(TransactionAuditResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene auditoría por rango de fechas con paginación
     */
    public Page<TransactionAuditResponse> getAuditHistoryByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        log.info("Getting paginated audit history from {} to {}", startDate, endDate);
        return transactionAuditService.getAuditHistoryByDateRange(startDate, endDate, pageable)
                .map(TransactionAuditResponse::fromEntity);
    }

    /**
     * Obtiene auditoría por tipo de evento
     */
    public List<TransactionAuditResponse> getAuditHistoryByEventType(TransactionAudit.AuditEventType eventType) {
        log.info("Getting audit history for event type: {}", eventType);
        return transactionAuditService.getAuditHistoryByEventType(eventType)
                .stream()
                .map(TransactionAuditResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene auditoría por tipo de evento con paginación
     */
    public Page<TransactionAuditResponse> getAuditHistoryByEventType(TransactionAudit.AuditEventType eventType, Pageable pageable) {
        log.info("Getting paginated audit history for event type: {}", eventType);
        return transactionAuditService.getAuditHistoryByEventType(eventType, pageable)
                .map(TransactionAuditResponse::fromEntity);
    }

    /**
     * Busca auditoría por descripción
     */
    public List<TransactionAuditResponse> searchAuditHistory(String searchTerm) {
        log.info("Searching audit history with term: {}", searchTerm);
        return transactionAuditService.searchAuditHistory(searchTerm)
                .stream()
                .map(TransactionAuditResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene estadísticas de eventos por tipo en un rango de fechas
     */
    public Map<TransactionAudit.AuditEventType, Long> getEventStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Getting event statistics from {} to {}", startDate, endDate);
        return transactionAuditService.getEventStatistics(startDate, endDate);
    }

    /**
     * Busca transacciones sospechosas
     */
    public List<Object[]> findSuspiciousTransactions(LocalDateTime startDate, LocalDateTime endDate, int minFailures) {
        log.info("Finding suspicious transactions from {} to {} with minimum {} failures", startDate, endDate, minFailures);
        return transactionAuditService.findSuspiciousTransactions(startDate, endDate, minFailures);
    }

    /**
     * Obtiene auditoría por IP address
     */
    public List<TransactionAuditResponse> getAuditHistoryByIpAddress(String ipAddress) {
        log.info("Getting audit history for IP address: {}", ipAddress);
        return transactionAuditService.getAuditHistoryByIpAddress(ipAddress)
                .stream()
                .map(TransactionAuditResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene auditoría por session ID
     */
    public List<TransactionAuditResponse> getAuditHistoryBySessionId(String sessionId) {
        log.info("Getting audit history for session ID: {}", sessionId);
        return transactionAuditService.getAuditHistoryBySessionId(sessionId)
                .stream()
                .map(TransactionAuditResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene toda la auditoría con paginación
     */
    public Page<TransactionAuditResponse> getAllAuditHistory(Pageable pageable) {
        log.info("Getting all audit history with pagination");
        return transactionAuditService.getAllAuditHistory(pageable)
                .map(TransactionAuditResponse::fromEntity);
    }
} 