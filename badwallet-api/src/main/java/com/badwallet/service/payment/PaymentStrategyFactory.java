package com.badwallet.service.payment;

import com.badwallet.exception.WalletNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Factory du pattern Strategy.
 * Collecte toutes les implémentations de PaymentStrategy via injection Spring
 * et expose une méthode pour récupérer la stratégie appropriée par son type.
 *
 * Avantage : ajouter une nouvelle stratégie ne nécessite que de créer une
 * nouvelle classe @Component implémentant PaymentStrategy — zéro modification ici.
 */
@Component
public class PaymentStrategyFactory {

    private final Map<String, PaymentStrategy> strategies;

    @Autowired
    public PaymentStrategyFactory(List<PaymentStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(PaymentStrategy::getType, Function.identity()));
    }

    public PaymentStrategy getStrategy(String type) {
        PaymentStrategy strategy = strategies.get(type.toUpperCase());
        if (strategy == null) {
            throw new IllegalArgumentException(
                    "Type de paiement non supporté : " + type +
                    ". Types disponibles : " + strategies.keySet());
        }
        return strategy;
    }
}
