package com.mybank.domains.account.presentation.controller;

import com.mybank.shared.dto.ApiResponse;
import com.mybank.domains.account.application.dto.AccountResponse;
import com.mybank.domains.account.application.dto.CreateAccountRequest;
import com.mybank.domains.account.application.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Accounts", description = "API para gestión de cuentas bancarias")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    @Operation(summary = "Crear una nueva cuenta", description = "Crea una nueva cuenta bancaria para el usuario")
    public ResponseEntity<ApiResponse<AccountResponse>> createAccount(
            @Valid @RequestBody CreateAccountRequest request,
            @Parameter(description = "ID del usuario que crea la cuenta") 
            @RequestParam Long userId) {
        
        log.info("Received account creation request for user: {}, type: {}", userId, request.getAccountType());
        
        try {
            AccountResponse account = accountService.createAccount(request, userId);
            return ResponseEntity.ok(ApiResponse.success("Account created successfully", account));
        } catch (Exception e) {
            log.error("Error creating account: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{accountId}")
    @Operation(summary = "Obtener cuenta por ID", description = "Obtiene los detalles de una cuenta específica")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccountById(
            @Parameter(description = "ID de la cuenta") 
            @PathVariable Long accountId,
            @Parameter(description = "ID del usuario") 
            @RequestParam Long userId) {
        
        log.info("Getting account: {} for user: {}", accountId, userId);
        
        try {
            AccountResponse account = accountService.getAccountById(accountId, userId);
            return ResponseEntity.ok(ApiResponse.success("Account retrieved successfully", account));
        } catch (Exception e) {
            log.error("Error getting account: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Obtener todas las cuentas del usuario", description = "Obtiene todas las cuentas de un usuario específico")
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getUserAccounts(
            @Parameter(description = "ID del usuario") 
            @PathVariable Long userId) {
        
        log.info("Getting all accounts for user: {}", userId);
        
        try {
            List<AccountResponse> accounts = accountService.getUserAccounts(userId);
            return ResponseEntity.ok(ApiResponse.success("User accounts retrieved successfully", accounts));
        } catch (Exception e) {
            log.error("Error getting user accounts: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/number/{accountNumber}")
    @Operation(summary = "Obtener cuenta por número", description = "Obtiene los detalles de una cuenta por su número")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccountByNumber(
            @Parameter(description = "Número de cuenta") 
            @PathVariable String accountNumber,
            @Parameter(description = "ID del usuario") 
            @RequestParam Long userId) {
        
        log.info("Getting account by number: {} for user: {}", accountNumber, userId);
        
        try {
            AccountResponse account = accountService.getAccountByNumber(accountNumber, userId);
            return ResponseEntity.ok(ApiResponse.success("Account retrieved successfully", account));
        } catch (Exception e) {
            log.error("Error getting account by number: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
} 