package com.badwallet.service;

import com.badwallet.dto.request.WalletCreationRequest;
import com.badwallet.dto.response.BalanceResponse;
import com.badwallet.dto.response.TransactionResponse;
import com.badwallet.dto.response.WalletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WalletService {
    List<WalletResponse> seedWallets(int numWallets, int eventsPerWallet);
    WalletResponse createWallet(WalletCreationRequest request);
    Page<WalletResponse> getAllWallets(Pageable pageable);
    WalletResponse getWalletByPhone(String phone);
    BalanceResponse getWalletBalance(String phone);
    List<TransactionResponse> getTransactionHistory(String phone);
}
