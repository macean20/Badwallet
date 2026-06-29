# BadWallet Ecosystem 🚀

Ce dépôt contient le code source du projet **BadWallet**, développé dans le cadre de l'examen de Design Pattern et d'Architecture Logicielle.

## 🏗️ Architecture du Projet
Le système repose sur une architecture orientée microservices comprenant deux applications Spring Boot distinctes :

1. **`badwallet-api` (Port 8080)** : Le cœur du système gérant les portefeuilles (Wallets), les transactions (dépôts, retraits, transferts) et agissant comme proxy pour les paiements externes.
2. **`payment-service` (Port 8081)** : Le microservice externe simulant un facturier (SENELEC, ISM, WOYAFAL). Il gère et expose les factures impayées.

## 🛠️ Technologies Utilisées
- **Java 17**
- **Spring Boot 3** (Web, Data JPA, Validation)
- **H2 Database** (Base de données en mémoire pour faciliter les tests)
- **Lombok** (Réduction du code boilerplate)
- **Maven** (Gestion des dépendances)

## 📐 Design Patterns Appliqués
L'application respecte les principes **SOLID** et intègre plusieurs Design Patterns fondamentaux :
- **Strategy Pattern** : Utilisé dans `badwallet-api` pour gérer différents types de paiements (ex: `BillPaymentStrategy`, `MerchantPaymentStrategy`) via l'interface `PaymentStrategy`.
- **Factory Pattern** : Implémenté par `PaymentStrategyFactory` pour instancier dynamiquement la bonne stratégie de paiement en fonction du fournisseur (ISM, SENELEC, etc.).
- **Proxy Pattern** : L'API `badwallet-api` agit comme un proxy (via `ExternalFactureController` et `RestTemplate`) pour transférer les requêtes de consultation de factures vers le `payment-service`.
- **MVC (Model-View-Controller)** : Séparation stricte des responsabilités (Controllers sans logique métier, Services, Repositories).
- **DTO Pattern** : Isolement complet entre les entités de la base de données et les données exposées sur l'API (`WalletResponse`, `TransactionResponse`, etc.).

## 🚀 Comment lancer le projet ?

### 1. Démarrer le Payment Service
Ouvrez un terminal dans le dossier `payment-service` et exécutez :
```bash
cd payment-service
mvn spring-boot:run
```
*(Le service démarrera sur le port 8081 et injectera automatiquement des fausses factures pour les tests).*

### 2. Démarrer le BadWallet API
Ouvrez un second terminal dans le dossier `badwallet-api` et exécutez :
```bash
cd badwallet-api
mvn spring-boot:run
```
*(Le service démarrera sur le port 8080).*

## 🧪 Tests & Endpoints
À la racine du projet, vous trouverez le fichier **`badwallet-api.http`**. 
Il contient tous les jeux d'essais et les appels HTTP prêts à être exécutés via l'extension **REST Client** de VS Code.

- **Partie 1** : Gestion des wallets, transactions (frais de 1% max 5000 sur les retraits), et paiements.
- **Partie 2** : Consultation des factures impayées proxyées vers le port 8081.

## 🌿 Stratégie Git (Feature Branching)
Le développement a suivi un processus d'intégration continue strict :
- `main` : Branche de production.
- `develop` : Branche d'intégration.
- `feature/*` : Une branche éphémère créée pour chaque endpoint/fonctionnalité, mergée ensuite dans `develop`.
