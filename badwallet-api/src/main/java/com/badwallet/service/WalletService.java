package com.badwallet.service;

import com.badwallet.dto.request.WalletCreationRequest;
import com.badwallet.dto.response.WalletResponse;
import java.util.List;

public interface WalletService {
    List<WalletResponse> seedWallets();
    WalletResponse createWallet(WalletCreationRequest request);
    List<WalletResponse> getAllWallets();
}
