package com.bankingsystem.banking_system.service;

import com.bankingsystem.banking_system.entity.Account;
import com.bankingsystem.banking_system.entity.Transaction;
import com.bankingsystem.banking_system.entity.User;
import com.bankingsystem.banking_system.repository.AccountRepository;
import com.bankingsystem.banking_system.repository.TransactionRepository;
import com.bankingsystem.banking_system.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Account account;
    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setUsername("testuser");

        account = new Account();
        account.setAccountId(1L);
        account.setUser(user);
    }

    @Test
    void testGetTransactionsByAccount() {
        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("testuser");
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            when(accountRepository.existsById(1L)).thenReturn(true);
            when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
            when(transactionRepository.findByAccount_AccountId(1L))
                    .thenReturn(List.of(new Transaction()));

            List<Transaction> result =
                    transactionService.getTransactionsByAccount(1L);

            assertFalse(result.isEmpty());
        }
    }

    @Test
    void testAccountNotFound() {
        when(accountRepository.existsById(1L)).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> transactionService.getTransactionsByAccount(1L));
    }
}
