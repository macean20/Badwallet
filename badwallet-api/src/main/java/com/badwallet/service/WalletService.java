package com.badwallet.service;

import com.badwallet.dto.response.WalletResponse;
import java.util.List;

public interface WalletService {
    List<WalletResponse> seedWallets();
}
