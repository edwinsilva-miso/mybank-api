package com.mybank.domains.account.application.dto;

import com.mybank.domains.account.domain.entity.Account;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateAccountRequest {

    @NotNull(message = "Account type is required")
    private Account.AccountType accountType;

    private String description;
} 