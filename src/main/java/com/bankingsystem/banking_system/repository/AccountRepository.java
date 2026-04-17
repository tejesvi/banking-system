package com.bankingsystem.banking_system.repository;

import com.bankingsystem.banking_system.entity.Account;
import com.bankingsystem.banking_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUser(User user);
}
