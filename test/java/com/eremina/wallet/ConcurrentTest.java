package com.eremina.wallet;

import com.eremina.wallet.dto.WalletOperationRequestDTO;
import com.eremina.wallet.model.OperationType;
import com.eremina.wallet.model.WalletEntity;
import com.eremina.wallet.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ConcurrentTest {

    @Autowired
    private WalletService walletService;

    @Test
    void shouldCheckConcurrentOperations() throws InterruptedException {
        UUID walletId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        BigDecimal initialBalance = walletService.getBalance(walletId).getBalance();
        int threads = 100;
        BigDecimal amount = new BigDecimal("100.00");

        ExecutorService executor = Executors.newFixedThreadPool(50);
        CountDownLatch latch = new CountDownLatch(threads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    WalletOperationRequestDTO request = new WalletOperationRequestDTO();
                    request.setWalletId(walletId);
                    request.setOperationType(OperationType.DEPOSIT);
                    request.setAmount(amount);

                    walletService.performOperation(request);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    e.printStackTrace();
                    errorCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        WalletEntity finalWallet = walletService.getBalance(walletId);
        BigDecimal expectedBalance = initialBalance.add(amount.multiply(new BigDecimal(successCount.get())));

        assertEquals(threads, successCount.get(), "All operations should succeed");
        assertEquals(0, errorCount.get(), "No errors expected");
        assertEquals(expectedBalance, finalWallet.getBalance(), "Balance should match expected");
    }
}
