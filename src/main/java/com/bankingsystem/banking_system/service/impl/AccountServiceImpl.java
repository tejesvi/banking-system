package com.bankingsystem.banking_system.service.impl;

import com.bankingsystem.banking_system.entity.Account;
import com.bankingsystem.banking_system.entity.Transaction;
import com.bankingsystem.banking_system.entity.TransactionType;
import com.bankingsystem.banking_system.entity.User;
import com.bankingsystem.banking_system.repository.AccountRepository;
import com.bankingsystem.banking_system.repository.TransactionRepository;
import com.bankingsystem.banking_system.repository.UserRepository;
import com.bankingsystem.banking_system.util.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/accounts")
    public Account createAccount(Account account) {
        account.setCreatedTimestamp(System.currentTimeMillis());
        account.setBalance(
                account.getBalance() == null ? BigDecimal.ZERO : account.getBalance()
        );
        
        // Create or find user
        String username = account.getEmailId(); // Use email as username
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode("password")); // Default password
            user.setRole("ROLE_USER");
            user.setCreatedTimestamp(System.currentTimeMillis());
            user = userRepository.save(user);
        }
        account.setUser(user);
        
        return accountRepository.save(account);
    }

    @Override
    public Account getAccount(Long accountId) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        validateOwnership(account);

        return account;
    }

    @Override
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Override
    public List<Account> getAccountsByCurrentUser() {
        String currentUsername = SecurityUtil.getCurrentUsername();
        
        // If user is ADMIN, return all accounts
        if (SecurityUtil.hasRole("ADMIN")) {
            return accountRepository.findAll();
        }
        
        // For regular users, return only their accounts
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return accountRepository.findByUser(user);
    }

    @Override
    @Transactional
    public void deposit(Long accountId, BigDecimal amount) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow();

        validateOwnership(account);

        account.setBalance(account.getBalance().add(amount));
        account.setTotalTransactionValue(
                account.getTotalTransactionValue() == null ? amount :
                account.getTotalTransactionValue().add(amount)
        );

        Transaction txn = new Transaction();
        txn.setTransactionId(System.nanoTime());
        txn.setAccount(account);
        txn.setAmount(amount);
        txn.setType(TransactionType.CREDIT);
        txn.setTimestamp(System.currentTimeMillis());

        transactionRepository.save(txn);
        accountRepository.save(account);
    }

    @Override
    @Transactional
    public void withdraw(Long accountId, BigDecimal amount) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow();

        validateOwnership(account);

        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        account.setBalance(account.getBalance().subtract(amount));
        account.setTotalTransactionValue(
                account.getTotalTransactionValue().add(amount)
        );

        Transaction txn = new Transaction();
        txn.setTransactionId(System.nanoTime());
        txn.setAccount(account);
        txn.setAmount(amount);
        txn.setType(TransactionType.DEBIT);
        txn.setTimestamp(System.currentTimeMillis());

        transactionRepository.save(txn);
        accountRepository.save(account);
    }

    @Override
    public Account updateAccount(Long accountId, Account updatedAccount) {
        
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        
        validateOwnership(account);
        
        // Update only allowed fields
        if (updatedAccount.getFirstName() != null) {
            account.setFirstName(updatedAccount.getFirstName());
        }
        if (updatedAccount.getLastName() != null) {
            account.setLastName(updatedAccount.getLastName());
        }
        if (updatedAccount.getEmailId() != null) {
            account.setEmailId(updatedAccount.getEmailId());
        }
        if (updatedAccount.getMobileNumber() != null) {
            account.setMobileNumber(updatedAccount.getMobileNumber());
        }
        if (updatedAccount.getAddress() != null) {
            account.setAddress(updatedAccount.getAddress());
        }
        
        return accountRepository.save(account);
    }

    @Override
    public void deleteAccount(Long accountId) {
        
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        
        validateOwnership(account);
        
        // Delete associated transactions first
        transactionRepository.deleteAll(
            transactionRepository.findAll().stream()
                .filter(t -> t.getAccount().getAccountId().equals(accountId))
                .toList()
        );
        
        // Get the user associated with this account
        User user = account.getUser();
        
        // Delete the account
        accountRepository.delete(account);
        
        // Delete the user if this was their only account
        if (user != null) {
            long userAccountCount = accountRepository.findAll().stream()
                    .filter(a -> a.getUser() != null && a.getUser().getId().equals(user.getId()))
                    .count();
            
            // If user has no more accounts, delete the user
            if (userAccountCount == 0) {
                userRepository.delete(user);
            }
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
