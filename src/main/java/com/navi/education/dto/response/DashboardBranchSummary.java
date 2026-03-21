package com.navi.education.dto.response;

import com.navi.education.model.enums.BranchType;
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
public class DashboardBranchSummary {
    private Long id;
    private BranchType type;
    private String nameAr;

    // Dépenses
    private BigDecimal annualFixedExpenses;     // المصروفات الثابتة
    private BigDecimal annualExtraExpenses;     // المصروفات الإضافية
    private BigDecimal totalExpenses;           // إجمالي المصروفات

    // Donations
    private BigDecimal totalCommittedDonations; // إجمالي التعهدات
    private BigDecimal annualDonationsPaid;     // المدفوع من التعهدات
    private BigDecimal annualDonationsLate;     // المتأخر من التعهدات
    private BigDecimal totalExtraDonations;     // التبرعات الإضافية
    private BigDecimal totalReceived;           // إجمالي المستلم (paid + extra)

    // Bilan
    private BigDecimal branchBalance;           // رصيد الفرع (received - expenses)

    // Détail par classe
    private List<ClassDashboardSummary> classes;
}
