package com.navi.education.service;

import com.navi.education.dto.response.ImportResult;
import com.navi.education.repository.AnnualCommitmentRepository;
import com.navi.education.repository.DonorRepository;
import com.navi.education.repository.EducationClassRepository;
import com.navi.education.repository.ExpenseRepository;
import com.navi.education.repository.ExtraDonationRepository;
import com.navi.education.repository.PaymentRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Vérifie le cycle complet d'import Excel : génération du modèle, remplissage
 * programmatique, import valide (« tout ou rien ») et rejet d'un fichier
 * contenant une donnée invalide (aucune écriture).
 */
@SpringBootTest
class ExcelImportServiceTest {

    @Autowired
    private ExcelImportService importService;
    @Autowired
    private EducationClassRepository classRepository;
    @Autowired
    private DonorRepository donorRepository;
    @Autowired
    private AnnualCommitmentRepository commitmentRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private ExtraDonationRepository extraDonationRepository;

    private static final String CLASS_NAME = "فصل اختبار الاستيراد";
    private static final String FIRST = "اختبار";
    private static final String LAST = "الاستيراد";

    @Test
    void importValidWorkbook_persistsAllCategories() throws Exception {
        long classesBefore = classRepository.count();
        long donorsBefore = donorRepository.count();
        long commitmentsBefore = commitmentRepository.count();
        long paymentsBefore = paymentRepository.count();
        long expensesBefore = expenseRepository.count();
        long donationsBefore = extraDonationRepository.count();

        byte[] file = buildWorkbook(true);
        ImportResult result = importService.importWorkbook(new ByteArrayInputStream(file));

        assertTrue(result.isSuccess(), "L'import devrait réussir : " + result.getErrors());
        assertFalse(result.isHasErrors());
        assertEquals(classesBefore + 1, classRepository.count());
        assertEquals(donorsBefore + 1, donorRepository.count());
        assertEquals(commitmentsBefore + 1, commitmentRepository.count());
        assertEquals(paymentsBefore + 1, paymentRepository.count());
        assertEquals(expensesBefore + 1, expenseRepository.count());
        assertEquals(donationsBefore + 1, extraDonationRepository.count());
    }

    @Test
    void importWorkbookWithInvalidBranch_persistsNothing() throws Exception {
        long classesBefore = classRepository.count();

        byte[] file = buildWorkbook(false); // branche inexistante
        ImportResult result = importService.importWorkbook(new ByteArrayInputStream(file));

        assertFalse(result.isSuccess());
        assertTrue(result.isHasErrors());
        assertEquals(classesBefore, classRepository.count(), "Aucune classe ne doit être créée");
    }

    /** Construit un classeur conforme au modèle ; {@code validBranch=false} y glisse une branche invalide. */
    private byte[] buildWorkbook(boolean validBranch) throws Exception {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // الفصول
            Sheet classes = wb.createSheet(ExcelImportService.SHEET_CLASSES);
            header(classes, "اسم", "الفرع", "النوع", "الراتب", "البداية");
            row(classes, 1, CLASS_NAME, validBranch ? "دوز دوز" : "فرع غير موجود إطلاقاً",
                    "تلاوة", "10000", "2024-01-01");

            // المتبرعون
            Sheet donors = wb.createSheet(ExcelImportService.SHEET_DONORS);
            header(donors, "الأول", "الأخير", "الهاتف", "البريد", "ملاحظات");
            row(donors, 1, FIRST, LAST, "22000000", "", "");

            // الالتزامات
            Sheet commitments = wb.createSheet(ExcelImportService.SHEET_COMMITMENTS);
            header(commitments, "الأول", "الأخير", "الفصل", "المبلغ", "التاريخ");
            row(commitments, 1, FIRST, LAST, CLASS_NAME, "50000", "2024-02-01");

            // المدفوعات
            Sheet payments = wb.createSheet(ExcelImportService.SHEET_PAYMENTS);
            header(payments, "الأول", "الأخير", "الفصل", "المبلغ", "التاريخ", "الوسيلة", "الوصل");
            row(payments, 1, FIRST, LAST, CLASS_NAME, "20000", "2024-03-01", "نقداً", "R-001");

            // المصروفات الإضافية
            Sheet expenses = wb.createSheet(ExcelImportService.SHEET_EXPENSES);
            header(expenses, "الفصل", "المبلغ", "التاريخ", "البيان");
            row(expenses, 1, CLASS_NAME, "3000", "2024-03-05", "نقل");

            // الهبات
            Sheet donations = wb.createSheet(ExcelImportService.SHEET_DONATIONS);
            header(donations, "الأول", "الأخير", "الفصل", "المبلغ", "التاريخ");
            row(donations, 1, FIRST, LAST, CLASS_NAME, "5000", "2024-04-01");

            wb.write(out);
            return out.toByteArray();
        }
    }

    private void header(Sheet sheet, String... cols) {
        row(sheet, 0, cols);
    }

    private void row(Sheet sheet, int rowIdx, String... values) {
        Row row = sheet.createRow(rowIdx);
        for (int i = 0; i < values.length; i++) {
            row.createCell(i).setCellValue(values[i]);
        }
    }
}
