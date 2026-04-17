package com.bankingsystem.banking_system.service;

import com.bankingsystem.banking_system.entity.Account;
import com.bankingsystem.banking_system.entity.User;
import com.bankingsystem.banking_system.repository.AccountRepository;
import com.bankingsystem.banking_system.repository.TransactionRepository;
import com.bankingsystem.banking_system.repository.TransferRepository;
import com.bankingsystem.banking_system.service.impl.TransferServiceImpl;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransferRepository transferRepository;

    @InjectMocks
    private TransferServiceImpl transferService;

    private Account sourceAccount;
    private Account targetAccount;
    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setUsername("testuser");

        sourceAccount = new Account();
        sourceAccount.setAccountId(1L);
        sourceAccount.setBalance(BigDecimal.valueOf(1000));
        sourceAccount.setTotalTransactionValue(BigDecimal.ZERO);
        sourceAccount.setUser(user);

        targetAccount = new Account();
        targetAccount.setAccountId(2L);
        targetAccount.setBalance(BigDecimal.valueOf(500));
        targetAccount.setTotalTransactionValue(BigDecimal.ZERO);
        targetAccount.setUser(user);
    }

    @Test
    void testTransfer_success() {
        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("testuser");
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            when(accountRepository.findById(1L)).thenReturn(Optional.of(sourceAccount));
            when(accountRepository.findById(2L)).thenReturn(Optional.of(targetAccount));

            transferService.transfer(1L, 2L, BigDecimal.valueOf(200));

            assertEquals(BigDecimal.valueOf(800), sourceAccount.getBalance());
            assertEquals(BigDecimal.valueOf(700), targetAccount.getBalance());

            verify(transactionRepository, times(2)).save(any());
            verify(transferRepository, times(1)).save(any());
        }
    }

    @Test
    void testTransfer_insufficientBalance() {
        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("testuser");
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            when(accountRepository.findById(1L)).thenReturn(Optional.of(sourceAccount));
            when(accountRepository.findById(2L)).thenReturn(Optional.of(targetAccount));

            assertThrows(RuntimeException.class,
                    () -> transferService.transfer(1L, 2L, BigDecimal.valueOf(1500)));
        }
    }
}