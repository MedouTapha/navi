package com.navi.education.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class ClassFinancialYearSummary {
    private Integer financialYear;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private boolean current;

    // Dépenses
    private BigDecimal fixedExpenses;
    private BigDecimal extraExpenses;
    private BigDecimal totalExpenses;

    // Donations engagées (AnnualCommitment)
    private BigDecimal committedDonations;
    private BigDecimal paidDonations;
    private BigDecimal remainingDonations;

    // Dons ponctuels hors-engagement
    private BigDecimal extraDonations;

    // Total reçu = paidDonations + extraDonations
    private BigDecimal totalReceived;

    // Bilan = totalReceived - totalExpenses
    private BigDecimal balance;
}
