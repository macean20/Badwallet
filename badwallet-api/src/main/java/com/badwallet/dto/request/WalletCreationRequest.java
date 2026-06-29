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
public class WalletCreationRequest {

    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    @Pattern(regexp = "^\\+?[0-9]{9,15}$", message = "Format de numéro de téléphone invalide (ex: +221770000001)")
    private String phoneNumber;

    @NotNull(message = "Le solde initial est obligatoire")
    @DecimalMin(value = "0.00", message = "Le solde initial ne peut pas être négatif")
    private BigDecimal initialBalance;
}
