# 🚀 Projet BadWallet - Mon Dashboard Fintech

Salut ! Bienvenue sur le dépôt de mon projet **BadWallet**. 
C'est le projet final que j'ai réalisé pour valider mon module de Design Patterns et d'Architecture Logicielle. L'idée était de ne pas juste faire un petit TP, mais de coder une "vraie" mini-application Fintech de A à Z (avec des microservices en Java et un front en Angular).

## 🎯 C'est quoi l'idée ?
L'application permet de gérer des portefeuilles électroniques (comme Wave ou Orange Money). Il y a deux côtés :
- **Pour l'Agent (ou Admin)** : Il peut créer de nouveaux comptes clients et faire des dépôts ou des retraits pour eux.
- **Pour le Client** : Il se connecte pour voir son solde, envoyer de l'argent à d'autres personnes, ou même payer ses factures (comme la Senelec ou l'école ISM).

## 🏗️ Comment c'est construit ?
J'ai découpé le projet en 3 morceaux :

1. **`badwallet-api` (Port 8080)** : 
   C'est le cerveau du projet. Il gère l'argent, les transferts et calcule les frais (je prends 1% sur les retraits au passage 😅). C'est aussi lui qui fait le pont avec le système des factures.
   
2. **`payment-service` (Port 8081)** : 
   C'est un faux système externe que j'ai créé pour simuler la Senelec ou Woyofal. Il génère de fausses factures pour qu'on puisse les payer depuis l'application principale.

3. **Le Frontend Angular (Port 4200)** : 
   L'interface visuelle. J'ai utilisé Angular 17 et TailwindCSS pour que ça soit propre et moderne.

## 🛠️ La stack technique
- **Backend** : Java 17, Spring Boot 3, PostgreSQL.
- **Frontend** : Angular 17, RxJS, TailwindCSS.

## 🚀 Lancer le projet sur votre machine

C'est assez simple, il faut juste ouvrir 3 terminaux.

**Étape 1 : Le service des factures**
```bash
cd payment-service
mvn clean spring-boot:run
```
*(Il va tourner sur le port 8081)*

**Étape 2 : L'API principale**
Ouvrez un autre terminal :
```bash
cd badwallet-api
mvn clean spring-boot:run
```
*(Elle tourne sur le port 8080)*

**Étape 3 : L'interface visuelle**
Dans un dernier terminal :
```bash
cd badwallet-web
npm install
npm start
```
*(Allez ensuite sur http://localhost:4200 dans votre navigateur)*

## 🔑 Pour tester
J'ai préparé des fausses données dans la base de données pour aller plus vite :
- Si vous voulez tester le paiement de factures, connectez-vous avec ce client : **`+221770000003`**
- Si vous voulez voir l'interface de l'agent de guichet, utilisez : **`+221770000000`**

Merci d'avoir jeté un œil à mon code !
