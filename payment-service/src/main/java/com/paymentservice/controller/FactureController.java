package com.paymentservice.controller;

import com.paymentservice.dto.response.FactureResponse;
import com.paymentservice.service.FactureService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/payments/factures")
@RequiredArgsConstructor
public class FactureController {

    private final FactureService factureService;

    @GetMapping("/{clientCode}/current")
    public ResponseEntity<List<FactureResponse>> getCurrentFactures(
            @PathVariable String clientCode,
            @RequestParam(required = false) String unite) {
        return ResponseEntity.ok(factureService.getCurrentUnpaidFactures(clientCode, unite));
    }

    @GetMapping("/{clientCode}/periode")
    public ResponseEntity<List<FactureResponse>> getFacturesByPeriode(
            @PathVariable String clientCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ResponseEntity.ok(factureService.getUnpaidFacturesByPeriode(clientCode, debut, fin));
    }
}
