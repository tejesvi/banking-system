package com.bankingsystem.banking_system.service;

import com.bankingsystem.banking_system.entity.Account;
import com.bankingsystem.banking_system.entity.Transaction;
import com.bankingsystem.banking_system.entity.User;
import com.bankingsystem.banking_system.repository.AccountRepository;
import com.bankingsystem.banking_system.repository.TransactionRepository;
import com.bankingsystem.banking_system.repository.UserRepository;
import com.bankingsystem.banking_system.service.impl.AccountServiceImpl;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Account account;
    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        account = new Account();
        account.setAccountId(1L);
        account.setBalance(BigDecimal.valueOf(1000));
        account.setTotalTransactionValue(BigDecimal.ZERO);
        account.setUser(user);
    }

    @Test
    void testDeposit_success() {
        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("testuser");
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

            accountService.deposit(1L, BigDecimal.valueOf(200));

            assertEquals(BigDecimal.valueOf(1200), account.getBalance());
            verify(transactionRepository, times(1)).save(any(Transaction.class));
        }
    }

    @Test
    void testWithdraw_success() {
        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("testuser");
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

            accountService.withdraw(1L, BigDecimal.valueOf(300));

            assertEquals(BigDecimal.valueOf(700), account.getBalance());
        }
    }

    @Test
    void testWithdraw_insufficientBalance() {
        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("testuser");
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

            assertThrows(RuntimeException.class,
                    () -> accountService.withdraw(1L, BigDecimal.valueOf(2000)));
        }
    }

    @Test
    void testUpdateAccount_success() {
        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("testuser");
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            Account updatedAccount = new Account();
            updatedAccount.setFirstName("Jane");
            updatedAccount.setLastName("Doe");
            updatedAccount.setEmailId("jane@example.com");
            updatedAccount.setMobileNumber("9876543210");
            updatedAccount.setAddress("456 Oak St");

            when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
            when(accountRepository.save(any(Account.class))).thenReturn(account);

            Account result = accountService.updateAccount(1L, updatedAccount);

            assertEquals("Jane", result.getFirstName());
            assertEquals("Doe", result.getLastName());
            assertEquals("jane@example.com", result.getEmailId());
            assertEquals("9876543210", result.getMobileNumber());
            assertEquals("456 Oak St", result.getAddress());
            verify(accountRepository, times(1)).save(account);
        }
    }

    @Test
    void testUpdateAccount_partialUpdate() {
        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("testuser");
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            account.setFirstName("John");
            account.setEmailId("john@test.com");

            Account updatedAccount = new Account();
            updatedAccount.setMobileNumber("1111111111");  // Only update mobile

            when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
            when(accountRepository.save(any(Account.class))).thenReturn(account);

            Account result = accountService.updateAccount(1L, updatedAccount);

            assertEquals("John", result.getFirstName());  // Unchanged
            assertEquals("john@test.com", result.getEmailId());  // Unchanged
            assertEquals("1111111111", result.getMobileNumber());  // Updated
            verify(accountRepository, times(1)).save(account);
        }
    }

    @Test
    void testDeleteAccount_success() {
        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("testuser");
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
            when(accountRepository.findAll()).thenReturn(java.util.List.of());
            when(transactionRepository.findAll()).thenReturn(java.util.List.of());

            accountService.deleteAccount(1L);

            verify(accountRepository, times(1)).delete(account);
            verify(transactionRepository, times(1)).deleteAll(any());
        }
    }

    @Test
    void testDeleteAccount_withUserCleanup() {
        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("testuser");
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
            when(accountRepository.findAll()).thenReturn(java.util.List.of());  // No accounts left
            when(transactionRepository.findAll()).thenReturn(java.util.List.of());

            accountService.deleteAccount(1L);

            verify(accountRepository, times(1)).delete(account);
        }
    }

    @Test
    void testUpdateAccount_notFound() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> accountService.updateAccount(99L, new Account()));
    }

    @Test
    void testDeleteAccount_notFound() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> accountService.deleteAccount(99L));
    }
}
