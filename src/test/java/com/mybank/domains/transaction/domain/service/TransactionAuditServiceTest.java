package com.mybank.domains.transaction.domain.service;

import com.mybank.domains.account.domain.entity.Account;
import com.mybank.domains.transaction.domain.entity.Transaction;
import com.mybank.domains.transaction.domain.entity.TransactionAudit;
import com.mybank.domains.transaction.domain.repository.TransactionAuditRepository;
import com.mybank.domains.user.domain.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionAuditServiceTest {

    @Mock
    private TransactionAuditRepository transactionAuditRepository;

    @InjectMocks
    private TransactionAuditService transactionAuditService;

    private User testUser;
    private Account testAccount;
    private Transaction testTransaction;
    private TransactionAudit testAudit;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        testAccount = new Account();
        testAccount.setId(1L);
        testAccount.setAccountNumber("AC1234567890123456789");
        testAccount.setAccountType(Account.AccountType.CHECKING);
        testAccount.setStatus(Account.AccountStatus.ACTIVE);
        testAccount.setBalance(new BigDecimal("1000.00"));
        testAccount.setUser(testUser);

        testTransaction = new Transaction();
        testTransaction.setId(1L);
        testTransaction.setTransactionNumber("TXN1234567890123456789");
        testTransaction.setType(Transaction.TransactionType.DEPOSIT);
        testTransaction.setStatus(Transaction.TransactionStatus.PENDING);
        testTransaction.setAmount(new BigDecimal("100.00"));
        testTransaction.setFee(BigDecimal.ZERO);
        testTransaction.setTax(BigDecimal.ZERO);
        testTransaction.setTotalAmount(new BigDecimal("100.00"));
        testTransaction.setUser(testUser);
        testTransaction.setAccount(testAccount);

        testAudit = new TransactionAudit();
        testAudit.setId(1L);
        testAudit.setTransactionId(1L);
        testAudit.setTransactionNumber("TXN1234567890123456789");
        testAudit.setEventType(TransactionAudit.AuditEventType.TRANSACTION_CREATED);
        testAudit.setEventDescription("Transaction created successfully");
        testAudit.setUserId(1L);
        testAudit.setUserUsername("testuser");
        testAudit.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void logEvent_Success() {
        // Given
        when(transactionAuditRepository.save(any(TransactionAudit.class))).thenReturn(testAudit);

        // When
        TransactionAudit result = transactionAuditService.logEvent(testTransaction, 
                                                                 TransactionAudit.AuditEventType.TRANSACTION_CREATED, 
                                                                 "Test event");

        // Then
        assertNotNull(result);
        assertEquals(testAudit.getId(), result.getId());
        assertEquals(testAudit.getTransactionId(), result.getTransactionId());
        assertEquals(testAudit.getEventType(), result.getEventType());
        verify(transactionAuditRepository).save(any(TransactionAudit.class));
    }

    @Test
    void logEvent_WithAdditionalInfo_Success() {
        // Given
        when(transactionAuditRepository.save(any(TransactionAudit.class))).thenReturn(testAudit);

        // When
        TransactionAudit result = transactionAuditService.logEvent(testTransaction, 
                                                                 TransactionAudit.AuditEventType.TRANSACTION_CREATED, 
                                                                 "Test event",
                                                                 "192.168.1.1",
                                                                 "Mozilla/5.0",
                                                                 "session123",
                                                                 "additional data");

        // Then
        assertNotNull(result);
        verify(transactionAuditRepository).save(any(TransactionAudit.class));
    }

    @Test
    void logStatusChange_Success() {
        // Given
        when(transactionAuditRepository.save(any(TransactionAudit.class))).thenReturn(testAudit);

        // When
        TransactionAudit result = transactionAuditService.logStatusChange(testTransaction, 
                                                                         Transaction.TransactionStatus.PENDING,
                                                                         Transaction.TransactionStatus.COMPLETED,
                                                                         "Transaction completed");

        // Then
        assertNotNull(result);
        assertEquals(testAudit.getId(), result.getId());
        verify(transactionAuditRepository).save(any(TransactionAudit.class));
    }

    @Test
    void logValidationFailure_Success() {
        // Given
        TransactionAudit validationAudit = new TransactionAudit();
        validationAudit.setId(1L);
        validationAudit.setEventType(TransactionAudit.AuditEventType.VALIDATION_FAILED);
        validationAudit.setEventDescription("Validation failed: Invalid amount");
        when(transactionAuditRepository.save(any(TransactionAudit.class))).thenReturn(validationAudit);

        // When
        TransactionAudit result = transactionAuditService.logValidationFailure(testTransaction, "Invalid amount");

        // Then
        assertNotNull(result);
        assertEquals(TransactionAudit.AuditEventType.VALIDATION_FAILED, result.getEventType());
        assertTrue(result.getEventDescription().contains("Invalid amount"));
        verify(transactionAuditRepository).save(any(TransactionAudit.class));
    }

    @Test
    void logSystemError_Success() {
        // Given
        TransactionAudit systemErrorAudit = new TransactionAudit();
        systemErrorAudit.setId(1L);
        systemErrorAudit.setEventType(TransactionAudit.AuditEventType.SYSTEM_ERROR);
        systemErrorAudit.setEventDescription("System error: Database connection failed");
        when(transactionAuditRepository.save(any(TransactionAudit.class))).thenReturn(systemErrorAudit);

        // When
        TransactionAudit result = transactionAuditService.logSystemError(testTransaction, "Database connection failed");

        // Then
        assertNotNull(result);
        assertEquals(TransactionAudit.AuditEventType.SYSTEM_ERROR, result.getEventType());
        assertTrue(result.getEventDescription().contains("Database connection failed"));
        verify(transactionAuditRepository).save(any(TransactionAudit.class));
    }

    @Test
    void logFraudDetection_Success() {
        // Given
        TransactionAudit fraudAudit = new TransactionAudit();
        fraudAudit.setId(1L);
        fraudAudit.setEventType(TransactionAudit.AuditEventType.FRAUD_DETECTED);
        fraudAudit.setEventDescription("Fraud detected: Unusual transaction pattern");
        when(transactionAuditRepository.save(any(TransactionAudit.class))).thenReturn(fraudAudit);

        // When
        TransactionAudit result = transactionAuditService.logFraudDetection(testTransaction, "Unusual transaction pattern");

        // Then
        assertNotNull(result);
        assertEquals(TransactionAudit.AuditEventType.FRAUD_DETECTED, result.getEventType());
        assertTrue(result.getEventDescription().contains("Unusual transaction pattern"));
        verify(transactionAuditRepository).save(any(TransactionAudit.class));
    }

    @Test
    void logComplianceCheck_Success() {
        // Given
        TransactionAudit complianceAudit = new TransactionAudit();
        complianceAudit.setId(1L);
        complianceAudit.setEventType(TransactionAudit.AuditEventType.COMPLIANCE_CHECK);
        complianceAudit.setEventDescription("Compliance check: AML check passed");
        when(transactionAuditRepository.save(any(TransactionAudit.class))).thenReturn(complianceAudit);

        // When
        TransactionAudit result = transactionAuditService.logComplianceCheck(testTransaction, "AML check passed");

        // Then
        assertNotNull(result);
        assertEquals(TransactionAudit.AuditEventType.COMPLIANCE_CHECK, result.getEventType());
        assertTrue(result.getEventDescription().contains("AML check passed"));
        verify(transactionAuditRepository).save(any(TransactionAudit.class));
    }

    @Test
    void getTransactionAuditHistory_Success() {
        // Given
        List<TransactionAudit> auditList = Arrays.asList(testAudit);
        when(transactionAuditRepository.findByTransactionIdOrderByCreatedAtDesc(1L)).thenReturn(auditList);

        // When
        List<TransactionAudit> result = transactionAuditService.getTransactionAuditHistory(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAudit.getId(), result.get(0).getId());
        verify(transactionAuditRepository).findByTransactionIdOrderByCreatedAtDesc(1L);
    }

    @Test
    void getTransactionAuditHistoryByNumber_Success() {
        // Given
        List<TransactionAudit> auditList = Arrays.asList(testAudit);
        when(transactionAuditRepository.findByTransactionNumberOrderByCreatedAtDesc("TXN1234567890123456789")).thenReturn(auditList);

        // When
        List<TransactionAudit> result = transactionAuditService.getTransactionAuditHistory("TXN1234567890123456789");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAudit.getId(), result.get(0).getId());
        verify(transactionAuditRepository).findByTransactionNumberOrderByCreatedAtDesc("TXN1234567890123456789");
    }

    @Test
    void getUserAuditHistory_Success() {
        // Given
        List<TransactionAudit> auditList = Arrays.asList(testAudit);
        when(transactionAuditRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(auditList);

        // When
        List<TransactionAudit> result = transactionAuditService.getUserAuditHistory(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAudit.getId(), result.get(0).getId());
        verify(transactionAuditRepository).findByUserIdOrderByCreatedAtDesc(1L);
    }

    @Test
    void getUserAuditHistoryPaginated_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<TransactionAudit> auditPage = new PageImpl<>(Arrays.asList(testAudit), pageable, 1);
        when(transactionAuditRepository.findByUserIdOrderByCreatedAtDesc(1L, pageable)).thenReturn(auditPage);

        // When
        Page<TransactionAudit> result = transactionAuditService.getUserAuditHistory(1L, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testAudit.getId(), result.getContent().get(0).getId());
        verify(transactionAuditRepository).findByUserIdOrderByCreatedAtDesc(1L, pageable);
    }

    @Test
    void getAccountAuditHistory_Success() {
        // Given
        List<TransactionAudit> auditList = Arrays.asList(testAudit);
        when(transactionAuditRepository.findByAccountId(1L)).thenReturn(auditList);

        // When
        List<TransactionAudit> result = transactionAuditService.getAccountAuditHistory(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAudit.getId(), result.get(0).getId());
        verify(transactionAuditRepository).findByAccountId(1L);
    }

    @Test
    void getAuditHistoryByDateRange_Success() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now();
        List<TransactionAudit> auditList = Arrays.asList(testAudit);
        when(transactionAuditRepository.findByDateRange(startDate, endDate)).thenReturn(auditList);

        // When
        List<TransactionAudit> result = transactionAuditService.getAuditHistoryByDateRange(startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAudit.getId(), result.get(0).getId());
        verify(transactionAuditRepository).findByDateRange(startDate, endDate);
    }

    @Test
    void getAuditHistoryByEventType_Success() {
        // Given
        List<TransactionAudit> auditList = Arrays.asList(testAudit);
        when(transactionAuditRepository.findByEventTypeOrderByCreatedAtDesc(TransactionAudit.AuditEventType.TRANSACTION_CREATED)).thenReturn(auditList);

        // When
        List<TransactionAudit> result = transactionAuditService.getAuditHistoryByEventType(TransactionAudit.AuditEventType.TRANSACTION_CREATED);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAudit.getId(), result.get(0).getId());
        verify(transactionAuditRepository).findByEventTypeOrderByCreatedAtDesc(TransactionAudit.AuditEventType.TRANSACTION_CREATED);
    }

    @Test
    void searchAuditHistory_Success() {
        // Given
        List<TransactionAudit> auditList = Arrays.asList(testAudit);
        when(transactionAuditRepository.findByEventDescriptionContaining("test")).thenReturn(auditList);

        // When
        List<TransactionAudit> result = transactionAuditService.searchAuditHistory("test");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAudit.getId(), result.get(0).getId());
        verify(transactionAuditRepository).findByEventDescriptionContaining("test");
    }

    @Test
    void getEventStatistics_Success() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now();
        List<Object[]> statisticsData = Arrays.asList(
            new Object[]{TransactionAudit.AuditEventType.TRANSACTION_CREATED, 5L},
            new Object[]{TransactionAudit.AuditEventType.TRANSACTION_COMPLETED, 3L}
        );
        when(transactionAuditRepository.countEventsByTypeInDateRange(startDate, endDate)).thenReturn(statisticsData);

        // When
        Map<TransactionAudit.AuditEventType, Long> result = transactionAuditService.getEventStatistics(startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(5L, result.get(TransactionAudit.AuditEventType.TRANSACTION_CREATED));
        assertEquals(3L, result.get(TransactionAudit.AuditEventType.TRANSACTION_COMPLETED));
        verify(transactionAuditRepository).countEventsByTypeInDateRange(startDate, endDate);
    }

    @Test
    void findSuspiciousTransactions_Success() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now();
        List<Object[]> suspiciousData = Arrays.asList(
            new Object[]{1L, 5L},
            new Object[]{2L, 3L}
        );
        when(transactionAuditRepository.findSuspiciousTransactions(startDate, endDate, 3)).thenReturn(suspiciousData);

        // When
        List<Object[]> result = transactionAuditService.findSuspiciousTransactions(startDate, endDate, 3);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(transactionAuditRepository).findSuspiciousTransactions(startDate, endDate, 3);
    }

    @Test
    void getAuditHistoryByIpAddress_Success() {
        // Given
        List<TransactionAudit> auditList = Arrays.asList(testAudit);
        when(transactionAuditRepository.findByIpAddressOrderByCreatedAtDesc("192.168.1.1")).thenReturn(auditList);

        // When
        List<TransactionAudit> result = transactionAuditService.getAuditHistoryByIpAddress("192.168.1.1");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAudit.getId(), result.get(0).getId());
        verify(transactionAuditRepository).findByIpAddressOrderByCreatedAtDesc("192.168.1.1");
    }

    @Test
    void getAllAuditHistoryPaginated_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<TransactionAudit> auditPage = new PageImpl<>(Arrays.asList(testAudit), pageable, 1);
        when(transactionAuditRepository.findAllByOrderByCreatedAtDesc(pageable)).thenReturn(auditPage);

        // When
        Page<TransactionAudit> result = transactionAuditService.getAllAuditHistory(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testAudit.getId(), result.getContent().get(0).getId());
        verify(transactionAuditRepository).findAllByOrderByCreatedAtDesc(pageable);
    }
} 