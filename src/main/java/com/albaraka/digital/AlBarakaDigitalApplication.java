package com.albaraka.digital;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for Al Baraka Digital Banking Platform.
 * 
 * Plateforme bancaire sécurisée avec:
 * - Authentification JWT
 * - Gestion des opérations bancaires (dépôts, retraits, virements)
 * - Workflow de validation selon le montant
 * - Gestion des rôles (CLIENT, AGENT_BANCAIRE, ADMIN)
 */
@SpringBootApplication
public class AlBarakaDigitalApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlBarakaDigitalApplication.class, args);
    }
}
