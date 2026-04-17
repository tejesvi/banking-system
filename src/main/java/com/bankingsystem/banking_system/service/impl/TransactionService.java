package com.bankingsystem.banking_system.service.impl;

import com.bankingsystem.banking_system.entity.Transaction;
import com.bankingsystem.banking_system.entity.TransactionType;

import java.util.List;

public interface TransactionService {

    Transaction createTransaction(Transaction transaction);

    Transaction getTransaction(Long transactionId);

    List<Transaction> getTransactionsByAccount(Long accountId);

    List<Transaction> getTransactionsByCurrentUser();

    List<Transaction> getTransactionsByAccountAndType(Long accountId, TransactionType type);

    List<Transaction> getTransactionsByTimeRange(Long accountId, Long startTime, Long endTime);
}
