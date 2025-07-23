package com.mybank.domains.user.domain.repository;

import com.mybank.domains.user.domain.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    Optional<User> findByUsernameOrEmail(String usernameOrEmail);
    
    User save(User user);
    
    Optional<User> findById(Long id);
    
    List<User> findAll();
    
    void deleteById(Long id);
} 