package com.bankingsystem.banking_system.controller;

import com.bankingsystem.banking_system.dto.AccountRequest;
import com.bankingsystem.banking_system.dto.AccountResponse;
import com.bankingsystem.banking_system.entity.Account;
import com.bankingsystem.banking_system.mapper.AccountMapper;
import com.bankingsystem.banking_system.service.impl.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public AccountResponse create(@Valid @RequestBody AccountRequest request) {
        Account account = accountService.createAccount(AccountMapper.toEntity(request));
        return AccountMapper.toResponse(account);
    }

    @GetMapping("/{accountId}")
    public AccountResponse getAccount(@PathVariable Long accountId) {
        Account account = accountService.getAccount(accountId);
        return AccountMapper.toResponse(account);
    }

    @GetMapping("/all")
    public List<AccountResponse> getAllAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        return accounts.stream()
                .map(AccountMapper::toResponse)
                .toList();
    }

    @GetMapping("/my-accounts")
    public List<AccountResponse> getMyAccounts() {
        List<Account> accounts = accountService.getAccountsByCurrentUser();
        return accounts.stream()
                .map(AccountMapper::toResponse)
                .toList();
    }

    @PostMapping("/{accountId}/deposit")
    public ResponseEntity deposit(@PathVariable Long accountId, @RequestParam BigDecimal amount) {
        accountService.deposit(accountId, amount);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity withdraw(@PathVariable Long accountId, @RequestParam BigDecimal amount) {
        accountService.withdraw(accountId, amount);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<AccountResponse> updateAccount(@PathVariable Long accountId,
                                                         @Valid @RequestBody AccountRequest request) {
        Account updatedAccount = AccountMapper.toEntity(request);
        Account account = accountService.updateAccount(accountId, updatedAccount);
        return ResponseEntity.ok(AccountMapper.toResponse(account));
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<String> deleteAccount(@PathVariable Long accountId) {
        accountService.deleteAccount(accountId);
        return ResponseEntity.ok("Account deleted successfully");
    }
}