# 🚀 Projet BadWallet - Backend (Microservices)

Bienvenue sur le dépôt Backend du projet **BadWallet**, réalisé pour valider le module de Design Patterns et d'Architecture Logicielle. L'objectif était de construire une API robuste et résiliente, capable d'être interrogée par un frontend moderne (Single Page Application).

## 🏗️ Architecture du Projet
L'architecture Backend est divisée en deux microservices distincts, ce qui permet un découplage fort entre la gestion de l'argent et le système de paiement des fournisseurs.

1. **`badwallet-api` (Port 8080)** : 
   C'est le composant principal. Il gère la création des portefeuilles, valide les transactions (avec calcul des frais à 1%) et agit comme un proxy pour interroger le service de paiement.
   
2. **`payment-service` (Port 8081)** : 
   Il s'agit d'un simulateur de système externe (comme Senelec, Woyofal, ou ISM). Il génère automatiquement de fausses factures en base de données et valide les requêtes de paiement transmises par l'API principale.

## 🛠️ Stack Technique
- Java 17
- Spring Boot 3 (Web, Data JPA, Validation)
- PostgreSQL
- Maven

## ⚠️ Comment lancer le backend (IMPORTANT)

Pour que l'application fonctionne entièrement, **il est impératif d'ouvrir 2 terminaux séparés** afin de lancer les deux microservices en parallèle. Assurez-vous que PostgreSQL est démarré sur le port 5432.

**Terminal 1 : Démarrer le service des factures**
```bash
cd payment-service
mvn clean spring-boot:run
```
*(Ce service s'exécutera sur le port 8081).*

**Terminal 2 : Démarrer l'API principale**
```bash
cd badwallet-api
mvn clean spring-boot:run
```
*(L'API s'exécutera sur le port 8080).*

## 🧪 Tests & Endpoints
Les jeux d'essais (utilisateurs, portefeuilles et factures) sont injectés automatiquement au démarrage.
Le fichier `badwallet-api.http` à la racine contient des requêtes prêtes à être exécutées pour tester les différentes fonctionnalités (Création, Dépôt, Transfert, Factures).
