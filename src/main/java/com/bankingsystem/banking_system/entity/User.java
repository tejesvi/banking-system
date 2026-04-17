package com.bankingsystem.banking_system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "users")
@Getter @Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    private String role;

    private Long createdTimestamp;

    // 🔗 Link user → accounts
    @OneToMany
    @JoinColumn(name = "user_id")
    private List<Account> accounts;
}