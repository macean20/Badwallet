package com.badwallet.service.payment;

import com.badwallet.dto.request.PaymentRequest;
import com.badwallet.dto.response.PaymentResponse;
import com.badwallet.entity.Transaction;
import com.badwallet.entity.TransactionType;
import com.badwallet.entity.Wallet;
import com.badwallet.exception.InsufficientBalanceException;
import com.badwallet.exception.WalletNotFoundException;
import com.badwallet.repository.TransactionRepository;
import com.badwallet.repository.WalletRepository;
import com.badwallet.util.ReferenceGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Stratégie de paiement de facture (Proxy vers payment-service).
 * Gère la transaction locale et l'appel HTTP vers le service externe.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BillPaymentStrategy implements PaymentStrategy {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final ReferenceGenerator referenceGenerator;
    private final RestTemplate restTemplate;

    @Value("${payment-service.url:http://localhost:8081/api/payments/bills}")
    private String paymentServiceUrl;

    @Override
    public String getType() {
        return "ISM";
    }

    @Override
    @Transactional
    public PaymentResponse execute(PaymentRequest request) {
        // 1. Valider le wallet local et le solde
        Wallet wallet = walletRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new WalletNotFoundException(
                        "Aucun wallet trouvé pour le numéro : " + request.getPhoneNumber()));

        if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException(
                    "Solde insuffisant pour payer cette facture. Solde disponible : " + wallet.getBalance());
        }

        // 2. Préparer l'appel au service externe
        Map<String, Object> billRequest = new HashMap<>();
        billRequest.put("phoneNumber", request.getPhoneNumber());
        billRequest.put("amount", request.getAmount());
        billRequest.put("serviceName", request.getServiceName());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(billRequest, headers);

        try {
            // Appel synchrone vers le port 8081
            log.info("Appel du payment-service pour la facture : {}", request.getServiceName());
            ResponseEntity<String> response = restTemplate.postForEntity(paymentServiceUrl, entity, String.class);
            
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new IllegalStateException("Le service de paiement a rejeté la transaction.");
            }
            log.info("Paiement de facture réussi via payment-service");

        } catch (RestClientException e) {
            log.error("Erreur de communication avec le payment-service", e);
            throw new IllegalStateException("Impossible de traiter la facture via le service externe (payment-service injoignable ou erreur interne).", e);
        }

        // 3. Si le service externe a répondu OK, on débite et on sauvegarde la transaction locale
        wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
        walletRepository.save(wallet);

        String reference = referenceGenerator.generate();

        Transaction transaction = Transaction.builder()
                .reference(reference)
                .type(TransactionType.PAYMENT)
                .amount(request.getAmount())
                .wallet(wallet)
                .destinationPhone(request.getServiceName()) // La description contient la ref facture
                .build();
        transactionRepository.save(transaction);

        return PaymentResponse.builder()
                .reference(reference)
                .phoneNumber(request.getPhoneNumber())
                .amount(request.getAmount())
                .paymentType(getType())
                .description(request.getServiceName())
                .paidAt(LocalDateTime.now())
                .build();
    }
}
