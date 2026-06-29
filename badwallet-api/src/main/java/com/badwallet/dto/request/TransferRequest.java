package com.badwallet.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferRequest {

    @NotBlank(message = "Le numéro expéditeur est obligatoire")
    @Pattern(regexp = "^\\+?[0-9]{9,15}$", message = "Format de numéro expéditeur invalide")
    private String senderPhone;

    @NotBlank(message = "Le numéro destinataire est obligatoire")
    @Pattern(regexp = "^\\+?[0-9]{9,15}$", message = "Format de numéro destinataire invalide")
    private String recipientPhone;

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant du transfert doit être supérieur à zéro")
    private BigDecimal amount;
}
