package com.badwallet.service.impl;

import com.badwallet.dto.response.WalletResponse;
import com.badwallet.entity.Wallet;
import com.badwallet.mapper.WalletMapper;
import com.badwallet.repository.WalletRepository;
import com.badwallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletMapper walletMapper;

    @Override
    @Transactional
    public List<WalletResponse> seedWallets() {
        List<Wallet> seedData = Arrays.asList(
                Wallet.builder().phoneNumber("+221770000001").balance(new BigDecimal("5000.00")).build(),
                Wallet.builder().phoneNumber("+221770000002").balance(new BigDecimal("10000.00")).build(),
                Wallet.builder().phoneNumber("+221770000003").balance(new BigDecimal("20000.00")).build()
        );

        return seedData.stream()
                .map(wallet -> {
                    return walletRepository.findByPhoneNumber(wallet.getPhoneNumber())
                            .orElseGet(() -> walletRepository.save(wallet));
                })
                .map(walletMapper::toResponse)
                .collect(Collectors.toList());
    }
}
