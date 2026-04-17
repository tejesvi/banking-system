package com.bankingsystem.banking_system.controller;

import com.bankingsystem.banking_system.dto.TransactionResponse;
import com.bankingsystem.banking_system.entity.TransactionType;
import com.bankingsystem.banking_system.mapper.TransactionMapper;
import com.bankingsystem.banking_system.service.impl.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/{id}")
    public TransactionResponse getTransaction(@PathVariable Long id) {
        return TransactionMapper.toResponse(
                transactionService.getTransaction(id)
        );
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/my-transactions")
    public List<TransactionResponse> getMyTransactions() {
        return transactionService.getTransactionsByCurrentUser()
                .stream()
                .map(TransactionMapper::toResponse)
                .toList();
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/account/{accountId}")
    public List<TransactionResponse> getByAccount(@PathVariable Long accountId) {
        return transactionService.getTransactionsByAccount(accountId)
                .stream()
                .map(TransactionMapper::toResponse)
                .toList();
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/account/{accountId}/type")
    public List<TransactionResponse> getByType(
            @PathVariable Long accountId,
            @RequestParam TransactionType type) {

        return transactionService
                .getTransactionsByAccountAndType(accountId, type)
                .stream()
                .map(TransactionMapper::toResponse)
                .toList();
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/account/{accountId}/range")
    public List<TransactionResponse> getByTimeRange(
            @PathVariable Long accountId,
            @RequestParam Long startTime,
            @RequestParam Long endTime) {

        return transactionService
                .getTransactionsByTimeRange(accountId, startTime, endTime)
                .stream()
                .map(TransactionMapper::toResponse)
                .toList();
    }
}