//package com.eremina.wallet.config;
//
//import com.eremina.wallet.model.WalletEntity;
//import com.eremina.wallet.repository.WalletRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import java.math.BigDecimal;
//import java.util.Arrays;
//import java.util.List;
//import java.util.UUID;
//
//@Configuration
//@Slf4j
//public class DataInitializer {
//
//    @Bean
//    @Profile("dev")
//    public CommandLineRunner initDevData(WalletRepository walletRepository) {
//        return args -> {
//            log.info("Initializing development data...");
//
//            // Дополнительные программные данные (если нужны)
//            List<WalletEntity> wallets = Arrays.asList(
//                    createWallet(UUID.fromString("12345678-1234-1234-1234-123456789012"),
//                            new BigDecimal("10000.00"), "Тестовый счет"),
//                    createWallet(UUID.fromString("87654321-4321-4321-4321-210987654321"),
//                            new BigDecimal("50000.00"), "Бизнес счет"),
//                    createWallet(UUID.fromString("abcd1234-abcd-1234-abcd-1234abcd1234"),
//                            new BigDecimal("1000.00"), "Личный счет")
//            );
//
//            wallets.forEach(wallet -> {
//                if (!walletRepository.existsById(wallet.getId())) {
//                    walletRepository.save(wallet);
//                    log.info("Created wallet: {} with balance: {}",
//                            wallet.getId(), wallet.getBalance());
//                }
//            });
//
//            log.info("Development data initialized successfully!");
//        };
//    }
//
//    @Bean
//    @Profile("test")
//    public CommandLineRunner initTestData(WalletRepository walletRepository) {
//        return args -> {
//            log.info("Initializing test data...");
//
//            // Проверяем, что тестовые данные загружены
//            long count = walletRepository.count();
//            log.info("Test database contains {} wallets", count);
//
//            // Выводим все кошельки для отладки
//            walletRepository.findAll().forEach(wallet ->
//                    log.debug("Test wallet: {} = {}", wallet.getId(), wallet.getBalance())
//            );
//        };
//    }
//
//    private WalletEntity createWallet(UUID id, BigDecimal balance, String description) {
//        WalletEntity wallet = new WalletEntity();
//        wallet.setId(id);
//        wallet.setBalance(balance);
//        return wallet;
//    }
//}
