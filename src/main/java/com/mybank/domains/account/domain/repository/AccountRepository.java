package com.mybank.domains.account.domain.repository;

import com.mybank.domains.account.domain.entity.Account;
import com.mybank.domains.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);
    
    List<Account> findByUser(User user);
    
    List<Account> findByUserAndStatus(User user, Account.AccountStatus status);
    
    @Query("SELECT a FROM Account a WHERE a.user.id = :userId")
    List<Account> findByUserId(@Param("userId") Long userId);
    
    @Query("SELECT a FROM Account a WHERE a.user.id = :userId AND a.accountType = :accountType")
    List<Account> findByUserIdAndAccountType(@Param("userId") Long userId, @Param("accountType") Account.AccountType accountType);
    
    @Query("SELECT a FROM Account a WHERE a.user.id = :userId AND a.accountType = :accountType")
    List<Account> findByUserAndAccountType(@Param("userId") Long userId, @Param("accountType") Account.AccountType accountType);
    
    boolean existsByAccountNumber(String accountNumber);
} 