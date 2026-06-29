package com.badwallet.controller;

import com.badwallet.dto.response.WalletResponse;
import com.badwallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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
}
