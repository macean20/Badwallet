package com.badwallet.service.payment;

import com.badwallet.dto.request.PaymentRequest;
import com.badwallet.dto.response.PaymentResponse;

/**
 * Interface Strategy pour les différents types de paiement.
 * Chaque implémentation encapsule un comportement de paiement distinct.
 */
public interface PaymentStrategy {

    /**
     * Exécute le paiement selon la stratégie choisie.
     * @param request DTO contenant les informations de paiement.
     * @return PaymentResponse contenant la confirmation du paiement.
     */
    PaymentResponse execute(PaymentRequest request);

    /**
     * Retourne l'identifiant unique de la stratégie (ex: "MERCHANT").
     */
    String getType();
}
