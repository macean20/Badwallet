package com.badwallet.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayFacturesRequest {

    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    private String phoneNumber;

    @NotBlank(message = "Le nom du service est obligatoire")
    private String serviceName;

    @NotEmpty(message = "La liste des références de factures ne peut pas être vide")
    private List<String> factureReferences;
}
