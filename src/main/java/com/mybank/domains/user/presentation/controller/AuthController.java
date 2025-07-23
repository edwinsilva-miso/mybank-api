package com.mybank.domains.user.presentation.controller;

import com.mybank.shared.dto.ApiResponse;
import com.mybank.shared.dto.LoginResponse;
import com.mybank.domains.user.application.dto.LoginRequest;
import com.mybank.domains.user.application.dto.UserRegistrationRequest;
import com.mybank.domains.user.domain.entity.User;
import com.mybank.domains.user.application.service.UserService;
import com.mybank.shared.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account")
    public ResponseEntity<ApiResponse<User>> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        log.info("Received registration request for user: {}", request.getUsername());
        
        try {
            User registeredUser = userService.registerUser(request);
            return ResponseEntity.ok(ApiResponse.success("User registered successfully", registeredUser));
        } catch (Exception e) {
            log.error("Error registering user: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates a user and returns JWT token")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Received login request for user: {}", request.getUsernameOrEmail());
        
        try {
            // Buscar el usuario
            Optional<User> userOpt = userService.findByUsernameOrEmail(request.getUsernameOrEmail());
            log.info("User found: {}", userOpt.isPresent());
            
            if (userOpt.isEmpty()) {
                log.error("User not found for: {}", request.getUsernameOrEmail());
                return ResponseEntity.badRequest().body(ApiResponse.error("Invalid username or password"));
            }
            
            User user = userOpt.get();
            log.info("User found with ID: {}, username: {}", user.getId(), user.getUsername());
            
            // Validar contraseña
            boolean passwordValid = userService.validatePassword(request.getPassword(), user.getPassword());
            log.info("Password validation result: {}", passwordValid);
            
            if (!passwordValid) {
                log.error("Invalid password for user: {}", user.getUsername());
                return ResponseEntity.badRequest().body(ApiResponse.error("Invalid username or password"));
            }
            
            // Generar token JWT
            String token = jwtService.generateToken(user.getUsername());
            
            // Crear respuesta
            LoginResponse loginResponse = new LoginResponse(
                    token,
                    "Bearer",
                    user.getUsername(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName()
            );
            
            return ResponseEntity.ok(ApiResponse.success("Login successful", loginResponse));
        } catch (Exception e) {
            log.error("Error during login: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid username or password"));
        }
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check if the authentication service is running")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("Authentication service is running"));
    }
} 