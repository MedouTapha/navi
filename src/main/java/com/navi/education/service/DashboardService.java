package com.navi.education.service;

import com.navi.education.dto.response.DashboardBranchSummary;
import com.navi.education.dto.response.DashboardSummary;
import com.navi.education.model.entity.Branch;
import com.navi.education.model.enums.ExpenseType;
import com.navi.education.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final BranchRepository branchRepository;
    private final ExpenseRepository expenseRepository;
    private final PaymentRepository paymentRepository;
    private final AnnualCommitmentRepository commitmentRepository;
    private final EducationClassRepository classRepository;

    @Transactional(readOnly = true)
    public DashboardSummary getDashboardSummary() {
        List<Branch> allBranches = branchRepository.findAll();
        List<DashboardBranchSummary> branchSummaries = new ArrayList<>();

        // Totaux globaux
        BigDecimal totalFixedExpenses = BigDecimal.ZERO;
        BigDecimal totalExtraExpenses = BigDecimal.ZERO;
        BigDecimal totalDonationsPaid = BigDecimal.ZERO;
        BigDecimal totalDonationsLate = BigDecimal.ZERO;

        // Calculer pour chaque branche
        for (Branch branch : allBranches) {
            DashboardBranchSummary branchSummary = calculateBranchSummary(branch);
            branchSummaries.add(branchSummary);

            // Accumuler les totaux
            totalFixedExpenses = totalFixedExpenses.add(branchSummary.getAnnualFixedExpenses());
            totalExtraExpenses = totalExtraExpenses.add(branchSummary.getAnnualExtraExpenses());
            totalDonationsPaid = totalDonationsPaid.add(branchSummary.getAnnualDonationsPaid());
            totalDonationsLate = totalDonationsLate.add(branchSummary.getAnnualDonationsLate());
        }

        // Solde global = Total Donations Payées - Total Dépenses
        BigDecimal totalExpenses = totalFixedExpenses.add(totalExtraExpenses);
        BigDecimal globalBalance = totalDonationsPaid.subtract(totalExpenses);

        // Statistiques globales
        long totalClassesCount = classRepository.count();
        Integer totalDonors = getTotalUniqueDonors();

        return DashboardSummary.builder()
                .branches(branchSummaries)
                .totalFixedExpenses(totalFixedExpenses)
                .totalExtraExpenses(totalExtraExpenses)
                .totalDonationsPaid(totalDonationsPaid)
                .totalDonationsLate(totalDonationsLate)
                .globalBalance(globalBalance)
                .totalBranches(allBranches.size())
                .totalClasses((int) totalClassesCount)
                .totalDonors(totalDonors != null ? totalDonors : 0)
                .build();
    }

    private DashboardBranchSummary calculateBranchSummary(Branch branch) {
        Long branchId = branch.getId();

        // Dépenses annuelles par type
        BigDecimal annualFixedExpenses = expenseRepository
                .getTotalExpensesByBranchAndType(branchId, ExpenseType.FIXED);
        BigDecimal annualExtraExpenses = expenseRepository
                .getTotalExpensesByBranchAndType(branchId, ExpenseType.EXTRA);

        // Donations annuelles
        BigDecimal totalCommitments = commitmentRepository.getTotalCommitmentsByBranch(branchId);
        BigDecimal totalPaid = paymentRepository.getTotalPaymentsByBranch(branchId);

        // Donations payées et en retard
        BigDecimal annualDonationsPaid = totalPaid != null ? totalPaid : BigDecimal.ZERO;
        BigDecimal annualDonationsLate = (totalCommitments != null && totalPaid != null)
                ? totalCommitments.subtract(totalPaid)
                : (totalCommitments != null ? totalCommitments : BigDecimal.ZERO);

        // Solde de la branche
        BigDecimal totalBranchExpenses = BigDecimal.ZERO;
        if (annualFixedExpenses != null) {
            totalBranchExpenses = totalBranchExpenses.add(annualFixedExpenses);
        }
        if (annualExtraExpenses != null) {
            totalBranchExpenses = totalBranchExpenses.add(annualExtraExpenses);
        }
        BigDecimal branchBalance = annualDonationsPaid.subtract(totalBranchExpenses);

        return DashboardBranchSummary.builder()
                .id(branch.getId())
                .type(branch.getType())
                .nameAr(branch.getNameAr())
                .annualFixedExpenses(annualFixedExpenses != null ? annualFixedExpenses : BigDecimal.ZERO)
                .annualExtraExpenses(annualExtraExpenses != null ? annualExtraExpenses : BigDecimal.ZERO)
                .annualDonationsPaid(annualDonationsPaid)
                .annualDonationsLate(annualDonationsLate)
                .branchBalance(branchBalance)
                .build();
    }

    private Integer getTotalUniqueDonors() {
        // Compter tous les donateurs uniques de toutes les branches
        try {
            // On peut faire une requête native ou compter via tous les commitments
            return (int) commitmentRepository.findAll()
                    .stream()
                    .map(c -> c.getDonor().getId())
                    .distinct()
                    .count();
        } catch (Exception e) {
            log.error("Erreur lors du comptage des donateurs: {}", e.getMessage());
            return 0;
        }
    }
}
