package com.mybank.domains.transaction.domain.service;

import com.mybank.domains.transaction.domain.entity.Transaction;
import com.mybank.domains.transaction.domain.entity.TransactionAudit;
import com.mybank.domains.transaction.domain.repository.TransactionAuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TransactionAuditService {

    private final TransactionAuditRepository transactionAuditRepository;

    /**
     * Registra un evento de auditoría
     */
    public TransactionAudit logEvent(Transaction transaction, 
                                   TransactionAudit.AuditEventType eventType, 
                                   String eventDescription) {
        return logEvent(transaction, eventType, eventDescription, null, null, null, null);
    }

    /**
     * Registra un evento de auditoría con información adicional
     */
    public TransactionAudit logEvent(Transaction transaction, 
                                   TransactionAudit.AuditEventType eventType, 
                                   String eventDescription,
                                   String ipAddress,
                                   String userAgent,
                                   String sessionId,
                                   String additionalData) {
        
        TransactionAudit audit = TransactionAudit.fromTransaction(transaction, eventType, eventDescription);
        audit.setIpAddress(ipAddress);
        audit.setUserAgent(userAgent);
        audit.setSessionId(sessionId);
        audit.setAdditionalData(additionalData);

        TransactionAudit savedAudit = transactionAuditRepository.save(audit);
        log.info("Audit event logged: {} for transaction {} - {}", eventType, transaction.getTransactionNumber(), eventDescription);
        
        return savedAudit;
    }

    /**
     * Registra un cambio de estado de transacción
     */
    public TransactionAudit logStatusChange(Transaction transaction, 
                                          Transaction.TransactionStatus previousStatus,
                                          Transaction.TransactionStatus newStatus,
                                          String reason) {
        return logStatusChange(transaction, previousStatus, newStatus, reason, null, null, null);
    }

    /**
     * Registra un cambio de estado de transacción con información adicional
     */
    public TransactionAudit logStatusChange(Transaction transaction, 
                                          Transaction.TransactionStatus previousStatus,
                                          Transaction.TransactionStatus newStatus,
                                          String reason,
                                          String ipAddress,
                                          String userAgent,
                                          String sessionId) {
        
        TransactionAudit audit = TransactionAudit.statusChange(transaction, previousStatus, newStatus, reason);
        audit.setIpAddress(ipAddress);
        audit.setUserAgent(userAgent);
        audit.setSessionId(sessionId);

        TransactionAudit savedAudit = transactionAuditRepository.save(audit);
        log.info("Status change logged: {} -> {} for transaction {} - {}", 
                previousStatus, newStatus, transaction.getTransactionNumber(), reason);
        
        return savedAudit;
    }

    /**
     * Registra un evento de validación fallida
     */
    public TransactionAudit logValidationFailure(Transaction transaction, String validationError) {
        return logEvent(transaction, TransactionAudit.AuditEventType.VALIDATION_FAILED, 
                       "Validation failed: " + validationError);
    }

    /**
     * Registra un evento de error del sistema
     */
    public TransactionAudit logSystemError(Transaction transaction, String errorMessage) {
        return logEvent(transaction, TransactionAudit.AuditEventType.SYSTEM_ERROR, 
                       "System error: " + errorMessage);
    }

    /**
     * Registra un evento de detección de fraude
     */
    public TransactionAudit logFraudDetection(Transaction transaction, String fraudIndicator) {
        return logEvent(transaction, TransactionAudit.AuditEventType.FRAUD_DETECTED, 
                       "Fraud detected: " + fraudIndicator);
    }

    /**
     * Registra un evento de verificación de cumplimiento
     */
    public TransactionAudit logComplianceCheck(Transaction transaction, String complianceRule) {
        return logEvent(transaction, TransactionAudit.AuditEventType.COMPLIANCE_CHECK, 
                       "Compliance check: " + complianceRule);
    }

    // Métodos de consulta

    /**
     * Obtiene el historial de auditoría de una transacción
     */
    @Transactional(readOnly = true)
    public List<TransactionAudit> getTransactionAuditHistory(Long transactionId) {
        return transactionAuditRepository.findByTransactionIdOrderByCreatedAtDesc(transactionId);
    }

    /**
     * Obtiene el historial de auditoría de una transacción por número
     */
    @Transactional(readOnly = true)
    public List<TransactionAudit> getTransactionAuditHistory(String transactionNumber) {
        return transactionAuditRepository.findByTransactionNumberOrderByCreatedAtDesc(transactionNumber);
    }

    /**
     * Obtiene el historial de auditoría de un usuario
     */
    @Transactional(readOnly = true)
    public List<TransactionAudit> getUserAuditHistory(Long userId) {
        return transactionAuditRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Obtiene el historial de auditoría de un usuario con paginación
     */
    @Transactional(readOnly = true)
    public Page<TransactionAudit> getUserAuditHistory(Long userId, Pageable pageable) {
        return transactionAuditRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    /**
     * Obtiene el historial de auditoría de una cuenta
     */
    @Transactional(readOnly = true)
    public List<TransactionAudit> getAccountAuditHistory(Long accountId) {
        return transactionAuditRepository.findByAccountId(accountId);
    }

    /**
     * Obtiene el historial de auditoría de una cuenta por número
     */
    @Transactional(readOnly = true)
    public List<TransactionAudit> getAccountAuditHistory(String accountNumber) {
        return transactionAuditRepository.findByAccountNumber(accountNumber);
    }

    /**
     * Obtiene auditoría por rango de fechas
     */
    @Transactional(readOnly = true)
    public List<TransactionAudit> getAuditHistoryByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return transactionAuditRepository.findByDateRange(startDate, endDate);
    }

    /**
     * Obtiene auditoría por rango de fechas con paginación
     */
    @Transactional(readOnly = true)
    public Page<TransactionAudit> getAuditHistoryByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return transactionAuditRepository.findByDateRange(startDate, endDate, pageable);
    }

    /**
     * Obtiene auditoría por tipo de evento
     */
    @Transactional(readOnly = true)
    public List<TransactionAudit> getAuditHistoryByEventType(TransactionAudit.AuditEventType eventType) {
        return transactionAuditRepository.findByEventTypeOrderByCreatedAtDesc(eventType);
    }

    /**
     * Obtiene auditoría por tipo de evento con paginación
     */
    @Transactional(readOnly = true)
    public Page<TransactionAudit> getAuditHistoryByEventType(TransactionAudit.AuditEventType eventType, Pageable pageable) {
        return transactionAuditRepository.findByEventTypeOrderByCreatedAtDesc(eventType, pageable);
    }

    /**
     * Busca auditoría por descripción
     */
    @Transactional(readOnly = true)
    public List<TransactionAudit> searchAuditHistory(String searchTerm) {
        return transactionAuditRepository.findByEventDescriptionContaining(searchTerm);
    }

    /**
     * Obtiene estadísticas de eventos por tipo en un rango de fechas
     */
    @Transactional(readOnly = true)
    public Map<TransactionAudit.AuditEventType, Long> getEventStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> results = transactionAuditRepository.countEventsByTypeInDateRange(startDate, endDate);
        return results.stream()
                .collect(java.util.stream.Collectors.toMap(
                        row -> (TransactionAudit.AuditEventType) row[0],
                        row -> (Long) row[1]
                ));
    }

    /**
     * Busca transacciones sospechosas
     */
    @Transactional(readOnly = true)
    public List<Object[]> findSuspiciousTransactions(LocalDateTime startDate, LocalDateTime endDate, int minFailures) {
        return transactionAuditRepository.findSuspiciousTransactions(startDate, endDate, minFailures);
    }

    /**
     * Obtiene auditoría por IP address
     */
    @Transactional(readOnly = true)
    public List<TransactionAudit> getAuditHistoryByIpAddress(String ipAddress) {
        return transactionAuditRepository.findByIpAddressOrderByCreatedAtDesc(ipAddress);
    }

    /**
     * Obtiene auditoría por session ID
     */
    @Transactional(readOnly = true)
    public List<TransactionAudit> getAuditHistoryBySessionId(String sessionId) {
        return transactionAuditRepository.findBySessionIdOrderByCreatedAtDesc(sessionId);
    }

    /**
     * Obtiene toda la auditoría con paginación
     */
    @Transactional(readOnly = true)
    public Page<TransactionAudit> getAllAuditHistory(Pageable pageable) {
        return transactionAuditRepository.findAllByOrderByCreatedAtDesc(pageable);
    }
} 