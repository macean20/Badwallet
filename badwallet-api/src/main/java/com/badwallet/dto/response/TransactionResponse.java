package com.badwallet.dto.response;

import com.badwallet.entity.TransactionType;
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
public class TransactionResponse {
    private Long id;
    private String reference;
    private TransactionType type;
    private BigDecimal amount;
    private String walletPhone;
    private String destinationPhone;
    private LocalDateTime createdAt;
}
