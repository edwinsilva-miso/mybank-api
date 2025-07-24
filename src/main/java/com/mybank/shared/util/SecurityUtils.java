package com.mybank.shared.util;

import com.mybank.shared.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final JwtService jwtService;

    /**
     * Obtiene el userId del usuario autenticado desde el token JWT
     * @return Long userId o null si no está disponible
     */
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            // El token se puede obtener del contexto de seguridad
            // Por simplicidad, asumimos que el username es suficiente para identificar al usuario
            String username = authentication.getName();
            // En una implementación más robusta, podrías extraer el token completo
            // y usar jwtService.extractUserId(token)
            return null; // Por ahora retornamos null, se puede mejorar
        }
        return null;
    }

    /**
     * Obtiene el username del usuario autenticado
     * @return String username o null si no está autenticado
     */
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }

    /**
     * Verifica si el usuario actual tiene el userId especificado
     * @param userId userId a verificar
     * @return true si coincide, false en caso contrario
     */
    public boolean isCurrentUser(Long userId) {
        Long currentUserId = getCurrentUserId();
        return currentUserId != null && currentUserId.equals(userId);
    }
} 