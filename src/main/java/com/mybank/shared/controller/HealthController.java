package com.mybank.shared.controller;

import com.mybank.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")
@Slf4j
@Tag(name = "Health", description = "Health check APIs")
public class HealthController {

    @GetMapping
    @Operation(summary = "Application health check", description = "Returns the health status of the application")
    public ResponseEntity<ApiResponse<Map<String, Object>>> health() {
        log.info("Health check requested");
        
        Map<String, Object> healthData = new HashMap<>();
        healthData.put("status", "UP");
        healthData.put("timestamp", LocalDateTime.now());
        healthData.put("service", "MyBank API");
        healthData.put("version", "0.1.0");
        
        return ResponseEntity.ok(ApiResponse.success("Application is healthy", healthData));
    }
} 