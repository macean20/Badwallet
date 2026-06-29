package com.badwallet.service.payment;

import com.badwallet.dto.request.PaymentRequest;
import com.badwallet.dto.response.PaymentResponse;
import com.badwallet.entity.Transaction;
import com.badwallet.entity.TransactionType;
import com.badwallet.entity.Wallet;
import com.badwallet.exception.InsufficientBalanceException;
import com.badwallet.exception.WalletNotFoundException;
import com.badwallet.mapper.TransactionMapper;
import com.badwallet.repository.TransactionRepository;
import com.badwallet.repository.WalletRepository;
import com.badwallet.util.ReferenceGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

/**
 * Stratégie de paiement marchand standard.
 * Débite directement le wallet du client et enregistre la transaction.
 */
@Component
@RequiredArgsConstructor
public class MerchantPaymentStrategy implements PaymentStrategy {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final ReferenceGenerator referenceGenerator;

    @Override
    public String getType() {
        return "MERCHANT";
    }

    @Override
    @Transactional
    public PaymentResponse execute(PaymentRequest request) {
        Wallet wallet = walletRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new WalletNotFoundException(
                        "Aucun wallet trouvé pour le numéro : " + request.getPhoneNumber()));

        if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException(
                    "Solde insuffisant pour ce paiement. Solde disponible : " + wallet.getBalance());
        }

        wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
        walletRepository.save(wallet);

        String reference = referenceGenerator.generate();

        Transaction transaction = Transaction.builder()
                .reference(reference)
                .type(TransactionType.PAYMENT)
                .amount(request.getAmount())
                .wallet(wallet)
                .destinationPhone(request.getDescription())
                .build();
        transactionRepository.save(transaction);

        return PaymentResponse.builder()
                .reference(reference)
                .phoneNumber(request.getPhoneNumber())
                .amount(request.getAmount())
                .paymentType(getType())
                .description(request.getDescription())
                .paidAt(LocalDateTime.now())
                .build();
    }
}
