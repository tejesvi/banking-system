package com.bankingsystem.banking_system.exception;

public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException(String message) {
        super(message);
    }

    public InsufficientBalanceException(Long accountId, String message) {
        super("Account ID: " + accountId + " - " + message);
    }
}