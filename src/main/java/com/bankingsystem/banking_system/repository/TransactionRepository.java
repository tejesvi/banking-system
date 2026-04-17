package com.bankingsystem.banking_system.repository;

import com.bankingsystem.banking_system.entity.Transaction;
import com.bankingsystem.banking_system.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccount_AccountIdAndTimestampBetween(Long accountId, Long startTime, Long endTime);

    List<Transaction> findByAccount_AccountIdAndType(Long accountId, TransactionType type);

    List<Transaction> findByAccount_AccountId(Long accountId);
}