package com.badwallet.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/external/factures")
@RequiredArgsConstructor
public class ExternalFactureController {

    private final RestTemplate restTemplate;
    private final String paymentServiceUrl = "http://localhost:8081/api/payments/factures";

    @GetMapping("/{clientCode}/current")
    public ResponseEntity<String> getCurrentFactures(
            @PathVariable String clientCode,
            @RequestParam(required = false) String unite) {
        
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(paymentServiceUrl + "/" + clientCode + "/current");
        if (unite != null) {
            builder.queryParam("unite", unite);
        }
        
        ResponseEntity<String> response = restTemplate.getForEntity(builder.toUriString(), String.class);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    @GetMapping("/{clientCode}/periode")
    public ResponseEntity<String> getFacturesByPeriode(
            @PathVariable String clientCode,
            @RequestParam String debut,
            @RequestParam String fin) {
        
        String url = UriComponentsBuilder.fromHttpUrl(paymentServiceUrl + "/" + clientCode + "/periode")
                .queryParam("debut", debut)
                .queryParam("fin", fin)
                .toUriString();
                
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }
}
