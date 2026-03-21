package com.navi.education.service;

import com.navi.education.dto.response.AnnualCommitmentResponse;
import com.navi.education.dto.response.DonorResponse;
import com.navi.education.dto.response.PaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final DonorService donorService;
    private final PaymentService paymentService;
    private final AnnualCommitmentService commitmentService;

    @Transactional(readOnly = true)
    public byte[] generateDonorPaymentReport(Long donorId) {
        DonorResponse donor = donorService.getDonorById(donorId);
        List<PaymentResponse> payments = paymentService.getPaymentsByDonor(donorId);
        List<AnnualCommitmentResponse> commitments = commitmentService.getCommitmentsByDonor(donorId);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(baos);

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        writer.println("<!DOCTYPE html>");
        writer.println("<html lang='ar' dir='rtl'>");
        writer.println("<head>");
        writer.println("<meta charset='UTF-8'>");
        writer.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        writer.println("<title>بيان المتبرع - " + donor.getFullName() + "</title>");
        writer.println("<style>");
        writer.println("body { font-family: Arial, sans-serif; margin: 30px; direction: rtl; }");
        writer.println("h1 { color: #2c3e50; text-align: center; font-size: 22px; margin-bottom: 4px; }");
        writer.println(".subtitle { text-align: center; color: #7f8c8d; font-size: 14px; margin-bottom: 20px; }");
        writer.println("h2 { color: #34495e; border-bottom: 2px solid #3498db; padding-bottom: 8px; margin-top: 30px; font-size: 16px; }");
        writer.println(".info-box { background: #f8f9fa; border-right: 4px solid #3498db; padding: 15px 20px; margin: 15px 0; border-radius: 4px; }");
        writer.println(".info-box p { margin: 4px 0; font-size: 14px; }");
        writer.println(".summary { display: flex; gap: 16px; margin: 20px 0; flex-wrap: wrap; }");
        writer.println(".sum-card { flex: 1; min-width: 150px; padding: 14px; border-radius: 8px; text-align: center; }");
        writer.println(".sum-card .label { font-size: 12px; color: #7f8c8d; margin-bottom: 6px; }");
        writer.println(".sum-card .val { font-size: 18px; font-weight: bold; direction: ltr; }");
        writer.println(".card-commit { background: #f0f4ff; border: 1px solid #b8c8ff; }");
        writer.println(".card-paid   { background: #f0fff4; border: 1px solid #b8f0c8; color: #155724; }");
        writer.println(".card-remain { background: #fff4f4; border: 1px solid #f0b8b8; color: #721c24; }");
        writer.println("table { width: 100%; border-collapse: collapse; margin: 15px 0; font-size: 13px; }");
        writer.println("th, td { border: 1px solid #dee2e6; padding: 10px 12px; }");
        writer.println("th { background: #3498db; color: white; font-weight: 600; }");
        writer.println("tr:nth-child(even) td { background: #f8f9fa; }");
        writer.println(".paid    { color: #27ae60; font-weight: 600; }");
        writer.println(".remain  { color: #e74c3c; font-weight: 600; }");
        writer.println(".done    { color: #27ae60; }");
        writer.println(".ongoing { color: #f39c12; }");
        writer.println(".ltr     { direction: ltr; }");
        writer.println(".footer  { margin-top: 40px; text-align: center; color: #aaa; font-size: 12px; border-top: 1px solid #eee; padding-top: 16px; }");
        writer.println(".no-print { display: block; text-align: center; margin: 20px 0; }");
        writer.println("@media print { .no-print { display: none !important; } }");
        writer.println("</style>");
        writer.println("</head>");
        writer.println("<body>");

        // زر الطباعة
        writer.println("<div class='no-print'>");
        writer.println("<button onclick='window.print()' style='padding:8px 20px;background:#3498db;color:white;border:none;border-radius:4px;cursor:pointer;font-size:14px;'>🖨️ طباعة / حفظ PDF</button>");
        writer.println("</div>");

        // رأس التقرير
        writer.println("<h1>معهد الإمام نافع لتعليم القرآن الكريم وعلومه</h1>");
        writer.println("<p class='subtitle'>بيان المتبرع — " + today + "</p>");

        // معلومات المتبرع
        writer.println("<div class='info-box'>");
        writer.println("<p><strong>الاسم:</strong> " + donor.getFullName() + "</p>");
        writer.println("<p><strong>الهاتف:</strong> " + donor.getTelephone() + "</p>");
        if (donor.getEmail() != null && !donor.getEmail().isBlank()) {
            writer.println("<p><strong>البريد:</strong> " + donor.getEmail() + "</p>");
        }
        writer.println("</div>");

        // بطاقات الملخص
        writer.println("<div class='summary'>");
        writer.println("<div class='sum-card card-commit'><div class='label'>إجمالي الالتزامات</div><div class='val'>" + formatAmount(donor.getTotalCommitments()) + " MRU</div></div>");
        writer.println("<div class='sum-card card-paid'><div class='label'>إجمالي المدفوع</div><div class='val paid'>" + formatAmount(donor.getTotalPaid()) + " MRU</div></div>");
        writer.println("<div class='sum-card card-remain'><div class='label'>المتبقي</div><div class='val remain'>" + formatAmount(donor.getRemainingBalance()) + " MRU</div></div>");
        writer.println("</div>");

        // جدول الالتزامات
        writer.println("<h2>الالتزامات السنوية</h2>");
        writer.println("<table><thead><tr>");
        writer.println("<th>الفرع</th><th>الفصل</th><th>السنة المالية</th>");
        writer.println("<th>الالتزام السنوي</th><th>المدفوع</th><th>المتبقي</th><th>الحالة</th>");
        writer.println("</tr></thead><tbody>");

        for (AnnualCommitmentResponse c : commitments) {
            String status = c.getFullyPaid()
                    ? "<span class='done'>✓ مكتمل</span>"
                    : "<span class='ongoing'>جاري</span>";
            writer.println("<tr>");
            writer.println("<td>" + (c.getBranchName() != null ? c.getBranchName() : "-") + "</td>");
            writer.println("<td>" + c.getClassName() + "</td>");
            writer.println("<td>" + c.getFinancialYear() + "</td>");
            writer.println("<td class='ltr'>" + formatAmount(c.getAnnualAmount()) + " MRU</td>");
            writer.println("<td class='ltr paid'>" + formatAmount(c.getTotalPaid()) + " MRU</td>");
            writer.println("<td class='ltr remain'>" + formatAmount(c.getRemainingBalance()) + " MRU</td>");
            writer.println("<td>" + status + "</td>");
            writer.println("</tr>");
        }
        writer.println("</tbody></table>");

        // جدول المدفوعات
        writer.println("<h2>سجل المدفوعات</h2>");
        if (payments.isEmpty()) {
            writer.println("<p style='color:#7f8c8d;'>لا توجد مدفوعات مسجلة</p>");
        } else {
            writer.println("<table><thead><tr>");
            writer.println("<th>التاريخ</th><th>الفصل</th><th>المبلغ</th><th>وسيلة الدفع</th><th>رقم الوصل</th>");
            writer.println("</tr></thead><tbody>");
            for (PaymentResponse p : payments) {
                writer.println("<tr>");
                writer.println("<td>" + p.getPaymentDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "</td>");
                writer.println("<td>" + p.getClassName() + "</td>");
                writer.println("<td class='ltr paid'>" + formatAmount(p.getAmount()) + " MRU</td>");
                writer.println("<td>" + p.getPaymentMethod().getDisplayName() + "</td>");
                writer.println("<td>" + (p.getReceiptNumber() != null ? p.getReceiptNumber() : "-") + "</td>");
                writer.println("</tr>");
            }
            writer.println("</tbody></table>");
        }

        writer.println("<div class='footer'>صدر بتاريخ " + today + " — معهد الإمام نافع</div>");
        writer.println("</body></html>");

        writer.flush();
        writer.close();

        log.info("Rapport généré pour le donateur: {}", donor.getFullName());
        return baos.toByteArray();
    }

    private String formatAmount(BigDecimal amount) {
        if (amount == null) return "0";
        return String.format("%,.0f", amount);
    }
}
