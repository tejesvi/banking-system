package com.bankingsystem.banking_system.service.impl;

import com.bankingsystem.banking_system.entity.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {

    Account createAccount(Account account);

    Account getAccount(Long accountId);

    List<Account> getAllAccounts();

    List<Account> getAccountsByCurrentUser();

    void deposit(Long accountId, BigDecimal amount);

    void withdraw(Long accountId, BigDecimal amount);

    Account updateAccount(Long accountId, Account account);

    void deleteAccount(Long accountId);
}