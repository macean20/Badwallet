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
        PaymentStrategy strategy = strategyFactory.getStrategy(request.getPaymentType());
        return strategy.execute(request);
    }
}
