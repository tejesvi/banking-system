package com.bankingsystem.banking_system.service.impl;

import com.bankingsystem.banking_system.dto.TransferRequest;
import com.bankingsystem.banking_system.entity.*;
import com.bankingsystem.banking_system.repository.AccountRepository;
import com.bankingsystem.banking_system.repository.TransactionRepository;
import com.bankingsystem.banking_system.repository.TransferRepository;
import com.bankingsystem.banking_system.repository.UserRepository;
import com.bankingsystem.banking_system.util.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransferRepository transferRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public void transfer(Long sourceId, Long targetId, BigDecimal amount) {

        if (sourceId.equals(targetId)) {
            throw new RuntimeException("Source and target cannot be same");
        }

        Account source = accountRepository.findById(sourceId)
                .orElseThrow(() -> new RuntimeException("Source account not found"));

        Account target = accountRepository.findById(targetId)
                .orElseThrow(() -> new RuntimeException("Target account not found"));

        validateOwnership(source);

        if (source.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        // Balance updates
        source.setBalance(source.getBalance().subtract(amount));
        target.setBalance(target.getBalance().add(amount));

        source.setTotalTransactionValue(
                source.getTotalTransactionValue().add(amount)
        );

        target.setTotalTransactionValue(
                target.getTotalTransactionValue().add(amount)
        );

        // Save transfer
        Transfer transfer = new Transfer();
        transfer.setTransferId(System.nanoTime());
        transfer.setSourceAccount(source);
        transfer.setTargetAccount(target);
        transfer.setAmount(amount);
        transfer.setTimestamp(System.currentTimeMillis());
        transfer.setAccepted(true);

        transferRepository.save(transfer);

        // Debit transaction
        Transaction debit = new Transaction();
        debit.setTransactionId(System.nanoTime());
        debit.setAccount(source);
        debit.setAmount(amount);
        debit.setType(TransactionType.DEBIT);
        debit.setTimestamp(System.currentTimeMillis());
        debit.setReferenceId(transfer.getTransferId());

        // Credit transaction
        Transaction credit = new Transaction();
        credit.setTransactionId(System.nanoTime());
        credit.setAccount(target);
        credit.setAmount(amount);
        credit.setType(TransactionType.CREDIT);
        credit.setTimestamp(System.currentTimeMillis());
        credit.setReferenceId(transfer.getTransferId());

        transactionRepository.save(debit);
        transactionRepository.save(credit);

        accountRepository.save(source);
        accountRepository.save(target);
    }

    @Override
    public void transfer(TransferRequest request) {
            transfer(request.getSourceAccountId(), request.getTargetAccountId(), request.getAmount());
    }

    private void validateOwnership(Account account) {
        String username = SecurityUtil.getCurrentUsername();
        if (account.getUser() == null ||
                !account.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized access to account");
        }
    }

    public List<Transfer> getTransfersByCurrentUser() {
        String currentUsername = SecurityUtil.getCurrentUsername();

        // If user is ADMIN, return all transactions
        if (SecurityUtil.hasRole("ADMIN")) {
            return transferRepository.findAll();
        }

        return transferRepository
                .findBySourceAccount_User_UsernameOrTargetAccount_User_Username(
                        currentUsername, currentUsername
                );
    }
}
