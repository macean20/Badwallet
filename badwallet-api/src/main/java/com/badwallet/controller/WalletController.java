package com.badwallet.controller;

import com.badwallet.dto.request.DepositRequest;
import com.badwallet.dto.request.PayFacturesRequest;
import com.badwallet.dto.request.PaymentRequest;
import com.badwallet.dto.request.TransferRequest;
import com.badwallet.dto.request.WalletCreationRequest;
import com.badwallet.dto.request.WithdrawRequest;
import com.badwallet.dto.response.BalanceResponse;
import com.badwallet.dto.response.PaymentResponse;
import com.badwallet.dto.response.TransactionResponse;
import com.badwallet.dto.response.WalletResponse;
import com.badwallet.service.PaymentService;
import com.badwallet.service.TransactionService;
import com.badwallet.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;
    private final TransactionService transactionService;
    private final PaymentService paymentService;

    @PostMapping("/seed")
    public ResponseEntity<List<WalletResponse>> seedWallets(
            @RequestParam(defaultValue = "10") int numWallets,
            @RequestParam(defaultValue = "100") int eventsPerWallet) {
        return ResponseEntity.ok(walletService.seedWallets(numWallets, eventsPerWallet));
    }

    @PostMapping
    public ResponseEntity<WalletResponse> createWallet(@Valid @RequestBody WalletCreationRequest request) {
        return new ResponseEntity<>(walletService.createWallet(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<WalletResponse>> getAllWallets(Pageable pageable) {
        return ResponseEntity.ok(walletService.getAllWallets(pageable));
    }

    @GetMapping("/{phone}")
    public ResponseEntity<WalletResponse> getWalletByPhone(@PathVariable String phone) {
        return ResponseEntity.ok(walletService.getWalletByPhone(phone));
    }

    @GetMapping("/{phone}/balance")
    public ResponseEntity<BalanceResponse> getWalletBalance(@PathVariable String phone) {
        return ResponseEntity.ok(walletService.getWalletBalance(phone));
    }

    // ─── Opérations transactionnelles ───────────────────────────────────────

    @PostMapping("/{id}/deposit")
    public ResponseEntity<TransactionResponse> deposit(
            @PathVariable Long id,
            @Valid @RequestBody DepositRequest request) {
        return new ResponseEntity<>(transactionService.deposit(id, request), HttpStatus.CREATED);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(@Valid @RequestBody WithdrawRequest request) {
        return new ResponseEntity<>(transactionService.withdraw(request), HttpStatus.CREATED);
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(@Valid @RequestBody TransferRequest request) {
        return new ResponseEntity<>(transactionService.transfer(request), HttpStatus.CREATED);
    }

    @GetMapping("/{phone}/transactions")
    public ResponseEntity<List<TransactionResponse>> getTransactionHistory(@PathVariable String phone) {
        return ResponseEntity.ok(walletService.getTransactionHistory(phone));
    }

    // ─── Paiements (Strategy Pattern) ────────────────────────────────────────

    @PostMapping("/pay")
    public ResponseEntity<PaymentResponse> pay(@Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.pay(request));
    }

    @PostMapping("/pay-factures")
    public ResponseEntity<PaymentResponse> payFactures(@Valid @RequestBody PayFacturesRequest request) {
        return ResponseEntity.ok(paymentService.payFactures(request));
    }
}
