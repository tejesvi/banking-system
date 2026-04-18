package com.bankingsystem.banking_system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountRequest {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String address;

    @NotBlank
    @Pattern(regexp = "^[0-9]{10}$", message = "Invalid mobile number")
    private String mobileNumber;

    @NotBlank
    @Email
    private String emailId;

    // Optional initial deposit
    private BigDecimal initialBalance;

    private BigDecimal totalTransactionValue;
}
