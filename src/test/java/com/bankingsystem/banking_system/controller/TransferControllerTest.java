package com.bankingsystem.banking_system.controller;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

import com.bankingsystem.banking_system.service.impl.TransferService;
import com.bankingsystem.banking_system.repository.TransferRepository;
import com.bankingsystem.banking_system.entity.Transfer;
import com.bankingsystem.banking_system.entity.Account;

import java.util.List;
import com.bankingsystem.banking_system.util.JwtUtil;
import com.bankingsystem.banking_system.service.impl.CustomUserDetailsService;

@SpringBootTest
@AutoConfigureMockMvc
class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransferService transferService;

    @MockBean
    private TransferRepository transferRepository;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser(roles = "USER")
    void testTransfer() throws Exception {

        Transfer mockTransfer = new Transfer();
        mockTransfer.setTransferId(1L);
        mockTransfer.setAmount(java.math.BigDecimal.valueOf(100.00));
        mockTransfer.setAccepted(true);

        Account sourceAccount = new Account();
        sourceAccount.setAccountId(1L);
        mockTransfer.setSourceAccount(sourceAccount);

        Account targetAccount = new Account();
        targetAccount.setAccountId(2L);
        mockTransfer.setTargetAccount(targetAccount);

        when(transferRepository.findAll()).thenReturn(List.of(mockTransfer));

        String json = """
        {
          "sourceAccountId": 1,
          "targetAccountId": 2,
          "amount": 100.00
        }
        """;

        mockMvc.perform(post("/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }
}