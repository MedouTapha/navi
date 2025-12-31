# 📡 Exemples d'API REST

Ce document contient des exemples d'appels API pour tester toutes les fonctionnalités de Navi.

## Variables

```bash
BASE_URL="http://localhost:8080/api"
```

## 🏢 Branches

### Lister toutes les branches
```bash
curl -X GET "${BASE_URL}/branches"
```

### Obtenir une branche par ID
```bash
curl -X GET "${BASE_URL}/branches/1"
```

### Obtenir une branche par type
```bash
curl -X GET "${BASE_URL}/branches/type/NOUAKCHOTT"
```

## 📚 Classes

### Lister toutes les classes
```bash
curl -X GET "${BASE_URL}/classes"
```

### Filtrer les classes par branche
```bash
curl -X GET "${BASE_URL}/classes?branchId=1"
```

### Filtrer les classes par type
```bash
curl -X GET "${BASE_URL}/classes?classType=RECITATION"
```

### Filtrer par branche et type
```bash
curl -X GET "${BASE_URL}/classes?branchId=1&classType=RECITATION"
```

### Obtenir une classe par ID
```bash
curl -X GET "${BASE_URL}/classes/1"
```

### Créer une nouvelle classe
```bash
curl -X POST "${BASE_URL}/classes" \
  -H "Content-Type: application/json" \
  -d '{
    "nameFr": "Classe Al-Qur'\''an",
    "nameAr": "صف القرآن",
    "classType": "RECITATION",
    "branchId": 1,
    "monthlyFixedAmount": 50000,
    "startDate": "2024-01-01",
    "active": true,
    "description": "Classe de récitation du Coran pour débutants"
  }'
```

### Créer une classe pédagogique
```bash
curl -X POST "${BASE_URL}/classes" \
  -H "Content-Type: application/json" \
  -d '{
    "nameFr": "Classe Mathématiques",
    "nameAr": "صف الرياضيات",
    "classType": "PEDAGOGICAL",
    "branchId": 1,
    "monthlyFixedAmount": 75000,
    "startDate": "2024-01-15",
    "active": true
  }'
```

### Modifier une classe
```bash
curl -X PUT "${BASE_URL}/classes/1" \
  -H "Content-Type: application/json" \
  -d '{
    "monthlyFixedAmount": 55000,
    "description": "Montant mis à jour"
  }'
```

### Supprimer une classe
```bash
curl -X DELETE "${BASE_URL}/classes/1"
```

## 💰 Dépenses

### Créer une dépense exceptionnelle
```bash
curl -X POST "${BASE_URL}/expenses/extra" \
  -H "Content-Type: application/json" \
  -d '{
    "classId": 1,
    "amount": 25000,
    "expenseDate": "2024-03-15",
    "description": "Achat de fournitures scolaires"
  }'
```

### Lister les dépenses d'une classe
```bash
curl -X GET "${BASE_URL}/expenses?classId=1"
```

### Filtrer par type de dépense
```bash
curl -X GET "${BASE_URL}/expenses?classId=1&type=EXTRA"
```

### Filtrer par année financière
```bash
curl -X GET "${BASE_URL}/expenses?classId=1&financialYear=1"
```

### Filtrer par période
```bash
curl -X GET "${BASE_URL}/expenses?classId=1&startDate=2024-01-01&endDate=2024-12-31"
```

### Obtenir une dépense par ID
```bash
curl -X GET "${BASE_URL}/expenses/1"
```

### Supprimer une dépense exceptionnelle
```bash
curl -X DELETE "${BASE_URL}/expenses/1"
```

## 🤝 Donateurs

### Lister tous les donateurs
```bash
curl -X GET "${BASE_URL}/donors"
```

### Rechercher un donateur
```bash
curl -X GET "${BASE_URL}/donors?search=Mohamed"
```

### Obtenir un donateur par ID
```bash
curl -X GET "${BASE_URL}/donors/1"
```

