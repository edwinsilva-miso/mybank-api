package com.mybank.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String tokenType = "Bearer";
    private Long userId;  // Nuevo campo para identificación del usuario
    private String username;
    private String email;
    private String firstName;
    private String lastName;
} 