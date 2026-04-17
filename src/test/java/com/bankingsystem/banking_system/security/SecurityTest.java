package com.bankingsystem.banking_system.security;

import com.bankingsystem.banking_system.controller.AccountController;
import com.bankingsystem.banking_system.service.impl.AccountService;
import com.bankingsystem.banking_system.service.impl.CustomUserDetailsService;
import com.bankingsystem.banking_system.util.JwtUtil;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bankingsystem.banking_system.entity.Account;
import com.bankingsystem.banking_system.entity.User;
import java.math.BigDecimal;

@WebMvcTest(AccountController.class)
class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/accounts/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testAuthorizedAccess() throws Exception {
        // Mock the account service to return a valid account
        Account mockAccount = new Account();
        mockAccount.setAccountId(1L);
        mockAccount.setBalance(BigDecimal.valueOf(1000));
        User user = new User();
        user.setUsername("testuser");
        mockAccount.setUser(user);

        when(accountService.getAccount(1L)).thenReturn(mockAccount);

        mockMvc.perform(get("/accounts/1"))
                .andExpect(status().isOk());
    }
}
