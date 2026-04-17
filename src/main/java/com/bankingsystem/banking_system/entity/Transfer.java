package com.bankingsystem.banking_system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "transfers")
public class Transfer {

    @Id
    @Column(name = "transfer_id")
    private Long transferId;

    @ManyToOne
    @JoinColumn(name = "source_account_id")
    private Account sourceAccount;

    @ManyToOne
    @JoinColumn(name = "target_account_id")
    private Account targetAccount;

    private BigDecimal amount;

    private Long timestamp;

    private boolean accepted;
}