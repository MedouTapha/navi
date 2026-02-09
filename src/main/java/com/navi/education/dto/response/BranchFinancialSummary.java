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
public class BranchFinancialSummary {
    private Long id;
    private BranchType type;
    private String nameFr;
    private String nameAr;

    // Engagements et Paiements
    private BigDecimal totalCommitments;      // Total des engagements promis
    private BigDecimal totalPaymentsReceived; // Total des paiements reçus
    private BigDecimal remainingToReceive;    // Reste à recevoir des donateurs

    // Dépenses
    private BigDecimal totalFixedExpenses;    // Total dépenses FIXED (annuelles)
    private BigDecimal totalExtraExpenses;    // Total dépenses EXTRA (exceptionnelles)
    private BigDecimal totalExpenses;         // Total toutes dépenses

    // Bilan
    private BigDecimal balance;               // Solde = Paiements reçus - Dépenses

    // Statistiques
    private Integer totalClasses;
    private Integer totalDonors;
}
