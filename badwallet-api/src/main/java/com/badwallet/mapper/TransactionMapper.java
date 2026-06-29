package com.badwallet.mapper;

import com.badwallet.dto.response.TransactionResponse;
import com.badwallet.entity.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public TransactionResponse toResponse(Transaction transaction) {
        if (transaction == null) {
            return null;
        }
        return TransactionResponse.builder()
                .id(transaction.getId())
                .reference(transaction.getReference())
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .walletPhone(transaction.getWallet().getPhoneNumber())
                .destinationPhone(transaction.getDestinationPhone())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
