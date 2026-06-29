package com.paymentservice.service;

import com.paymentservice.dto.response.FactureResponse;

import java.time.LocalDate;
import java.util.List;

public interface FactureService {

    List<FactureResponse> getCurrentUnpaidFactures(String clientCode, String unite);

    List<FactureResponse> getUnpaidFacturesByPeriode(String clientCode, LocalDate debut, LocalDate fin);
}
