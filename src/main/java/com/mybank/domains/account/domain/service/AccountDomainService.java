package com.mybank.domains.account.domain.service;

import com.mybank.domains.account.domain.entity.Account;
import com.mybank.domains.account.domain.repository.AccountRepository;
import com.mybank.domains.user.domain.entity.User;
import com.mybank.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountDomainService {

    private final AccountRepository accountRepository;

    public Account createAccount(Account account) {
        log.info("Creating new account for user: {}, type: {}", account.getUser().getUsername(), account.getAccountType());
        
        // Validar que el usuario no tenga demasiadas cuentas del mismo tipo
        validateAccountLimit(account);
        
        // Generar número de cuenta único
        account.setAccountNumber(generateAccountNumber());
        
        // Validar que el número de cuenta sea único
        if (accountRepository.existsByAccountNumber(account.getAccountNumber())) {
            throw new BusinessException("Account number already exists", "ACCOUNT_NUMBER_EXISTS", "ACCOUNT");
        }

        Account savedAccount = accountRepository.save(account);
        log.info("Account created successfully: {}", savedAccount.getAccountNumber());
        
        return savedAccount;
    }

    public Account findById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Account not found", "ACCOUNT_NOT_FOUND", "ACCOUNT"));
    }

    public Optional<Account> findByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    public List<Account> findByUser(User user) {
        return accountRepository.findByUser(user);
    }

    public List<Account> findByUserId(Long userId) {
        return accountRepository.findByUserId(userId);
    }

    public List<Account> findByUserAndStatus(User user, Account.AccountStatus status) {
        return accountRepository.findByUserAndStatus(user, status);
    }

    public boolean existsByAccountNumber(String accountNumber) {
        return accountRepository.existsByAccountNumber(accountNumber);
    }

    public Account updateBalance(Account account, BigDecimal newBalance) {
        account.setBalance(newBalance);
        return accountRepository.save(account);
    }

    public Account updateStatus(Account account, Account.AccountStatus status) {
        account.setStatus(status);
        return accountRepository.save(account);
    }

    private void validateAccountLimit(Account account) {
        List<Account> existingAccounts = accountRepository.findByUserAndAccountType(
                account.getUser().getId(), 
                account.getAccountType()
        );
        
        // Límite de 2 cuentas por tipo por usuario
        if (existingAccounts.size() >= 2) {
            throw new BusinessException(
                    "User already has maximum number of " + account.getAccountType() + " accounts", 
                    "ACCOUNT_LIMIT_EXCEEDED", 
                    "ACCOUNT"
            );
        }
    }

    private String generateAccountNumber() {
        // Generar número de cuenta único: AC + timestamp + 4 dígitos aleatorios
        long timestamp = System.currentTimeMillis();
        int random = (int) (Math.random() * 10000);
        return String.format("AC%d%04d", timestamp, random);
    }
} 