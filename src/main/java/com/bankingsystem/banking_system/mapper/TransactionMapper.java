package com.bankingsystem.banking_system.mapper;

import com.bankingsystem.banking_system.dto.TransactionResponse;
import com.bankingsystem.banking_system.entity.Transaction;

public class TransactionMapper {

    public static TransactionResponse toResponse(Transaction txn) {
        return TransactionResponse.builder()
                .transactionId(txn.getTransactionId())
                .accountId(txn.getAccount().getAccountId())
                .amount(txn.getAmount())
                .type(txn.getType())
                .timestamp(txn.getTimestamp())
                .referenceId(txn.getReferenceId())
                .build();
    }
}
