package com.mybank.domains.user.domain.service;

import com.mybank.domains.user.domain.entity.User;
import com.mybank.domains.user.domain.repository.UserRepository;
import com.mybank.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDomainService {

    private final UserRepository userRepository;

    public User createUser(User user) {
        log.info("Creating new user with username: {}", user.getUsername());
        
        // Validar que el usuario no exista
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new BusinessException("Username already exists", "USER_ALREADY_EXISTS", "USER");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new BusinessException("Email already exists", "EMAIL_ALREADY_EXISTS", "USER");
        }

        User savedUser = userRepository.save(user);
        log.info("User created successfully with ID: {}", savedUser.getId());
        
        return savedUser;
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found", "USER_NOT_FOUND", "USER"));
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail);
    }
} 