package com.navi.education.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyProjection {
    private int monthNumber;
    private String monthNameAr;
    private int year;
    private BigDecimal expectedExpenses;   // salaires fixes
    private BigDecimal expectedIncome;     // entrées attendues (restant / mois restants)
    private BigDecimal monthlyBalance;     // entrées - sorties
    private BigDecimal cumulativeBalance;  // solde cumulé
}
