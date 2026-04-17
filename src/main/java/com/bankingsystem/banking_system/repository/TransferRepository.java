package com.bankingsystem.banking_system.repository;

import com.bankingsystem.banking_system.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
    List<Transfer> findBySourceAccount_User_UsernameOrTargetAccount_User_Username(
            String sourceUsername,
            String targetUsername
    );
}