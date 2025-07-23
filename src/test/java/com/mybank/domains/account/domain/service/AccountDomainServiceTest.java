package com.mybank.domains.account.domain.service;

import com.mybank.domains.account.domain.entity.Account;
import com.mybank.domains.account.domain.repository.AccountRepository;
import com.mybank.domains.user.domain.entity.User;
import com.mybank.shared.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountDomainServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountDomainService accountDomainService;

    private User testUser;
    private Account testAccount;

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
        testAccount.setBalance(BigDecimal.ZERO);
        testAccount.setUser(testUser);
    }

    @Test
    void createAccount_Success() {
        // Given
        when(accountRepository.findByUserAndAccountType(anyLong(), any(Account.AccountType.class)))
                .thenReturn(Arrays.asList());
        when(accountRepository.existsByAccountNumber(anyString())).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        // When
        Account result = accountDomainService.createAccount(testAccount);

        // Then
        assertNotNull(result);
        assertNotNull(result.getAccountNumber());
        assertTrue(result.getAccountNumber().startsWith("AC"));
        verify(accountRepository).findByUserAndAccountType(testUser.getId(), testAccount.getAccountType());
        verify(accountRepository).save(testAccount);
    }

    @Test
    void createAccount_AccountLimitExceeded_ThrowsException() {
        // Given
        Account existingAccount = new Account();
        existingAccount.setAccountType(Account.AccountType.CHECKING);
        
        when(accountRepository.findByUserAndAccountType(anyLong(), any(Account.AccountType.class)))
                .thenReturn(Arrays.asList(existingAccount, new Account())); // 2 cuentas existentes

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            accountDomainService.createAccount(testAccount);
        });

        assertTrue(exception.getMessage().contains("maximum number of"));
        assertEquals("ACCOUNT_LIMIT_EXCEEDED", exception.getErrorCode());
        verify(accountRepository).findByUserAndAccountType(testUser.getId(), testAccount.getAccountType());
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void createAccount_AccountNumberExists_ThrowsException() {
        // Given
        when(accountRepository.findByUserAndAccountType(anyLong(), any(Account.AccountType.class)))
                .thenReturn(Arrays.asList());
        when(accountRepository.existsByAccountNumber(anyString())).thenReturn(true);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            accountDomainService.createAccount(testAccount);
        });

        assertEquals("Account number already exists", exception.getMessage());
        assertEquals("ACCOUNT_NUMBER_EXISTS", exception.getErrorCode());
        verify(accountRepository).findByUserAndAccountType(testUser.getId(), testAccount.getAccountType());
        verify(accountRepository).existsByAccountNumber(anyString());
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void findById_Success() {
        // Given
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        // When
        Account result = accountDomainService.findById(1L);

        // Then
        assertNotNull(result);
        assertEquals(testAccount.getId(), result.getId());
        verify(accountRepository).findById(1L);
    }

    @Test
    void findById_AccountNotFound_ThrowsException() {
        // Given
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            accountDomainService.findById(1L);
        });

        assertEquals("Account not found", exception.getMessage());
        assertEquals("ACCOUNT_NOT_FOUND", exception.getErrorCode());
        verify(accountRepository).findById(1L);
    }

    @Test
    void findByAccountNumber_Success() {
        // Given
        when(accountRepository.findByAccountNumber("AC1234567890123456789")).thenReturn(Optional.of(testAccount));

        // When
        Optional<Account> result = accountDomainService.findByAccountNumber("AC1234567890123456789");

        // Then
        assertTrue(result.isPresent());
        assertEquals(testAccount.getAccountNumber(), result.get().getAccountNumber());
        verify(accountRepository).findByAccountNumber("AC1234567890123456789");
    }

    @Test
    void findByUser_Success() {
        // Given
        List<Account> accounts = Arrays.asList(testAccount);
        when(accountRepository.findByUser(testUser)).thenReturn(accounts);

        // When
        List<Account> result = accountDomainService.findByUser(testUser);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAccount.getId(), result.get(0).getId());
        verify(accountRepository).findByUser(testUser);
    }

    @Test
    void updateBalance_Success() {
        // Given
        BigDecimal newBalance = new BigDecimal("1000.00");
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        // When
        Account result = accountDomainService.updateBalance(testAccount, newBalance);

        // Then
        assertNotNull(result);
        assertEquals(newBalance, testAccount.getBalance());
        verify(accountRepository).save(testAccount);
    }

    @Test
    void updateStatus_Success() {
        // Given
        Account.AccountStatus newStatus = Account.AccountStatus.SUSPENDED;
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        // When
        Account result = accountDomainService.updateStatus(testAccount, newStatus);

        // Then
        assertNotNull(result);
        assertEquals(newStatus, testAccount.getStatus());
        verify(accountRepository).save(testAccount);
    }
} 