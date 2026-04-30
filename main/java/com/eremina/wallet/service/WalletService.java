package com.eremina.wallet.service;

import com.eremina.wallet.dto.WalletOperationRequestDTO;
import com.eremina.wallet.exception.InsufficientFundsException;
import com.eremina.wallet.exception.WalletNotFoundException;
import com.eremina.wallet.model.OperationType;
import com.eremina.wallet.model.WalletEntity;
import com.eremina.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {
    private final WalletRepository walletRepository;

    @Transactional
    protected WalletEntity doOperation(WalletOperationRequestDTO request) {
        WalletEntity wallet = walletRepository.findById(request.getWalletId())
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found"));

        if (request.getOperationType() == OperationType.DEPOSIT) {
            wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        } else if (request.getOperationType() == OperationType.WITHDRAW) {
            if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
                throw new InsufficientFundsException("Insufficient funds");
            }
            wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
        }

        return walletRepository.save(wallet);
    }

    @Transactional(readOnly = true)
    public WalletEntity getBalance(UUID walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException(
                        "Wallet not found with id: " + walletId));
    }

    public WalletEntity performOperation(WalletOperationRequestDTO request) {
        long deadline = System.currentTimeMillis() + 2000;
        int attempt = 0;

        while (System.currentTimeMillis() < deadline) {
            try {
                return doOperation(request);
            } catch (ObjectOptimisticLockingFailureException | StaleObjectStateException ex) {
                attempt++;
                sleep(attempt);
            }
        }
        throw new RuntimeException("Failed to perform operation after retries");
    }

    private void sleep(int attempt) {
        try {
            long delay = Math.min(50, 5L * attempt); // плавный рост
            Thread.sleep(delay);
        } catch (InterruptedException ignored) {}
    }
}
