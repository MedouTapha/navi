# 🎓 Navi - Système de Gestion Éducative

Application Spring Boot pour la gestion des branches éducatives, classes, dépenses et donations en Mauritanie.

## 📋 Fonctionnalités

### EPIC 1 — Gestion des Branches
- ✅ Consulter la liste des branches (Nouakchott, Nouadhibou, Rosso, Ksar)
- ✅ Consulter le détail d'une branche avec statistiques
- ✅ Visualiser les classes rattachées à une branche

### EPIC 2 — Gestion des Classes
- ✅ Lister toutes les classes
- ✅ Filtrer les classes par branche et type (Récitation/Pédagogique)
- ✅ Créer une nouvelle classe
- ✅ Modifier une classe existante
- ✅ Consulter le détail d'une classe
- ✅ Support bilinguisme (Français/Arabe)

### EPIC 3 — Gestion des Dépenses
- ✅ Génération automatique des dépenses mensuelles fixes (le 1er de chaque mois)
- ✅ Gestion dynamique de l'année financière
- ✅ Ajouter/Supprimer des dépenses exceptionnelles
- ✅ Consulter les dépenses d'une classe
- ✅ Filtrer les dépenses par période/année financière

### EPIC 6 — Gestion des Donateurs
- ✅ Créer un donateur
- ✅ Consulter la liste des donateurs
- ✅ Consulter le détail d'un donateur
- ✅ Créer un engagement annuel
- ✅ Empêcher les doublons d'engagement
- ✅ Modifier un engagement annuel
- ✅ Enregistrer un paiement de donation
- ✅ Suivi automatique du solde
- ✅ Gérer les paiements partiels
- ✅ Visualiser les donations par classe
- ✅ Historique des donations

### EPIC 7 — Impressions
- ✅ Imprimer les rapports de paiement par donateur (HTML/PDF)

## 🏗️ Architecture

### Technologies
- **Backend**: Spring Boot 3.2.1
- **Base de données**: H2 (dev), PostgreSQL (production)
- **Frontend**: Thymeleaf, HTML5, CSS3 (Mobile-First)
- **Build**: Maven
- **Java**: 17

### Structure du Projet
```
navi/
├── src/main/java/com/navi/education/
│   ├── controller/          # REST Controllers
│   │   ├── BranchController.java
│   │   ├── EducationClassController.java
│   │   ├── ExpenseController.java
│   │   ├── DonorController.java
│   │   ├── AnnualCommitmentController.java
│   │   ├── PaymentController.java
│   │   ├── ReportController.java
│   │   └── web/            # Web View Controllers
│   ├── dto/
│   │   ├── request/        # DTOs de requête
│   │   └── response/       # DTOs de réponse
│   ├── exception/          # Gestion des exceptions
│   ├── model/
│   │   ├── entity/         # Entités JPA
│   │   └── enums/          # Énumérations
│   ├── repository/         # Repositories JPA
│   └── service/            # Logique métier
├── src/main/resources/
│   ├── templates/          # Templates Thymeleaf
│   │   ├── branches/
│   │   ├── classes/
│   │   ├── donors/
│   │   └── fragments/
│   └── application.properties
└── pom.xml
```

## 🚀 Installation et Démarrage

### Prérequis
- Java 17 ou supérieur
- Maven 3.6 ou supérieur

### Lancer l'application

1. **Cloner le projet**
```bash
git clone <repository-url>
cd navi
```

2. **Compiler le projet**
```bash
mvn clean install
```

3. **Lancer l'application**
```bash
mvn spring-boot:run
```

4. **Accéder à l'application**
- Interface Web: http://localhost:8080
- API REST: http://localhost:8080/api
- Console H2: http://localhost:8080/h2-console
  - URL JDBC: `jdbc:h2:mem:navidb`
  - Username: `sa`
  - Password: (vide)

## 📡 API REST

### Branches
```
GET    /api/branches              - Liste toutes les branches
GET    /api/branches/{id}         - Détails d'une branche
GET    /api/branches/type/{type}  - Branche par type
```

### Classes
```
GET    /api/classes                          - Liste toutes les classes
GET    /api/classes/{id}                     - Détails d'une classe
POST   /api/classes                          - Créer une classe
PUT    /api/classes/{id}                     - Modifier une classe
DELETE /api/classes/{id}                     - Supprimer une classe
GET    /api/classes?branchId=1               - Filtrer par branche
GET    /api/classes?classType=RECITATION     - Filtrer par type
```

### Dépenses
```
GET    /api/expenses?classId=1                  - Liste des dépenses d'une classe
POST   /api/expenses/extra                      - Créer une dépense exceptionnelle
DELETE /api/expenses/{id}                       - Supprimer une dépense exceptionnelle
GET    /api/expenses?classId=1&financialYear=1  - Filtrer par année financière
```

### Donateurs
```
GET    /api/donors           - Liste tous les donateurs
GET    /api/donors/{id}      - Détails d'un donateur
POST   /api/donors           - Créer un donateur
PUT    /api/donors/{id}      - Modifier un donateur
DELETE /api/donors/{id}      - Supprimer un donateur
GET    /api/donors?search=   - Rechercher un donateur
```

