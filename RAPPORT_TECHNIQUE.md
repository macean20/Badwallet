# 📄 Rapport Technique - Mon Projet BadWallet

Ce document résume les grands choix que j'ai faits pour développer BadWallet, ainsi que les galères que j'ai rencontrées (et comment je m'en suis sorti !).

## 1. Pourquoi des Microservices ?

Au début, j'aurais pu tout mettre dans une seule application. Mais j'ai décidé de séparer le projet en deux vrais services (`badwallet-api` et `payment-service`). 
**L'idée ?** Le portefeuille électronique ne doit pas planter si jamais le système externe de la Senelec tombe en panne. Le `badwallet-api` sert de "Proxy" : le frontend d'Angular ne lui parle qu'à lui, et c'est lui qui va discrètement chercher les factures sur le deuxième service (qui tourne sur le port 8081) grâce à un `RestTemplate`. C'est plus professionnel et plus résilient.

## 2. Les Design Patterns utilisés

Pour que le code soit propre et validé pour l'examen, j'ai mis en place quelques patrons de conception :

- **Strategy & Factory** :
  C'est ma plus grande fierté sur ce projet ! Au lieu de faire des gros `if (service == "SENELEC") ... else if (service == "ISM") ...` pour gérer les paiements, j'ai créé une interface `PaymentStrategy`. 
  Ensuite, j'ai fait une classe pour les factures (`BillPaymentStrategy`) et une autre pour les marchands normaux (`MerchantPaymentStrategy`). C'est la classe `PaymentStrategyFactory` qui choisit toute seule la bonne stratégie en fonction de ce que le client veut payer. C'est beaucoup plus facile à modifier ou à étendre par la suite si on ajoute de nouveaux fournisseurs.

- **Le DTO Pattern** :
  Je n'envoie jamais mes vraies entités de base de données (`Wallet`, `Transaction`) directement au frontend. Je passe toujours par des DTO (Data Transfer Object) et des Mappers pour filtrer les données. Comme ça, c'est plus sécurisé et je ne montre que ce qui est strictement nécessaire.

## 3. Les galères et comment je les ai réglées

Pendant le développement, je suis tombé sur quelques problèmes assez bloquants :

### A. L'erreur CORS (Bloqué par le navigateur)
Quand j'ai branché mon interface Angular (sur le port 4200) à mon API Spring Boot (sur le port 8080), le navigateur a tout bloqué par sécurité. J'ai dû rajouter une classe `CorsConfig` dans mon backend pour dire à Spring Boot "C'est bon, tu peux accepter les requêtes qui viennent de mon client Angular".

### B. Le bug de l'URL et du symbole "+"
Celle-là m'a fait perdre pas mal de temps. Mon numéro de client de test, c'est `+221770000003`. Quand Angular l'envoyait dans l'URL pour récupérer le solde, le serveur Tomcat comprenait le `+` comme un espace (" "). Du coup, le backend cherchait le client ` 221770000003` et renvoyait une erreur réseau 500 parce qu'il ne trouvait pas la bonne route.
**La solution** : J'ai dû modifier mes requêtes dans Angular en utilisant `encodeURIComponent()` pour transformer le `+` en `%2B` sur le réseau. Après ça, Spring Boot arrivait parfaitement à lire le numéro.

### C. Gérer l'état sans recharger la page
Je voulais que l'application soit super fluide. J'ai donc utilisé la nouvelle fonctionnalité d'Angular (les **Signals**). J'ai créé un tout petit store (`balance.store.ts`) qui garde en mémoire si l'utilisateur est un Agent ou un Client, et quel est son solde actuel. Ça m'a évité de faire des requêtes API inutiles à chaque fois qu'on change de page et l'expérience utilisateur est top.
