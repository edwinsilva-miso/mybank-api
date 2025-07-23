package com.mybank.domains.account.application.dto;

import com.mybank.domains.account.domain.entity.Account;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AccountResponse {
    private Long id;
    private String accountNumber;
    private Account.AccountType accountType;
    private Account.AccountStatus status;
    private BigDecimal balance;
    private Long userId;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AccountResponse fromEntity(Account account) {
        AccountResponse response = new AccountResponse();
        response.setId(account.getId());
        response.setAccountNumber(account.getAccountNumber());
        response.setAccountType(account.getAccountType());
        response.setStatus(account.getStatus());
        response.setBalance(account.getBalance());
        response.setUserId(account.getUser().getId());
        response.setUsername(account.getUser().getUsername());
        response.setCreatedAt(account.getCreatedAt());
        response.setUpdatedAt(account.getUpdatedAt());
        return response;
    }
} 