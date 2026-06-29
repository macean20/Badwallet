package com.badwallet.mapper;

import com.badwallet.dto.response.WalletResponse;
import com.badwallet.entity.Wallet;
import org.springframework.stereotype.Component;

@Component
public class WalletMapper {

    public WalletResponse toResponse(Wallet wallet) {
        if (wallet == null) {
            return null;
        }
        return WalletResponse.builder()
                .id(wallet.getId())
                .phoneNumber(wallet.getPhoneNumber())
                .email(wallet.getEmail())
                .code(wallet.getCode())
                .currency(wallet.getCurrency())
                .balance(wallet.getBalance())
                .createdAt(wallet.getCreatedAt())
                .updatedAt(wallet.getUpdatedAt())
                .build();
    }
}
