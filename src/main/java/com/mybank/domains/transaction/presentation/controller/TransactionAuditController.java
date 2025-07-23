package com.mybank.domains.transaction.presentation.controller;

import com.mybank.domains.transaction.application.dto.TransactionAuditResponse;
import com.mybank.domains.transaction.application.service.TransactionAuditApplicationService;
import com.mybank.domains.transaction.domain.entity.TransactionAudit;
import com.mybank.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/transactions/audit")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Transaction Audit", description = "API para auditoría de transacciones bancarias")
public class TransactionAuditController {

    private final TransactionAuditApplicationService transactionAuditApplicationService;

    @GetMapping("/transaction/{transactionId}")
    @Operation(summary = "Obtener historial de auditoría de una transacción", 
               description = "Retorna todos los eventos de auditoría de una transacción específica")
    public ResponseEntity<ApiResponse<List<TransactionAuditResponse>>> getTransactionAuditHistory(
            @Parameter(description = "ID de la transacción") 
            @PathVariable Long transactionId) {
        log.info("Getting audit history for transaction: {}", transactionId);
        try {
            List<TransactionAuditResponse> auditHistory = transactionAuditApplicationService.getTransactionAuditHistory(transactionId);
            return ResponseEntity.ok(ApiResponse.success("Audit history retrieved successfully", auditHistory));
        } catch (Exception e) {
            log.error("Error getting transaction audit history: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/transaction/number/{transactionNumber}")
    @Operation(summary = "Obtener historial de auditoría por número de transacción", 
               description = "Retorna todos los eventos de auditoría de una transacción por su número")
    public ResponseEntity<ApiResponse<List<TransactionAuditResponse>>> getTransactionAuditHistoryByNumber(
            @Parameter(description = "Número de la transacción") 
            @PathVariable String transactionNumber) {
        log.info("Getting audit history for transaction number: {}", transactionNumber);
        try {
            List<TransactionAuditResponse> auditHistory = transactionAuditApplicationService.getTransactionAuditHistory(transactionNumber);
            return ResponseEntity.ok(ApiResponse.success("Audit history retrieved successfully", auditHistory));
        } catch (Exception e) {
            log.error("Error getting transaction audit history by number: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Obtener historial de auditoría de un usuario", 
               description = "Retorna todos los eventos de auditoría de un usuario específico")
    public ResponseEntity<ApiResponse<List<TransactionAuditResponse>>> getUserAuditHistory(
            @Parameter(description = "ID del usuario") 
            @PathVariable Long userId) {
        log.info("Getting audit history for user: {}", userId);
        try {
            List<TransactionAuditResponse> auditHistory = transactionAuditApplicationService.getUserAuditHistory(userId);
            return ResponseEntity.ok(ApiResponse.success("User audit history retrieved successfully", auditHistory));
        } catch (Exception e) {
            log.error("Error getting user audit history: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}/paginated")
    @Operation(summary = "Obtener historial de auditoría de un usuario con paginación", 
               description = "Retorna los eventos de auditoría de un usuario con paginación")
    public ResponseEntity<ApiResponse<Page<TransactionAuditResponse>>> getUserAuditHistoryPaginated(
            @Parameter(description = "ID del usuario") 
            @PathVariable Long userId,
            @Parameter(description = "Número de página (0-based)") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de la página") 
            @RequestParam(defaultValue = "20") int size) {
        log.info("Getting paginated audit history for user: {}, page: {}, size: {}", userId, page, size);
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<TransactionAuditResponse> auditHistory = transactionAuditApplicationService.getUserAuditHistory(userId, pageable);
            return ResponseEntity.ok(ApiResponse.success("User audit history retrieved successfully", auditHistory));
        } catch (Exception e) {
            log.error("Error getting paginated user audit history: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/account/{accountId}")
    @Operation(summary = "Obtener historial de auditoría de una cuenta", 
               description = "Retorna todos los eventos de auditoría de una cuenta específica")
    public ResponseEntity<ApiResponse<List<TransactionAuditResponse>>> getAccountAuditHistory(
            @Parameter(description = "ID de la cuenta") 
            @PathVariable Long accountId) {
        log.info("Getting audit history for account: {}", accountId);
        try {
            List<TransactionAuditResponse> auditHistory = transactionAuditApplicationService.getAccountAuditHistory(accountId);
            return ResponseEntity.ok(ApiResponse.success("Account audit history retrieved successfully", auditHistory));
        } catch (Exception e) {
            log.error("Error getting account audit history: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/account/number/{accountNumber}")
    @Operation(summary = "Obtener historial de auditoría por número de cuenta", 
               description = "Retorna todos los eventos de auditoría de una cuenta por su número")
    public ResponseEntity<ApiResponse<List<TransactionAuditResponse>>> getAccountAuditHistoryByNumber(
            @Parameter(description = "Número de la cuenta") 
            @PathVariable String accountNumber) {
        log.info("Getting audit history for account number: {}", accountNumber);
        try {
            List<TransactionAuditResponse> auditHistory = transactionAuditApplicationService.getAccountAuditHistory(accountNumber);
            return ResponseEntity.ok(ApiResponse.success("Account audit history retrieved successfully", auditHistory));
        } catch (Exception e) {
            log.error("Error getting account audit history by number: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/date-range")
    @Operation(summary = "Obtener auditoría por rango de fechas", 
               description = "Retorna todos los eventos de auditoría en un rango de fechas específico")
    public ResponseEntity<ApiResponse<List<TransactionAuditResponse>>> getAuditHistoryByDateRange(
            @Parameter(description = "Fecha de inicio (yyyy-MM-dd'T'HH:mm:ss)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Fecha de fin (yyyy-MM-dd'T'HH:mm:ss)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Getting audit history from {} to {}", startDate, endDate);
        try {
            List<TransactionAuditResponse> auditHistory = transactionAuditApplicationService.getAuditHistoryByDateRange(startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success("Date range audit history retrieved successfully", auditHistory));
        } catch (Exception e) {
            log.error("Error getting date range audit history: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/date-range/paginated")
    @Operation(summary = "Obtener auditoría por rango de fechas con paginación", 
               description = "Retorna los eventos de auditoría en un rango de fechas con paginación")
    public ResponseEntity<ApiResponse<Page<TransactionAuditResponse>>> getAuditHistoryByDateRangePaginated(
            @Parameter(description = "Fecha de inicio (yyyy-MM-dd'T'HH:mm:ss)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Fecha de fin (yyyy-MM-dd'T'HH:mm:ss)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @Parameter(description = "Número de página (0-based)") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de la página") 
            @RequestParam(defaultValue = "20") int size) {
        log.info("Getting paginated audit history from {} to {}, page: {}, size: {}", startDate, endDate, page, size);
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<TransactionAuditResponse> auditHistory = transactionAuditApplicationService.getAuditHistoryByDateRange(startDate, endDate, pageable);
            return ResponseEntity.ok(ApiResponse.success("Date range audit history retrieved successfully", auditHistory));
        } catch (Exception e) {
            log.error("Error getting paginated date range audit history: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/event-type/{eventType}")
    @Operation(summary = "Obtener auditoría por tipo de evento", 
               description = "Retorna todos los eventos de auditoría de un tipo específico")
    public ResponseEntity<ApiResponse<List<TransactionAuditResponse>>> getAuditHistoryByEventType(
            @Parameter(description = "Tipo de evento de auditoría") 
            @PathVariable TransactionAudit.AuditEventType eventType) {
        log.info("Getting audit history for event type: {}", eventType);
        try {
            List<TransactionAuditResponse> auditHistory = transactionAuditApplicationService.getAuditHistoryByEventType(eventType);
            return ResponseEntity.ok(ApiResponse.success("Event type audit history retrieved successfully", auditHistory));
        } catch (Exception e) {
            log.error("Error getting event type audit history: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar en auditoría por descripción", 
               description = "Busca eventos de auditoría que contengan el término especificado en la descripción")
    public ResponseEntity<ApiResponse<List<TransactionAuditResponse>>> searchAuditHistory(
            @Parameter(description = "Término de búsqueda") 
            @RequestParam String searchTerm) {
        log.info("Searching audit history with term: {}", searchTerm);
        try {
            List<TransactionAuditResponse> auditHistory = transactionAuditApplicationService.searchAuditHistory(searchTerm);
            return ResponseEntity.ok(ApiResponse.success("Audit history search completed successfully", auditHistory));
        } catch (Exception e) {
            log.error("Error searching audit history: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/statistics")
    @Operation(summary = "Obtener estadísticas de eventos", 
               description = "Retorna estadísticas de eventos por tipo en un rango de fechas")
    public ResponseEntity<ApiResponse<Map<TransactionAudit.AuditEventType, Long>>> getEventStatistics(
            @Parameter(description = "Fecha de inicio (yyyy-MM-dd'T'HH:mm:ss)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Fecha de fin (yyyy-MM-dd'T'HH:mm:ss)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Getting event statistics from {} to {}", startDate, endDate);
        try {
            Map<TransactionAudit.AuditEventType, Long> statistics = transactionAuditApplicationService.getEventStatistics(startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success("Event statistics retrieved successfully", statistics));
        } catch (Exception e) {
            log.error("Error getting event statistics: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/suspicious")
    @Operation(summary = "Buscar transacciones sospechosas", 
               description = "Busca transacciones con múltiples intentos fallidos en un rango de fechas")
    public ResponseEntity<ApiResponse<List<Object[]>>> findSuspiciousTransactions(
            @Parameter(description = "Fecha de inicio (yyyy-MM-dd'T'HH:mm:ss)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Fecha de fin (yyyy-MM-dd'T'HH:mm:ss)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @Parameter(description = "Número mínimo de fallos") 
            @RequestParam(defaultValue = "3") int minFailures) {
        log.info("Finding suspicious transactions from {} to {} with minimum {} failures", startDate, endDate, minFailures);
        try {
            List<Object[]> suspiciousTransactions = transactionAuditApplicationService.findSuspiciousTransactions(startDate, endDate, minFailures);
            return ResponseEntity.ok(ApiResponse.success("Suspicious transactions search completed successfully", suspiciousTransactions));
        } catch (Exception e) {
            log.error("Error finding suspicious transactions: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/ip/{ipAddress}")
    @Operation(summary = "Obtener auditoría por IP address", 
               description = "Retorna todos los eventos de auditoría de una dirección IP específica")
    public ResponseEntity<ApiResponse<List<TransactionAuditResponse>>> getAuditHistoryByIpAddress(
            @Parameter(description = "Dirección IP") 
            @PathVariable String ipAddress) {
        log.info("Getting audit history for IP address: {}", ipAddress);
        try {
            List<TransactionAuditResponse> auditHistory = transactionAuditApplicationService.getAuditHistoryByIpAddress(ipAddress);
            return ResponseEntity.ok(ApiResponse.success("IP address audit history retrieved successfully", auditHistory));
        } catch (Exception e) {
            log.error("Error getting IP address audit history: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/all/paginated")
    @Operation(summary = "Obtener toda la auditoría con paginación", 
               description = "Retorna todos los eventos de auditoría con paginación")
    public ResponseEntity<ApiResponse<Page<TransactionAuditResponse>>> getAllAuditHistory(
            @Parameter(description = "Número de página (0-based)") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de la página") 
            @RequestParam(defaultValue = "20") int size) {
        log.info("Getting all audit history with pagination, page: {}, size: {}", page, size);
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<TransactionAuditResponse> auditHistory = transactionAuditApplicationService.getAllAuditHistory(pageable);
            return ResponseEntity.ok(ApiResponse.success("All audit history retrieved successfully", auditHistory));
        } catch (Exception e) {
            log.error("Error getting all audit history: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
} 