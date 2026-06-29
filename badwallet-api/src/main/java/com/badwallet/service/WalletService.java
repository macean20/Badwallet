package com.badwallet.service;

import com.badwallet.dto.request.WalletCreationRequest;
import com.badwallet.dto.response.BalanceResponse;
import com.badwallet.dto.response.TransactionResponse;
import com.badwallet.dto.response.WalletResponse;

import java.util.List;

public interface WalletService {
    List<WalletResponse> seedWallets();
    WalletResponse createWallet(WalletCreationRequest request);
    List<WalletResponse> getAllWallets();
    WalletResponse getWalletByPhone(String phone);
    BalanceResponse getWalletBalance(String phone);
    List<TransactionResponse> getTransactionHistory(String phone);
}
