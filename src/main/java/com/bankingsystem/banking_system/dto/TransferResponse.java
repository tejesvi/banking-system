package com.bankingsystem.banking_system.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TransferResponse {

    private Long transferId;

    private Long sourceAccountId;
    private Long targetAccountId;

    private BigDecimal amount;
    private Long timestamp;

    private boolean accepted;
}