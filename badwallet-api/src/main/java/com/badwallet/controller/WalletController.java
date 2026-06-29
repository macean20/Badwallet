package com.badwallet.controller;

import com.badwallet.dto.request.WalletCreationRequest;
import com.badwallet.dto.response.WalletResponse;
import com.badwallet.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/seed")
    public ResponseEntity<List<WalletResponse>> seedWallets() {
        List<WalletResponse> seeded = walletService.seedWallets();
        return new ResponseEntity<>(seeded, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<WalletResponse> createWallet(@Valid @RequestBody WalletCreationRequest request) {
        WalletResponse response = walletService.createWallet(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
