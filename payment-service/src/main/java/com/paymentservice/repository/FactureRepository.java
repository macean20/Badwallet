package com.paymentservice.repository;

import com.paymentservice.entity.Facture;
import com.paymentservice.entity.FactureStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FactureRepository extends JpaRepository<Facture, Long> {

    List<Facture> findByClientCodeAndStatus(String clientCode, FactureStatus status);

    List<Facture> findByClientCodeAndStatusAndServiceNameIgnoreCase(String clientCode, FactureStatus status,
            String serviceName);

    List<Facture> findByClientCodeAndStatusAndDueDateBetween(String clientCode, FactureStatus status,
            LocalDate startDate, LocalDate endDate);
}
