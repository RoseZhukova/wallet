package com.eremina.wallet.controller;

import com.eremina.wallet.dto.WalletOperationRequestDTO;
import com.eremina.wallet.model.OperationType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.util.UUID;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/data-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCheckDatabaseInitialization() throws Exception {
        mockMvc.perform(get("/api/v1/wallets/a1a1a1a1-a1a1-a1a1-a1a1-a1a1a1a1a1a1"))
                .andExpect(status().isOk())
                .andDo(result -> System.out.println("Response: " + result.getResponse().getContentAsString()));
    }

    @Test
    void shouldDepositSum() throws Exception {
        WalletOperationRequestDTO request = new WalletOperationRequestDTO();
        request.setWalletId(UUID.fromString("a1a1a1a1-a1a1-a1a1-a1a1-a1a1a1a1a1a1"));
        request.setOperationType(OperationType.DEPOSIT);
        request.setAmount(new BigDecimal("100.00"));

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("a1a1a1a1-a1a1-a1a1-a1a1-a1a1a1a1a1a1"))
                .andExpect(jsonPath("$.balance").value(1100.00));
    }

    @Test
    void shouldWithdrawSum() throws Exception {
        WalletOperationRequestDTO request = new WalletOperationRequestDTO();
        request.setWalletId(UUID.fromString("b2b2b2b2-b2b2-b2b2-b2b2-b2b2b2b2b2b2"));
        request.setOperationType(OperationType.WITHDRAW);
        request.setAmount(new BigDecimal("100.00"));

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(400.00));
    }

    @Test
    void shouldGetCorrectBalance() throws Exception {
        mockMvc.perform(get("/api/v1/wallets/c3c3c3c3-c3c3-c3c3-c3c3-c3c3c3c3c3c3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("c3c3c3c3-c3c3-c3c3-c3c3-c3c3c3c3c3c3"));
    }

    @Test
    void shouldThrowExceptionWhenWalletNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/wallets/00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Wallet Not Found"));
    }

    @Test
    void shouldThrowExceptionWhenInsufficientFunds() throws Exception {
        WalletOperationRequestDTO request = new WalletOperationRequestDTO();
        request.setWalletId(UUID.fromString("d4d4d4d4-d4d4-d4d4-d4d4-d4d4d4d4d4d4"));
        request.setOperationType(OperationType.WITHDRAW);
        request.setAmount(new BigDecimal("1000.00"));

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Insufficient Funds"));
    }

    @Test
    void shouldThrowExceptionWhenInvalidJson() throws Exception {
        String invalidJson = "{\"walletId\": \"invalid-uuid\", \"operationType\": \"DEPOSIT\", \"amount\": 100}";

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldThrowExceptionWhenInvalidOperationType() throws Exception {
        String invalidRequest = "{\"walletId\": \"e5e5e5e5-e5e5-e5e5-e5e5-e5e5e5e5e5e5\", \"operationType\": \"INVALID\", \"amount\": 100}";

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldThrowExceptionWhenNegativeAmount() throws Exception {
        WalletOperationRequestDTO request = new WalletOperationRequestDTO();
        request.setWalletId(UUID.fromString("f6f6f6f6-f6f6-f6f6-f6f6-f6f6f6f6f6f6"));
        request.setOperationType(OperationType.DEPOSIT);
        request.setAmount(new BigDecimal("-100.00"));

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
