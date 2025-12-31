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

        // HTML Header
        writer.println("<!DOCTYPE html>");
        writer.println("<html lang='fr'>");
        writer.println("<head>");
        writer.println("<meta charset='UTF-8'>");
        writer.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        writer.println("<title>Rapport de Paiements - " + donor.getFullName() + "</title>");
        writer.println("<style>");
        writer.println("body { font-family: Arial, sans-serif; margin: 20px; }");
        writer.println("h1 { color: #2c3e50; text-align: center; }");
        writer.println("h2 { color: #34495e; border-bottom: 2px solid #3498db; padding-bottom: 10px; }");
        writer.println(".info { margin: 20px 0; }");
        writer.println(".info p { margin: 5px 0; }");
        writer.println("table { width: 100%; border-collapse: collapse; margin: 20px 0; }");
        writer.println("th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }");
        writer.println("th { background-color: #3498db; color: white; }");
        writer.println("tr:nth-child(even) { background-color: #f2f2f2; }");
        writer.println(".summary { background-color: #ecf0f1; padding: 15px; margin: 20px 0; border-radius: 5px; }");
        writer.println(".summary p { font-weight: bold; margin: 5px 0; }");
        writer.println(".paid { color: #27ae60; }");
        writer.println(".remaining { color: #e74c3c; }");
        writer.println("@media print { .no-print { display: none; } }");
        writer.println("</style>");
        writer.println("</head>");
        writer.println("<body>");

        // Header
        writer.println("<h1>Rapport de Paiements</h1>");
        writer.println("<div class='info'>");
        writer.println("<p><strong>Donateur:</strong> " + donor.getFullName() + "</p>");
        writer.println("<p><strong>Téléphone:</strong> " + donor.getTelephone() + "</p>");
        if (donor.getEmail() != null) {
            writer.println("<p><strong>Email:</strong> " + donor.getEmail() + "</p>");
        }
        writer.println("<p><strong>Date du rapport:</strong> " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "</p>");
        writer.println("</div>");

        // Summary
        writer.println("<div class='summary'>");
        writer.println("<p>Total des engagements: " + formatAmount(donor.getTotalCommitments()) + " MRU</p>");
        writer.println("<p class='paid'>Total payé: " + formatAmount(donor.getTotalPaid()) + " MRU</p>");
        writer.println("<p class='remaining'>Solde restant: " + formatAmount(donor.getRemainingBalance()) + " MRU</p>");
        writer.println("</div>");

        // Commitments by Class
        writer.println("<h2>Engagements par Classe</h2>");
        writer.println("<table>");
        writer.println("<thead>");
        writer.println("<tr>");
        writer.println("<th>Classe</th>");
        writer.println("<th>Année Financière</th>");
        writer.println("<th>Montant Engagé</th>");
        writer.println("<th>Montant Payé</th>");
        writer.println("<th>Solde Restant</th>");
        writer.println("<th>Statut</th>");
        writer.println("</tr>");
        writer.println("</thead>");
        writer.println("<tbody>");

        for (AnnualCommitmentResponse commitment : commitments) {
            writer.println("<tr>");
            writer.println("<td>" + commitment.getClassName() + "</td>");
            writer.println("<td>" + commitment.getFinancialYear() + "</td>");
            writer.println("<td>" + formatAmount(commitment.getAnnualAmount()) + " MRU</td>");
            writer.println("<td class='paid'>" + formatAmount(commitment.getTotalPaid()) + " MRU</td>");
            writer.println("<td class='remaining'>" + formatAmount(commitment.getRemainingBalance()) + " MRU</td>");
            writer.println("<td>" + (commitment.getFullyPaid() ? "✓ Payé" : "En cours") + "</td>");
            writer.println("</tr>");
        }

        writer.println("</tbody>");
        writer.println("</table>");

        // Payment History
        writer.println("<h2>Historique des Paiements</h2>");
        writer.println("<table>");
        writer.println("<thead>");
        writer.println("<tr>");
        writer.println("<th>Date</th>");
        writer.println("<th>Classe</th>");
        writer.println("<th>Montant</th>");
        writer.println("<th>Moyen de Paiement</th>");
        writer.println("<th>N° Reçu</th>");
        writer.println("</tr>");
        writer.println("</thead>");
        writer.println("<tbody>");

        for (PaymentResponse payment : payments) {
            writer.println("<tr>");
            writer.println("<td>" + payment.getPaymentDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "</td>");
            writer.println("<td>" + payment.getClassName() + "</td>");
            writer.println("<td>" + formatAmount(payment.getAmount()) + " MRU</td>");
            writer.println("<td>" + payment.getPaymentMethod().getDisplayName() + "</td>");
            writer.println("<td>" + (payment.getReceiptNumber() != null ? payment.getReceiptNumber() : "-") + "</td>");
            writer.println("</tr>");
        }

        writer.println("</tbody>");
        writer.println("</table>");

        // Footer
        writer.println("<div style='margin-top: 40px; text-align: center; color: #7f8c8d;'>");
        writer.println("<p>Généré le " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "</p>");
        writer.println("</div>");

        writer.println("</body>");
        writer.println("</html>");

        writer.flush();
        writer.close();

        log.info("Rapport généré pour le donateur: {}", donor.getFullName());
        return baos.toByteArray();
    }

    private String formatAmount(BigDecimal amount) {
        if (amount == null) return "0.00";
        return String.format("%,.2f", amount);
    }
}
