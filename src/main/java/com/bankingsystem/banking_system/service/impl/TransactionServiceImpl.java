package com.bankingsystem.banking_system.service.impl;

import com.bankingsystem.banking_system.entity.Account;
import com.bankingsystem.banking_system.entity.Transaction;
import com.bankingsystem.banking_system.entity.TransactionType;
import com.bankingsystem.banking_system.entity.User;
import com.bankingsystem.banking_system.repository.AccountRepository;
import com.bankingsystem.banking_system.repository.TransactionRepository;
import com.bankingsystem.banking_system.repository.UserRepository;
import com.bankingsystem.banking_system.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Override
    public Transaction createTransaction(Transaction transaction) {
        if (transaction.getTransactionId() == null) {
            transaction.setTransactionId(System.nanoTime());
        }
        if (transaction.getTimestamp() == null) {
            transaction.setTimestamp(System.currentTimeMillis());
        }
        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction getTransaction(Long transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    @Override
    public List<Transaction> getTransactionsByAccount(Long accountId) {
        validateAccount(accountId);
        Account account = accountRepository.findById(accountId)
                .orElseThrow();

        validateOwnership(account);
        return transactionRepository.findByAccount_AccountId(accountId);
    }

    @Override
    public List<Transaction> getTransactionsByCurrentUser() {
        String currentUsername = SecurityUtil.getCurrentUsername();
        
        // If user is ADMIN, return all transactions
        if (SecurityUtil.hasRole("ADMIN")) {
            return transactionRepository.findAll();
        }
        
        // For regular users, return only their transactions
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Get all accounts for this user
        List<Account> userAccounts = accountRepository.findByUser(user);
        
        // Get all transactions for all user accounts
        return userAccounts.stream()
                .flatMap(account -> transactionRepository.findByAccount_AccountId(account.getAccountId()).stream())
                .toList();
    }

    @Override
    public List<Transaction> getTransactionsByAccountAndType(Long accountId, TransactionType type) {
        validateAccount(accountId);
        return transactionRepository.findByAccount_AccountIdAndType(accountId, type);
    }

    @Override
    public List<Transaction> getTransactionsByTimeRange(Long accountId, Long startTime, Long endTime) {
        validateAccount(accountId);
        return transactionRepository.findByAccount_AccountIdAndTimestampBetween(
                accountId, startTime, endTime
        );
    }

    private void validateAccount(Long accountId) {
        if (!accountRepository.existsById(accountId)) {
            throw new RuntimeException("Account not found");
        }
    }

    private void validateOwnership(Account account) {
        String username = SecurityUtil.getCurrentUsername();
        // Allow ADMIN to access any account
        if (SecurityUtil.hasRole("ADMIN")) {
            return;
        }
        if (account.getUser() == null ||
                !account.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized access to account");
        }
    }
}