### Créer un donateur
```bash
curl -X POST "${BASE_URL}/donors" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Mohamed",
    "lastName": "Ould Ahmed",
    "telephone": "+22222123456",
    "email": "mohamed@example.com",
    "notes": "Donateur régulier depuis 2023"
  }'
```

### Créer plusieurs donateurs
```bash
# Donateur 2
curl -X POST "${BASE_URL}/donors" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Fatima",
    "lastName": "Mint Mohamed",
    "telephone": "+22222234567",
    "email": "fatima@example.com"
  }'

# Donateur 3
curl -X POST "${BASE_URL}/donors" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Ahmed",
    "lastName": "Ould Salem",
    "telephone": "+22222345678"
  }'
```

### Modifier un donateur
```bash
curl -X PUT "${BASE_URL}/donors/1" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "nouveau.email@example.com",
    "notes": "Informations mises à jour"
  }'
```

### Supprimer un donateur
```bash
curl -X DELETE "${BASE_URL}/donors/1"
```

## 📋 Engagements Annuels

### Lister tous les engagements
```bash
curl -X GET "${BASE_URL}/commitments"
```

### Lister les engagements d'un donateur
```bash
curl -X GET "${BASE_URL}/commitments?donorId=1"
```

### Lister les engagements d'une classe
```bash
curl -X GET "${BASE_URL}/commitments?classId=1"
```

### Lister les engagements d'une classe pour une année financière
```bash
curl -X GET "${BASE_URL}/commitments?classId=1&financialYear=1"
```

### Obtenir un engagement par ID
```bash
curl -X GET "${BASE_URL}/commitments/1"
```

### Créer un engagement annuel
```bash
curl -X POST "${BASE_URL}/commitments" \
  -H "Content-Type: application/json" \
  -d '{
    "donorId": 1,
    "classId": 1,
    "annualAmount": 600000,
    "commitmentDate": "2024-01-15",
    "active": true,
    "notes": "Paiement prévu en 3 fois"
  }'
```

### Créer plusieurs engagements
```bash
# Engagement 2
curl -X POST "${BASE_URL}/commitments" \
  -H "Content-Type: application/json" \
  -d '{
    "donorId": 2,
    "classId": 1,
    "annualAmount": 300000,
    "commitmentDate": "2024-02-01",
    "active": true
  }'

# Engagement 3 (autre classe)
curl -X POST "${BASE_URL}/commitments" \
  -H "Content-Type: application/json" \
  -d '{
    "donorId": 1,
    "classId": 2,
    "annualAmount": 500000,
    "commitmentDate": "2024-01-20",
    "active": true
  }'
```

### Modifier un engagement
```bash
curl -X PUT "${BASE_URL}/commitments/1" \
  -H "Content-Type: application/json" \
  -d '{
    "annualAmount": 650000,
    "notes": "Montant augmenté à la demande du donateur"
  }'
```

### Supprimer un engagement
```bash
curl -X DELETE "${BASE_URL}/commitments/1"
```

## 💳 Paiements

### Lister tous les paiements
```bash
curl -X GET "${BASE_URL}/payments"
```

### Lister les paiements d'un engagement
```bash
curl -X GET "${BASE_URL}/payments?commitmentId=1"
```

### Lister les paiements d'un donateur
```bash
curl -X GET "${BASE_URL}/payments?donorId=1"
```

### Lister les paiements d'une classe
```bash
curl -X GET "${BASE_URL}/payments?classId=1"
```

### Obtenir un paiement par ID
```bash
curl -X GET "${BASE_URL}/payments/1"
```

### Enregistrer un paiement complet
```bash
curl -X POST "${BASE_URL}/payments" \
  -H "Content-Type: application/json" \
  -d '{
    "commitmentId": 1,
    "amount": 600000,
    "paymentDate": "2024-02-01",
    "paymentMethod": "BANKILY",
    "comment": "Paiement complet de l'\''engagement annuel",
    "receiptNumber": "REC-2024-001"
  }'
```

