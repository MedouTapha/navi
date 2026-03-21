package com.navi.education.service;

import com.navi.education.model.entity.*;
import com.navi.education.model.enums.BranchType;
import com.navi.education.model.enums.ClassType;
import com.navi.education.model.enums.ExpenseType;
import com.navi.education.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataInitializerService implements CommandLineRunner {

    private final BranchService branchService;
    private final BranchRepository branchRepository;
    private final EducationClassRepository classRepository;
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

        initializeRealData();

        log.info("Initialisation terminée avec succès.");
    }

    @Transactional
    public void initializeRealData() {
        log.info("Création des fصول et dépenses de l'Institut Imam Nafi...");

        // ===================== دوز دوز =====================
        Branch douzDouz = getBranch(BranchType.DOUZ_DOUZ);
        EducationClass douzClass1 = createClass(
                "Mohamed Abdellah / Maysara", "محمد عبد الله / ميسرة",
                ClassType.RECITATION, douzDouz, bd("150000"), LocalDate.of(2026, 1, 1));
        createMonthlyExpenses(douzClass1, LocalDate.of(2026, 2, 1));
        createExpense(douzClass1, ExpenseType.EXTRA, bd("6000"), LocalDate.of(2026, 2, 1), "النقل");
        createExpense(douzClass1, ExpenseType.EXTRA, bd("70000"), LocalDate.of(2026, 2, 1), "التراويح");

        // ===================== فرع لمليكة =====================
        Branch lmilika = getBranch(BranchType.LMILIKA);
        EducationClass lmilikaClass1 = createClass(
                "Al-Chafi'i Mohamed", "الشافعي محمد",
                ClassType.RECITATION, lmilika, bd("100000"), LocalDate.of(2024, 6, 1));
        createMonthlyExpenses(lmilikaClass1, LocalDate.of(2026, 2, 1));
        createExpense(lmilikaClass1, ExpenseType.EXTRA, bd("20000"), LocalDate.of(2026, 2, 1), "النقل");

        // ===================== فرع آجوير =====================
        Branch ajouir = getBranch(BranchType.AJOUIR);

        EducationClass ajouirF1 = createClass(
                "Houzayfa - Fasl 1", "حذيفة - الفصل 1",
                ClassType.RECITATION, ajouir, bd("200000"), LocalDate.of(2023, 9, 1));
        EducationClass ajouirF2 = createClass(
                "Mohamadou / Al-Husayn - Fasl 2", "محمدو / الحسين - الفصل 2",
                ClassType.RECITATION, ajouir, bd("150000"), LocalDate.of(2023, 9, 1));
        EducationClass ajouirF3 = createClass(
                "Saleh Mohamed - Fasl 3", "صالح محمد - الفصل 3",
                ClassType.RECITATION, ajouir, bd("150000"), LocalDate.of(2023, 9, 1));
        EducationClass ajouirF4 = createClass(
                "Mohamed Salem - Fasl 4", "محمد سالم - الفصل 4",
                ClassType.RECITATION, ajouir, bd("100000"), LocalDate.of(2023, 9, 1));
        EducationClass ajouirF5 = createClass(
                "Yaacoub / Ishaq - Fasl 5", "يعقوب / إسحاق - الفصل 5",
                ClassType.RECITATION, ajouir, bd("100000"), LocalDate.of(2023, 9, 1));
        EducationClass ajouirF6 = createClass(
                "Afah / Bayat - Fasl 6", "أفاه / بيات - الفصل 6",
                ClassType.RECITATION, ajouir, bd("100000"), LocalDate.of(2023, 9, 1));
        EducationClass ajouirF7 = createClass(
                "Al-Sa'd - Fasl 7", "السعد - الفصل 7",
                ClassType.RECITATION, ajouir, bd("100000"), LocalDate.of(2023, 9, 1));
        EducationClass ajouirF8 = createClass(
                "Al-Mokhtar Ibrahim - Fasl 8", "المختار إبراهيم - الفصل 8",
                ClassType.RECITATION, ajouir, bd("100000"), LocalDate.of(2023, 9, 1));
        EducationClass ajouirBir = createClass(
                "Oustaz Al-Bir", "أستاذ البير",
                ClassType.RECITATION, ajouir, bd("150000"), LocalDate.of(2023, 9, 1));

        for (EducationClass c : new EducationClass[]{ajouirF1, ajouirF2, ajouirF3, ajouirF4,
                ajouirF5, ajouirF6, ajouirF7, ajouirF8, ajouirBir}) {
            createMonthlyExpenses(c, LocalDate.of(2026, 2, 1));
        }
        createExpense(ajouirF1, ExpenseType.EXTRA, bd("20000"), LocalDate.of(2026, 2, 1), "النقل");

        // ===================== فرع الربينة =====================
        Branch rabina = getBranch(BranchType.RABINA);
        EducationClass rabinaC1 = createClass(
                "Hamah Allah", "حماه الله",
                ClassType.RECITATION, rabina, bd("150000"), LocalDate.of(2023, 9, 4));
        EducationClass rabinaC2 = createClass(
                "Yahya", "يحيى",
                ClassType.RECITATION, rabina, bd("150000"), LocalDate.of(2023, 9, 4));
        createMonthlyExpenses(rabinaC1, LocalDate.of(2026, 2, 1));
        createMonthlyExpenses(rabinaC2, LocalDate.of(2026, 2, 1));
        createExpense(rabinaC1, ExpenseType.EXTRA, bd("33000"), LocalDate.of(2026, 2, 1), "النقل");

        // ===================== فرع أولاد اخطيرة =====================
        Branch ouladAkhtira = getBranch(BranchType.OULAD_AKHTIRA);
        EducationClass ouladC1 = createClass(
                "Lababa", "الأستاذة لبابة",
                ClassType.RECITATION, ouladAkhtira, bd("120000"), LocalDate.of(2023, 7, 13));
        createMonthlyExpenses(ouladC1, LocalDate.of(2026, 2, 1));

        // ===================== فرع إجغماجك - رحاب القرآن =====================
        Branch ijghmajak = getBranch(BranchType.IJGHMAJAK);
        EducationClass ijghC1 = createClass(
                "Oustaz Ijghmajak", "أستاذ الفرع",
                ClassType.RECITATION, ijghmajak, bd("100000"), LocalDate.of(2023, 7, 16));
        createMonthlyExpenses(ijghC1, LocalDate.of(2026, 2, 1));

        // ===================== فرع بالغربان 1 =====================
        Branch balgharban1 = getBranch(BranchType.BALGHARBAN_1);
        EducationClass balgharban1C1 = createClass(
                "Mohamadou Nini", "محمدو نيني",
                ClassType.RECITATION, balgharban1, bd("150000"), LocalDate.of(2025, 3, 1));
        EducationClass balgharban1C2 = createClass(
                "Mohamadou Mahfoudh", "محمدو محفوظ",
                ClassType.RECITATION, balgharban1, bd("100000"), LocalDate.of(2025, 3, 1));
        createMonthlyExpenses(balgharban1C1, LocalDate.of(2026, 2, 1));
        createMonthlyExpenses(balgharban1C2, LocalDate.of(2026, 2, 1));
        createExpense(balgharban1C1, ExpenseType.EXTRA, bd("5000"), LocalDate.of(2026, 2, 1), "النقل");

        // ===================== فرع بالغربان 2 =====================
        Branch balgharban2 = getBranch(BranchType.BALGHARBAN_2);
        EducationClass balgharban2C1 = createClass(
                "Al-Fath", "الأستاذ الفتح",
                ClassType.RECITATION, balgharban2, bd("100000"), LocalDate.of(2025, 3, 1));
        EducationClass balgharban2C2 = createClass(
                "Mohamed Al-Qadh", "محمد القاظ",
                ClassType.RECITATION, balgharban2, bd("150000"), LocalDate.of(2025, 3, 1));
        createMonthlyExpenses(balgharban2C1, LocalDate.of(2026, 2, 1));
        createMonthlyExpenses(balgharban2C2, LocalDate.of(2026, 2, 1));
        createExpense(balgharban2C1, ExpenseType.EXTRA, bd("5000"), LocalDate.of(2026, 2, 1), "النقل");

        // ===================== فرع بير الفتح =====================
        Branch birAlFath = getBranch(BranchType.BIR_AL_FATH);
        EducationClass birC1 = createClass(
                "Oustaz Al-Nissa'", "أستاذ النساء",
                ClassType.RECITATION, birAlFath, bd("225000"), LocalDate.of(2025, 1, 1));
        EducationClass birC2 = createClass(
                "Al-Taysir", "التيسير",
                ClassType.PEDAGOGICAL, birAlFath, bd("100000"), LocalDate.of(2025, 1, 1));
        createMonthlyExpenses(birC1, LocalDate.of(2026, 2, 1));
        createMonthlyExpenses(birC2, LocalDate.of(2026, 2, 1));

        // ===================== فصل دار السلامة =====================
        Branch darAlSalama = getBranch(BranchType.DAR_AL_SALAMA);
        EducationClass darSalamaC1 = createClass(
                "Oustaz Dar Al-Salama", "أستاذ دار السلامة",
                ClassType.RECITATION, darAlSalama, bd("150000"), LocalDate.of(2024, 8, 5));
        createMonthlyExpenses(darSalamaC1, LocalDate.of(2026, 2, 1));

        // ===================== فصل أبو بكر الصديق =====================
        Branch abuBakr = getBranch(BranchType.ABU_BAKR_SIDDIQ);
        EducationClass abuBakrC1 = createClass(
                "Oustaz Abu Bakr", "أستاذ أبو بكر الصديق",
                ClassType.RECITATION, abuBakr, bd("150000"), LocalDate.of(2024, 10, 1));
        createMonthlyExpenses(abuBakrC1, LocalDate.of(2026, 2, 1));

        // ===================== فصل القلعة =====================
        Branch alQala = getBranch(BranchType.AL_QALA);
        EducationClass alQalaC1 = createClass(
                "Oustaz Al-Qala'a", "أستاذ القلعة",
                ClassType.RECITATION, alQala, bd("150000"), LocalDate.of(2024, 11, 1));
        createMonthlyExpenses(alQalaC1, LocalDate.of(2026, 2, 1));

        // ===================== فصل الشيخ أحمد =====================
        Branch sheikhAhmad = getBranch(BranchType.SHEIKH_AHMAD);
        EducationClass sheikhAhmadC1 = createClass(
                "Sheikh Ahmad", "الشيخ أحمد",
                ClassType.RECITATION, sheikhAhmad, bd("150000"), LocalDate.of(2025, 7, 1));
        createMonthlyExpenses(sheikhAhmadC1, LocalDate.of(2026, 2, 1));

        // ===================== فرع ينابيع الهداية اركيز =====================
        Branch yanabiHidaya = getBranch(BranchType.YANABI_HIDAYA);
        EducationClass yanabiC1 = createClass(
                "Oustaz Yanabi Al-Hidaya", "أستاذ ينابيع الهداية",
                ClassType.RECITATION, yanabiHidaya, bd("150000"), LocalDate.of(2025, 8, 1));
        createMonthlyExpenses(yanabiC1, LocalDate.of(2026, 2, 1));

        // ===================== فرع لكنيلة (مغلق) =====================
        Branch lknila = getBranch(BranchType.LKNILA);
        EducationClass lknilaC1 = createClass(
                "Oustaz Lknila", "أستاذ لكنيلة",
                ClassType.RECITATION, lknila, bd("100000"), LocalDate.of(2023, 5, 1));
        lknilaC1.setActive(false);
        lknilaC1.setEndDate(LocalDate.of(2023, 11, 1));
        classRepository.save(lknilaC1);

        // ===================== فرع دار الخير (مغلق) =====================
        Branch darAlKheir = getBranch(BranchType.DAR_AL_KHEIR);
        EducationClass darKheirC1 = createClass(
                "Oustaz Dar Al-Kheir", "أستاذ دار الخير",
                ClassType.RECITATION, darAlKheir, bd("100000"), LocalDate.of(2023, 10, 3));
        darKheirC1.setActive(false);
        darKheirC1.setEndDate(LocalDate.of(2024, 10, 3));
        classRepository.save(darKheirC1);

        log.info("Données de l'Institut Imam Nafi créées avec succès.");
    }

    private Branch getBranch(BranchType type) {
        return branchRepository.findByType(type)
                .orElseThrow(() -> new RuntimeException("Branche non trouvée: " + type));
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
                .build();
        return classRepository.save(educationClass);
    }

    private void createMonthlyExpenses(EducationClass educationClass, LocalDate month) {
        if (!educationClass.getActive()) return;
        if (educationClass.getStartDate().isAfter(month)) return;
        if (educationClass.getEndDate() != null && educationClass.getEndDate().isBefore(month)) return;

        Expense expense = Expense.builder()
                .educationClass(educationClass)
                .expenseType(ExpenseType.FIXED)
                .amount(educationClass.getMonthlyFixedAmount())
                .expenseDate(month.withDayOfMonth(1))
                .description("المرتب الشهري - " + educationClass.getNameAr())
                .build();
        expenseRepository.save(expense);
    }

    private void createExpense(EducationClass educationClass, ExpenseType type,
                               BigDecimal amount, LocalDate date, String description) {
        Expense expense = Expense.builder()
                .educationClass(educationClass)
                .expenseType(type)
                .amount(amount)
                .expenseDate(date)
                .description(description)
                .build();
        expenseRepository.save(expense);
    }

    private BigDecimal bd(String value) {
        return new BigDecimal(value);
    }
}
