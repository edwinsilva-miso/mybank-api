package com.mybank.domains.account.application.service;

import com.mybank.domains.account.application.dto.AccountResponse;
import com.mybank.domains.account.application.dto.CreateAccountRequest;
import com.mybank.domains.account.domain.entity.Account;
import com.mybank.domains.account.domain.service.AccountDomainService;
import com.mybank.domains.user.domain.entity.User;
import com.mybank.domains.user.domain.repository.UserRepository;
import com.mybank.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AccountService {

    private final AccountDomainService accountDomainService;
    private final UserRepository userRepository;

    public AccountResponse createAccount(CreateAccountRequest request, Long userId) {
        log.info("Creating account for user: {}, type: {}", userId, request.getAccountType());

        // Obtener el usuario
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found", "USER_NOT_FOUND", "ACCOUNT"));

        // Crear la cuenta
        Account account = new Account();
        account.setAccountType(request.getAccountType());
        account.setUser(user);
        account.setStatus(Account.AccountStatus.ACTIVE);
        account.setBalance(java.math.BigDecimal.ZERO);

        // Guardar la cuenta
        Account savedAccount = accountDomainService.createAccount(account);

        log.info("Account created successfully: {}", savedAccount.getAccountNumber());

        return AccountResponse.fromEntity(savedAccount);
    }

    public AccountResponse getAccountById(Long accountId, Long userId) {
        log.info("Getting account: {} for user: {}", accountId, userId);

        Account account = accountDomainService.findById(accountId);

        // Validar que el usuario tenga acceso a la cuenta
        if (!account.getUser().getId().equals(userId)) {
            throw new BusinessException("Access denied to account", "ACCESS_DENIED", "ACCOUNT");
        }

        return AccountResponse.fromEntity(account);
    }

    public List<AccountResponse> getUserAccounts(Long userId) {
        log.info("Getting all accounts for user: {}", userId);

        // Verificar que el usuario existe
        userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found", "USER_NOT_FOUND", "ACCOUNT"));

        List<Account> accounts = accountDomainService.findByUserId(userId);

        return accounts.stream()
                .map(AccountResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public AccountResponse getAccountByNumber(String accountNumber, Long userId) {
        log.info("Getting account by number: {} for user: {}", accountNumber, userId);

        Account account = accountDomainService.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BusinessException("Account not found", "ACCOUNT_NOT_FOUND", "ACCOUNT"));

        // Validar que el usuario tenga acceso a la cuenta
        if (!account.getUser().getId().equals(userId)) {
            throw new BusinessException("Access denied to account", "ACCESS_DENIED", "ACCOUNT");
        }

        return AccountResponse.fromEntity(account);
    }
} 