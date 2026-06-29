# 📄 Rapport Technique - Projet BadWallet

Ce document résume les choix architecturaux majeurs pris pour développer BadWallet, ainsi que les défis techniques rencontrés lors de l'intégration.

## 1. Pourquoi des Microservices ?

Au lieu de concevoir une application monolithique, le projet a été séparé en deux services : `badwallet-api` et `payment-service`. 
L'objectif est d'assurer la résilience du portefeuille électronique. Si le système externe de la Senelec (`payment-service`) tombe en panne, l'application principale (`badwallet-api`) continue de fonctionner pour les opérations de base (transferts, dépôts). L'API principale sert de "Proxy" : le frontend Angular interroge uniquement le port 8080, qui se charge de relayer la requête vers le port 8081 via un `RestTemplate`.

## 2. Les Design Patterns utilisés

Plusieurs patrons de conception ont été implémentés pour garantir un code maintenable et extensible :

- **Strategy & Factory** :
  Plutôt que d'utiliser des structures conditionnelles complexes pour gérer les différents types de paiements (factures vs marchands), une interface `PaymentStrategy` a été créée. Elle est implémentée par `BillPaymentStrategy` et `MerchantPaymentStrategy`. Une classe `PaymentStrategyFactory` se charge de sélectionner dynamiquement la stratégie appropriée à l'exécution en fonction de la requête du client.

- **Le DTO Pattern** :
  Les entités de base de données (`Wallet`, `Transaction`) ne sont jamais exposées directement au frontend. Le transfert de données se fait via des DTO (Data Transfer Object) et des Mappers, garantissant la sécurité et le contrôle strict des informations renvoyées.

## 3. Les défis techniques rencontrés

### A. La politique CORS
La communication entre le frontend Angular (port 4200) et l'API Spring Boot (port 8080) était initialement bloquée par le navigateur. Une classe `CorsConfig` a été ajoutée côté backend pour autoriser explicitement les requêtes cross-origin depuis le client web.

### B. L'encodage du symbole "+" dans les URL
Lors des requêtes GET utilisant un numéro de téléphone international (ex: `+221770000003`) dans l'URL, Tomcat interprétait le symbole `+` comme un espace (" "). Le backend cherchait alors le client ` 221770000003`, générant une erreur réseau 500 (No static resource).
**Solution** : Les services HTTP dans Angular ont été modifiés pour utiliser `encodeURIComponent()`, transformant le `+` en `%2B` avant l'envoi sur le réseau.

### C. La gestion de l'état Frontend
Pour éviter les requêtes API redondantes et offrir une navigation fluide sans rechargement de page, l'API des **Signals** d'Angular (version 16+) a été utilisée. Un store léger (`balance.store.ts`) maintient l'état global (solde, informations de session, rôle Agent/Client).
