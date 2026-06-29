package com.badwallet.util;

import org.springframework.stereotype.Component;
import java.util.UUID;

/**
 * Utilitaire de génération de références de transaction uniques.
 * Utilise UUID pour garantir l'unicité sans coordination centralisée.
 */
@Component
public class ReferenceGenerator {

    public String generate() {
        return "TXN-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
}
