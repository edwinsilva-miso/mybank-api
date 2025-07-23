package com.mybank.domains.transaction.presentation.controller;

import com.mybank.domains.transaction.application.dto.CreateTransactionRequest;
import com.mybank.domains.transaction.application.dto.TransactionResponse;
import com.mybank.domains.transaction.application.service.TransactionService;
import com.mybank.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Transactions", description = "API para gestión de transacciones bancarias")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    @Operation(summary = "Crear una nueva transacción", description = "Crea una nueva transacción bancaria")
    public ResponseEntity<ApiResponse<TransactionResponse>> createTransaction(
            @Valid @RequestBody CreateTransactionRequest request,
            @Parameter(description = "ID del usuario que realiza la transacción") 
            @RequestParam Long userId) {
        
        log.info("Received transaction creation request for user: {}, type: {}, amount: {}, accountId: {}", 
                userId, request.getType(), request.getAmount(), request.getAccountId());
        
        try {
            TransactionResponse transaction = transactionService.createTransaction(request, userId);
            return ResponseEntity.ok(ApiResponse.success("Transaction created successfully", transaction));
        } catch (Exception e) {
            log.error("Error creating transaction: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{transactionId}/process")
    @Operation(summary = "Procesar una transacción", description = "Procesa una transacción pendiente")
    public ResponseEntity<ApiResponse<TransactionResponse>> processTransaction(
            @Parameter(description = "ID de la transacción") 
            @PathVariable Long transactionId) {
        
        log.info("Received transaction processing request for ID: {}", transactionId);
        
        try {
            TransactionResponse transaction = transactionService.processTransaction(transactionId);
            return ResponseEntity.ok(ApiResponse.success("Transaction processed successfully", transaction));
        } catch (Exception e) {
            log.error("Error processing transaction: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{transactionId}")
    @Operation(summary = "Obtener transacción por ID", description = "Obtiene los detalles de una transacción específica")
    public ResponseEntity<ApiResponse<TransactionResponse>> getTransactionById(
            @Parameter(description = "ID de la transacción") 
            @PathVariable Long transactionId) {
        
        log.info("Getting transaction by ID: {}", transactionId);
        
        try {
            TransactionResponse transaction = transactionService.getTransactionById(transactionId);
            return ResponseEntity.ok(ApiResponse.success("Transaction retrieved successfully", transaction));
        } catch (Exception e) {
            log.error("Error getting transaction: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/number/{transactionNumber}")
    @Operation(summary = "Obtener transacción por número", description = "Obtiene los detalles de una transacción por su número único")
    public ResponseEntity<ApiResponse<TransactionResponse>> getTransactionByNumber(
            @Parameter(description = "Número de la transacción") 
            @PathVariable String transactionNumber) {
        
        log.info("Getting transaction by number: {}", transactionNumber);
        
        try {
            TransactionResponse transaction = transactionService.getTransactionByNumber(transactionNumber);
            return ResponseEntity.ok(ApiResponse.success("Transaction retrieved successfully", transaction));
        } catch (Exception e) {
            log.error("Error getting transaction: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Obtener transacciones por usuario", description = "Obtiene todas las transacciones de un usuario específico")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactionsByUserId(
            @Parameter(description = "ID del usuario") 
            @PathVariable Long userId) {
        
        log.info("Getting transactions for user: {}", userId);
        
        try {
            List<TransactionResponse> transactions = transactionService.getTransactionsByUserId(userId);
            return ResponseEntity.ok(ApiResponse.success("Transactions retrieved successfully", transactions));
        } catch (Exception e) {
            log.error("Error getting transactions: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}/date-range")
    @Operation(summary = "Obtener transacciones por usuario y rango de fechas", description = "Obtiene las transacciones de un usuario en un rango de fechas específico")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactionsByUserIdAndDateRange(
            @Parameter(description = "ID del usuario") 
            @PathVariable Long userId,
            @Parameter(description = "Fecha de inicio (yyyy-MM-dd'T'HH:mm:ss)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Fecha de fin (yyyy-MM-dd'T'HH:mm:ss)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        log.info("Getting transactions for user: {} between {} and {}", userId, startDate, endDate);
        
        try {
            List<TransactionResponse> transactions = transactionService.getTransactionsByUserIdAndDateRange(userId, startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success("Transactions retrieved successfully", transactions));
        } catch (Exception e) {
            log.error("Error getting transactions: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/account/{accountId}")
    @Operation(summary = "Obtener transacciones por cuenta", description = "Obtiene todas las transacciones de una cuenta específica")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactionsByAccountId(
            @Parameter(description = "ID de la cuenta") 
            @PathVariable Long accountId) {
        
        log.info("Getting transactions for account: {}", accountId);
        
        try {
            List<TransactionResponse> transactions = transactionService.getTransactionsByAccountId(accountId);
            return ResponseEntity.ok(ApiResponse.success("Transactions retrieved successfully", transactions));
        } catch (Exception e) {
            log.error("Error getting transactions: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/pending")
    @Operation(summary = "Obtener transacciones pendientes", description = "Obtiene todas las transacciones que están pendientes de procesar")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getPendingTransactions() {
        
        log.info("Getting all pending transactions");
        
        try {
            List<TransactionResponse> transactions = transactionService.getPendingTransactions();
            return ResponseEntity.ok(ApiResponse.success("Pending transactions retrieved successfully", transactions));
        } catch (Exception e) {
            log.error("Error getting pending transactions: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
} 