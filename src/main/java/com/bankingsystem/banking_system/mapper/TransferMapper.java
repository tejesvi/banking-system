package com.bankingsystem.banking_system.mapper;

import com.bankingsystem.banking_system.dto.TransferResponse;
import com.bankingsystem.banking_system.entity.Transfer;

public class TransferMapper {

    public static TransferResponse toResponse(Transfer transfer) {
        return TransferResponse.builder()
                .transferId(transfer.getTransferId())
                .sourceAccountId(transfer.getSourceAccount().getAccountId())
                .targetAccountId(transfer.getTargetAccount().getAccountId())
                .amount(transfer.getAmount())
                .timestamp(transfer.getTimestamp())
                .accepted(transfer.isAccepted())
                .build();
    }
}