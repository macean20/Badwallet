package com.badwallet.service;

import com.badwallet.dto.request.DepositRequest;
import com.badwallet.dto.request.TransferRequest;
import com.badwallet.dto.request.WithdrawRequest;
import com.badwallet.dto.response.TransactionResponse;

public interface TransactionService {
    TransactionResponse deposit(Long walletId, DepositRequest request);
    TransactionResponse withdraw(WithdrawRequest request);
    TransactionResponse transfer(TransferRequest request);
}
