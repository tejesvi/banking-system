package com.bankingsystem.banking_system.controller;

import com.bankingsystem.banking_system.dto.TransactionResponse;
import com.bankingsystem.banking_system.dto.TransferRequest;
import com.bankingsystem.banking_system.dto.TransferResponse;
import com.bankingsystem.banking_system.entity.Transfer;
import com.bankingsystem.banking_system.mapper.TransactionMapper;
import com.bankingsystem.banking_system.mapper.TransferMapper;
import com.bankingsystem.banking_system.repository.TransferRepository;
import com.bankingsystem.banking_system.service.impl.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;
    private final TransferRepository transferRepository;

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping
    public TransferResponse transfer(@RequestBody @Valid TransferRequest request) {

        transferService.transfer(
                request.getSourceAccountId(),
                request.getTargetAccountId(),
                request.getAmount()
        );

        // Fetch latest transfer (simplified approach)
        Transfer transfer = transferRepository
                .findAll()
                .stream()
                .reduce((first, second) -> second)
                .orElseThrow();

        return TransferMapper.toResponse(transfer);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/{id}")
    public TransferResponse getTransfer(@PathVariable Long id) {
        Transfer transfer = transferRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transfer not found"));

        return TransferMapper.toResponse(transfer);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/my-transfers")
    public List<TransferResponse> getMyTransfers() {
        System.out.println("Fetching transfers for current user...");
        return transferService.getTransfersByCurrentUser()
                .stream()
                .map(TransferMapper::toResponse)
                .toList();
    }
}