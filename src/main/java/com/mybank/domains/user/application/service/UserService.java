package com.mybank.domains.user.application.service;

import com.mybank.domains.user.application.dto.UserRegistrationRequest;
import com.mybank.domains.user.domain.entity.User;
import com.mybank.domains.user.domain.service.UserDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserDomainService userDomainService;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(UserRegistrationRequest request) {
        log.info("Registering new user with username: {}", request.getUsername());

        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        
        // Set default role
        HashSet<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        user.setRoles(roles);

        return userDomainService.createUser(user);
    }

    public Optional<User> findByUsername(String username) {
        return userDomainService.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userDomainService.findByEmail(email);
    }

    public User findById(Long id) {
        return userDomainService.findById(id);
    }

    public boolean existsByUsername(String username) {
        return userDomainService.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userDomainService.existsByEmail(email);
    }

    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        return userDomainService.findByUsernameOrEmail(usernameOrEmail);
    }

    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
} 