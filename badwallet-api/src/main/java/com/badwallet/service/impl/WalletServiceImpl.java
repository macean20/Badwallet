package com.badwallet.service.impl;

import com.badwallet.dto.request.WalletCreationRequest;
import com.badwallet.dto.response.BalanceResponse;
import com.badwallet.dto.response.TransactionResponse;
import com.badwallet.dto.response.WalletResponse;
import com.badwallet.entity.Wallet;
import com.badwallet.exception.WalletAlreadyExistsException;
import com.badwallet.exception.WalletNotFoundException;
import com.badwallet.mapper.TransactionMapper;
import com.badwallet.mapper.WalletMapper;
import com.badwallet.repository.TransactionRepository;
import com.badwallet.repository.WalletRepository;
import com.badwallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final WalletMapper walletMapper;
    private final TransactionMapper transactionMapper;

    @Override
    @Transactional
    public List<WalletResponse> seedWallets(int numWallets, int eventsPerWallet) {
        List<Wallet> seedData = new ArrayList<>();
        for (int i = 1; i <= numWallets; i++) {
            String paddedId = String.format("%03d", i);
            seedData.add(Wallet.builder()
                    .phoneNumber("+221770000" + paddedId)
                    .email("user" + i + "@example.com")
                    .code("WLT-" + paddedId)
                    .currency("XOF")
                    .balance(new BigDecimal("50000.00"))
                    .build());
        }

        return seedData.stream()
                .map(wallet -> walletRepository.findByPhoneNumber(wallet.getPhoneNumber())
                        .orElseGet(() -> walletRepository.save(wallet)))
                .map(walletMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public WalletResponse createWallet(WalletCreationRequest request) {
        if (walletRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new WalletAlreadyExistsException("Un wallet avec ce numéro de téléphone existe déjà.");
        }

        Wallet wallet = Wallet.builder()
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .code(request.getCode())
                .currency(request.getCurrency())
                .balance(request.getInitialBalance())
                .build();

        Wallet savedWallet = walletRepository.save(wallet);
        return walletMapper.toResponse(savedWallet);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WalletResponse> getAllWallets(Pageable pageable) {
        return walletRepository.findAll(pageable)
                .map(walletMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public WalletResponse getWalletByPhone(String phone) {
        Wallet wallet = walletRepository.findByPhoneNumber(phone)
                .orElseThrow(() -> new WalletNotFoundException(
                        "Aucun wallet trouvé pour le numéro : " + phone));
        return walletMapper.toResponse(wallet);
    }

    @Override
    @Transactional(readOnly = true)
    public BalanceResponse getWalletBalance(String phone) {
        Wallet wallet = walletRepository.findByPhoneNumber(phone)
                .orElseThrow(() -> new WalletNotFoundException(
                        "Aucun wallet trouvé pour le numéro : " + phone));
        return BalanceResponse.builder()
                .phoneNumber(wallet.getPhoneNumber())
                .balance(wallet.getBalance())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionHistory(String phone) {
        // Vérification de l'existence du wallet avant de chercher ses transactions
        if (!walletRepository.existsByPhoneNumber(phone)) {
            throw new WalletNotFoundException(
                    "Aucun wallet trouvé pour le numéro : " + phone);
        }
        return transactionRepository
                .findByWalletPhoneNumberOrderByCreatedAtDesc(phone)
                .stream()
                .map(transactionMapper::toResponse)
                .collect(Collectors.toList());
    }
}
