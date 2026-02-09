package com.navi.education.dto.response;

import com.navi.education.model.enums.BranchType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardBranchSummary {
    private Long id;
    private BranchType type;
    private String nameAr;

    // Dépenses annuelles
    private BigDecimal annualFixedExpenses;   // المصروفات الثابتة السنوية
    private BigDecimal annualExtraExpenses;   // المصروفات الإضافية السنوية

    // Donations annuelles
    private BigDecimal annualDonationsPaid;   // التبرعات المدفوعة السنوية
    private BigDecimal annualDonationsLate;   // التبرعات المتأخرة السنوية

    // Solde de la branche
    private BigDecimal branchBalance;         // رصيد الفرع
}