### Engagements
```
GET    /api/commitments                    - Liste tous les engagements
GET    /api/commitments/{id}               - Détails d'un engagement
POST   /api/commitments                    - Créer un engagement
PUT    /api/commitments/{id}               - Modifier un engagement
DELETE /api/commitments/{id}               - Supprimer un engagement
GET    /api/commitments?donorId=1          - Par donateur
GET    /api/commitments?classId=1          - Par classe
```

### Paiements
```
GET    /api/payments                   - Liste tous les paiements
GET    /api/payments/{id}              - Détails d'un paiement
POST   /api/payments                   - Créer un paiement
DELETE /api/payments/{id}              - Supprimer un paiement
GET    /api/payments?donorId=1         - Par donateur
GET    /api/payments?classId=1         - Par classe
```

### Rapports
```
GET    /api/reports/donor/{id}/payments  - Rapport de paiements d'un donateur (HTML)
```

## 📱 Interface Web

### Pages disponibles
- `/` - Redirection vers le tableau de bord
- `/dashboard` - Tableau de bord avec statistiques
- `/branches` - Liste des branches
- `/branches/{id}` - Détails d'une branche
- `/classes` - Liste des classes
- `/classes/{id}` - Détails d'une classe
- `/classes/new` - Formulaire nouvelle classe
- `/donors` - Liste des donateurs
- `/donors/{id}` - Détails d'un donateur
- `/donors/new` - Formulaire nouveau donateur

## 🔧 Configuration

### Base de données H2 (Développement)
```properties
spring.datasource.url=jdbc:h2:mem:navidb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
```

### Base de données PostgreSQL (Production)
Modifiez `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/navi
spring.datasource.username=votre_username
spring.datasource.password=votre_password
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

## ⏰ Tâches Planifiées

### Génération automatique des dépenses mensuelles
- **Quand**: Le 1er de chaque mois à 00:01
- **Fonction**: Crée automatiquement une dépense fixe mensuelle pour chaque classe active
- **Configuration**: `@Scheduled(cron = "0 1 0 1 * *")`

## 📊 Modèle de Données

### Entités Principales
- **Branch**: Branches éducatives (Nouakchott, Nouadhibou, Rosso, Ksar)
- **EducationClass**: Classes avec support bilinguisme
- **Expense**: Dépenses (fixes ou exceptionnelles)
- **Donor**: Donateurs
- **AnnualCommitment**: Engagements annuels
- **Payment**: Paiements des donations

### Relations
- Une Branche → Plusieurs Classes
- Une Classe → Plusieurs Dépenses
- Une Classe → Plusieurs Engagements
- Un Donateur → Plusieurs Engagements
- Un Engagement → Plusieurs Paiements

## 🔐 Validation

L'application utilise la validation Bean Validation (JSR-303) sur tous les DTOs de requête.

## 🐛 Gestion des Erreurs

Toutes les exceptions sont gérées centralement via `GlobalExceptionHandler`:
- 404: Ressource non trouvée
- 409: Conflit (ex: engagement dupliqué)
- 400: Requête invalide (validation)
- 500: Erreur interne du serveur

## 📝 Exemples d'utilisation

### Créer une classe
```bash
curl -X POST http://localhost:8080/api/classes \
  -H "Content-Type: application/json" \
  -d '{
    "nameFr": "Classe Al-Qur'an",
    "nameAr": "صف القرآن",
    "classType": "RECITATION",
    "branchId": 1,
    "monthlyFixedAmount": 50000,
    "startDate": "2024-01-01"
  }'
```

### Créer un donateur
```bash
curl -X POST http://localhost:8080/api/donors \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Mohamed",
    "lastName": "Ould Ahmed",
    "telephone": "+22222123456",
    "email": "mohamed@example.com"
  }'
```

### Créer un engagement
```bash
curl -X POST http://localhost:8080/api/commitments \
  -H "Content-Type: application/json" \
  -d '{
    "donorId": 1,
    "classId": 1,
    "annualAmount": 600000,
    "commitmentDate": "2024-01-15"
  }'
```

### Enregistrer un paiement
```bash
curl -X POST http://localhost:8080/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "commitmentId": 1,
    "amount": 200000,
    "paymentDate": "2024-02-01",
    "paymentMethod": "BANKILY",
    "receiptNumber": "REC-2024-001"
  }'
```

## 🌍 Support Multilingue

L'application supporte le français et l'arabe pour:
- Noms des branches
- Noms des classes
- Types de classes

## 📈 Fonctionnalités Avancées

### Calcul Dynamique de l'Année Financière
Chaque classe a sa propre année financière calculée dynamiquement à partir de sa date de début.

### Prévention des Doublons
Le système empêche la création de plusieurs engagements pour le même donateur, la même classe et la même année financière.

### Validation des Paiements
Le système vérifie que le montant d'un paiement ne dépasse pas le solde restant de l'engagement.

## 📄 Licence

© 2024 Navi Education Management System

## 👥 Contact

Pour toute question ou support, contactez l'équipe de développement.
