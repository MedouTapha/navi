package com.navi.education.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class ClassDashboardSummary {
    private Long classId;
    private String classNameAr;
    private String classNameFr;
    private Integer financialYear;
    private LocalDate periodStart;

    // Dépenses
    private BigDecimal fixedExpenses;       // المصروفات الثابتة (salaires)
    private BigDecimal extraExpenses;       // المصروفات الإضافية (transport...)
    private BigDecimal totalExpenses;

    // Donations engagées (AnnualCommitment)
    private BigDecimal committedDonations;  // التعهدات
    private BigDecimal paidDonations;       // المدفوع من التعهدات
    private BigDecimal remainingDonations;  // المتبقي من التعهدات

    // Dons ponctuels hors-engagement
    private BigDecimal extraDonations;      // التبرعات الإضافية

    // Total reçu = paidDonations + extraDonations
    private BigDecimal totalReceived;

    // Bilan = totalReceived - totalExpenses
    private BigDecimal balance;
}
