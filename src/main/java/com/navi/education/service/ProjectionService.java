package com.navi.education.service;

import com.navi.education.dto.response.MonthlyProjection;
import com.navi.education.repository.AnnualCommitmentRepository;
import com.navi.education.repository.EducationClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectionService {

    private final EducationClassRepository classRepository;
    private final AnnualCommitmentRepository commitmentRepository;
    private final DashboardService dashboardService;

    private static final String[] MONTHS_AR = {
        "", "يناير","فبراير","مارس","أبريل","مايو","يونيو",
        "يوليو","أغسطس","سبتمبر","أكتوبر","نوفمبر","ديسمبر"
    };

    @Transactional(readOnly = true)
    public List<MonthlyProjection> getProjection() {
        LocalDate today = LocalDate.now();

        // Dépenses fixes mensuelles = somme de tous les salaires des classes actives
        BigDecimal monthlyExpenses = classRepository.findByActiveTrue().stream()
                .map(c -> c.getMonthlyFixedAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Montant total restant sur tous les engagements non soldés
        BigDecimal totalRemaining = commitmentRepository.findAll().stream()
                .filter(c -> !c.isFullyPaid())
                .map(c -> c.getRemainingBalance())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Répartition équitable sur 6 mois
        BigDecimal monthlyIncome = totalRemaining.compareTo(BigDecimal.ZERO) > 0
                ? totalRemaining.divide(BigDecimal.valueOf(6), 0, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Solde actuel = total payé - total dépenses (depuis le dashboard)
        BigDecimal currentBalance = dashboardService.getDashboardSummary(today).getGlobalBalance();

        List<MonthlyProjection> projections = new ArrayList<>();
        BigDecimal cumulative = currentBalance;

        for (int i = 0; i < 6; i++) {
            LocalDate month = today.plusMonths(i).withDayOfMonth(1);
            BigDecimal balance = monthlyIncome.subtract(monthlyExpenses);
            cumulative = cumulative.add(balance);

            projections.add(MonthlyProjection.builder()
                    .monthNumber(month.getMonthValue())
                    .monthNameAr(MONTHS_AR[month.getMonthValue()])
                    .year(month.getYear())
                    .expectedExpenses(monthlyExpenses)
                    .expectedIncome(monthlyIncome)
                    .monthlyBalance(balance)
                    .cumulativeBalance(cumulative)
                    .build());
        }

        return projections;
    }
}
