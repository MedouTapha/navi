package com.navi.education.service;

import com.navi.education.dto.response.ImportResult;
import com.navi.education.model.entity.AnnualCommitment;
import com.navi.education.model.entity.Branch;
import com.navi.education.model.entity.Donor;
import com.navi.education.model.entity.EducationClass;
import com.navi.education.model.entity.Expense;
import com.navi.education.model.entity.ExtraDonation;
import com.navi.education.model.entity.Payment;
import com.navi.education.model.enums.BranchType;
import com.navi.education.model.enums.ClassType;
import com.navi.education.model.enums.ExpenseType;
import com.navi.education.model.enums.PaymentMethod;
import com.navi.education.repository.AnnualCommitmentRepository;
import com.navi.education.repository.BranchRepository;
import com.navi.education.repository.DonorRepository;
import com.navi.education.repository.EducationClassRepository;
import com.navi.education.repository.ExpenseRepository;
import com.navi.education.repository.ExtraDonationRepository;
import com.navi.education.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Génère un modèle Excel (un onglet par catégorie de données) et importe le
 * fichier rempli. La validation est exhaustive et la persistance « tout ou
 * rien » : aucune ligne n'est enregistrée tant que le fichier entier n'est pas
 * valide, ce qui évite les imports partiels difficiles à rattraper.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExcelImportService {

    private final BranchRepository branchRepository;
    private final EducationClassRepository classRepository;
    private final DonorRepository donorRepository;
    private final AnnualCommitmentRepository commitmentRepository;
    private final PaymentRepository paymentRepository;
    private final ExpenseRepository expenseRepository;
    private final ExtraDonationRepository extraDonationRepository;

    // ───────────────────────── Noms de feuilles ─────────────────────────
    public static final String SHEET_CLASSES = "الفصول";
    public static final String SHEET_DONORS = "المتبرعون";
    public static final String SHEET_COMMITMENTS = "الالتزامات";
    public static final String SHEET_PAYMENTS = "المدفوعات";
    public static final String SHEET_EXPENSES = "المصروفات الإضافية";
    public static final String SHEET_DONATIONS = "الهبات";
    private static final String SHEET_INSTRUCTIONS = "تعليمات";
    private static final String SHEET_REFS = "_قوائم";

    // En-têtes (l'ordre des colonnes fait foi : le générateur et le lecteur les partagent)
    private static final String[] H_CLASSES = {
            "اسم الفصل / الأستاذ", "الفرع", "نوع الفصل (تلاوة/تربوي)",
            "الراتب الشهري الثابت (MRU)", "تاريخ البداية (YYYY-MM-DD)"};
    private static final String[] H_DONORS = {
            "الاسم الأول", "الاسم الأخير", "الهاتف", "البريد الإلكتروني (اختياري)", "ملاحظات (اختياري)"};
    private static final String[] H_COMMITMENTS = {
            "الاسم الأول للمتبرع", "الاسم الأخير للمتبرع", "الفصل",
            "المبلغ السنوي (MRU)", "تاريخ الالتزام (YYYY-MM-DD)"};
    private static final String[] H_PAYMENTS = {
            "الاسم الأول للمتبرع", "الاسم الأخير للمتبرع", "الفصل", "المبلغ المدفوع (MRU)",
            "تاريخ الدفع (YYYY-MM-DD)", "وسيلة الدفع", "رقم الوصل (اختياري)"};
    private static final String[] H_EXPENSES = {
            "الفصل", "المبلغ (MRU)", "التاريخ (YYYY-MM-DD)", "البيان / الوصف"};
    private static final String[] H_DONATIONS = {
            "الاسم الأول للمتبرع", "الاسم الأخير للمتبرع", "الفصل",
            "المبلغ (MRU)", "التاريخ (YYYY-MM-DD)"};

    private static final String[] TYPE_OPTIONS = {"تلاوة", "تربوي"};
    private static final String[] METHOD_OPTIONS = {"نقداً", "Bankily", "Masrivi", "Sedad"};

    private static final DateTimeFormatter[] DATE_FORMATS = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy")};

    // ═════════════════════════ Génération du modèle ═════════════════════════

    public byte[] generateTemplate() {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            buildInstructionsSheet(wb);
            createDataSheet(wb, SHEET_CLASSES, H_CLASSES);
            createDataSheet(wb, SHEET_DONORS, H_DONORS);
            createDataSheet(wb, SHEET_COMMITMENTS, H_COMMITMENTS);
            createDataSheet(wb, SHEET_PAYMENTS, H_PAYMENTS);
            createDataSheet(wb, SHEET_EXPENSES, H_EXPENSES);
            createDataSheet(wb, SHEET_DONATIONS, H_DONATIONS);
            buildRefsSheet(wb);
            addDropdowns(wb);

            wb.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Impossible de générer le modèle Excel", e);
        }
    }

    private void createDataSheet(Workbook wb, String name, String[] headers) {
        Sheet sheet = wb.createSheet(name);
        sheet.setRightToLeft(true);

        Font font = wb.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        org.apache.poi.ss.usermodel.CellStyle style = wb.createCellStyle();
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);

        Row header = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
            sheet.setColumnWidth(i, 256 * 26);
        }
        sheet.createFreezePane(0, 1);
    }

    private void buildRefsSheet(Workbook wb) {
        Sheet refs = wb.createSheet(SHEET_REFS);
        Row title = refs.createRow(0);
        title.createCell(0).setCellValue("الفروع المتاحة");
        BranchType[] types = BranchType.values();
        for (int i = 0; i < types.length; i++) {
            refs.createRow(i + 1).createCell(0).setCellValue(types[i].getArabicName());
        }
        wb.setSheetHidden(wb.getSheetIndex(SHEET_REFS), true);
    }

    private void addDropdowns(Workbook wb) {
        int lastBranchRow = 1 + BranchType.values().length; // ligne Excel 1-based dans _قوائم

        // الفرع (feuille الفصول, colonne B) → référence la liste cachée
        Sheet classes = wb.getSheet(SHEET_CLASSES);
        DataValidationHelper helper = classes.getDataValidationHelper();
        DataValidationConstraint branchC = helper.createFormulaListConstraint(
                "'" + SHEET_REFS + "'!$A$2:$A$" + lastBranchRow);
        applyValidation(classes, helper, branchC, 1);
        // نوع الفصل (colonne C)
        applyValidation(classes, helper, helper.createExplicitListConstraint(TYPE_OPTIONS), 2);

        // وسيلة الدفع (feuille المدفوعات, colonne F)
        Sheet payments = wb.getSheet(SHEET_PAYMENTS);
        DataValidationHelper pHelper = payments.getDataValidationHelper();
        applyValidation(payments, pHelper, pHelper.createExplicitListConstraint(METHOD_OPTIONS), 5);
    }

    private void applyValidation(Sheet sheet, DataValidationHelper helper,
                                 DataValidationConstraint constraint, int col) {
        CellRangeAddressList range = new CellRangeAddressList(1, 1000, col, col);
        DataValidation validation = helper.createValidation(constraint, range);
        validation.setShowErrorBox(true);
        validation.setSuppressDropDownArrow(true);
        sheet.addValidationData(validation);
    }

    private void buildInstructionsSheet(Workbook wb) {
        Sheet sheet = wb.createSheet(SHEET_INSTRUCTIONS);
        sheet.setRightToLeft(true);
        sheet.setColumnWidth(0, 256 * 90);

        String[] lines = {
                "معهد الإمام نافع — نموذج إدخال البيانات",
                "",
                "طريقة الاستعمال:",
                "١. املأ الأوراق التالية حسب حاجتك (يمكنك ترك أي ورقة فارغة إن لم تكن لديك بياناتها).",
                "٢. لا تُغيّر صفّ العناوين (الصف الأول) في أي ورقة، ولا تُعِد ترتيب الأعمدة.",
                "٣. التواريخ بصيغة YYYY-MM-DD (مثال: 2024-09-01).",
                "٤. المبالغ أرقام فقط بالأوقية (MRU) بدون رموز.",
                "",
                "الأوراق:",
                "• الفصول: اسم الفصل/الأستاذ، الفرع (اخترهُ من القائمة)، نوع الفصل (تلاوة أو تربوي)، الراتب الشهري، تاريخ البداية.",
                "• المتبرعون: الاسم الأول، الاسم الأخير، الهاتف، البريد (اختياري)، ملاحظات (اختياري).",
                "• الالتزامات: اسم المتبرع (الأول + الأخير كما في ورقة المتبرعين)، الفصل (كما في ورقة الفصول)، المبلغ السنوي، التاريخ.",
                "• المدفوعات: المتبرع، الفصل، المبلغ المدفوع، التاريخ، وسيلة الدفع (نقداً/Bankily/Masrivi/Sedad)، رقم الوصل (اختياري).",
                "• المصروفات الإضافية: الفصل، المبلغ، التاريخ، البيان.",
                "• الهبات: المتبرع، الفصل، المبلغ، التاريخ.",
                "",
                "ملاحظات مهمة:",
                "• يجب أن تتطابق أسماء المتبرعين والفصول في أوراق الالتزامات/المدفوعات/المصروفات/الهبات مع ما كتبتَه في أوراق المتبرعين والفصول (أو ما هو موجود مسبقاً في النظام).",
                "• الترتيب بين الأوراق لا يهم: النظام يربط البيانات تلقائياً بالاسم.",
                "• إذا كان فصل أو متبرع موجوداً مسبقاً في النظام بنفس الاسم، فلن يُكرَّر (سيُتجاهَل مع تنبيه).",
                "• إن وُجد أي خطأ، لن يُحفظ أي شيء: سيعرض النظام قائمة الأخطاء بالورقة والسطر لتصحيحها وإعادة الرفع.",
                "",
                "الفروع المتاحة (انسخها حرفياً في عمود الفرع):"
        };
        int r = 0;
        Font bold = wb.createFont();
        bold.setBold(true);
        org.apache.poi.ss.usermodel.CellStyle titleStyle = wb.createCellStyle();
        titleStyle.setFont(bold);
        for (String line : lines) {
            Row row = sheet.createRow(r++);
            Cell c = row.createCell(0);
            c.setCellValue(line);
            if (line.endsWith(":") || r == 1) {
                c.setCellStyle(titleStyle);
            }
        }
        for (BranchType type : BranchType.values()) {
            sheet.createRow(r++).createCell(0).setCellValue("   • " + type.getArabicName());
        }
    }

    // ═════════════════════════ Import du fichier ═════════════════════════

    @Transactional
    public ImportResult importWorkbook(InputStream in) throws IOException {
        ImportResult result = ImportResult.builder().build();

        try (Workbook wb = new XSSFWorkbook(in)) {

            // ── Index de résolution (par nom), alimenté depuis la base existante ──
            Map<String, Branch> branchByName = new HashMap<>();
            for (Branch b : branchRepository.findAll()) {
                branchByName.put(norm(b.getNameAr()), b);
                branchByName.put(norm(b.getNameFr()), b);
            }
            Map<String, EducationClass> classByKey = new HashMap<>();
            Map<String, LocalDate> classStartByKey = new HashMap<>();
            for (EducationClass c : classRepository.findAll()) {
                String k = norm(c.getNameAr());
                classByKey.put(k, c);
                classStartByKey.put(k, c.getStartDate());
            }
            Map<String, Donor> donorByKey = new HashMap<>();
            for (Donor d : donorRepository.findAll()) {
                donorByKey.put(donorKey(d.getFirstName(), d.getLastName()), d);
            }
            Map<String, AnnualCommitment> commitmentByDonorClass = new HashMap<>();
            Set<String> commitmentYearKeys = new HashSet<>();
            Map<String, BigDecimal> annualByDonorClass = new HashMap<>();
            Map<String, BigDecimal> paidByDonorClass = new HashMap<>();
            for (AnnualCommitment c : commitmentRepository.findAll()) {
                String dk = donorKey(c.getDonor().getFirstName(), c.getDonor().getLastName());
                String ck = norm(c.getEducationClass().getNameAr());
                String key2 = dk + "||" + ck;
                commitmentYearKeys.add(key2 + "||" + c.getFinancialYear());
                commitmentByDonorClass.put(key2, c);
                annualByDonorClass.merge(key2, c.getAnnualAmount(), BigDecimal::add);
                BigDecimal paid = c.getPayments().stream()
                        .map(Payment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                paidByDonorClass.merge(key2, paid, BigDecimal::add);
            }

            // ── Passe 1 : lecture + validation (aucune écriture) ──
            List<ClassRow> classRows = parseClasses(wb, result, branchByName, classByKey, classStartByKey);
            List<DonorRow> donorRows = parseDonors(wb, result, donorByKey);
            List<CommitmentRow> commitmentRows = parseCommitments(
                    wb, result, donorByKey, classByKey, classStartByKey,
                    commitmentYearKeys, annualByDonorClass);
            List<PaymentRow> paymentRows = parsePayments(
                    wb, result, donorByKey, classByKey, annualByDonorClass, paidByDonorClass);
            List<ExpenseRow> expenseRows = parseExpenses(wb, result, classByKey);
            List<DonationRow> donationRows = parseDonations(wb, result, donorByKey, classByKey);

            if (result.isHasErrors()) {
                result.setSuccess(false);
                return result; // rien n'est persisté
            }

            // ── Passe 2 : persistance (le fichier est entièrement valide) ──
            int nClasses = 0;
            for (ClassRow cr : classRows) {
                Branch branch = branchByName.get(norm(cr.branchName));
                EducationClass c = EducationClass.builder()
                        .nameFr(cr.nameAr).nameAr(cr.nameAr).classType(cr.type)
                        .branch(branch).monthlyFixedAmount(cr.monthly).startDate(cr.start)
                        .active(true).build();
                c = classRepository.save(c);
                classByKey.put(norm(cr.nameAr), c);
                nClasses++;
            }
            int nDonors = 0;
            for (DonorRow dr : donorRows) {
                Donor d = Donor.builder()
                        .firstName(dr.first).lastName(dr.last).telephone(dr.tel)
                        .email(dr.email).notes(dr.notes).build();
                d = donorRepository.save(d);
                donorByKey.put(donorKey(dr.first, dr.last), d);
                nDonors++;
            }
            int nCommitments = 0;
            for (CommitmentRow cr : commitmentRows) {
                Donor donor = donorByKey.get(donorKey(cr.first, cr.last));
                EducationClass cls = classByKey.get(norm(cr.className));
                AnnualCommitment ac = AnnualCommitment.builder()
                        .donor(donor).educationClass(cls).annualAmount(cr.amount)
                        .commitmentDate(cr.date).financialYear(cls.getFinancialYear(cr.date))
                        .active(true).build();
                ac = commitmentRepository.save(ac);
                commitmentByDonorClass.put(donorKey(cr.first, cr.last) + "||" + norm(cr.className), ac);
                nCommitments++;
            }
            int nPayments = 0;
            for (PaymentRow pr : paymentRows) {
                AnnualCommitment ac = commitmentByDonorClass.get(
                        donorKey(pr.first, pr.last) + "||" + norm(pr.className));
                Payment p = Payment.builder()
                        .commitment(ac).amount(pr.amount).paymentDate(pr.date)
                        .paymentMethod(pr.method).receiptNumber(pr.receipt).build();
                paymentRepository.save(p);
                nPayments++;
            }
            int nExpenses = 0;
            for (ExpenseRow er : expenseRows) {
                EducationClass cls = classByKey.get(norm(er.className));
                Expense e = Expense.builder()
                        .educationClass(cls).expenseType(ExpenseType.EXTRA).amount(er.amount)
                        .expenseDate(er.date).description(er.description).build();
                expenseRepository.save(e);
                nExpenses++;
            }
            int nDonations = 0;
            for (DonationRow dr : donationRows) {
                Donor donor = donorByKey.get(donorKey(dr.first, dr.last));
                EducationClass cls = classByKey.get(norm(dr.className));
                ExtraDonation x = ExtraDonation.builder()
                        .donor(donor).educationClass(cls).amount(dr.amount)
                        .donationDate(dr.date).build();
                extraDonationRepository.save(x);
                nDonations++;
            }

            result.getCreated().put(SHEET_CLASSES, nClasses);
            result.getCreated().put(SHEET_DONORS, nDonors);
            result.getCreated().put(SHEET_COMMITMENTS, nCommitments);
            result.getCreated().put(SHEET_PAYMENTS, nPayments);
            result.getCreated().put(SHEET_EXPENSES, nExpenses);
            result.getCreated().put(SHEET_DONATIONS, nDonations);
            result.setSuccess(true);
            log.info("Import Excel réussi : {} lignes créées", result.getTotalCreated());
            return result;
        }
    }

    // ───────────────────────── Parsing par feuille ─────────────────────────

    private List<ClassRow> parseClasses(Workbook wb, ImportResult result,
                                        Map<String, Branch> branchByName,
                                        Map<String, EducationClass> classByKey,
                                        Map<String, LocalDate> classStartByKey) {
        List<ClassRow> rows = new ArrayList<>();
        Sheet sheet = wb.getSheet(SHEET_CLASSES);
        if (sheet == null) return rows;
        for (int r = 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (isEmpty(row, H_CLASSES.length)) continue;
            int excel = r + 1;
            try {
                String nameAr = req(str(row, 0), "اسم الفصل");
                String branch = req(str(row, 1), "الفرع");
                ClassType type = parseClassType(req(str(row, 2), "نوع الفصل"));
                BigDecimal monthly = reqAmount(amount(row, 3), "الراتب الشهري");
                LocalDate start = reqDate(date(row, 4), "تاريخ البداية");

                if (!branchByName.containsKey(norm(branch))) {
                    addError(result, SHEET_CLASSES, excel, "فرع غير معروف: « " + branch + " »");
                    continue;
                }
                String key = norm(nameAr);
                if (classByKey.containsKey(key)) {
                    result.getWarnings().add(SHEET_CLASSES + " (سطر " + excel + "): الفصل « "
                            + nameAr + " » موجود مسبقاً، تم تجاهله.");
                    continue;
                }
                // Enregistré tout de suite (valeur null = à créer) pour que les feuilles
                // suivantes puissent référencer cette classe par son nom.
                classByKey.put(key, null);
                classStartByKey.put(key, start);
                rows.add(new ClassRow(excel, nameAr, branch, type, monthly, start));
            } catch (Exception e) {
                addError(result, SHEET_CLASSES, excel, e.getMessage());
            }
        }
        return rows;
    }

    private List<DonorRow> parseDonors(Workbook wb, ImportResult result, Map<String, Donor> donorByKey) {
        List<DonorRow> rows = new ArrayList<>();
        Sheet sheet = wb.getSheet(SHEET_DONORS);
        if (sheet == null) return rows;
        for (int r = 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (isEmpty(row, H_DONORS.length)) continue;
            int excel = r + 1;
            try {
                String first = req(str(row, 0), "الاسم الأول");
                String last = req(str(row, 1), "الاسم الأخير");
                String tel = req(str(row, 2), "الهاتف");
                if (!tel.matches("^[0-9+\\s()-]+$")) {
                    throw new IllegalArgumentException("رقم هاتف غير صالح: " + tel);
                }
                String email = str(row, 3);
                if (email != null && !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
                    throw new IllegalArgumentException("بريد إلكتروني غير صالح: " + email);
                }
                String notes = str(row, 4);
                String key = donorKey(first, last);
                if (donorByKey.containsKey(key)) {
                    result.getWarnings().add(SHEET_DONORS + " (سطر " + excel + "): المتبرع « "
                            + first + " " + last + " » موجود مسبقاً، تم تجاهله.");
                    continue;
                }
                // Enregistré tout de suite (valeur null = à créer) pour les feuilles suivantes.
                donorByKey.put(key, null);
                rows.add(new DonorRow(excel, first, last, tel, email, notes));
            } catch (Exception e) {
                addError(result, SHEET_DONORS, excel, e.getMessage());
            }
        }
        return rows;
    }

    private List<CommitmentRow> parseCommitments(Workbook wb, ImportResult result,
                                                 Map<String, Donor> donorByKey,
                                                 Map<String, EducationClass> classByKey,
                                                 Map<String, LocalDate> classStartByKey,
                                                 Set<String> commitmentYearKeys,
                                                 Map<String, BigDecimal> annualByDonorClass) {
        List<CommitmentRow> rows = new ArrayList<>();
        Sheet sheet = wb.getSheet(SHEET_COMMITMENTS);
        if (sheet == null) return rows;
        for (int r = 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (isEmpty(row, H_COMMITMENTS.length)) continue;
            int excel = r + 1;
            try {
                String first = req(str(row, 0), "الاسم الأول للمتبرع");
                String last = req(str(row, 1), "الاسم الأخير للمتبرع");
                String className = req(str(row, 2), "الفصل");
                BigDecimal amount = reqAmount(amount(row, 3), "المبلغ السنوي");
                LocalDate dDate = reqDate(date(row, 4), "تاريخ الالتزام");

                String dk = donorKey(first, last);
                String ck = norm(className);
                if (!donorByKey.containsKey(dk)) {
                    addError(result, SHEET_COMMITMENTS, excel, "متبرع غير معروف: « " + first + " " + last
                            + " » — أضِفه في ورقة المتبرعين أولاً.");
                    continue;
                }
                if (!classByKey.containsKey(ck) && !classStartByKey.containsKey(ck)) {
                    addError(result, SHEET_COMMITMENTS, excel, "فصل غير معروف: « " + className
                            + " » — أضِفه في ورقة الفصول أولاً.");
                    continue;
                }
                LocalDate start = classStartByKey.get(ck);
                int year = financialYear(start, dDate);
                if (year == 0) {
                    addError(result, SHEET_COMMITMENTS, excel,
                            "تاريخ الالتزام قبل تاريخ بداية الفصل « " + className + " ».");
                    continue;
                }
                String key2 = dk + "||" + ck;
                String yearKey = key2 + "||" + year;
                if (commitmentYearKeys.contains(yearKey)) {
                    result.getWarnings().add(SHEET_COMMITMENTS + " (سطر " + excel
                            + "): التزام مكرّر لنفس المتبرع والفصل والسنة، تم تجاهله.");
                    continue;
                }
                commitmentYearKeys.add(yearKey);
                annualByDonorClass.merge(key2, amount, BigDecimal::add);
                rows.add(new CommitmentRow(excel, first, last, className, amount, dDate));
            } catch (Exception e) {
                addError(result, SHEET_COMMITMENTS, excel, e.getMessage());
            }
        }
        return rows;
    }

    private List<PaymentRow> parsePayments(Workbook wb, ImportResult result,
                                           Map<String, Donor> donorByKey,
                                           Map<String, EducationClass> classByKey,
                                           Map<String, BigDecimal> annualByDonorClass,
                                           Map<String, BigDecimal> paidByDonorClass) {
        List<PaymentRow> rows = new ArrayList<>();
        Sheet sheet = wb.getSheet(SHEET_PAYMENTS);
        if (sheet == null) return rows;
        for (int r = 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (isEmpty(row, H_PAYMENTS.length)) continue;
            int excel = r + 1;
            try {
                String first = req(str(row, 0), "الاسم الأول للمتبرع");
                String last = req(str(row, 1), "الاسم الأخير للمتبرع");
                String className = req(str(row, 2), "الفصل");
                BigDecimal amount = reqAmount(amount(row, 3), "المبلغ المدفوع");
                LocalDate pDate = reqDate(date(row, 4), "تاريخ الدفع");
                PaymentMethod method = parseMethod(req(str(row, 5), "وسيلة الدفع"));
                String receipt = str(row, 6);

                String key2 = donorKey(first, last) + "||" + norm(className);
                if (!annualByDonorClass.containsKey(key2)) {
                    addError(result, SHEET_PAYMENTS, excel, "لا يوجد التزام للمتبرع « " + first + " " + last
                            + " » في الفصل « " + className + " » — أضِف الالتزام أولاً.");
                    continue;
                }
                BigDecimal annual = annualByDonorClass.get(key2);
                BigDecimal running = paidByDonorClass.getOrDefault(key2, BigDecimal.ZERO).add(amount);
                if (running.compareTo(annual) > 0) {
                    addError(result, SHEET_PAYMENTS, excel,
                            "مجموع المدفوعات (" + running + ") يتجاوز قيمة الالتزام (" + annual + ").");
                    continue;
                }
                paidByDonorClass.put(key2, running);
                rows.add(new PaymentRow(excel, first, last, className, amount, pDate, method, receipt));
            } catch (Exception e) {
                addError(result, SHEET_PAYMENTS, excel, e.getMessage());
            }
        }
        return rows;
    }

    private List<ExpenseRow> parseExpenses(Workbook wb, ImportResult result,
                                           Map<String, EducationClass> classByKey) {
        List<ExpenseRow> rows = new ArrayList<>();
        Sheet sheet = wb.getSheet(SHEET_EXPENSES);
        if (sheet == null) return rows;
        for (int r = 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (isEmpty(row, H_EXPENSES.length)) continue;
            int excel = r + 1;
            try {
                String className = req(str(row, 0), "الفصل");
                BigDecimal amount = reqAmount(amount(row, 1), "المبلغ");
                LocalDate eDate = reqDate(date(row, 2), "التاريخ");
                String desc = req(str(row, 3), "البيان");

                if (!classByKey.containsKey(norm(className))) {
                    addError(result, SHEET_EXPENSES, excel, "فصل غير معروف: « " + className + " ».");
                    continue;
                }
                rows.add(new ExpenseRow(excel, className, amount, eDate, desc));
            } catch (Exception e) {
                addError(result, SHEET_EXPENSES, excel, e.getMessage());
            }
        }
        return rows;
    }

    private List<DonationRow> parseDonations(Workbook wb, ImportResult result,
                                             Map<String, Donor> donorByKey,
                                             Map<String, EducationClass> classByKey) {
        List<DonationRow> rows = new ArrayList<>();
        Sheet sheet = wb.getSheet(SHEET_DONATIONS);
        if (sheet == null) return rows;
        for (int r = 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (isEmpty(row, H_DONATIONS.length)) continue;
            int excel = r + 1;
            try {
                String first = req(str(row, 0), "الاسم الأول للمتبرع");
                String last = req(str(row, 1), "الاسم الأخير للمتبرع");
                String className = req(str(row, 2), "الفصل");
                BigDecimal amount = reqAmount(amount(row, 3), "المبلغ");
                LocalDate dDate = reqDate(date(row, 4), "التاريخ");

                if (!donorByKey.containsKey(donorKey(first, last))) {
                    addError(result, SHEET_DONATIONS, excel, "متبرع غير معروف: « " + first + " " + last + " ».");
                    continue;
                }
                if (!classByKey.containsKey(norm(className))) {
                    addError(result, SHEET_DONATIONS, excel, "فصل غير معروف: « " + className + " ».");
                    continue;
                }
                rows.add(new DonationRow(excel, first, last, className, amount, dDate));
            } catch (Exception e) {
                addError(result, SHEET_DONATIONS, excel, e.getMessage());
            }
        }
        return rows;
    }

    // ───────────────────────── Lecture des cellules ─────────────────────────

    private String str(Row row, int col) {
        Cell c = row.getCell(col);
        if (c == null) return null;
        String v;
        switch (c.getCellType()) {
            case STRING -> v = c.getStringCellValue();
            case BOOLEAN -> v = String.valueOf(c.getBooleanCellValue());
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(c)) {
                    v = c.getLocalDateTimeCellValue().toLocalDate().toString();
                } else {
                    double d = c.getNumericCellValue();
                    v = (d == Math.floor(d) && !Double.isInfinite(d))
                            ? String.valueOf((long) d) : String.valueOf(d);
                }
            }
            case FORMULA -> {
                try {
                    v = c.getStringCellValue();
                } catch (IllegalStateException e) {
                    v = String.valueOf(c.getNumericCellValue());
                }
            }
            default -> v = null;
        }
        if (v == null) return null;
        v = v.trim();
        return v.isEmpty() ? null : v;
    }

    private BigDecimal amount(Row row, int col) {
        Cell c = row.getCell(col);
        if (c == null) return null;
        if (c.getCellType() == CellType.NUMERIC) {
            return BigDecimal.valueOf(c.getNumericCellValue());
        }
        String s = str(row, col);
        if (s == null) return null;
        s = s.replace(",", "").replace("MRU", "").replace("أوقية", "").replaceAll("\\s+", "");
        if (s.isEmpty()) return null;
        try {
            return new BigDecimal(s);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("مبلغ غير صالح: " + str(row, col));
        }
    }

    private LocalDate date(Row row, int col) {
        Cell c = row.getCell(col);
        if (c == null) return null;
        if (c.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(c)) {
            return c.getLocalDateTimeCellValue().toLocalDate();
        }
        String s = str(row, col);
        if (s == null) return null;
        for (DateTimeFormatter f : DATE_FORMATS) {
            try {
                return LocalDate.parse(s, f);
            } catch (DateTimeParseException ignored) {
                // essaie le format suivant
            }
        }
        throw new IllegalArgumentException("تاريخ غير صالح: « " + s + " » (الصيغة المطلوبة YYYY-MM-DD)");
    }

    // ───────────────────────── Conversions / utilitaires ─────────────────────────

    private ClassType parseClassType(String s) {
        String n = norm(s);
        if (n.contains("تلاوة") || n.contains("تحفيظ") || n.contains("قرآن")
                || n.contains("recitation") || n.contains("récitation")) {
            return ClassType.RECITATION;
        }
        if (n.contains("تربوي") || n.contains("تعليمي")
                || n.contains("pedagog") || n.contains("pédagog")) {
            return ClassType.PEDAGOGICAL;
        }
        throw new IllegalArgumentException("نوع الفصل غير معروف: « " + s + " » (المسموح: تلاوة أو تربوي)");
    }

    private PaymentMethod parseMethod(String s) {
        String n = norm(s);
        if (n.contains("نقد") || n.contains("cash")) return PaymentMethod.CASH;
        if (n.contains("bankily")) return PaymentMethod.BANKILY;
        if (n.contains("sedad") || n.contains("سداد")) return PaymentMethod.SEDAD;
        if (n.contains("masr") || n.contains("مصرفي")) return PaymentMethod.MASRVI;
        throw new IllegalArgumentException("وسيلة دفع غير معروفة: « " + s
                + " » (المسموح: نقداً، Bankily، Masrivi، Sedad)");
    }

    /** Réplique {@code EducationClass.getFinancialYear} pour les classes pas encore persistées. */
    private int financialYear(LocalDate startDate, LocalDate date) {
        if (startDate == null || date.isBefore(startDate)) return 0;
        LocalDate cursor = startDate;
        int count = 1;
        while (!cursor.plusYears(1).isAfter(date)) {
            cursor = cursor.plusYears(1);
            count++;
        }
        return startDate.getYear() + (count - 1);
    }

    private String norm(String s) {
        return s == null ? "" : s.trim().replaceAll("\\s+", " ").toLowerCase();
    }

    private String donorKey(String first, String last) {
        return norm(first) + "||" + norm(last);
    }

    private boolean isEmpty(Row row, int cols) {
        if (row == null) return true;
        for (int i = 0; i < cols; i++) {
            if (str(row, i) != null) return false;
        }
        return true;
    }

    private String req(String v, String field) {
        if (v == null) throw new IllegalArgumentException(field + " مطلوب");
        return v;
    }

    private BigDecimal reqAmount(BigDecimal v, String field) {
        if (v == null) throw new IllegalArgumentException(field + " مطلوب");
        if (v.signum() <= 0) throw new IllegalArgumentException(field + " يجب أن يكون رقماً موجباً");
        return v;
    }

    private LocalDate reqDate(LocalDate v, String field) {
        if (v == null) throw new IllegalArgumentException(field + " مطلوب");
        return v;
    }

    private void addError(ImportResult result, String sheet, int row, String message) {
        result.getErrors().add(new ImportResult.RowError(sheet, row,
                message == null ? "خطأ غير محدد" : message));
    }

    // ───────────────────────── Lignes lues (en mémoire) ─────────────────────────

    private record ClassRow(int excel, String nameAr, String branchName, ClassType type,
                            BigDecimal monthly, LocalDate start) {}

    private record DonorRow(int excel, String first, String last, String tel, String email, String notes) {}

    private record CommitmentRow(int excel, String first, String last, String className,
                                 BigDecimal amount, LocalDate date) {}

    private record PaymentRow(int excel, String first, String last, String className, BigDecimal amount,
                              LocalDate date, PaymentMethod method, String receipt) {}

    private record ExpenseRow(int excel, String className, BigDecimal amount, LocalDate date, String description) {}

    private record DonationRow(int excel, String first, String last, String className,
                               BigDecimal amount, LocalDate date) {}
}