### Enregistrer un paiement partiel
```bash
# Premier paiement partiel
curl -X POST "${BASE_URL}/payments" \
  -H "Content-Type: application/json" \
  -d '{
    "commitmentId": 1,
    "amount": 200000,
    "paymentDate": "2024-02-01",
    "paymentMethod": "BANKILY",
    "comment": "1er versement sur 3",
    "receiptNumber": "REC-2024-002"
  }'

# Deuxième paiement partiel
curl -X POST "${BASE_URL}/payments" \
  -H "Content-Type: application/json" \
  -d '{
    "commitmentId": 1,
    "amount": 200000,
    "paymentDate": "2024-03-01",
    "paymentMethod": "CASH",
    "comment": "2ème versement sur 3",
    "receiptNumber": "REC-2024-003"
  }'

# Troisième paiement partiel
curl -X POST "${BASE_URL}/payments" \
  -H "Content-Type: application/json" \
  -d '{
    "commitmentId": 1,
    "amount": 200000,
    "paymentDate": "2024-04-01",
    "paymentMethod": "SEDAD",
    "comment": "Dernier versement",
    "receiptNumber": "REC-2024-004"
  }'
```

### Paiements avec différents moyens
```bash
# Paiement Bankily
curl -X POST "${BASE_URL}/payments" \
  -H "Content-Type: application/json" \
  -d '{
    "commitmentId": 2,
    "amount": 150000,
    "paymentDate": "2024-03-15",
    "paymentMethod": "BANKILY"
  }'

# Paiement Sedad
curl -X POST "${BASE_URL}/payments" \
  -H "Content-Type: application/json" \
  -d '{
    "commitmentId": 2,
    "amount": 150000,
    "paymentDate": "2024-04-15",
    "paymentMethod": "SEDAD"
  }'

# Paiement Cash
curl -X POST "${BASE_URL}/payments" \
  -H "Content-Type: application/json" \
  -d '{
    "commitmentId": 3,
    "amount": 250000,
    "paymentDate": "2024-03-10",
    "paymentMethod": "CASH"
  }'

# Paiement Masrvi
curl -X POST "${BASE_URL}/payments" \
  -H "Content-Type: application/json" \
  -d '{
    "commitmentId": 3,
    "amount": 250000,
    "paymentDate": "2024-04-10",
    "paymentMethod": "MASRVI"
  }'
```

### Supprimer un paiement
```bash
curl -X DELETE "${BASE_URL}/payments/1"
```

## 📄 Rapports

### Générer un rapport de paiement pour un donateur (HTML)
```bash
curl -X GET "${BASE_URL}/reports/donor/1/payments" > rapport_donateur_1.html
```

### Ouvrir le rapport dans un navigateur
```bash
curl -X GET "${BASE_URL}/reports/donor/1/payments" > rapport.html && xdg-open rapport.html
```

## 🔄 Scénario Complet de Test

Voici un scénario complet pour tester toutes les fonctionnalités:

