package com.badwallet.controller;

import com.badwallet.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WalletRepository walletRepository;

    @BeforeEach
    void setUp() {
        walletRepository.deleteAll();
    }

    @Test
    void shouldSeedWallets() throws Exception {
        mockMvc.perform(post("/api/wallets/seed")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].phoneNumber").value("+221770000001"))
                .andExpect(jsonPath("$[0].balance").value(5000.00))
                .andExpect(jsonPath("$[1].phoneNumber").value("+221770000002"))
                .andExpect(jsonPath("$[1].balance").value(10000.00))
                .andExpect(jsonPath("$[2].phoneNumber").value("+221770000003"))
                .andExpect(jsonPath("$[2].balance").value(20000.00));
    }
}
