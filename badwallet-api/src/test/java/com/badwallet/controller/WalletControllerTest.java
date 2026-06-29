package com.badwallet.controller;

import com.badwallet.repository.TransactionRepository;
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

    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        // Supprimer les transactions en premier pour respecter la contrainte FK
        transactionRepository.deleteAll();
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

    @Test
    void shouldGetWalletByPhone() throws Exception {
        mockMvc.perform(post("/api/wallets/seed").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/wallets/+221770000001")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phoneNumber").value("+221770000001"))
                .andExpect(jsonPath("$.balance").value(5000.00));
    }

    @Test
    void shouldGetWalletBalance() throws Exception {
        mockMvc.perform(post("/api/wallets/seed").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/wallets/+221770000002/balance")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phoneNumber").value("+221770000002"))
                .andExpect(jsonPath("$.balance").value(10000.00));
    }

    @Test
    void shouldReturn404WhenWalletNotFound() throws Exception {
        mockMvc.perform(get("/api/wallets/+221799999999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    // ─── Tests Dépôt ──────────────────────────────────────────────────────────

    @Test
    void shouldDepositSuccessfully() throws Exception {
        // Seed et extraire l'ID du premier wallet dynamiquement
        String seedResponse = mockMvc.perform(post("/api/wallets/seed")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // Extraire le premier ID via parsing simple du JSON
        Integer rawId = com.jayway.jsonpath.JsonPath.read(seedResponse, "$[0].id");
        long walletId = rawId.longValue();

        mockMvc.perform(post("/api/wallets/" + walletId + "/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":1000.00}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("DEPOSIT"))
                .andExpect(jsonPath("$.amount").value(1000.00))
                .andExpect(jsonPath("$.reference").isNotEmpty());
    }

    @Test
    void shouldReturn404OnDepositUnknownWallet() throws Exception {
        mockMvc.perform(post("/api/wallets/9999/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":100.00}"))
                .andExpect(status().isNotFound());
    }

    // ─── Tests Retrait ────────────────────────────────────────────────────────

    @Test
    void shouldWithdrawSuccessfully() throws Exception {
        mockMvc.perform(post("/api/wallets/seed").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/wallets/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"phoneNumber\":\"+221770000003\",\"amount\":5000.00}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("WITHDRAWAL"))
                .andExpect(jsonPath("$.amount").value(5000.00));
    }

    @Test
    void shouldReturn422OnInsufficientBalance() throws Exception {
        mockMvc.perform(post("/api/wallets/seed").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/wallets/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"phoneNumber\":\"+221770000001\",\"amount\":99999.00}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422));
    }

    // ─── Tests Transfert ──────────────────────────────────────────────────────

    @Test
    void shouldTransferSuccessfully() throws Exception {
        mockMvc.perform(post("/api/wallets/seed").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/wallets/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"senderPhone\":\"+221770000002\",\"recipientPhone\":\"+221770000001\",\"amount\":2000.00}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("TRANSFER"))
                .andExpect(jsonPath("$.destinationPhone").value("+221770000001"));
    }

    @Test
    void shouldReturn400WhenTransferToSameWallet() throws Exception {
        mockMvc.perform(post("/api/wallets/seed").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/wallets/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"senderPhone\":\"+221770000001\",\"recipientPhone\":\"+221770000001\",\"amount\":100.00}"))
                .andExpect(status().isBadRequest());
    }

    // ─── Tests Historique ────────────────────────────────────────────────────

    @Test
    void shouldReturnEmptyHistoryForNewWallet() throws Exception {
        mockMvc.perform(post("/api/wallets/seed").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/wallets/+221770000001/transactions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldReturnHistoryAfterTransactions() throws Exception {
        mockMvc.perform(post("/api/wallets/seed").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Effectuer un retrait sur +221770000003 (solde = 20000)
        mockMvc.perform(post("/api/wallets/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"phoneNumber\":\"+221770000003\",\"amount\":500.00}"))
                .andExpect(status().isCreated());

        // Effectuer un transfert depuis +221770000003 vers +221770000001
        mockMvc.perform(post("/api/wallets/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"senderPhone\":\"+221770000003\",\"recipientPhone\":\"+221770000001\",\"amount\":1000.00}"))
                .andExpect(status().isCreated());

        // L'historique doit contenir 2 transactions (du plus récent au plus ancien)
        mockMvc.perform(get("/api/wallets/+221770000003/transactions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].type").value("TRANSFER"))
                .andExpect(jsonPath("$[1].type").value("WITHDRAWAL"));
    }

    @Test
    void shouldReturn404OnHistoryForUnknownWallet() throws Exception {
        mockMvc.perform(get("/api/wallets/+221700000000/transactions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // ─── Tests Paiement (Strategy Pattern) ───────────────────────────────────

    @Test
    void shouldPayMerchantSuccessfully() throws Exception {
        mockMvc.perform(post("/api/wallets/seed").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        String requestBody = "{\"phoneNumber\":\"+221770000001\",\"amount\":1500.00,\"paymentType\":\"MERCHANT\",\"description\":\"Achat en ligne\"}";
        
        mockMvc.perform(post("/api/wallets/pay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentType").value("MERCHANT"))
                .andExpect(jsonPath("$.amount").value(1500.00))
                .andExpect(jsonPath("$.reference").isNotEmpty());
    }

    @Test
    void shouldFailToPayWhenStrategyUnknown() throws Exception {
        mockMvc.perform(post("/api/wallets/seed").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        String requestBody = "{\"phoneNumber\":\"+221770000001\",\"amount\":1500.00,\"paymentType\":\"UNKNOWN\",\"description\":\"Test\"}";
        
        mockMvc.perform(post("/api/wallets/pay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }
}
