package com.badwallet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {
    private String reference;
    private String phoneNumber;
    private BigDecimal amount;
    private String paymentType;
    private String description;
    private LocalDateTime paidAt;
}
