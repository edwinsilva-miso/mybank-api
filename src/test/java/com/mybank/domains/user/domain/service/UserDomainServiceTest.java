package com.mybank.domains.user.domain.service;

import com.mybank.domains.user.domain.entity.User;
import com.mybank.domains.user.domain.repository.UserRepository;
import com.mybank.shared.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDomainServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDomainService userDomainService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setStatus(User.UserStatus.ACTIVE);
    }

    @Test
    void createUser_Success() {
        // Given
        when(userRepository.existsByUsername(testUser.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userDomainService.createUser(testUser);

        // Then
        assertNotNull(result);
        assertEquals(testUser.getUsername(), result.getUsername());
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepository).existsByUsername(testUser.getUsername());
        verify(userRepository).existsByEmail(testUser.getEmail());
        verify(userRepository).save(testUser);
    }

    @Test
    void createUser_UsernameAlreadyExists_ThrowsException() {
        // Given
        when(userRepository.existsByUsername(testUser.getUsername())).thenReturn(true);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userDomainService.createUser(testUser);
        });

        assertEquals("Username already exists", exception.getMessage());
        assertEquals("USER_ALREADY_EXISTS", exception.getErrorCode());
        verify(userRepository).existsByUsername(testUser.getUsername());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_EmailAlreadyExists_ThrowsException() {
        // Given
        when(userRepository.existsByUsername(testUser.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(true);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userDomainService.createUser(testUser);
        });

        assertEquals("Email already exists", exception.getMessage());
        assertEquals("EMAIL_ALREADY_EXISTS", exception.getErrorCode());
        verify(userRepository).existsByUsername(testUser.getUsername());
        verify(userRepository).existsByEmail(testUser.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void findById_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        User result = userDomainService.findById(1L);

        // Then
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        verify(userRepository).findById(1L);
    }

    @Test
    void findById_UserNotFound_ThrowsException() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userDomainService.findById(1L);
        });

        assertEquals("User not found", exception.getMessage());
        assertEquals("USER_NOT_FOUND", exception.getErrorCode());
        verify(userRepository).findById(1L);
    }

    @Test
    void findByUsername_Success() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userDomainService.findByUsername("testuser");

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser.getUsername(), result.get().getUsername());
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void findByUsername_UserNotFound_ReturnsEmpty() {
        // Given
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When
        Optional<User> result = userDomainService.findByUsername("nonexistent");

        // Then
        assertFalse(result.isPresent());
        verify(userRepository).findByUsername("nonexistent");
    }
} 