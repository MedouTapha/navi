package com.navi.education.service;

import com.navi.education.model.entity.*;
import com.navi.education.model.enums.BranchType;
import com.navi.education.model.enums.ClassType;
import com.navi.education.model.enums.ExpenseType;
import com.navi.education.model.enums.PaymentMethod;
import com.navi.education.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataInitializerService implements CommandLineRunner {

    private final BranchService branchService;
    private final BranchRepository branchRepository;
    private final EducationClassRepository classRepository;
    private final DonorRepository donorRepository;
    private final AnnualCommitmentRepository commitmentRepository;
    private final PaymentRepository paymentRepository;
    private final ExpenseRepository expenseRepository;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Initialisation des données de l'application...");

        // Initialiser les branches
        branchService.initializeBranches();

        // Vérifier si les données existent déjà
        if (classRepository.count() > 0) {
            log.info("Les données existent déjà, initialisation ignorée.");
            return;
        }

        // Créer des données de test
        initializeSampleData();

        log.info("Initialisation terminée avec succès.");
    }

    private void initializeSampleData() {
        log.info("Création des données de test...");

        // Récupérer les branches
        Branch nouakchott = branchRepository.findByType(BranchType.NOUAKCHOTT).orElseThrow();
        Branch nouadhibou = branchRepository.findByType(BranchType.NOUADHIBOU).orElseThrow();
        Branch rosso = branchRepository.findByType(BranchType.ROSSO).orElseThrow();
        Branch ksar = branchRepository.findByType(BranchType.KSAR).orElseThrow();

        // 1. Créer des classes
        log.info("Création des classes...");
        List<EducationClass> classes = createClasses(nouakchott, nouadhibou, rosso, ksar);

        // 2. Créer des donateurs
        log.info("Création des donateurs...");
        List<Donor> donors = createDonors();

        // 3. Créer des engagements annuels
        log.info("Création des engagements annuels...");
        List<AnnualCommitment> commitments = createCommitments(donors, classes);

        // 4. Créer des paiements
        log.info("Création des paiements...");
        createPayments(commitments);

        // 5. Créer quelques dépenses
        log.info("Création des dépenses...");
        createExpenses(classes);

        log.info("Données de test créées avec succès.");
    }

    private List<EducationClass> createClasses(Branch nouakchott, Branch nouadhibou, Branch rosso, Branch ksar) {
        List<EducationClass> classes = new ArrayList<>();
        LocalDate startDate = LocalDate.of(2025, 9, 1); // Début année scolaire 2025

        // Classes Nouakchott
        classes.add(createClass("Classe Récitation Niveau 1", "فصل التحفيظ المستوى 1",
                ClassType.RECITATION, nouakchott, new BigDecimal("50000"), startDate));
        classes.add(createClass("Classe Pédagogique Niveau 1", "فصل التعليم المستوى 1",
                ClassType.PEDAGOGICAL, nouakchott, new BigDecimal("45000"), startDate));
        classes.add(createClass("Classe Récitation Niveau 2", "فصل التحفيظ المستوى 2",
                ClassType.RECITATION, nouakchott, new BigDecimal("55000"), startDate));

        // Classes Nouadhibou
        classes.add(createClass("Classe Récitation Nouadhibou", "فصل التحفيظ نواذيبو",
                ClassType.RECITATION, nouadhibou, new BigDecimal("40000"), startDate));
        classes.add(createClass("Classe Pédagogique Nouadhibou", "فصل التعليم نواذيبو",
                ClassType.PEDAGOGICAL, nouadhibou, new BigDecimal("38000"), startDate));

        // Classes Rosso
        classes.add(createClass("Classe Récitation Rosso", "فصل التحفيظ روصو",
                ClassType.RECITATION, rosso, new BigDecimal("35000"), startDate));
        classes.add(createClass("Classe Pédagogique Rosso", "فصل التعليم روصو",
                ClassType.PEDAGOGICAL, rosso, new BigDecimal("32000"), startDate));

        // Classes Ksar
        classes.add(createClass("Classe Récitation Ksar", "فصل التحفيظ كصر",
                ClassType.RECITATION, ksar, new BigDecimal("30000"), startDate));

        return classes;
    }

    private EducationClass createClass(String nameFr, String nameAr, ClassType type,
                                       Branch branch, BigDecimal monthlyAmount, LocalDate startDate) {
        EducationClass educationClass = EducationClass.builder()
                .nameFr(nameFr)
                .nameAr(nameAr)
                .classType(type)
                .branch(branch)
                .monthlyFixedAmount(monthlyAmount)
                .startDate(startDate)
                .active(true)
                .description("Classe créée automatiquement lors de l'initialisation")
                .build();
        return classRepository.save(educationClass);
    }

    private List<Donor> createDonors() {
        List<Donor> donors = new ArrayList<>();

        donors.add(createDonor("Ahmed", "Ould Mohamed", "22123456", "ahmed.mohamed@example.mr",
                "Donateur régulier depuis 2023"));
        donors.add(createDonor("Fatima", "Mint Ahmed", "22234567", "fatima.ahmed@example.mr",
                "Soutient les programmes d'éducation"));
        donors.add(createDonor("Mohamed", "Ould Ali", "22345678", null,
                "Engagement annuel pour classe de récitation"));
        donors.add(createDonor("Mariem", "Mint Sidi", "22456789", "mariem.sidi@example.mr",
                "Préfère les paiements via Bankily"));
        donors.add(createDonor("Sidi", "Ould Brahim", "22567890", null,
                "Donateur pour plusieurs classes"));
        donors.add(createDonor("Aissata", "Mint Cheikh", "22678901", "aissata.cheikh@example.mr",
                "Soutient les classes pédagogiques"));

        return donors;
    }

    private Donor createDonor(String firstName, String lastName, String telephone,
                             String email, String notes) {
        Donor donor = Donor.builder()
                .firstName(firstName)
                .lastName(lastName)
                .telephone(telephone)
                .email(email)
                .notes(notes)
                .build();
        return donorRepository.save(donor);
    }

    private List<AnnualCommitment> createCommitments(List<Donor> donors, List<EducationClass> classes) {
        List<AnnualCommitment> commitments = new ArrayList<>();
        LocalDate commitmentDate = LocalDate.of(2025, 9, 15);

        // Donateur 1 -> Classe 1
        commitments.add(createCommitment(donors.get(0), classes.get(0),
                new BigDecimal("600000"), commitmentDate, "Engagement annuel complet"));

        // Donateur 2 -> Classe 2
        commitments.add(createCommitment(donors.get(1), classes.get(1),
                new BigDecimal("540000"), commitmentDate, "Paiements mensuels"));

        // Donateur 3 -> Classe 1 et Classe 3
        commitments.add(createCommitment(donors.get(2), classes.get(0),
                new BigDecimal("300000"), commitmentDate, "Contribution partielle"));
        commitments.add(createCommitment(donors.get(2), classes.get(2),
                new BigDecimal("660000"), commitmentDate, "Engagement pour niveau 2"));

        // Donateur 4 -> Classe 4
        commitments.add(createCommitment(donors.get(3), classes.get(3),
                new BigDecimal("480000"), commitmentDate, "Classe Nouadhibou"));

        // Donateur 5 -> Plusieurs classes
        commitments.add(createCommitment(donors.get(4), classes.get(4),
                new BigDecimal("456000"), commitmentDate, "Pédagogique Nouadhibou"));
        commitments.add(createCommitment(donors.get(4), classes.get(5),
                new BigDecimal("420000"), commitmentDate, "Récitation Rosso"));

        // Donateur 6 -> Classe 6 et 7
        commitments.add(createCommitment(donors.get(5), classes.get(6),
                new BigDecimal("384000"), commitmentDate, "Pédagogique Rosso"));

        return commitments;
    }

    private AnnualCommitment createCommitment(Donor donor, EducationClass educationClass,
                                              BigDecimal annualAmount, LocalDate commitmentDate, String notes) {
        AnnualCommitment commitment = AnnualCommitment.builder()
                .donor(donor)
                .educationClass(educationClass)
                .annualAmount(annualAmount)
                .commitmentDate(commitmentDate)
                .notes(notes)
                .active(true)
                .build();
        return commitmentRepository.save(commitment);
    }

    private void createPayments(List<AnnualCommitment> commitments) {
        LocalDate paymentDate = LocalDate.of(2025, 10, 1);

        // Paiements pour engagement 1 (complet en une fois)
        createPayment(commitments.get(0), new BigDecimal("600000"), paymentDate,
                PaymentMethod.BANKILY, "Paiement annuel complet", "BKY-2025-001");

        // Paiements pour engagement 2 (3 paiements mensuels)
        createPayment(commitments.get(1), new BigDecimal("180000"), paymentDate,
                PaymentMethod.SEDAD, "Paiement 1/3", "SDD-2025-001");
        createPayment(commitments.get(1), new BigDecimal("180000"), paymentDate.plusMonths(1),
                PaymentMethod.SEDAD, "Paiement 2/3", "SDD-2025-002");
        createPayment(commitments.get(1), new BigDecimal("180000"), paymentDate.plusMonths(2),
                PaymentMethod.SEDAD, "Paiement 3/3", "SDD-2025-003");

        // Paiements pour engagement 3 (partiel)
        createPayment(commitments.get(2), new BigDecimal("150000"), paymentDate,
                PaymentMethod.CASH, "Premier versement", null);
        createPayment(commitments.get(2), new BigDecimal("150000"), paymentDate.plusMonths(1),
                PaymentMethod.CASH, "Deuxième versement", null);

        // Paiements pour engagement 4
        createPayment(commitments.get(3), new BigDecimal("240000"), paymentDate,
                PaymentMethod.BANKILY, "Paiement 50%", "BKY-2025-002");
        createPayment(commitments.get(3), new BigDecimal("240000"), paymentDate.plusMonths(2),
                PaymentMethod.BANKILY, "Solde 50%", "BKY-2025-003");

        // Paiements pour engagement 5
        createPayment(commitments.get(4), new BigDecimal("228000"), paymentDate,
                PaymentMethod.MASRVI, "Acompte 50%", "MSR-2025-001");

        // Paiements pour engagement 6
        createPayment(commitments.get(5), new BigDecimal("420000"), paymentDate,
                PaymentMethod.BANKILY, "Paiement intégral", "BKY-2025-004");

        // Paiements pour engagement 7
        createPayment(commitments.get(6), new BigDecimal("192000"), paymentDate,
                PaymentMethod.SEDAD, "Acompte 50%", "SDD-2025-004");
    }

    private void createPayment(AnnualCommitment commitment, BigDecimal amount,
                              LocalDate paymentDate, PaymentMethod method, String comment, String receiptNumber) {
        Payment payment = Payment.builder()
                .commitment(commitment)
                .amount(amount)
                .paymentDate(paymentDate)
                .paymentMethod(method)
                .comment(comment)
                .receiptNumber(receiptNumber)
                .build();
        paymentRepository.save(payment);
    }

    private void createExpenses(List<EducationClass> classes) {
        log.info("Création des dépenses fixes annuelles pour chaque classe...");

        // Créer une dépense FIXED pour chaque classe (dépense annuelle fixe)
        for (EducationClass educationClass : classes) {
            // Calculer le montant annuel : monthlyFixedAmount × 12
            BigDecimal annualFixedAmount = educationClass.getMonthlyFixedAmount().multiply(new BigDecimal("12"));

            // Créer la dépense fixe à la date de début de la classe
            createExpense(educationClass, ExpenseType.FIXED, annualFixedAmount,
                    educationClass.getStartDate(),
                    "المصروفات الثابتة السنوية للفصل - Dépense fixe annuelle de la classe");

            log.info("Dépense FIXED créée pour {} : {} أوقية",
                    educationClass.getNameAr(), annualFixedAmount);
        }

        // Créer quelques dépenses EXTRA comme exemples
        log.info("Création de quelques dépenses exceptionnelles...");
        LocalDate extraExpenseDate = LocalDate.now().withDayOfMonth(5);

        createExpense(classes.get(0), ExpenseType.EXTRA, new BigDecimal("5000"),
                extraExpenseDate, "فواتير الكهرباء والماء - Électricité et eau");

        createExpense(classes.get(4), ExpenseType.EXTRA, new BigDecimal("8000"),
                extraExpenseDate, "إصلاح الطاولات - Réparation des tables");

        createExpense(classes.get(1), ExpenseType.EXTRA, new BigDecimal("12000"),
                extraExpenseDate, "شراء اللوازم المدرسية - Achat de fournitures scolaires");
    }

    private void createExpense(EducationClass educationClass, ExpenseType expenseType,
                              BigDecimal amount, LocalDate expenseDate, String description) {
        Expense expense = Expense.builder()
                .educationClass(educationClass)
                .expenseType(expenseType)
                .amount(amount)
                .expenseDate(expenseDate)
                .description(description)
                .build();
        expenseRepository.save(expense);
    }
}
