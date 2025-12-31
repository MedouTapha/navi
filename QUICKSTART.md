# 🚀 Guide de Démarrage Rapide

## Installation en 5 minutes

### Prérequis
- Java 17+
- Maven 3.6+

### Étapes

1. **Cloner et accéder au projet**
```bash
cd navi
```

2. **Compiler l'application**
```bash
mvn clean install
```

3. **Lancer l'application**
```bash
mvn spring-boot:run
```

4. **Accéder à l'interface**
Ouvrez votre navigateur: http://localhost:8080

## Premiers Pas

### Via l'Interface Web

1. **Voir les branches** → http://localhost:8080/branches
2. **Créer une classe** → Utiliser l'API REST (voir ci-dessous)
3. **Créer un donateur** → Utiliser l'API REST
4. **Créer un engagement** → Utiliser l'API REST
5. **Enregistrer un paiement** → Utiliser l'API REST

### Via l'API REST

#### 1. Créer une classe
```bash
curl -X POST http://localhost:8080/api/classes \
  -H "Content-Type: application/json" \
  -d '{
    "nameFr": "Classe Al-Qur'\''an",
    "nameAr": "صف القرآن",
    "classType": "RECITATION",
    "branchId": 1,
    "monthlyFixedAmount": 50000,
    "startDate": "2024-01-01"
  }'
```

#### 2. Créer un donateur
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

#### 3. Créer un engagement
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

#### 4. Enregistrer un paiement
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

## Pages Principales

- **Tableau de Bord**: http://localhost:8080/dashboard
- **Branches**: http://localhost:8080/branches
- **Classes**: http://localhost:8080/classes
- **Donateurs**: http://localhost:8080/donors
- **Console H2**: http://localhost:8080/h2-console

## Fonctionnalités Automatiques

### Dépenses Mensuelles
Le système génère automatiquement les dépenses mensuelles fixes le 1er de chaque mois à 00:01.

Pour tester manuellement:
```java
// Appeler via un endpoint personnalisé ou directement dans le code
scheduledExpenseService.generateMonthlyExpensesManually();
```

## Documentation Complète

- **README.md** - Documentation complète de l'application
- **API_EXAMPLES.md** - Exemples d'utilisation de l'API
- **DEPLOYMENT.md** - Guide de déploiement en production

## Support

Pour toute question, consultez d'abord:
1. README.md pour la documentation générale
2. API_EXAMPLES.md pour des exemples d'API
3. Les logs de l'application

## Arrêter l'Application

Appuyez sur `Ctrl+C` dans le terminal où l'application est lancée.

## Prochaines Étapes

1. Créer vos branches, classes et donateurs
2. Configurer les engagements annuels
3. Enregistrer les paiements
4. Générer des rapports
5. Consulter les statistiques dans le tableau de bord

Bon démarrage avec Navi! 🎓
