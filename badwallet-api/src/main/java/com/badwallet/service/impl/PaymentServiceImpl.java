package com.badwallet.service.impl;

import com.badwallet.dto.request.PaymentRequest;
import com.badwallet.dto.response.PaymentResponse;
import com.badwallet.service.PaymentService;
import com.badwallet.service.payment.PaymentStrategy;
import com.badwallet.service.payment.PaymentStrategyFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentStrategyFactory strategyFactory;

    @Override
    public PaymentResponse pay(PaymentRequest request) {
        PaymentStrategy strategy = strategyFactory.getStrategy(request.getServiceName());
        return strategy.execute(request);
    }

    @Override
    public PaymentResponse payFactures(com.badwallet.dto.request.PayFacturesRequest request) {
        // Implementation for multiple factures
        // For now, we will use the ISM strategy logic manually or proxy it directly
        // Because of missing amount in request, we bypass balance deduction in this stub
        return PaymentResponse.builder()
                .reference("MULTI-" + System.currentTimeMillis())
                .phoneNumber(request.getPhoneNumber())
                .paymentType(request.getServiceName())
                .description("Paiement multiple: " + String.join(", ", request.getFactureReferences()))
                .amount(java.math.BigDecimal.ZERO) // Amount unknown from request
                .paidAt(java.time.LocalDateTime.now())
                .build();
    }
}
