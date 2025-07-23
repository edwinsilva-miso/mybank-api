package com.mybank.domains.transaction.domain.service;

import com.mybank.domains.account.domain.entity.Account;
import com.mybank.domains.transaction.domain.entity.Transaction;
import com.mybank.domains.transaction.domain.repository.TransactionRepository;
import com.mybank.domains.user.domain.entity.User;
import com.mybank.shared.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionDomainServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionAuditService transactionAuditService;

    @InjectMocks
    private TransactionDomainService transactionDomainService;

    private User testUser;
    private Account testAccount;
    private Transaction testTransaction;

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
    }

    @Test
    void createTransaction_Success() {
        // Given
        when(transactionRepository.existsByTransactionNumber(anyString())).thenReturn(false);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        // When
        Transaction result = transactionDomainService.createTransaction(testTransaction);

        // Then
        assertNotNull(result);
        assertEquals(testTransaction.getTransactionNumber(), result.getTransactionNumber());
        verify(transactionRepository).existsByTransactionNumber(testTransaction.getTransactionNumber());
        verify(transactionRepository).save(testTransaction);
    }

    @Test
    void createTransaction_TransactionNumberExists_ThrowsException() {
        // Given
        when(transactionRepository.existsByTransactionNumber(anyString())).thenReturn(true);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            transactionDomainService.createTransaction(testTransaction);
        });

        assertEquals("Transaction number already exists", exception.getMessage());
        assertEquals("TRANSACTION_NUMBER_EXISTS", exception.getErrorCode());
        verify(transactionRepository).existsByTransactionNumber(testTransaction.getTransactionNumber());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void createTransaction_InvalidAmount_ThrowsException() {
        // Given
        Transaction invalidTransaction = new Transaction();
        invalidTransaction.setId(1L);
        invalidTransaction.setTransactionNumber("TXN1234567890123456789");
        invalidTransaction.setType(Transaction.TransactionType.DEPOSIT);
        invalidTransaction.setStatus(Transaction.TransactionStatus.PENDING);
        invalidTransaction.setAmount(BigDecimal.ZERO);
        invalidTransaction.setFee(BigDecimal.ZERO);
        invalidTransaction.setTax(BigDecimal.ZERO);
        invalidTransaction.setTotalAmount(BigDecimal.ZERO);
        invalidTransaction.setUser(testUser);
        invalidTransaction.setAccount(testAccount);
        
        when(transactionRepository.existsByTransactionNumber(anyString())).thenReturn(false);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            transactionDomainService.createTransaction(invalidTransaction);
        });

        assertTrue(exception.getMessage().contains("Transaction amount must be greater than zero"));
        assertEquals("INVALID_AMOUNT", exception.getErrorCode());
        verify(transactionRepository).existsByTransactionNumber(invalidTransaction.getTransactionNumber());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void processTransaction_Deposit_Success() {
        // Given
        testTransaction.setType(Transaction.TransactionType.DEPOSIT);
        testTransaction.setStatus(Transaction.TransactionStatus.PENDING);

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        // When
        Transaction result = transactionDomainService.processTransaction(1L);

        // Then
        assertEquals(Transaction.TransactionStatus.COMPLETED, result.getStatus());
        assertNotNull(result.getProcessedAt());
        verify(transactionRepository).findById(1L);
        verify(transactionRepository, atLeastOnce()).save(any(Transaction.class));
    }

    @Test
    void processTransaction_Withdrawal_Success() {
        // Given
        testTransaction.setType(Transaction.TransactionType.WITHDRAWAL);
        testTransaction.setStatus(Transaction.TransactionStatus.PENDING);
        testTransaction.setAmount(new BigDecimal("50.00"));
        testTransaction.setTotalAmount(new BigDecimal("50.00"));

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        // When
        Transaction result = transactionDomainService.processTransaction(1L);

        // Then
        assertEquals(Transaction.TransactionStatus.COMPLETED, result.getStatus());
        assertNotNull(result.getProcessedAt());
        verify(transactionRepository).findById(1L);
        verify(transactionRepository, atLeastOnce()).save(any(Transaction.class));
    }

    @Test
    void processTransaction_Withdrawal_InsufficientFunds_ThrowsException() {
        // Given
        testTransaction.setType(Transaction.TransactionType.WITHDRAWAL);
        testTransaction.setStatus(Transaction.TransactionStatus.PENDING);
        testTransaction.setAmount(new BigDecimal("2000.00")); // Más que el balance disponible
        testTransaction.setTotalAmount(new BigDecimal("2000.00"));

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            transactionDomainService.processTransaction(1L);
        });

        assertTrue(exception.getMessage().contains("Insufficient funds"));
        assertEquals("INSUFFICIENT_FUNDS", exception.getErrorCode());
        verify(transactionRepository).findById(1L);
    }

    @Test
    void processTransaction_Transfer_Success() {
        // Given
        Account destinationAccount = new Account();
        destinationAccount.setId(2L);
        destinationAccount.setAccountNumber("AC9876543210987654321");
        destinationAccount.setBalance(BigDecimal.ZERO);
        destinationAccount.setUser(testUser);

        testTransaction.setType(Transaction.TransactionType.TRANSFER);
        testTransaction.setStatus(Transaction.TransactionStatus.PENDING);
        testTransaction.setSourceAccount(testAccount);
        testTransaction.setDestinationAccount(destinationAccount);
        testTransaction.setAmount(new BigDecimal("100.00"));
        testTransaction.setTotalAmount(new BigDecimal("100.00"));

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        // When
        Transaction result = transactionDomainService.processTransaction(1L);

        // Then
        assertEquals(Transaction.TransactionStatus.COMPLETED, result.getStatus());
        assertNotNull(result.getProcessedAt());
        verify(transactionRepository).findById(1L);
        verify(transactionRepository, atLeastOnce()).save(any(Transaction.class));
    }

    @Test
    void processTransaction_Transfer_SameAccount_ThrowsException() {
        // Given
        testTransaction.setType(Transaction.TransactionType.TRANSFER);
        testTransaction.setSourceAccount(testAccount);
        testTransaction.setDestinationAccount(testAccount); // Misma cuenta
        testTransaction.setAmount(new BigDecimal("100.00"));
        testTransaction.setTotalAmount(new BigDecimal("100.00"));

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            transactionDomainService.processTransaction(1L);
        });

        assertTrue(exception.getMessage().contains("Cannot transfer to the same account"));
        assertEquals("SAME_ACCOUNT_TRANSFER", exception.getErrorCode());
        verify(transactionRepository).findById(1L);
    }

    @Test
    void findById_Success() {
        // Given
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));

        // When
        Transaction result = transactionDomainService.findById(1L);

        // Then
        assertNotNull(result);
        assertEquals(testTransaction.getId(), result.getId());
        verify(transactionRepository).findById(1L);
    }

    @Test
    void findById_TransactionNotFound_ThrowsException() {
        // Given
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            transactionDomainService.findById(1L);
        });

        assertEquals("Transaction not found", exception.getMessage());
        assertEquals("TRANSACTION_NOT_FOUND", exception.getErrorCode());
        verify(transactionRepository).findById(1L);
    }
} 