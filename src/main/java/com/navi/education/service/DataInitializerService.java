package com.navi.education.service;

import com.navi.education.model.entity.*;
import com.navi.education.model.enums.*;
import com.navi.education.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataInitializerService implements CommandLineRunner {

    private final BranchService branchService;
    private final BranchRepository branchRepository;
    private final EducationClassRepository classRepository;
    private final ExpenseRepository expenseRepository;
    private final DonorRepository donorRepository;
    private final AnnualCommitmentRepository commitmentRepository;
    private final PaymentRepository paymentRepository;

    @Value("${app.seed-demo-data:true}")
    private boolean seedDemoData;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Initialisation des données de l'application...");
        // Les 5 branches de référence sont toujours créées (données structurelles).
        branchService.initializeBranches();

        if (!seedDemoData) {
            log.info("Données de démonstration désactivées (app.seed-demo-data=false). Démarrage avec une base vide.");
            return;
        }
        if (classRepository.count() > 0) {
            log.info("Les données existent déjà, initialisation ignorée.");
            return;
        }
        initializeRealData();
        log.info("Initialisation terminée avec succès.");
    }

    @Transactional
    public void initializeRealData() {
        log.info("Création des فصول, dépenses et donations de l'Institut Imam Nafi...");

        // ─────────────────────────────────────────────────────────────────────
        // BRANCHES ET CLASSES
        // Référence : 21/03/2026 — chaque classe utilise sa propre année financière
        // ─────────────────────────────────────────────────────────────────────

        // ===== دوز دوز =====
        // FY en cours : 2026 (Jan 2026 → Déc 2026) — 2 mois complétés (Jan-Fév)
        Branch douzDouz = getBranch(BranchType.DOUZ_DOUZ);
        EducationClass douzC1 = createClass("Mohamed Abdellah / Maysara", "محمد عبد الله / ميسرة",
                ClassType.RECITATION, douzDouz, bd("150000"), LocalDate.of(2026, 1, 1));
        createSalaryRange(douzC1, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 2, 1));
        createExtraRange(douzC1, bd("6000"), "النقل", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 2, 1));
        createExpense(douzC1, ExpenseType.EXTRA, bd("70000"), LocalDate.of(2026, 2, 1), "التراويح");

        // ===== فرع لمليكة =====
        // FY en cours : 2025 (Jun 2025 → Mai 2026) — 9 mois complétés (Jun 2025 - Fév 2026)
        Branch lmilika = getBranch(BranchType.LMILIKA);
        EducationClass lmilikaC1 = createClass("Al-Chafi'i Mohamed", "الشافعي محمد",
                ClassType.RECITATION, lmilika, bd("100000"), LocalDate.of(2024, 6, 1));
        createSalaryRange(lmilikaC1, LocalDate.of(2025, 6, 1), LocalDate.of(2026, 2, 1));
        createExtraRange(lmilikaC1, bd("20000"), "النقل", LocalDate.of(2025, 6, 1), LocalDate.of(2026, 2, 1));

        // ===== فرع آجوير =====
        // FY en cours : 2025 (Sep 2025 → Août 2026) — 6 mois complétés (Sep 2025 - Fév 2026)
        Branch ajouir = getBranch(BranchType.AJOUIR);
        EducationClass ajouirF1 = createClass("Houzayfa - Fasl 1", "حذيفة - الفصل 1",
                ClassType.RECITATION, ajouir, bd("200000"), LocalDate.of(2023, 9, 1));
        EducationClass ajouirF2 = createClass("Mohamadou / Al-Husayn - Fasl 2", "محمدو / الحسين - الفصل 2",
                ClassType.RECITATION, ajouir, bd("150000"), LocalDate.of(2023, 9, 1));
        EducationClass ajouirF3 = createClass("Saleh Mohamed - Fasl 3", "صالح محمد - الفصل 3",
                ClassType.RECITATION, ajouir, bd("150000"), LocalDate.of(2023, 9, 1));
        EducationClass ajouirF4 = createClass("Mohamed Salem - Fasl 4", "محمد سالم - الفصل 4",
                ClassType.RECITATION, ajouir, bd("100000"), LocalDate.of(2023, 9, 1));
        EducationClass ajouirF5 = createClass("Yaacoub / Ishaq - Fasl 5", "يعقوب / إسحاق - الفصل 5",
                ClassType.RECITATION, ajouir, bd("100000"), LocalDate.of(2023, 9, 1));
        EducationClass ajouirF6 = createClass("Afah / Bayat - Fasl 6", "أفاه / بيات - الفصل 6",
                ClassType.RECITATION, ajouir, bd("100000"), LocalDate.of(2023, 9, 1));
        EducationClass ajouirF7 = createClass("Al-Sa'd - Fasl 7", "السعد - الفصل 7",
                ClassType.RECITATION, ajouir, bd("100000"), LocalDate.of(2023, 9, 1));
        EducationClass ajouirF8 = createClass("Al-Mokhtar Ibrahim - Fasl 8", "المختار إبراهيم - الفصل 8",
                ClassType.RECITATION, ajouir, bd("100000"), LocalDate.of(2023, 9, 1));
        EducationClass ajouirBir = createClass("Oustaz Al-Bir", "أستاذ البير",
                ClassType.RECITATION, ajouir, bd("150000"), LocalDate.of(2023, 9, 1));
        for (EducationClass c : List.of(ajouirF1, ajouirF2, ajouirF3, ajouirF4,
                ajouirF5, ajouirF6, ajouirF7, ajouirF8, ajouirBir)) {
            createSalaryRange(c, LocalDate.of(2025, 9, 1), LocalDate.of(2026, 2, 1));
        }
        createExtraRange(ajouirF1, bd("20000"), "النقل", LocalDate.of(2025, 9, 1), LocalDate.of(2026, 2, 1));

        // ===== فرع الربينة =====
        // FY en cours : 2025 (Sep 2025 → Sep 2026) — 6 mois complétés
        Branch rabina = getBranch(BranchType.RABINA);
        EducationClass rabinaC1 = createClass("Hamah Allah", "حماه الله",
                ClassType.RECITATION, rabina, bd("150000"), LocalDate.of(2023, 9, 4));
        EducationClass rabinaC2 = createClass("Yahya", "يحيى",
                ClassType.RECITATION, rabina, bd("150000"), LocalDate.of(2023, 9, 4));
        createSalaryRange(rabinaC1, LocalDate.of(2025, 9, 1), LocalDate.of(2026, 2, 1));
        createSalaryRange(rabinaC2, LocalDate.of(2025, 9, 1), LocalDate.of(2026, 2, 1));
        createExtraRange(rabinaC1, bd("33000"), "النقل", LocalDate.of(2025, 9, 1), LocalDate.of(2026, 2, 1));

        // ===== فرع أولاد اخطيرة =====
        // FY en cours : 2025 (Jul 2025 → Jul 2026) — 8 mois complétés
        Branch ouladAkhtira = getBranch(BranchType.OULAD_AKHTIRA);
        EducationClass ouladC1 = createClass("Lababa", "الأستاذة لبابة",
                ClassType.RECITATION, ouladAkhtira, bd("120000"), LocalDate.of(2023, 7, 13));
        createSalaryRange(ouladC1, LocalDate.of(2025, 7, 1), LocalDate.of(2026, 2, 1));

        // ===== فرع إجغماجك - رحاب القرآن =====
        // FY en cours : 2025 (Jul 2025 → Jul 2026) — 8 mois complétés
        Branch ijghmajak = getBranch(BranchType.IJGHMAJAK);
        EducationClass ijghC1 = createClass("Oustaz Ijghmajak", "أستاذ الفرع",
                ClassType.RECITATION, ijghmajak, bd("100000"), LocalDate.of(2023, 7, 16));
        createSalaryRange(ijghC1, LocalDate.of(2025, 7, 1), LocalDate.of(2026, 2, 1));

        // ===== فرع بالغربان 1 =====
        // FY en cours : 2026 (Mar 2026 → Fév 2027) — 0 mois complets (année vient de commencer)
        // Historique année précédente (Mar 2025 - Fév 2026) conservé dans la base
        Branch balgharban1 = getBranch(BranchType.BALGHARBAN_1);
        EducationClass balgharban1C1 = createClass("Mohamadou Nini", "محمدو نيني",
                ClassType.RECITATION, balgharban1, bd("150000"), LocalDate.of(2025, 3, 1));
        EducationClass balgharban1C2 = createClass("Mohamadou Mahfoudh", "محمدو محفوظ",
                ClassType.RECITATION, balgharban1, bd("100000"), LocalDate.of(2025, 3, 1));
        createSalaryRange(balgharban1C1, LocalDate.of(2025, 3, 1), LocalDate.of(2026, 2, 1));
        createSalaryRange(balgharban1C2, LocalDate.of(2025, 3, 1), LocalDate.of(2026, 2, 1));
        createExtraRange(balgharban1C1, bd("5000"), "النقل", LocalDate.of(2025, 3, 1), LocalDate.of(2026, 2, 1));

        // ===== فرع بالغربان 2 =====
        Branch balgharban2 = getBranch(BranchType.BALGHARBAN_2);
        EducationClass balgharban2C1 = createClass("Al-Fath", "الأستاذ الفتح",
                ClassType.RECITATION, balgharban2, bd("100000"), LocalDate.of(2025, 3, 1));
        EducationClass balgharban2C2 = createClass("Mohamed Al-Qadh", "محمد القاظ",
                ClassType.RECITATION, balgharban2, bd("150000"), LocalDate.of(2025, 3, 1));
        createSalaryRange(balgharban2C1, LocalDate.of(2025, 3, 1), LocalDate.of(2026, 2, 1));
        createSalaryRange(balgharban2C2, LocalDate.of(2025, 3, 1), LocalDate.of(2026, 2, 1));
        createExtraRange(balgharban2C1, bd("5000"), "النقل", LocalDate.of(2025, 3, 1), LocalDate.of(2026, 2, 1));

        // ===== فرع بير الفتح =====
        // FY en cours : 2026 (Jan 2026 → Déc 2026) — 2 mois complétés (Jan-Fév)
        Branch birAlFath = getBranch(BranchType.BIR_AL_FATH);
        EducationClass birC1 = createClass("Oustaz Al-Nissa'", "أستاذ النساء",
                ClassType.RECITATION, birAlFath, bd("225000"), LocalDate.of(2025, 1, 1));
        EducationClass birC2 = createClass("Al-Taysir", "التيسير",
                ClassType.PEDAGOGICAL, birAlFath, bd("100000"), LocalDate.of(2025, 1, 1));
        createSalaryRange(birC1, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 2, 1));
        createSalaryRange(birC2, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 2, 1));

        // ===== فصل دار السلامة =====
        // FY en cours : 2025 (Août 2025 → Août 2026) — 7 mois complétés
        Branch darAlSalama = getBranch(BranchType.DAR_AL_SALAMA);
        EducationClass darSalamaC1 = createClass("Oustaz Dar Al-Salama", "أستاذ دار السلامة",
                ClassType.RECITATION, darAlSalama, bd("150000"), LocalDate.of(2024, 8, 5));
        createSalaryRange(darSalamaC1, LocalDate.of(2025, 8, 1), LocalDate.of(2026, 2, 1));

        // ===== فصل أبو بكر الصديق =====
        // FY en cours : 2025 (Oct 2025 → Sep 2026) — 5 mois complétés
        Branch abuBakr = getBranch(BranchType.ABU_BAKR_SIDDIQ);
        EducationClass abuBakrC1 = createClass("Oustaz Abu Bakr", "أستاذ أبو بكر الصديق",
                ClassType.RECITATION, abuBakr, bd("150000"), LocalDate.of(2024, 10, 1));
        createSalaryRange(abuBakrC1, LocalDate.of(2025, 10, 1), LocalDate.of(2026, 2, 1));

        // ===== فصل القلعة =====
        // FY en cours : 2025 (Nov 2025 → Oct 2026) — 4 mois complétés
        Branch alQala = getBranch(BranchType.AL_QALA);
        EducationClass alQalaC1 = createClass("Oustaz Al-Qala'a", "أستاذ القلعة",
                ClassType.RECITATION, alQala, bd("150000"), LocalDate.of(2024, 11, 1));
        createSalaryRange(alQalaC1, LocalDate.of(2025, 11, 1), LocalDate.of(2026, 2, 1));

        // ===== فصل الشيخ أحمد =====
        // FY en cours : 2025 (Jul 2025 → Jun 2026) — 8 mois complétés
        Branch sheikhAhmad = getBranch(BranchType.SHEIKH_AHMAD);
        EducationClass sheikhAhmadC1 = createClass("Sheikh Ahmad", "الشيخ أحمد",
                ClassType.RECITATION, sheikhAhmad, bd("150000"), LocalDate.of(2025, 7, 1));
        createSalaryRange(sheikhAhmadC1, LocalDate.of(2025, 7, 1), LocalDate.of(2026, 2, 1));

        // ===== فرع ينابيع الهداية اركيز =====
        // FY en cours : 2025 (Août 2025 → Jul 2026) — 7 mois complétés
        Branch yanabiHidaya = getBranch(BranchType.YANABI_HIDAYA);
        EducationClass yanabiC1 = createClass("Oustaz Yanabi Al-Hidaya", "أستاذ ينابيع الهداية",
                ClassType.RECITATION, yanabiHidaya, bd("150000"), LocalDate.of(2025, 8, 1));
        createSalaryRange(yanabiC1, LocalDate.of(2025, 8, 1), LocalDate.of(2026, 2, 1));

        // ===== فرع لكنيلة (مغلق) =====
        Branch lknila = getBranch(BranchType.LKNILA);
        EducationClass lknilaC1 = createClass("Oustaz Lknila", "أستاذ لكنيلة",
                ClassType.RECITATION, lknila, bd("100000"), LocalDate.of(2023, 5, 1));
        lknilaC1.setActive(false);
        lknilaC1.setEndDate(LocalDate.of(2023, 11, 1));
        classRepository.save(lknilaC1);

        // ===== فرع دار الخير (مغلق) =====
        Branch darAlKheir = getBranch(BranchType.DAR_AL_KHEIR);
        EducationClass darKheirC1 = createClass("Oustaz Dar Al-Kheir", "أستاذ دار الخير",
                ClassType.RECITATION, darAlKheir, bd("100000"), LocalDate.of(2023, 10, 3));
        darKheirC1.setActive(false);
        darKheirC1.setEndDate(LocalDate.of(2024, 10, 3));
        classRepository.save(darKheirC1);

        // ─────────────────────────────────────────────────────────────────────
        // DONATEURS, ENGAGEMENTS ET PAIEMENTS
        // ─────────────────────────────────────────────────────────────────────

        Donor lhbib    = createDonor("محمد لحبيب", "ولد محمد",          "22334455");
        Donor mokhtar  = createDonor("المختار",    "ولد إبراهيم",        "36112233");
        Donor khadija  = createDonor("خديجة",      "بنت أحمد",           "46223344");
        Donor abdullah = createDonor("عبد الله",   "ولد أحمد الأمين",    "22445566");
        Donor sidi     = createDonor("سيدي",       "ولد حمدي",           "25334455");
        Donor maysara  = createDonor("ميسرة",      "بنت أحمد",           "44223300");

        // --- دوز دوز (FY 2026 — commitmentDate en Jan 2026) ---
        AnnualCommitment ac = createCommitment(maysara, douzC1, bd("400000"), LocalDate.of(2026, 1, 10));
        createPayment(ac, bd("200000"), LocalDate.of(2026, 2, 10), PaymentMethod.BANKILY);

        // --- لمليكة (FY 2025 — commitmentDate en Jun 2025) ---
        ac = createCommitment(mokhtar, lmilikaC1, bd("600000"), LocalDate.of(2025, 6, 10));
        createPayment(ac, bd("400000"), LocalDate.of(2025, 9, 1), PaymentMethod.BANKILY);

        // --- آجوير (FY 2025 — commitmentDate en Sep 2025) ---
        ac = createCommitment(lhbib, ajouirF1, bd("1200000"), LocalDate.of(2025, 9, 10));
        createPayment(ac, bd("700000"), LocalDate.of(2025, 11, 1), PaymentMethod.BANKILY);

        ac = createCommitment(lhbib, ajouirF2, bd("900000"), LocalDate.of(2025, 9, 10));
        createPayment(ac, bd("500000"), LocalDate.of(2025, 11, 1), PaymentMethod.BANKILY);

        ac = createCommitment(mokhtar, ajouirF3, bd("900000"), LocalDate.of(2025, 9, 10));
        createPayment(ac, bd("450000"), LocalDate.of(2025, 10, 15), PaymentMethod.CASH);

        ac = createCommitment(mokhtar, ajouirF4, bd("600000"), LocalDate.of(2025, 9, 10));
        createPayment(ac, bd("300000"), LocalDate.of(2025, 10, 15), PaymentMethod.CASH);

        ac = createCommitment(mokhtar, ajouirF5, bd("600000"), LocalDate.of(2025, 9, 10));
        createPayment(ac, bd("300000"), LocalDate.of(2025, 12, 1), PaymentMethod.SEDAD);

        // F6 sans engagement confirmé

        ac = createCommitment(mokhtar, ajouirF7, bd("400000"), LocalDate.of(2025, 9, 10));
        createPayment(ac, bd("200000"), LocalDate.of(2025, 11, 20), PaymentMethod.CASH);

        ac = createCommitment(lhbib, ajouirF8, bd("400000"), LocalDate.of(2025, 9, 10));
        createPayment(ac, bd("200000"), LocalDate.of(2026, 1, 5), PaymentMethod.BANKILY);

        ac = createCommitment(mokhtar, ajouirBir, bd("600000"), LocalDate.of(2025, 9, 10));
        createPayment(ac, bd("300000"), LocalDate.of(2025, 10, 1), PaymentMethod.CASH);

        // --- الربينة (FY 2025 — commitmentDate en Sep 2025) ---
        ac = createCommitment(khadija, rabinaC1, bd("750000"), LocalDate.of(2025, 9, 10));
        createPayment(ac, bd("400000"), LocalDate.of(2025, 10, 5), PaymentMethod.MASRVI);

        ac = createCommitment(khadija, rabinaC2, bd("750000"), LocalDate.of(2025, 9, 10));
        createPayment(ac, bd("350000"), LocalDate.of(2025, 10, 5), PaymentMethod.MASRVI);

        // --- أولاد اخطيرة (FY 2025 — commitmentDate en Jul 2025) ---
        ac = createCommitment(khadija, ouladC1, bd("480000"), LocalDate.of(2025, 7, 20));
        createPayment(ac, bd("300000"), LocalDate.of(2025, 10, 1), PaymentMethod.CASH);

        // --- إجغماجك (FY 2025 — payé intégralement dès le départ) ---
        ac = createCommitment(maysara, ijghC1, bd("400000"), LocalDate.of(2025, 7, 20));
        createPayment(ac, bd("400000"), LocalDate.of(2025, 8, 1), PaymentMethod.BANKILY);

        // --- بالغربان 1 (FY 2026 — année vient de commencer, aucun paiement encore) ---
        createCommitment(mokhtar, balgharban1C1, bd("900000"), LocalDate.of(2026, 3, 5));
        createCommitment(mokhtar, balgharban1C2, bd("600000"), LocalDate.of(2026, 3, 5));

        // --- بالغربان 2 (FY 2026 — idem) ---
        createCommitment(lhbib, balgharban2C1, bd("600000"), LocalDate.of(2026, 3, 5));
        createCommitment(lhbib, balgharban2C2, bd("900000"), LocalDate.of(2026, 3, 5));

        // --- بير الفتح (FY 2026 — commitmentDate en Jan 2026) ---
        ac = createCommitment(sidi, birC1, bd("500000"), LocalDate.of(2026, 1, 10));
        createPayment(ac, bd("450000"), LocalDate.of(2026, 2, 1), PaymentMethod.BANKILY);

        ac = createCommitment(sidi, birC2, bd("200000"), LocalDate.of(2026, 1, 10));
        createPayment(ac, bd("200000"), LocalDate.of(2026, 1, 20), PaymentMethod.CASH);

        // --- دار السلامة (FY 2025 — commitmentDate en Août 2025) ---
        ac = createCommitment(abdullah, darSalamaC1, bd("700000"), LocalDate.of(2025, 8, 10));
        createPayment(ac, bd("400000"), LocalDate.of(2025, 11, 1), PaymentMethod.SEDAD);

        // --- أبو بكر الصديق (FY 2025 — commitmentDate en Oct 2025) ---
        ac = createCommitment(sidi, abuBakrC1, bd("600000"), LocalDate.of(2025, 10, 10));
        createPayment(ac, bd("400000"), LocalDate.of(2025, 12, 1), PaymentMethod.BANKILY);

        // --- القلعة (FY 2025 — commitmentDate en Nov 2025) ---
        ac = createCommitment(sidi, alQalaC1, bd("500000"), LocalDate.of(2025, 11, 10));
        createPayment(ac, bd("300000"), LocalDate.of(2026, 1, 15), PaymentMethod.MASRVI);

        // --- الشيخ أحمد (FY 2025 — commitmentDate en Jul 2025) ---
        ac = createCommitment(abdullah, sheikhAhmadC1, bd("800000"), LocalDate.of(2025, 7, 10));
        createPayment(ac, bd("500000"), LocalDate.of(2025, 10, 1), PaymentMethod.BANKILY);

        // --- ينابيع الهداية (FY 2025 — commitmentDate en Août 2025) ---
        ac = createCommitment(abdullah, yanabiC1, bd("700000"), LocalDate.of(2025, 8, 10));
        createPayment(ac, bd("350000"), LocalDate.of(2025, 11, 15), PaymentMethod.CASH);

        log.info("Données de l'Institut Imam Nafi créées avec succès.");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────────────────────

    private Branch getBranch(BranchType type) {
        return branchRepository.findByType(type)
                .orElseThrow(() -> new RuntimeException("Branche non trouvée: " + type));
    }

    private EducationClass createClass(String nameFr, String nameAr, ClassType type,
                                       Branch branch, BigDecimal monthlyAmount, LocalDate startDate) {
        return classRepository.save(EducationClass.builder()
                .nameFr(nameFr).nameAr(nameAr).classType(type).branch(branch)
                .monthlyFixedAmount(monthlyAmount).startDate(startDate).active(true).build());
    }

    /** Crée les salaires mensuels (FIXED) du premier au dernier mois inclus */
    private void createSalaryRange(EducationClass cls, LocalDate from, LocalDate to) {
        LocalDate month = from.withDayOfMonth(1);
        LocalDate end   = to.withDayOfMonth(1);
        while (!month.isAfter(end)) {
            if (!cls.getStartDate().isAfter(month)) {
                expenseRepository.save(Expense.builder()
                        .educationClass(cls)
                        .expenseType(ExpenseType.FIXED)
                        .amount(cls.getMonthlyFixedAmount())
                        .expenseDate(month)
                        .description("المرتب الشهري - " + cls.getNameAr())
                        .build());
            }
            month = month.plusMonths(1);
        }
    }

    /** Crée une dépense extra récurrente chaque mois */
    private void createExtraRange(EducationClass cls, BigDecimal amount, String desc,
                                  LocalDate from, LocalDate to) {
        LocalDate month = from.withDayOfMonth(1);
        LocalDate end   = to.withDayOfMonth(1);
        while (!month.isAfter(end)) {
            if (!cls.getStartDate().isAfter(month)) {
                createExpense(cls, ExpenseType.EXTRA, amount, month, desc);
            }
            month = month.plusMonths(1);
        }
    }

    private void createExpense(EducationClass cls, ExpenseType type,
                               BigDecimal amount, LocalDate date, String description) {
        expenseRepository.save(Expense.builder()
                .educationClass(cls).expenseType(type)
                .amount(amount).expenseDate(date).description(description).build());
    }

    private Donor createDonor(String firstName, String lastName, String phone) {
        return donorRepository.save(Donor.builder()
                .firstName(firstName).lastName(lastName).telephone(phone).build());
    }

    private AnnualCommitment createCommitment(Donor donor, EducationClass cls,
                                              BigDecimal amount, LocalDate date) {
        return commitmentRepository.save(AnnualCommitment.builder()
                .donor(donor).educationClass(cls)
                .annualAmount(amount).commitmentDate(date).build());
    }

    private void createPayment(AnnualCommitment commitment, BigDecimal amount,
                               LocalDate date, PaymentMethod method) {
        paymentRepository.save(Payment.builder()
                .commitment(commitment).amount(amount)
                .paymentDate(date).paymentMethod(method).build());
    }

    private BigDecimal bd(String value) {
        return new BigDecimal(value);
    }
}
