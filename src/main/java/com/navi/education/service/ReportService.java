package com.navi.education.service;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.layout.font.FontProvider;
import com.navi.education.dto.response.AnnualCommitmentResponse;
import com.navi.education.dto.response.DonorResponse;
import com.navi.education.dto.response.PaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

        String html = buildHtml(donor, commitments, payments);

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ConverterProperties props = new ConverterProperties();
            FontProvider fontProvider = new FontProvider();
            fontProvider.addFont(loadFont("/fonts/DejaVuSans.ttf"));
            fontProvider.addFont(loadFont("/fonts/DejaVuSans-Bold.ttf"));
            props.setFontProvider(fontProvider);
            HtmlConverter.convertToPdf(html, baos, props);
            log.info("PDF généré pour le donateur: {}", donor.getFullName());
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Erreur génération PDF pour donateur {}: {}", donorId, e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }

    private byte[] loadFont(String resourcePath) throws IOException {
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) throw new IOException("Font not found: " + resourcePath);
            return is.readAllBytes();
        }
    }

    private String buildHtml(DonorResponse donor,
                              List<AnnualCommitmentResponse> commitments,
                              List<PaymentResponse> payments) {

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        StringBuilder sb = new StringBuilder();

        sb.append("<!DOCTYPE html>");
        sb.append("<html lang='ar' dir='rtl'>");
        sb.append("<head><meta charset='UTF-8'>");
        sb.append("<style>");
        sb.append("body{font-family:'DejaVu Sans',Arial,sans-serif;margin:30px;direction:rtl;font-size:13px;}");
        sb.append("h1{color:#2c3e50;text-align:center;font-size:20px;margin-bottom:4px;}");
        sb.append(".subtitle{text-align:center;color:#7f8c8d;font-size:12px;margin-bottom:20px;}");
        sb.append("h2{color:#34495e;border-bottom:2px solid #3498db;padding-bottom:6px;margin-top:28px;font-size:15px;}");
        sb.append(".info-box{background:#f8f9fa;border-right:4px solid #3498db;padding:12px 16px;margin:12px 0;border-radius:4px;}");
        sb.append(".info-box p{margin:4px 0;font-size:13px;}");
        sb.append(".summary{display:flex;gap:12px;margin:16px 0;flex-wrap:wrap;}");
        sb.append(".sum-card{flex:1;min-width:130px;padding:12px;border-radius:6px;text-align:center;}");
        sb.append(".sum-card .label{font-size:11px;color:#7f8c8d;margin-bottom:5px;}");
        sb.append(".sum-card .val{font-size:16px;font-weight:bold;direction:ltr;}");
        sb.append(".card-commit{background:#f0f4ff;border:1px solid #b8c8ff;}");
        sb.append(".card-paid{background:#f0fff4;border:1px solid #b8f0c8;color:#155724;}");
        sb.append(".card-remain{background:#fff4f4;border:1px solid #f0b8b8;color:#721c24;}");
        sb.append("table{width:100%;border-collapse:collapse;margin:12px 0;font-size:12px;}");
        sb.append("th,td{border:1px solid #dee2e6;padding:8px 10px;}");
        sb.append("th{background:#3498db;color:white;font-weight:600;}");
        sb.append("tr:nth-child(even) td{background:#f8f9fa;}");
        sb.append(".paid{color:#27ae60;font-weight:600;}");
        sb.append(".remain{color:#e74c3c;font-weight:600;}");
        sb.append(".done{color:#27ae60;}");
        sb.append(".ongoing{color:#f39c12;}");
        sb.append(".ltr{direction:ltr;}");
        sb.append(".footer{margin-top:36px;text-align:center;color:#aaa;font-size:11px;border-top:1px solid #eee;padding-top:14px;}");
        sb.append("</style></head><body>");

        // رأس التقرير
        sb.append("<h1>معهد الإمام نافع لتعليم القرآن الكريم وعلومه</h1>");
        sb.append("<p class='subtitle'>بيان المتبرع — ").append(today).append("</p>");

        // معلومات المتبرع
        sb.append("<div class='info-box'>");
        sb.append("<p><strong>الاسم:</strong> ").append(esc(donor.getFullName())).append("</p>");
        sb.append("<p><strong>الهاتف:</strong> ").append(esc(donor.getTelephone())).append("</p>");
        if (donor.getEmail() != null && !donor.getEmail().isBlank()) {
            sb.append("<p><strong>البريد:</strong> ").append(esc(donor.getEmail())).append("</p>");
        }
        sb.append("</div>");

        // بطاقات الملخص
        sb.append("<div class='summary'>");
        sb.append("<div class='sum-card card-commit'><div class='label'>إجمالي الالتزامات</div>")
          .append("<div class='val'>").append(fmt(donor.getTotalCommitments())).append(" MRU</div></div>");
        sb.append("<div class='sum-card card-paid'><div class='label'>إجمالي المدفوع</div>")
          .append("<div class='val paid'>").append(fmt(donor.getTotalPaid())).append(" MRU</div></div>");
        sb.append("<div class='sum-card card-remain'><div class='label'>المتبقي</div>")
          .append("<div class='val remain'>").append(fmt(donor.getRemainingBalance())).append(" MRU</div></div>");
        sb.append("</div>");

        // جدول الالتزامات
        sb.append("<h2>الالتزامات السنوية</h2>");
        sb.append("<table><thead><tr>");
        sb.append("<th>الفرع</th><th>الفصل</th><th>السنة المالية</th>");
        sb.append("<th>الالتزام السنوي</th><th>المدفوع</th><th>المتبقي</th><th>الحالة</th>");
        sb.append("</tr></thead><tbody>");

        for (AnnualCommitmentResponse c : commitments) {
            String status = Boolean.TRUE.equals(c.getFullyPaid())
                    ? "<span class='done'>مكتمل</span>"
                    : "<span class='ongoing'>جاري</span>";
            sb.append("<tr>");
            sb.append("<td>").append(esc(c.getBranchName() != null ? c.getBranchName() : "-")).append("</td>");
            sb.append("<td>").append(esc(c.getClassName())).append("</td>");
            sb.append("<td>").append(c.getFinancialYear()).append("</td>");
            sb.append("<td class='ltr'>").append(fmt(c.getAnnualAmount())).append(" MRU</td>");
            sb.append("<td class='ltr paid'>").append(fmt(c.getTotalPaid())).append(" MRU</td>");
            sb.append("<td class='ltr remain'>").append(fmt(c.getRemainingBalance())).append(" MRU</td>");
            sb.append("<td>").append(status).append("</td>");
            sb.append("</tr>");
        }
        sb.append("</tbody></table>");

        // جدول المدفوعات
        sb.append("<h2>سجل المدفوعات</h2>");
        if (payments.isEmpty()) {
            sb.append("<p style='color:#7f8c8d;'>لا توجد مدفوعات مسجلة</p>");
        } else {
            sb.append("<table><thead><tr>");
            sb.append("<th>التاريخ</th><th>الفصل</th><th>المبلغ</th><th>وسيلة الدفع</th><th>رقم الوصل</th>");
            sb.append("</tr></thead><tbody>");
            for (PaymentResponse p : payments) {
                sb.append("<tr>");
                sb.append("<td>").append(p.getPaymentDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("</td>");
                sb.append("<td>").append(esc(p.getClassName())).append("</td>");
                sb.append("<td class='ltr paid'>").append(fmt(p.getAmount())).append(" MRU</td>");
                sb.append("<td>").append(esc(p.getPaymentMethod().getDisplayName())).append("</td>");
                sb.append("<td>").append(p.getReceiptNumber() != null ? esc(p.getReceiptNumber()) : "-").append("</td>");
                sb.append("</tr>");
            }
            sb.append("</tbody></table>");
        }

        sb.append("<div class='footer'>صدر بتاريخ ").append(today).append(" — معهد الإمام نافع</div>");
        sb.append("</body></html>");

        return sb.toString();
    }

    private String fmt(BigDecimal amount) {
        if (amount == null) return "0";
        return String.format("%,.0f", amount);
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
