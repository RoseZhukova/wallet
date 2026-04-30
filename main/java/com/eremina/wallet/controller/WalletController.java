package com.eremina.wallet.controller;

import com.eremina.wallet.dto.WalletOperationRequestDTO;
import com.eremina.wallet.model.WalletEntity;
import com.eremina.wallet.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @PostMapping("/wallet")
    public ResponseEntity<WalletEntity> performOperation(@Valid @RequestBody WalletOperationRequestDTO request) {
        WalletEntity wallet = walletService.performOperation(request);
        return ResponseEntity.ok(wallet);
    }

    @GetMapping("/wallets/{walletId}")
    public ResponseEntity<WalletEntity> getBalance(@PathVariable UUID walletId) {
        WalletEntity wallet = walletService.getBalance(walletId);
        return ResponseEntity.ok(wallet);
    }
}
