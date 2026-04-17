package com.bankingsystem.banking_system;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.test.mock.mockito.MockBean;
import com.bankingsystem.banking_system.util.JwtUtil;
import com.bankingsystem.banking_system.service.impl.CustomUserDetailsService;

@SpringBootTest
@AutoConfigureMockMvc
class BankingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testFullFlow() throws Exception {

        // Create account
        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                  "firstName":"A",
                  "lastName":"B",
                  "mobileNumber":"9999999999",
                  "emailId":"a@test.com"
                }
                """))
                .andExpect(status().isOk());

        // Fetch account
        mockMvc.perform(get("/accounts/1"))
                .andExpect(status().isOk());
    }
}
