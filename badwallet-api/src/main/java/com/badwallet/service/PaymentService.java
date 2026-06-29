package com.badwallet.service;

import com.badwallet.dto.request.PaymentRequest;
import com.badwallet.dto.response.PaymentResponse;

public interface PaymentService {
    PaymentResponse pay(PaymentRequest request);
}
