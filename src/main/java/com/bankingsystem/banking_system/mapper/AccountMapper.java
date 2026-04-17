package com.bankingsystem.banking_system.mapper;

import com.bankingsystem.banking_system.dto.AccountRequest;
import com.bankingsystem.banking_system.dto.AccountResponse;
import com.bankingsystem.banking_system.entity.Account;

import java.math.BigDecimal;

public class AccountMapper {

    public static Account toEntity(AccountRequest request) {
        Account account = new Account();
        account.setFirstName(request.getFirstName());
        account.setLastName(request.getLastName());
        account.setAddress(request.getAddress());
        account.setMobileNumber(request.getMobileNumber());
        account.setEmailId(request.getEmailId());
        account.setBalance(
                request.getInitialBalance() == null ? BigDecimal.ZERO : request.getInitialBalance()
        );
        return account;
    }

    public static AccountResponse toResponse(Account account) {
        return AccountResponse.builder()
                .accountId(account.getAccountId())
                .firstName(account.getFirstName())
                .lastName(account.getLastName())
                .address(account.getAddress())
                .mobileNumber(account.getMobileNumber())
                .emailId(account.getEmailId())
                .balance(account.getBalance())
                .totalTransactionValue(account.getTotalTransactionValue())
                .createdTimestamp(account.getCreatedTimestamp())
                .build();
    }
}
