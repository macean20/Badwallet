package com.paymentservice.config;

import com.paymentservice.entity.Facture;
import com.paymentservice.entity.FactureStatus;
import com.paymentservice.repository.FactureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class PaymentDataSeeder {

    private final FactureRepository factureRepository;

    @Bean
    public CommandLineRunner initFactures() {
        return args -> {
            if (factureRepository.count() == 0) {
                log.info("Seeding initial factures into payment-service DB...");
                
                LocalDate currentMonthDate = LocalDate.now().withDayOfMonth(15);

                List<Facture> factures = List.of(
                        Facture.builder()
                                .reference("FAC-ISM-3-1")
                                .amount(new BigDecimal("50000.00"))
                                .serviceName("ISM")
                                .clientCode("WLT-0000003")
                                .dueDate(currentMonthDate)
                                .status(FactureStatus.IMPAYEE)
                                .build(),
                        Facture.builder()
                                .reference("FAC-ISM-3-3")
                                .amount(new BigDecimal("25000.00"))
                                .serviceName("ISM")
                                .clientCode("WLT-0000003")
                                .dueDate(currentMonthDate)
                                .status(FactureStatus.IMPAYEE)
                                .build(),
                        Facture.builder()
                                .reference("FAC-WOY-3-1")
                                .amount(new BigDecimal("15000.00"))
                                .serviceName("WOYAFAL")
                                .clientCode("WLT-0000003")
                                .dueDate(currentMonthDate)
                                .status(FactureStatus.IMPAYEE)
                                .build(),
                        Facture.builder()
                                .reference("FAC-SEN-3-1")
                                .amount(new BigDecimal("10000.00"))
                                .serviceName("SENELEC")
                                .clientCode("WLT-0000003")
                                .dueDate(LocalDate.of(2026, 6, 15)) // Unpaid facture during the requested period (May to July)
                                .status(FactureStatus.IMPAYEE)
                                .build()
                );

                factureRepository.saveAll(factures);
                log.info("Seeded 4 factures for client WLT-0000003.");
            }
        };
    }
}
