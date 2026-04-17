package com.bankingsystem.banking_system.service.impl;

import com.bankingsystem.banking_system.dto.TransferRequest;
import com.bankingsystem.banking_system.entity.Transfer;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public interface TransferService {
    @Transactional
    void transfer(Long sourceId, Long targetId, BigDecimal amount);

    void transfer(TransferRequest request);

    List<Transfer> getTransfersByCurrentUser();
}