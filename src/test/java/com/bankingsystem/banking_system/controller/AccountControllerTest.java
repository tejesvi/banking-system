package com.bankingsystem.banking_system.controller;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.*;

import com.bankingsystem.banking_system.service.impl.AccountService;
import com.bankingsystem.banking_system.entity.Account;
import com.bankingsystem.banking_system.dto.AccountRequest;
import com.bankingsystem.banking_system.util.JwtUtil;
import com.bankingsystem.banking_system.service.impl.CustomUserDetailsService;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateAccount() throws Exception {

        Account mockAccount = new Account();
        mockAccount.setAccountId(1L);
        mockAccount.setFirstName("John");
        mockAccount.setLastName("Doe");
        mockAccount.setEmailId("john@test.com");
        mockAccount.setMobileNumber("1234567890");

        when(accountService.createAccount(any(Account.class))).thenReturn(mockAccount);

        String json = """
        {
          "firstName":"John",
          "lastName":"Doe",
          "mobileNumber":"1234567890",
          "emailId":"john@test.com"
        }
        """;

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateAccount() throws Exception {
        Account existingAccount = new Account();
        existingAccount.setAccountId(1L);
        existingAccount.setFirstName("John");
        existingAccount.setLastName("Doe");
        existingAccount.setEmailId("john@test.com");
        existingAccount.setMobileNumber("1234567890");

        Account updatedAccount = new Account();
        updatedAccount.setAccountId(1L);
        updatedAccount.setFirstName("Jane");
        updatedAccount.setLastName("Smith");
        updatedAccount.setEmailId("jane@test.com");
        updatedAccount.setMobileNumber("9876543210");

        when(accountService.updateAccount(eq(1L), any(Account.class))).thenReturn(updatedAccount);

        String json = """
        {
          "firstName":"Jane",
          "lastName":"Smith",
          "mobileNumber":"9876543210",
          "emailId":"jane@test.com"
        }
        """;

        mockMvc.perform(put("/accounts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteAccount() throws Exception {
        mockMvc.perform(delete("/accounts/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetAccount() throws Exception {
        Account mockAccount = new Account();
        mockAccount.setAccountId(1L);
        mockAccount.setFirstName("John");
        mockAccount.setLastName("Doe");
        mockAccount.setEmailId("john@test.com");
        mockAccount.setMobileNumber("1234567890");

        when(accountService.getAccount(1L)).thenReturn(mockAccount);

        mockMvc.perform(get("/accounts/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetAllAccounts() throws Exception {
        mockMvc.perform(get("/accounts/all"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testDeposit() throws Exception {
        mockMvc.perform(post("/accounts/1/deposit")
                        .param("amount", "500.00"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testWithdraw() throws Exception {
        mockMvc.perform(post("/accounts/1/withdraw")
                        .param("amount", "100.00"))
                .andExpect(status().isOk());
    }
}

