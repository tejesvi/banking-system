package com.bankingsystem.banking_system.dto;

import com.bankingsystem.banking_system.entity.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TransactionResponse {

    private Long transactionId;

    private Long accountId;

    private BigDecimal amount;

    private TransactionType type;

    private Long timestamp;

    private Long referenceId;
}