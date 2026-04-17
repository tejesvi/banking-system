package com.bankingsystem.banking_system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String address;

    @Column(name = "mobile_number")
    private String mobileNumber;

    @Column(name = "email_id")
    private String emailId;

    @Column(precision = 15, scale = 2)
    private BigDecimal balance;

    @Column(name = "total_transaction_value", precision = 15, scale = 2)
    private BigDecimal totalTransactionValue;

    @Column(name = "created_timestamp")
    private Long createdTimestamp;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
