package com.bankingsystem.banking_system.dto;

import com.bankingsystem.banking_system.entity.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequest {

    @NotNull
    private Long accountId;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;

    @NotNull
    private TransactionType type;

    private Long referenceId;
}
