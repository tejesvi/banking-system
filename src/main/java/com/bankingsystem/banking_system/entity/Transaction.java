package com.bankingsystem.banking_system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "transactions")
public class Transaction {

    @Id
    @Column(name = "transaction_id")
    private Long transactionId;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    private BigDecimal amount;

    private Long timestamp;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(name = "reference_id")
    private Long referenceId;
}
