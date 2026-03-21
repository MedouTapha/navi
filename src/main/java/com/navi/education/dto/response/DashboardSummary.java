package com.navi.education.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummary {
    private List<DashboardBranchSummary> branches;

    // Date de référence
    private LocalDate referenceDate;

    // Totaux globaux de l'institution
    private BigDecimal totalFixedExpenses;          // إجمالي المصروفات الثابتة
    private BigDecimal totalExtraExpenses;          // إجمالي المصروفات الإضافية
    private BigDecimal totalExpenses;               // إجمالي المصروفات

    private BigDecimal totalCommittedDonations;     // إجمالي التعهدات
    private BigDecimal totalDonationsPaid;          // إجمالي المدفوع من التعهدات
    private BigDecimal totalDonationsLate;          // إجمالي المتأخر
    private BigDecimal totalExtraDonations;         // إجمالي التبرعات الإضافية
    private BigDecimal totalReceived;               // إجمالي المستلم (paid + extra)

    private BigDecimal globalBalance;               // الرصيد العام (received - expenses)

    // Statistiques
    private Integer totalBranches;
    private Integer totalClasses;
    private Integer totalDonors;
}
