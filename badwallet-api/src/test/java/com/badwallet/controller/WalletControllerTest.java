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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Test
    void shouldCreateWallet() throws Exception {
        String requestBody = "{\"phoneNumber\":\"+221775555555\",\"initialBalance\":1500.00}";
        mockMvc.perform(post("/api/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.phoneNumber").value("+221775555555"))
                .andExpect(jsonPath("$.balance").value(1500.00));
    }

    @Test
    void shouldFailToCreateWalletWhenAlreadyExists() throws Exception {
        // Seed first
        mockMvc.perform(post("/api/wallets/seed")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        String requestBody = "{\"phoneNumber\":\"+221770000001\",\"initialBalance\":100.00}";
        mockMvc.perform(post("/api/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Un wallet avec ce numéro de téléphone existe déjà."));
    }

    @Test
    void shouldFailToCreateWalletWhenValidationFails() throws Exception {
        // Empty phone number
        String requestBodyEmptyPhone = "{\"phoneNumber\":\"\",\"initialBalance\":100.00}";
        mockMvc.perform(post("/api/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyEmptyPhone))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Le numéro de téléphone est obligatoire")));

        // Negative balance
        String requestBodyNegativeBalance = "{\"phoneNumber\":\"+221779999999\",\"initialBalance\":-50.00}";
        mockMvc.perform(post("/api/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyNegativeBalance))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Le solde initial ne peut pas être négatif")));
    }

    @Test
    void shouldListAllWallets() throws Exception {
        // Seed 3 wallets first
        mockMvc.perform(post("/api/wallets/seed")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/wallets")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }
}
