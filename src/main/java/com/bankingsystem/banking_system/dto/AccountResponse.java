package com.bankingsystem.banking_system.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AccountResponse {

    private Long accountId;
    private String firstName;
    private String lastName;
    private String address;
    private String mobileNumber;
    private String emailId;

    private BigDecimal balance;
    private BigDecimal totalTransactionValue;

    private Long createdTimestamp;
}