```bash
#!/bin/bash

BASE_URL="http://localhost:8080/api"

echo "=== 1. Création d'une classe ==="
CLASS_ID=$(curl -s -X POST "${BASE_URL}/classes" \
  -H "Content-Type: application/json" \
  -d '{
    "nameFr": "Classe Test",
    "nameAr": "صف التجربة",
    "classType": "RECITATION",
    "branchId": 1,
    "monthlyFixedAmount": 50000,
    "startDate": "2024-01-01"
  }' | jq -r '.id')
echo "Classe créée avec ID: $CLASS_ID"

echo "=== 2. Création d'un donateur ==="
DONOR_ID=$(curl -s -X POST "${BASE_URL}/donors" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Test",
    "lastName": "Donateur",
    "telephone": "+22222999999",
    "email": "test@example.com"
  }' | jq -r '.id')
echo "Donateur créé avec ID: $DONOR_ID"

echo "=== 3. Création d'un engagement ==="
COMMITMENT_ID=$(curl -s -X POST "${BASE_URL}/commitments" \
  -H "Content-Type: application/json" \
  -d "{
    \"donorId\": $DONOR_ID,
    \"classId\": $CLASS_ID,
    \"annualAmount\": 600000,
    \"commitmentDate\": \"2024-01-15\"
  }" | jq -r '.id')
echo "Engagement créé avec ID: $COMMITMENT_ID"

echo "=== 4. Enregistrement de paiements ==="
curl -s -X POST "${BASE_URL}/payments" \
  -H "Content-Type: application/json" \
  -d "{
    \"commitmentId\": $COMMITMENT_ID,
    \"amount\": 300000,
    \"paymentDate\": \"2024-02-01\",
    \"paymentMethod\": \"BANKILY\"
  }" | jq '.'

echo "=== 5. Création d'une dépense exceptionnelle ==="
curl -s -X POST "${BASE_URL}/expenses/extra" \
  -H "Content-Type: application/json" \
  -d "{
    \"classId\": $CLASS_ID,
    \"amount\": 15000,
    \"expenseDate\": \"2024-03-01\",
    \"description\": \"Dépense de test\"
  }" | jq '.'

echo "=== 6. Consultation du rapport du donateur ==="
curl -X GET "${BASE_URL}/reports/donor/${DONOR_ID}/payments" > rapport_test.html
echo "Rapport généré: rapport_test.html"

echo "=== Test terminé ==="
```

## 🔍 Validation des Données

### Tentative de création avec données invalides

#### Classe avec montant négatif (doit échouer)
```bash
curl -X POST "${BASE_URL}/classes" \
  -H "Content-Type: application/json" \
  -d '{
    "nameFr": "Classe Test",
    "nameAr": "Test",
    "classType": "RECITATION",
    "branchId": 1,
    "monthlyFixedAmount": -1000,
    "startDate": "2024-01-01"
  }'
```

#### Engagement dupliqué (doit échouer)
```bash
# Créer un engagement
curl -X POST "${BASE_URL}/commitments" \
  -H "Content-Type: application/json" \
  -d '{
    "donorId": 1,
    "classId": 1,
    "annualAmount": 600000,
    "commitmentDate": "2024-01-15"
  }'

# Tenter de créer le même engagement (doit échouer)
curl -X POST "${BASE_URL}/commitments" \
  -H "Content-Type: application/json" \
  -d '{
    "donorId": 1,
    "classId": 1,
    "annualAmount": 500000,
    "commitmentDate": "2024-02-01"
  }'
```

#### Paiement dépassant le solde (doit échouer)
```bash
curl -X POST "${BASE_URL}/payments" \
  -H "Content-Type: application/json" \
  -d '{
    "commitmentId": 1,
    "amount": 999999999,
    "paymentDate": "2024-02-01",
    "paymentMethod": "CASH"
  }'
```

## 📊 Statistiques et Analyses

### Vue d'ensemble d'une branche
```bash
curl -X GET "${BASE_URL}/branches/1" | jq '{
  nom: .nameFr,
  totalClasses: .totalClasses,
  depensesMensuelles: .monthlyExpenses
}'
```

### Détails financiers d'une classe
```bash
curl -X GET "${BASE_URL}/classes/1" | jq '{
  nom: .nameFr,
  montantMensuel: .monthlyFixedAmount,
  totalDepenses: .totalExpenses,
  totalDonations: .totalDonations,
  solde: (.totalDonations - .totalExpenses)
}'
```

### Statistiques d'un donateur
```bash
curl -X GET "${BASE_URL}/donors/1" | jq '{
  nom: .fullName,
  totalEngagements: .totalCommitments,
  totalPaye: .totalPaid,
  soldeRestant: .remainingBalance
}'
```
