package com.paymentservice.service.impl;

import com.paymentservice.dto.response.FactureResponse;
import com.paymentservice.entity.Facture;
import com.paymentservice.entity.FactureStatus;
import com.paymentservice.repository.FactureRepository;
import com.paymentservice.service.FactureService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FactureServiceImpl implements FactureService {

    private final FactureRepository factureRepository;

    @Override
    @Transactional(readOnly = true)
    public List<FactureResponse> getCurrentUnpaidFactures(String clientCode, String unite) {
        YearMonth currentMonth = YearMonth.now();
        LocalDate startDate = currentMonth.atDay(1);
        LocalDate endDate = currentMonth.atEndOfMonth();

        List<Facture> factures;
        if (unite != null && !unite.trim().isEmpty()) {
            factures = factureRepository.findByClientCodeAndStatusAndServiceNameIgnoreCase(clientCode, FactureStatus.IMPAYEE, unite);
        } else {
            factures = factureRepository.findByClientCodeAndStatus(clientCode, FactureStatus.IMPAYEE);
        }

        // Filtre supplémentaire pour le "mois en cours"
        return factures.stream()
                .filter(f -> !f.getDueDate().isBefore(startDate) && !f.getDueDate().isAfter(endDate))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FactureResponse> getUnpaidFacturesByPeriode(String clientCode, LocalDate debut, LocalDate fin) {
        return factureRepository.findByClientCodeAndStatusAndDueDateBetween(clientCode, FactureStatus.IMPAYEE, debut, fin)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private FactureResponse mapToResponse(Facture facture) {
        return FactureResponse.builder()
                .reference(facture.getReference())
                .amount(facture.getAmount())
                .serviceName(facture.getServiceName())
                .clientCode(facture.getClientCode())
                .dueDate(facture.getDueDate())
                .status(facture.getStatus())
                .build();
    }
}
