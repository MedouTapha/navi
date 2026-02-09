package com.navi.education.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummary {
    private List<DashboardBranchSummary> branches;

    // Totaux globaux de l'institution
    private BigDecimal totalFixedExpenses;       // إجمالي المصروفات الثابتة
    private BigDecimal totalExtraExpenses;       // إجمالي المصروفات الإضافية
    private BigDecimal totalDonationsPaid;       // إجمالي التبرعات المدفوعة
    private BigDecimal totalDonationsLate;       // إجمالي التبرعات المتأخرة
    private BigDecimal globalBalance;            // الرصيد العام للمؤسسة

    // Statistiques
    private Integer totalBranches;
    private Integer totalClasses;
    private Integer totalDonors;
}
