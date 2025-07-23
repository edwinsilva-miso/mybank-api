package com.mybank.shared.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private UserDetails testUserDetails;
    private static final String TEST_SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private static final long TEST_EXPIRATION = 86400000; // 24 hours

    @BeforeEach
    void setUp() {
        // Configurar valores de prueba usando ReflectionTestUtils
        ReflectionTestUtils.setField(jwtService, "secretKey", TEST_SECRET);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", TEST_EXPIRATION);

        testUserDetails = User.builder()
                .username("testuser")
                .password("password")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();
    }

    @Test
    void generateToken_WithUserDetails_Success() {
        // When
        String token = jwtService.generateToken(testUserDetails);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        
        // Verificar que el token puede ser parseado
        Claims claims = Jwts.parser()
                .verifyWith(jwtService.getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        assertEquals("testuser", claims.getSubject());
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }

    @Test
    void generateToken_WithUsername_Success() {
        // When
        String token = jwtService.generateToken("testuser");

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        
        // Verificar que el token puede ser parseado
        Claims claims = Jwts.parser()
                .verifyWith(jwtService.getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        assertEquals("testuser", claims.getSubject());
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }

    @Test
    void generateToken_WithExtraClaims_Success() {
        // Given
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", "ADMIN");
        extraClaims.put("userId", 123L);

        // When
        String token = jwtService.generateToken(extraClaims, testUserDetails);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        
        // Verificar que el token puede ser parseado
        Claims claims = Jwts.parser()
                .verifyWith(jwtService.getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        assertEquals("testuser", claims.getSubject());
        assertEquals("ADMIN", claims.get("role"));
        assertEquals(123, claims.get("userId"));
    }

    @Test
    void extractUsername_ValidToken_Success() {
        // Given
        String token = jwtService.generateToken("testuser");

        // When
        String username = jwtService.extractUsername(token);

        // Then
        assertEquals("testuser", username);
    }

    @Test
    void extractClaim_ValidToken_Success() {
        // Given
        String token = jwtService.generateToken("testuser");

        // When
        String subject = jwtService.extractClaim(token, Claims::getSubject);
        Date issuedAt = jwtService.extractClaim(token, Claims::getIssuedAt);
        Date expiration = jwtService.extractClaim(token, Claims::getExpiration);

        // Then
        assertEquals("testuser", subject);
        assertNotNull(issuedAt);
        assertNotNull(expiration);
    }

    @Test
    void isTokenValid_ValidTokenAndUserDetails_ReturnsTrue() {
        // Given
        String token = jwtService.generateToken(testUserDetails);

        // When
        boolean isValid = jwtService.isTokenValid(token, testUserDetails);

        // Then
        assertTrue(isValid);
    }

    @Test
    void isTokenValid_InvalidUsername_ReturnsFalse() {
        // Given
        String token = jwtService.generateToken("testuser");
        UserDetails differentUser = User.builder()
                .username("differentuser")
                .password("password")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        // When
        boolean isValid = jwtService.isTokenValid(token, differentUser);

        // Then
        assertFalse(isValid);
    }

    @Test
    void isTokenValid_ExpiredToken_ReturnsFalse() {
        // Given
        // Crear un token con expiración muy corta
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 1L); // 1ms
        String token = jwtService.generateToken(testUserDetails);
        
        // Esperar a que expire
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When & Then
        assertThrows(Exception.class, () -> {
            jwtService.isTokenValid(token, testUserDetails);
        });
    }

    @Test
    void getSignInKey_ReturnsValidKey() {
        // When
        var key = jwtService.getSignInKey();

        // Then
        assertNotNull(key);
        assertTrue(key.getAlgorithm().startsWith("HmacSHA"));
    }
} 