package com.badwallet.service.impl;

import com.badwallet.dto.request.DepositRequest;
import com.badwallet.dto.request.TransferRequest;
import com.badwallet.dto.request.WithdrawRequest;
import com.badwallet.dto.response.TransactionResponse;
import com.badwallet.entity.Transaction;
import com.badwallet.entity.TransactionType;
import com.badwallet.entity.Wallet;
import com.badwallet.exception.InsufficientBalanceException;
import com.badwallet.exception.WalletNotFoundException;
import com.badwallet.mapper.TransactionMapper;
import com.badwallet.repository.TransactionRepository;
import com.badwallet.repository.WalletRepository;
import com.badwallet.service.TransactionService;
import com.badwallet.util.ReferenceGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final ReferenceGenerator referenceGenerator;

    @Override
    @Transactional
    public TransactionResponse deposit(Long walletId, DepositRequest request) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException(
                        "Aucun wallet trouvé avec l'identifiant : " + walletId));

        wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        walletRepository.save(wallet);

        Transaction transaction = Transaction.builder()
                .reference(referenceGenerator.generate())
                .type(TransactionType.DEPOSIT)
                .amount(request.getAmount())
                .wallet(wallet)
                .build();

        return transactionMapper.toResponse(transactionRepository.save(transaction));
    }

    @Override
    @Transactional
    public TransactionResponse withdraw(WithdrawRequest request) {
        Wallet wallet = walletRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new WalletNotFoundException(
                        "Aucun wallet trouvé pour le numéro : " + request.getPhoneNumber()));

        if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException(
                    "Solde insuffisant. Solde disponible : " + wallet.getBalance());
        }

        wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
        walletRepository.save(wallet);

        Transaction transaction = Transaction.builder()
                .reference(referenceGenerator.generate())
                .type(TransactionType.WITHDRAWAL)
                .amount(request.getAmount())
                .wallet(wallet)
                .build();

        return transactionMapper.toResponse(transactionRepository.save(transaction));
    }

    @Override
    @Transactional
    public TransactionResponse transfer(TransferRequest request) {
        if (request.getSenderPhone().equals(request.getRecipientPhone())) {
            throw new IllegalArgumentException(
                    "L'expéditeur et le destinataire ne peuvent pas être identiques.");
        }

        Wallet sender = walletRepository.findByPhoneNumber(request.getSenderPhone())
                .orElseThrow(() -> new WalletNotFoundException(
                        "Wallet expéditeur introuvable : " + request.getSenderPhone()));

        Wallet recipient = walletRepository.findByPhoneNumber(request.getRecipientPhone())
                .orElseThrow(() -> new WalletNotFoundException(
                        "Wallet destinataire introuvable : " + request.getRecipientPhone()));

        if (sender.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException(
                    "Solde insuffisant pour le transfert. Solde disponible : " + sender.getBalance());
        }

        sender.setBalance(sender.getBalance().subtract(request.getAmount()));
        recipient.setBalance(recipient.getBalance().add(request.getAmount()));
        walletRepository.save(sender);
        walletRepository.save(recipient);

        Transaction transaction = Transaction.builder()
                .reference(referenceGenerator.generate())
                .type(TransactionType.TRANSFER)
                .amount(request.getAmount())
                .wallet(sender)
                .destinationPhone(recipient.getPhoneNumber())
                .build();

        return transactionMapper.toResponse(transactionRepository.save(transaction));
    }
}
